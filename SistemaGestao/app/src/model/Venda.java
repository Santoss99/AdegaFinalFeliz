package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Venda {
    private String id;
    private String comandaId;
    private String identificacaoComanda;
    private double total;
    private double custo;
    private double lucro;
    private String data;
    private List<Pagamento> pagamentos;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Venda() {}

    public Venda(String id, String comandaId, String identificacaoComanda,
                 double total, double custo, List<Pagamento> pagamentos) {
        this.id = id;
        this.comandaId = comandaId;
        this.identificacaoComanda = identificacaoComanda;
        this.total = total;
        this.custo = custo;
        this.lucro = total - custo;
        this.pagamentos = pagamentos;
        this.data = LocalDateTime.now().format(FMT);
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getComandaId() { return comandaId; }
    public void setComandaId(String comandaId) { this.comandaId = comandaId; }

    public String getIdentificacaoComanda() { return identificacaoComanda; }
    public void setIdentificacaoComanda(String v) { this.identificacaoComanda = v; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public double getCusto() { return custo; }
    public void setCusto(double custo) { this.custo = custo; }

    public double getLucro() { return lucro; }
    public void setLucro(double lucro) { this.lucro = lucro; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public List<Pagamento> getPagamentos() { return pagamentos; }
    public void setPagamentos(List<Pagamento> pagamentos) { this.pagamentos = pagamentos; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Total: R$%.2f | Lucro: R$%.2f",
                data, identificacaoComanda, total, lucro);
    }
}
