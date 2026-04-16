package util;

import model.Produto;
import model.Venda;
import model.Pagamento;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CsvUtil {

    public static void exportarProdutos(List<Produto> produtos, String nomeArquivo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(nomeArquivo, StandardCharsets.UTF_8))) {
            pw.println("ID;Nome;Codigo;Preco Compra;Preco Venda;Qtd Estoque;Estoque Minimo;Estoque Baixo");
            for (Produto p : produtos) {
                pw.printf("%s;%s;%s;%.2f;%.2f;%d;%d;%s%n",
                        p.getId(), p.getNome(), p.getCodigo(),
                        p.getPrecoCompra(), p.getPrecoVenda(),
                        p.getQuantidadeEstoque(), p.getEstoqueMinimo(),
                        p.estoqueBaixo() ? "SIM" : "NAO");
            }
            System.out.println("Exportado: " + nomeArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao exportar produtos: " + e.getMessage());
        }
    }

    public static void exportarVendas(List<Venda> vendas, String nomeArquivo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(nomeArquivo, StandardCharsets.UTF_8))) {
            pw.println("ID;Comanda;Data;Total;Custo;Lucro;Pagamentos");
            for (Venda v : vendas) {
                StringBuilder pags = new StringBuilder();
                if (v.getPagamentos() != null) {
                    for (Pagamento p : v.getPagamentos()) {
                        if (pags.length() > 0) pags.append("|");
                        pags.append(p.getForma()).append(":R$").append(String.format("%.2f", p.getValor()));
                    }
                }
                pw.printf("%s;%s;%s;%.2f;%.2f;%.2f;%s%n",
                        v.getId(), v.getIdentificacaoComanda(), v.getData(),
                        v.getTotal(), v.getCusto(), v.getLucro(),
                        pags);
            }
            System.out.println("Exportado: " + nomeArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao exportar vendas: " + e.getMessage());
        }
    }
}
