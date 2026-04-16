package repository;

import model.Venda;
import util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class VendaRepository {
    private static final String ARQUIVO = "vendas.json";
    private List<Venda> vendas;

    public VendaRepository() {
        vendas = JsonUtil.lerLista(ARQUIVO, Venda.class);
    }

    public List<Venda> listarTodas() {
        return new ArrayList<>(vendas);
    }

    public List<Venda> listarPorData(String prefixoData) {
        // prefixoData: "17/01/2025" para dia ou "01/2025" para mês
        List<Venda> lista = new ArrayList<>();
        for (Venda v : vendas) {
            if (v.getData() != null && v.getData().startsWith(prefixoData)) {
                lista.add(v);
            }
        }
        return lista;
    }

    public void salvar(Venda venda) {
        vendas.removeIf(v -> v.getId().equals(venda.getId()));
        vendas.add(venda);
        persistir();
    }

    private void persistir() {
        JsonUtil.salvarLista(ARQUIVO, vendas);
    }
}
