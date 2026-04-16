package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MovimentacaoEstoque {
    public enum Tipo { ENTRADA, SAIDA, AJUSTE }

    private String id;
    private String produtoId;
    private String nomeProduto;
    private Tipo tipo;
    private int quantidade;
    private int quantidadeAnterior;
    private int quantidadeNova;
    private String data;
    private String observacao;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public MovimentacaoEstoque() {}

    public MovimentacaoEstoque(String id, String produtoId, String nomeProduto,
                                Tipo tipo, int quantidade,
                                int quantidadeAnterior, int quantidadeNova,
                                String observacao) {
        this.id = id;
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.quantidadeAnterior = quantidadeAnterior;
        this.quantidadeNova = quantidadeNova;
        this.data = LocalDateTime.now().format(FMT);
        this.observacao = observacao;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProdutoId() { return produtoId; }
    public void setProdutoId(String produtoId) { this.produtoId = produtoId; }

    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public int getQuantidadeAnterior() { return quantidadeAnterior; }
    public void setQuantidadeAnterior(int q) { this.quantidadeAnterior = q; }

    public int getQuantidadeNova() { return quantidadeNova; }
    public void setQuantidadeNova(int q) { this.quantidadeNova = q; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | Qtd: %d | %d -> %d | %s",
                data, nomeProduto, tipo, quantidade, quantidadeAnterior, quantidadeNova,
                observacao != null ? observacao : "");
    }
}
