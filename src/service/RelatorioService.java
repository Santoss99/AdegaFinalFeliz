package service;

import model.Produto;
import model.Venda;
import repository.VendaRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatorioService {
    private final VendaRepository vendaRepo;
    private final ProdutoService produtoService;

    private static final DateTimeFormatter FMT_DIA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_MES = DateTimeFormatter.ofPattern("MM/yyyy");

    public RelatorioService(VendaRepository vendaRepo, ProdutoService produtoService) {
        this.vendaRepo = vendaRepo;
        this.produtoService = produtoService;
    }

    public Map<String, Object> relatorioHoje() {
        String hoje = LocalDate.now().format(FMT_DIA);
        List<Venda> vendas = vendaRepo.listarPorData(hoje);
        return construirResumo("Hoje (" + hoje + ")", vendas);
    }

    public Map<String, Object> relatorioMesAtual() {
        String mes = LocalDate.now().format(FMT_MES);
        List<Venda> vendas = vendaRepo.listarPorData(mes.substring(3)); // "MM/yyyy" -> busca por "/yyyy" não, usa substring
        // Correto: busca vendas cujo campo data contém o mês/ano
        vendas = vendaRepo.listarPorData(mes.substring(0, 2) + "/" + mes.substring(3));
        // Na prática, data = "dd/MM/yyyy HH:mm:ss" → buscar por "MM/yyyy" precisa de contains
        vendas = filtrarPorMesAno(mes);
        return construirResumo("Mês atual (" + mes + ")", vendas);
    }

    public Map<String, Object> relatorioGeral() {
        List<Venda> vendas = vendaRepo.listarTodas();
        return construirResumo("Geral (todos os períodos)", vendas);
    }

    public Map<String, Object> relatorioEstoque() {
        List<Produto> produtos = produtoService.listarTodos();
        List<Produto> baixoEstoque = produtoService.produtosComEstoqueBaixo();

        double valorTotalEstoque = 0;
        double custoTotalEstoque = 0;
        for (Produto p : produtos) {
            valorTotalEstoque += p.getPrecoVenda() * p.getQuantidadeEstoque();
            custoTotalEstoque += p.getPrecoCompra() * p.getQuantidadeEstoque();
        }

        Map<String, Object> r = new HashMap<>();
        r.put("totalProdutos", produtos.size());
        r.put("produtosComEstoqueBaixo", baixoEstoque.size());
        r.put("valorTotalVenda", valorTotalEstoque);
        r.put("custoTotalEstoque", custoTotalEstoque);
        r.put("lucroEstimado", valorTotalEstoque - custoTotalEstoque);
        r.put("produtosBaixoEstoque", baixoEstoque);
        return r;
    }

    private List<Venda> filtrarPorMesAno(String mesAno) {
        // mesAno = "MM/yyyy", data = "dd/MM/yyyy HH:mm:ss"
        List<Venda> todas = vendaRepo.listarTodas();
        List<Venda> resultado = new java.util.ArrayList<>();
        for (Venda v : todas) {
            if (v.getData() != null && v.getData().contains("/" + mesAno.substring(0, 2) + "/" + mesAno.substring(3))) {
                resultado.add(v);
            }
        }
        // Abordagem mais simples: contém dd/MM/yyyy → pegar MM/yyyy do meio
        resultado.clear();
        String padrao = mesAno; // "MM/yyyy"
        for (Venda v : todas) {
            if (v.getData() != null) {
                // data = "17/01/2025 ..." → posição 3..9 = "01/2025"
                String dataMesAno = v.getData().length() >= 10 ? v.getData().substring(3, 10) : "";
                if (dataMesAno.equals(padrao)) resultado.add(v);
            }
        }
        return resultado;
    }

    private Map<String, Object> construirResumo(String periodo, List<Venda> vendas) {
        double totalVendas = 0, totalCusto = 0, totalLucro = 0;
        for (Venda v : vendas) {
            totalVendas += v.getTotal();
            totalCusto += v.getCusto();
            totalLucro += v.getLucro();
        }
        Map<String, Object> r = new HashMap<>();
        r.put("periodo", periodo);
        r.put("quantidadeVendas", vendas.size());
        r.put("totalVendas", totalVendas);
        r.put("totalCusto", totalCusto);
        r.put("totalLucro", totalLucro);
        r.put("vendas", vendas);
        return r;
    }
}
