package repository;

import model.Comanda;
import util.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComandaRepository {
    private static final String ARQUIVO = "comandas.json";
    private List<Comanda> comandas;

    public ComandaRepository() {
        comandas = JsonUtil.lerLista(ARQUIVO, Comanda.class);
        // Garantir listas não nulas após desserialização
        for (Comanda c : comandas) {
            if (c.getItens() == null) c.setItens(new ArrayList<>());
            if (c.getPagamentos() == null) c.setPagamentos(new ArrayList<>());
        }
    }

    public List<Comanda> listarTodas() {
        return new ArrayList<>(comandas);
    }

    public List<Comanda> listarAbertas() {
        List<Comanda> lista = new ArrayList<>();
        for (Comanda c : comandas) {
            if (c.getStatus() == Comanda.Status.ABERTA) lista.add(c);
        }
        return lista;
    }

    public Optional<Comanda> buscarPorId(String id) {
        return comandas.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    public void salvar(Comanda comanda) {
        comandas.removeIf(c -> c.getId().equals(comanda.getId()));
        comandas.add(comanda);
        persistir();
    }

    public boolean remover(String id) {
        boolean removido = comandas.removeIf(c -> c.getId().equals(id));
        if (removido) persistir();
        return removido;
    }

    private void persistir() {
        JsonUtil.salvarLista(ARQUIVO, comandas);
    }
}
