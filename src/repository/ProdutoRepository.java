package repository;

import model.Produto;
import util.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoRepository {
    private static final String ARQUIVO = "produtos.json";
    private List<Produto> produtos;

    public ProdutoRepository() {
        produtos = JsonUtil.lerLista(ARQUIVO, Produto.class);
    }

    public List<Produto> listarTodos() {
        return new ArrayList<>(produtos);
    }

    public Optional<Produto> buscarPorId(String id) {
        return produtos.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public Optional<Produto> buscarPorCodigo(String codigo) {
        return produtos.stream()
                .filter(p -> p.getCodigo().equalsIgnoreCase(codigo))
                .findFirst();
    }

    public List<Produto> buscarPorNome(String nome) {
        List<Produto> resultado = new ArrayList<>();
        for (Produto p : produtos) {
            if (p.getNome().toLowerCase().contains(nome.toLowerCase())) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public void salvar(Produto produto) {
        // Remove existente e adiciona atualizado
        produtos.removeIf(p -> p.getId().equals(produto.getId()));
        produtos.add(produto);
        persistir();
    }

    public boolean remover(String id) {
        boolean removido = produtos.removeIf(p -> p.getId().equals(id));
        if (removido) persistir();
        return removido;
    }

    public boolean existeCodigo(String codigo, String ignorarId) {
        return produtos.stream()
                .filter(p -> ignorarId == null || !p.getId().equals(ignorarId))
                .anyMatch(p -> p.getCodigo().equalsIgnoreCase(codigo));
    }

    private void persistir() {
        JsonUtil.salvarLista(ARQUIVO, produtos);
    }

    public List<Produto> produtosComEstoqueBaixo() {
        List<Produto> lista = new ArrayList<>();
        for (Produto p : produtos) {
            if (p.estoqueBaixo()) lista.add(p);
        }
        return lista;
    }
}
