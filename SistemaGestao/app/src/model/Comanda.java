package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Comanda {
    public enum Status { ABERTA, FECHADA }

    private String id;
    private String identificacao;  // nome ou número da mesa
    private List<ItemComanda> itens;
    private Status status;
    private String dataAbertura;
    private String dataFechamento;
    private List<Pagamento> pagamentos;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Comanda() {
        this.itens = new ArrayList<>();
        this.pagamentos = new ArrayList<>();
    }

    public Comanda(String id, String identificacao) {
        this.id = id;
        this.identificacao = identificacao;
        this.itens = new ArrayList<>();
        this.pagamentos = new ArrayList<>();
        this.status = Status.ABERTA;
        this.dataAbertura = LocalDateTime.now().format(FMT);
    }

    public double calcularTotal() {
        if (itens == null) return 0;
        return itens.stream().mapToDouble(ItemComanda::getSubtotal).sum();
    }

    public double calcularTotalPago() {
        if (pagamentos == null) return 0;
        return pagamentos.stream().mapToDouble(Pagamento::getValor).sum();
    }

    public double calcularTroco() {
        double pago = calcularTotalPago();
        double total = calcularTotal();
        return pago > total ? pago - total : 0;
    }

    public void fechar() {
        this.status = Status.FECHADA;
        this.dataFechamento = LocalDateTime.now().format(FMT);
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdentificacao() { return identificacao; }
    public void setIdentificacao(String identificacao) { this.identificacao = identificacao; }

    public List<ItemComanda> getItens() { return itens; }
    public void setItens(List<ItemComanda> itens) { this.itens = itens; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(String dataAbertura) { this.dataAbertura = dataAbertura; }

    public String getDataFechamento() { return dataFechamento; }
    public void setDataFechamento(String dataFechamento) { this.dataFechamento = dataFechamento; }

    public List<Pagamento> getPagamentos() { return pagamentos; }
    public void setPagamentos(List<Pagamento> pagamentos) { this.pagamentos = pagamentos; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Status: %s | Total: R$%.2f | Itens: %d",
                id, identificacao, status, calcularTotal(), itens != null ? itens.size() : 0);
    }
}
