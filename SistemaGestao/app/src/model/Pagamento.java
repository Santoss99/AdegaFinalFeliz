package model;

public class Pagamento {
    public enum Forma { DINHEIRO, PIX, CARTAO }

    private Forma forma;
    private double valor;

    public Pagamento() {}

    public Pagamento(Forma forma, double valor) {
        this.forma = forma;
        this.valor = valor;
    }

    // Getters e Setters
    public Forma getForma() { return forma; }
    public void setForma(Forma forma) { this.forma = forma; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    @Override
    public String toString() {
        return String.format("%s: R$%.2f", forma, valor);
    }
}
