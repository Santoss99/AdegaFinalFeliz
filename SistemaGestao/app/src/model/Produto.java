package model;

public class Produto {
    private String id;
    private String nome;
    private String codigo;
    private double precoCompra;
    private double precoVenda;
    private int quantidadeEstoque;
    private int estoqueMinimo;

    public Produto() {}

    public Produto(String id, String nome, String codigo, double precoCompra,
                   double precoVenda, int quantidadeEstoque, int estoqueMinimo) {
        this.id = id;
        this.nome = nome;
        this.codigo = codigo;
        this.precoCompra = precoCompra;
        this.precoVenda = precoVenda;
        this.quantidadeEstoque = quantidadeEstoque;
        this.estoqueMinimo = estoqueMinimo;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public double getPrecoCompra() { return precoCompra; }
    public void setPrecoCompra(double precoCompra) { this.precoCompra = precoCompra; }

    public double getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(double precoVenda) { this.precoVenda = precoVenda; }

    public int getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(int quantidadeEstoque) { this.quantidadeEstoque = quantidadeEstoque; }

    public int getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(int estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }

    public boolean estoqueBaixo() {
        return quantidadeEstoque <= estoqueMinimo;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - Cód: %s | Estoque: %d | Venda: R$%.2f",
                id, nome, codigo, quantidadeEstoque, precoVenda);
    }
}
