package service;

import model.*;
import repository.ComandaRepository;
import repository.VendaRepository;
import util.IdUtil;

import java.util.List;
import java.util.Optional;

public class ComandaService {
    private final ComandaRepository comandaRepo;
    private final VendaRepository vendaRepo;
    private final ProdutoService produtoService;
    private final EstoqueService estoqueService;
    private final CaixaService caixaService;

    public ComandaService(ComandaRepository comandaRepo, VendaRepository vendaRepo,
                          ProdutoService produtoService, EstoqueService estoqueService,
                          CaixaService caixaService) {
        this.comandaRepo = comandaRepo;
        this.vendaRepo = vendaRepo;
        this.produtoService = produtoService;
        this.estoqueService = estoqueService;
        this.caixaService = caixaService;
    }

    public Comanda criarComanda(String identificacao) {
        if (identificacao == null || identificacao.trim().isEmpty()) {
            throw new IllegalArgumentException("Identificação da comanda é obrigatória.");
        }
        Comanda c = new Comanda(IdUtil.novoId(), identificacao.trim());
        comandaRepo.salvar(c);
        return c;
    }

    public Comanda adicionarProduto(String comandaId, String produtoId, int quantidade) {
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        Comanda c = buscarAberta(comandaId);
        Produto p = produtoService.buscarPorId(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + produtoId));

        if (p.getQuantidadeEstoque() < quantidade) {
            throw new IllegalArgumentException("Estoque insuficiente. Disponível: " + p.getQuantidadeEstoque());
        }

        // Se já existe o item, soma a quantidade
        Optional<ItemComanda> existente = c.getItens().stream()
                .filter(i -> i.getProdutoId().equals(produtoId))
                .findFirst();

        if (existente.isPresent()) {
            int novaQtd = existente.get().getQuantidade() + quantidade;
            if (p.getQuantidadeEstoque() < quantidade) {
                throw new IllegalArgumentException("Estoque insuficiente para quantidade adicional.");
            }
            existente.get().setQuantidade(novaQtd);
        } else {
            c.getItens().add(new ItemComanda(produtoId, p.getNome(), quantidade, p.getPrecoVenda()));
        }

        comandaRepo.salvar(c);
        return c;
    }

    public Comanda removerProduto(String comandaId, String produtoId) {
        Comanda c = buscarAberta(comandaId);
        boolean removido = c.getItens().removeIf(i -> i.getProdutoId().equals(produtoId));
        if (!removido) throw new IllegalArgumentException("Produto não encontrado na comanda.");
        comandaRepo.salvar(c);
        return c;
    }

    public Comanda adicionarPagamento(String comandaId, Pagamento.Forma forma, double valor) {
        if (valor <= 0) throw new IllegalArgumentException("Valor de pagamento inválido.");
        Comanda c = buscarAberta(comandaId);
        c.getPagamentos().add(new Pagamento(forma, valor));
        comandaRepo.salvar(c);
        return c;
    }

    public Comanda removerPagamentos(String comandaId) {
        Comanda c = buscarAberta(comandaId);
        c.getPagamentos().clear();
        comandaRepo.salvar(c);
        return c;
    }

    public Venda fecharComanda(String comandaId) {
        Comanda c = buscarAberta(comandaId);
        if (c.getItens().isEmpty()) throw new IllegalArgumentException("Comanda sem itens.");

        double total = c.calcularTotal();
        double totalPago = c.calcularTotalPago();

        if (totalPago < total - 0.01) { // tolerância de 1 centavo
            throw new IllegalArgumentException(String.format(
                    "Pagamento insuficiente. Total: R$%.2f | Pago: R$%.2f | Faltam: R$%.2f",
                    total, totalPago, total - totalPago));
        }

        // Baixar estoque de cada item
        double custo = 0;
        for (ItemComanda item : c.getItens()) {
            estoqueService.baixarEstoqueVenda(item.getProdutoId(), item.getQuantidade());
            Produto p = produtoService.buscarPorId(item.getProdutoId()).orElse(null);
            if (p != null) custo += p.getPrecoCompra() * item.getQuantidade();
        }

        c.fechar();
        comandaRepo.salvar(c);

        // Registrar venda
        Venda v = new Venda(IdUtil.novoId(), c.getId(), c.getIdentificacao(),
                total, custo, c.getPagamentos());
        vendaRepo.salvar(v);

        // Registrar no caixa
        caixaService.registrarVenda(v);

        return v;
    }

    public Comanda juntarComandas(String comandaOrigemId, String comandaDestinoId) {
        Comanda origem = buscarAberta(comandaOrigemId);
        Comanda destino = buscarAberta(comandaDestinoId);

        for (ItemComanda item : origem.getItens()) {
            Optional<ItemComanda> existente = destino.getItens().stream()
                    .filter(i -> i.getProdutoId().equals(item.getProdutoId()))
                    .findFirst();
            if (existente.isPresent()) {
                existente.get().setQuantidade(existente.get().getQuantidade() + item.getQuantidade());
            } else {
                destino.getItens().add(item);
            }
        }

        comandaRepo.salvar(destino);
        comandaRepo.remover(origem.getId());
        return destino;
    }

    public List<Comanda> listarTodas() { return comandaRepo.listarTodas(); }
    public List<Comanda> listarAbertas() { return comandaRepo.listarAbertas(); }

    public Comanda buscarPorId(String id) {
        return comandaRepo.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Comanda não encontrada: " + id));
    }

    private Comanda buscarAberta(String id) {
        Comanda c = buscarPorId(id);
        if (c.getStatus() != Comanda.Status.ABERTA) {
            throw new IllegalArgumentException("Comanda já está fechada.");
        }
        return c;
    }
}
