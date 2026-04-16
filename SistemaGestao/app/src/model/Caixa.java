package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Caixa {
    public enum Status { ABERTO, FECHADO }

    private String id;
    private double saldoInicial;
    private double totalVendas;
    private double totalEntradas;   // entradas manuais
    private double totalSaidas;     // saídas manuais
    private double saldoFinal;
    private double lucroTotal;
    private Status status;
    private String dataAbertura;
    private String dataFechamento;
    private List<MovimentacaoCaixa> movimentacoes;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static class MovimentacaoCaixa {
        public enum Tipo { VENDA, ENTRADA_MANUAL, SAIDA_MANUAL }
        private String descricao;
        private double valor;
        private Tipo tipo;
        private String data;

        public MovimentacaoCaixa() {}
        public MovimentacaoCaixa(String descricao, double valor, Tipo tipo) {
            this.descricao = descricao;
            this.valor = valor;
            this.tipo = tipo;
            this.data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }

        public String getDescricao() { return descricao; }
        public void setDescricao(String d) { this.descricao = d; }
        public double getValor() { return valor; }
        public void setValor(double v) { this.valor = v; }
        public Tipo getTipo() { return tipo; }
        public void setTipo(Tipo t) { this.tipo = t; }
        public String getData() { return data; }
        public void setData(String d) { this.data = d; }
    }

    public Caixa() {
        this.movimentacoes = new ArrayList<>();
    }

    public Caixa(String id, double saldoInicial) {
        this.id = id;
        this.saldoInicial = saldoInicial;
        this.totalVendas = 0;
        this.totalEntradas = 0;
        this.totalSaidas = 0;
        this.lucroTotal = 0;
        this.status = Status.ABERTO;
        this.dataAbertura = LocalDateTime.now().format(FMT);
        this.movimentacoes = new ArrayList<>();
    }

    public double calcularSaldoAtual() {
        return saldoInicial + totalVendas + totalEntradas - totalSaidas;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getSaldoInicial() { return saldoInicial; }
    public void setSaldoInicial(double s) { this.saldoInicial = s; }

    public double getTotalVendas() { return totalVendas; }
    public void setTotalVendas(double t) { this.totalVendas = t; }

    public double getTotalEntradas() { return totalEntradas; }
    public void setTotalEntradas(double t) { this.totalEntradas = t; }

    public double getTotalSaidas() { return totalSaidas; }
    public void setTotalSaidas(double t) { this.totalSaidas = t; }

    public double getSaldoFinal() { return saldoFinal; }
    public void setSaldoFinal(double s) { this.saldoFinal = s; }

    public double getLucroTotal() { return lucroTotal; }
    public void setLucroTotal(double l) { this.lucroTotal = l; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(String d) { this.dataAbertura = d; }

    public String getDataFechamento() { return dataFechamento; }
    public void setDataFechamento(String d) { this.dataFechamento = d; }

    public List<MovimentacaoCaixa> getMovimentacoes() { return movimentacoes; }
    public void setMovimentacoes(List<MovimentacaoCaixa> m) { this.movimentacoes = m; }
}
