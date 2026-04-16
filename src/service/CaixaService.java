package service;

import model.Caixa;
import model.Caixa.MovimentacaoCaixa;
import model.Venda;
import repository.CaixaRepository;
import util.IdUtil;

import java.util.List;

public class CaixaService {
    private final CaixaRepository repo;

    public CaixaService(CaixaRepository repo) {
        this.repo = repo;
    }

    public Caixa abrirCaixa(double saldoInicial) {
        if (repo.buscarAberto() != null) {
            throw new IllegalStateException("Já existe um caixa aberto. Feche-o antes de abrir outro.");
        }
        Caixa c = new Caixa(IdUtil.novoId(), saldoInicial);
        repo.salvar(c);
        return c;
    }

    public Caixa fecharCaixa() {
        Caixa c = buscarAberto();
        c.setSaldoFinal(c.calcularSaldoAtual());
        c.setStatus(Caixa.Status.FECHADO);
        String agora = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date());
        c.setDataFechamento(agora);
        repo.salvar(c);
        return c;
    }

    public Caixa registrarEntradaManual(String descricao, double valor) {
        if (valor <= 0) throw new IllegalArgumentException("Valor deve ser maior que zero.");
        Caixa c = buscarAberto();
        c.setTotalEntradas(c.getTotalEntradas() + valor);
        c.getMovimentacoes().add(new MovimentacaoCaixa(descricao, valor, MovimentacaoCaixa.Tipo.ENTRADA_MANUAL));
        repo.salvar(c);
        return c;
    }

    public Caixa registrarSaidaManual(String descricao, double valor) {
        if (valor <= 0) throw new IllegalArgumentException("Valor deve ser maior que zero.");
        Caixa c = buscarAberto();
        if (c.calcularSaldoAtual() < valor) {
            throw new IllegalArgumentException("Saldo insuficiente no caixa.");
        }
        c.setTotalSaidas(c.getTotalSaidas() + valor);
        c.getMovimentacoes().add(new MovimentacaoCaixa(descricao, valor, MovimentacaoCaixa.Tipo.SAIDA_MANUAL));
        repo.salvar(c);
        return c;
    }

    // Chamado pelo ComandaService ao fechar comanda
    public void registrarVenda(Venda venda) {
        Caixa c = repo.buscarAberto();
        if (c == null) return; // caixa fechado, apenas registra a venda sem atualizar caixa
        c.setTotalVendas(c.getTotalVendas() + venda.getTotal());
        c.setLucroTotal(c.getLucroTotal() + venda.getLucro());
        c.getMovimentacoes().add(new MovimentacaoCaixa(
                "Venda: " + venda.getIdentificacaoComanda(),
                venda.getTotal(),
                MovimentacaoCaixa.Tipo.VENDA));
        repo.salvar(c);
    }

    public Caixa buscarAberto() {
        Caixa c = repo.buscarAberto();
        if (c == null) throw new IllegalStateException("Nenhum caixa aberto. Abra o caixa primeiro.");
        return c;
    }

    public Caixa buscarAbertoOuNull() {
        return repo.buscarAberto();
    }

    public List<Caixa> listarHistorico() {
        return repo.listarHistorico();
    }
}
