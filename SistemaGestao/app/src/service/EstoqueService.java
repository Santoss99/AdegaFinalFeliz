package service;

import model.MovimentacaoEstoque;
import model.Produto;
import repository.EstoqueRepository;
import util.IdUtil;

import java.util.List;

public class EstoqueService {
    private final EstoqueRepository estoqueRepo;
    private final ProdutoService produtoService;

    public EstoqueService(EstoqueRepository estoqueRepo, ProdutoService produtoService) {
        this.estoqueRepo = estoqueRepo;
        this.produtoService = produtoService;
    }

    public MovimentacaoEstoque registrarEntrada(String produtoId, int quantidade, String observacao) {
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        Produto p = produtoService.buscarPorId(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + produtoId));

        int anterior = p.getQuantidadeEstoque();
        int nova = anterior + quantidade;
        produtoService.atualizarEstoque(produtoId, nova);

        MovimentacaoEstoque mov = new MovimentacaoEstoque(
                IdUtil.novoId(), produtoId, p.getNome(),
                MovimentacaoEstoque.Tipo.ENTRADA, quantidade, anterior, nova, observacao);
        estoqueRepo.salvar(mov);
        return mov;
    }

    public MovimentacaoEstoque registrarSaida(String produtoId, int quantidade, String observacao) {
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        Produto p = produtoService.buscarPorId(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + produtoId));

        int anterior = p.getQuantidadeEstoque();
        if (anterior < quantidade) {
            throw new IllegalArgumentException("Estoque insuficiente. Disponível: " + anterior);
        }

        int nova = anterior - quantidade;
        produtoService.atualizarEstoque(produtoId, nova);

        MovimentacaoEstoque mov = new MovimentacaoEstoque(
                IdUtil.novoId(), produtoId, p.getNome(),
                MovimentacaoEstoque.Tipo.SAIDA, quantidade, anterior, nova, observacao);
        estoqueRepo.salvar(mov);
        return mov;
    }

    public MovimentacaoEstoque ajustarEstoque(String produtoId, int novaQuantidade, String observacao) {
        if (novaQuantidade < 0) throw new IllegalArgumentException("Quantidade não pode ser negativa.");
        Produto p = produtoService.buscarPorId(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + produtoId));

        int anterior = p.getQuantidadeEstoque();
        int diferenca = Math.abs(novaQuantidade - anterior);
        produtoService.atualizarEstoque(produtoId, novaQuantidade);

        MovimentacaoEstoque mov = new MovimentacaoEstoque(
                IdUtil.novoId(), produtoId, p.getNome(),
                MovimentacaoEstoque.Tipo.AJUSTE, diferenca, anterior, novaQuantidade, observacao);
        estoqueRepo.salvar(mov);
        return mov;
    }

    public List<MovimentacaoEstoque> listarHistorico() {
        return estoqueRepo.listarTodas();
    }

    public List<MovimentacaoEstoque> listarPorProduto(String produtoId) {
        return estoqueRepo.listarPorProduto(produtoId);
    }

    // Usado internamente pelas vendas - baixa estoque sem registrar movimentação separada
    public void baixarEstoqueVenda(String produtoId, int quantidade) {
        Produto p = produtoService.buscarPorId(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + produtoId));
        int anterior = p.getQuantidadeEstoque();
        if (anterior < quantidade) {
            throw new IllegalArgumentException("Estoque insuficiente para " + p.getNome()
                    + ". Disponível: " + anterior + ", solicitado: " + quantidade);
        }
        int nova = anterior - quantidade;
        produtoService.atualizarEstoque(produtoId, nova);

        MovimentacaoEstoque mov = new MovimentacaoEstoque(
                IdUtil.novoId(), produtoId, p.getNome(),
                MovimentacaoEstoque.Tipo.SAIDA, quantidade, anterior, nova, "Venda (comanda)");
        estoqueRepo.salvar(mov);
    }
}
