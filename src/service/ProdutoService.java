package service;

import model.Produto;
import repository.ProdutoRepository;
import util.IdUtil;

import java.util.List;
import java.util.Optional;

public class ProdutoService {
    private final ProdutoRepository repo;

    public ProdutoService(ProdutoRepository repo) {
        this.repo = repo;
    }

    public Produto cadastrar(String nome, String codigo, double precoCompra,
                              double precoVenda, int qtdEstoque, int estoqueMinimo) {
        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Nome obrigatório.");
        if (codigo == null || codigo.trim().isEmpty()) throw new IllegalArgumentException("Código obrigatório.");
        if (precoCompra < 0) throw new IllegalArgumentException("Preço de compra inválido.");
        if (precoVenda <= 0) throw new IllegalArgumentException("Preço de venda inválido.");
        if (qtdEstoque < 0) throw new IllegalArgumentException("Quantidade não pode ser negativa.");
        if (repo.existeCodigo(codigo.trim(), null)) {
            throw new IllegalArgumentException("Já existe produto com o código: " + codigo);
        }

        Produto p = new Produto(IdUtil.novoId(), nome.trim(), codigo.trim().toUpperCase(),
                precoCompra, precoVenda, qtdEstoque, estoqueMinimo);
        repo.salvar(p);
        return p;
    }

    public Produto editar(String id, String nome, String codigo, double precoCompra,
                           double precoVenda, int estoqueMinimo) {
        Produto p = repo.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));

        if (!p.getCodigo().equalsIgnoreCase(codigo) && repo.existeCodigo(codigo, id)) {
            throw new IllegalArgumentException("Código já em uso por outro produto.");
        }

        p.setNome(nome.trim());
        p.setCodigo(codigo.trim().toUpperCase());
        p.setPrecoCompra(precoCompra);
        p.setPrecoVenda(precoVenda);
        p.setEstoqueMinimo(estoqueMinimo);
        repo.salvar(p);
        return p;
    }

    public void remover(String id) {
        if (!repo.remover(id)) {
            throw new IllegalArgumentException("Produto não encontrado: " + id);
        }
    }

    public List<Produto> listarTodos() {
        return repo.listarTodos();
    }

    public Optional<Produto> buscarPorId(String id) {
        return repo.buscarPorId(id);
    }

    public Optional<Produto> buscarPorCodigo(String codigo) {
        return repo.buscarPorCodigo(codigo);
    }

    public List<Produto> buscarPorNome(String nome) {
        return repo.buscarPorNome(nome);
    }

    public List<Produto> produtosComEstoqueBaixo() {
        return repo.produtosComEstoqueBaixo();
    }

    // Chamado internamente pelo EstoqueService
    public void atualizarEstoque(String produtoId, int novaQuantidade) {
        Produto p = repo.buscarPorId(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + produtoId));
        p.setQuantidadeEstoque(novaQuantidade);
        repo.salvar(p);
    }
}
