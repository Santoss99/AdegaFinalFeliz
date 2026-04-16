package repository;

import model.Caixa;
import util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class CaixaRepository {
    private static final String ARQUIVO = "caixa.json";
    private List<Caixa> historico;

    public CaixaRepository() {
        historico = JsonUtil.lerLista(ARQUIVO, Caixa.class);
        for (Caixa c : historico) {
            if (c.getMovimentacoes() == null) c.setMovimentacoes(new ArrayList<>());
        }
    }

    public List<Caixa> listarHistorico() {
        return new ArrayList<>(historico);
    }

    public Caixa buscarAberto() {
        for (Caixa c : historico) {
            if (c.getStatus() == Caixa.Status.ABERTO) return c;
        }
        return null;
    }

    public void salvar(Caixa caixa) {
        historico.removeIf(c -> c.getId().equals(caixa.getId()));
        historico.add(caixa);
        persistir();
    }

    private void persistir() {
        JsonUtil.salvarLista(ARQUIVO, historico);
    }
}
