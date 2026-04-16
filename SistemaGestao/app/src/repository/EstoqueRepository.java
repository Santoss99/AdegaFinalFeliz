package repository;

import model.MovimentacaoEstoque;
import util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class EstoqueRepository {
    private static final String ARQUIVO = "estoque.json";
    private List<MovimentacaoEstoque> movimentacoes;

    public EstoqueRepository() {
        movimentacoes = JsonUtil.lerLista(ARQUIVO, MovimentacaoEstoque.class);
    }

    public List<MovimentacaoEstoque> listarTodas() {
        return new ArrayList<>(movimentacoes);
    }

    public List<MovimentacaoEstoque> listarPorProduto(String produtoId) {
        List<MovimentacaoEstoque> lista = new ArrayList<>();
        for (MovimentacaoEstoque m : movimentacoes) {
            if (m.getProdutoId().equals(produtoId)) lista.add(m);
        }
        return lista;
    }

    public void salvar(MovimentacaoEstoque mov) {
        movimentacoes.add(mov);
        persistir();
    }

    private void persistir() {
        JsonUtil.salvarLista(ARQUIVO, movimentacoes);
    }
}
