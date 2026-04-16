import http.Servidor;
import model.Pagamento;
import model.Produto;
import model.MovimentacaoEstoque;
import model.Comanda;
import model.Caixa;
import model.Venda;
import repository.*;
import service.*;
import util.CsvUtil;

import java.util.List;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);

    static Servidor servidor;

    // Repositórios
    static ProdutoRepository produtoRepo = new ProdutoRepository();
    static EstoqueRepository estoqueRepo = new EstoqueRepository();
    static ComandaRepository comandaRepo = new ComandaRepository();
    static VendaRepository vendaRepo     = new VendaRepository();
    static CaixaRepository caixaRepo     = new CaixaRepository();

    // Serviços
    static ProdutoService  produtoService  = new ProdutoService(produtoRepo);
    static EstoqueService  estoqueService  = new EstoqueService(estoqueRepo, produtoService);
    static CaixaService    caixaService    = new CaixaService(caixaRepo);
    static ComandaService  comandaService  = new ComandaService(comandaRepo, vendaRepo, produtoService, estoqueService, caixaService);
    static RelatorioService relatorioService = new RelatorioService(vendaRepo, produtoService);

    public static void main(String[] args) {

        // 👉 MODO WEB (quando rodar pelo .exe)
        if (args.length > 0 && args[0].equalsIgnoreCase("web")) {

            System.out.println("Iniciando servidor...");
            iniciarServidor();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                Runtime.getRuntime().exec(
                        "rundll32 url.dll,FileProtocolHandler http://localhost:8080"
                );
            } catch (Exception e) {
                System.out.println("Erro ao abrir navegador: " + e.getMessage());
            }

            // 🔴 IMPORTANTE: manter o programa vivo
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        // 👉 MODO TERMINAL (se não vier argumento)
        System.out.println("=================================");
        System.out.println("  SISTEMA DE GESTÃO v1.0");
        System.out.println("=================================");

        boolean rodando = true;
        while (rodando) {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1 - Produtos");
            System.out.println("2 - Movimentações de estoque");
            System.out.println("3 - Comandas");
            System.out.println("4 - Caixa");
            System.out.println("5 - Relatórios");
            System.out.println("6 - Exportar CSV");
            System.out.println("7 - Iniciar servidor web");
            System.out.println("0 - Sair");
            System.out.print("Opção: ");

            String opcao = scanner.nextLine().trim();
            switch (opcao) {
                case "1" -> menuProdutos();
                case "2" -> menuEstoque();
                case "3" -> menuComandas();
                case "4" -> menuCaixa();
                case "5" -> menuRelatorios();
                case "6" -> menuExportar();
                case "7" -> iniciarServidor();
                case "0" -> rodando = false;
                default  -> System.out.println("Opção inválida.");
            }
        }
        System.out.println("Sistema encerrado. Até logo!");
    }

    // ==================== PRODUTOS ====================
    static void menuProdutos() {
        boolean sub = true;
        while (sub) {
            System.out.println("\n--- PRODUTOS ---");
            System.out.println("1 - Listar todos");
            System.out.println("2 - Cadastrar");
            System.out.println("3 - Buscar por código");
            System.out.println("4 - Editar");
            System.out.println("5 - Remover");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");
            String op = scanner.nextLine().trim();
            switch (op) {
                case "1" -> listarProdutos();
                case "2" -> cadastrarProduto();
                case "3" -> buscarProduto();
                case "4" -> editarProduto();
                case "5" -> removerProduto();
                case "0" -> sub = false;
                default  -> System.out.println("Opção inválida.");
            }
        }
    }

    static void listarProdutos() {
        List<Produto> lista = produtoService.listarTodos();
        if (lista.isEmpty()) { System.out.println("Nenhum produto cadastrado."); return; }
        System.out.println("\n--- LISTA DE PRODUTOS ---");
        lista.forEach(System.out::println);
        List<Produto> baixo = produtoService.produtosComEstoqueBaixo();
        if (!baixo.isEmpty()) {
            System.out.println("\n⚠ ESTOQUE BAIXO:");
            baixo.forEach(p -> System.out.println("  ! " + p.getNome() + " - Qtd: " + p.getQuantidadeEstoque() + " / Mín: " + p.getEstoqueMinimo()));
        }
    }

    static void cadastrarProduto() {
        try {
            System.out.print("Nome: ");          String nome = scanner.nextLine().trim();
            System.out.print("Código: ");        String cod  = scanner.nextLine().trim();
            System.out.print("Preço compra: ");  double pc   = Double.parseDouble(scanner.nextLine().replace(",", "."));
            System.out.print("Preço venda: ");   double pv   = Double.parseDouble(scanner.nextLine().replace(",", "."));
            System.out.print("Qtd estoque: ");   int qtd     = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Estoque mínimo: "); int min    = Integer.parseInt(scanner.nextLine().trim());
            Produto p = produtoService.cadastrar(nome, cod, pc, pv, qtd, min);
            System.out.println("✓ Produto cadastrado: " + p);
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    static void buscarProduto() {
        System.out.print("Código: ");
        String cod = scanner.nextLine().trim();
        produtoService.buscarPorCodigo(cod)
                .ifPresentOrElse(System.out::println, () -> System.out.println("Produto não encontrado."));
    }

    static void editarProduto() {
        System.out.print("ID do produto: ");
        String id = scanner.nextLine().trim();
        produtoService.buscarPorId(id).ifPresentOrElse(p -> {
            try {
                System.out.println("Editando: " + p);
                System.out.print("Novo nome [" + p.getNome() + "]: ");      String nome = lerOuManter(p.getNome());
                System.out.print("Novo código [" + p.getCodigo() + "]: ");  String cod  = lerOuManter(p.getCodigo());
                System.out.print("Preço compra [" + p.getPrecoCompra() + "]: "); double pc = lerDoubleOuManter(p.getPrecoCompra());
                System.out.print("Preço venda [" + p.getPrecoVenda() + "]: ");   double pv = lerDoubleOuManter(p.getPrecoVenda());
                System.out.print("Estoque mín [" + p.getEstoqueMinimo() + "]: "); int min  = lerIntOuManter(p.getEstoqueMinimo());
                Produto editado = produtoService.editar(id, nome, cod, pc, pv, min);
                System.out.println("✓ Atualizado: " + editado);
            } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
        }, () -> System.out.println("Produto não encontrado."));
    }

    static void removerProduto() {
        System.out.print("ID do produto: ");
        String id = scanner.nextLine().trim();
        try { produtoService.remover(id); System.out.println("✓ Removido."); }
        catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    // ==================== ESTOQUE ====================
    static void menuEstoque() {
        boolean sub = true;
        while (sub) {
            System.out.println("\n--- ESTOQUE ---");
            System.out.println("1 - Histórico de movimentações");
            System.out.println("2 - Registrar entrada");
            System.out.println("3 - Registrar saída");
            System.out.println("4 - Ajuste manual");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");
            String op = scanner.nextLine().trim();
            switch (op) {
                case "1" -> estoqueService.listarHistorico().forEach(System.out::println);
                case "2" -> movimentacaoEstoque("entrada");
                case "3" -> movimentacaoEstoque("saida");
                case "4" -> ajusteEstoque();
                case "0" -> sub = false;
                default  -> System.out.println("Opção inválida.");
            }
        }
    }

    static void movimentacaoEstoque(String tipo) {
        try {
            System.out.print("ID do produto: "); String id  = scanner.nextLine().trim();
            System.out.print("Quantidade: ");    int qtd    = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Observação: ");    String obs = scanner.nextLine().trim();
            MovimentacaoEstoque m = tipo.equals("entrada")
                    ? estoqueService.registrarEntrada(id, qtd, obs)
                    : estoqueService.registrarSaida(id, qtd, obs);
            System.out.println("✓ " + m);
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    static void ajusteEstoque() {
        try {
            System.out.print("ID do produto: ");    String id  = scanner.nextLine().trim();
            System.out.print("Nova quantidade: ");  int nova   = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Observação: ");       String obs = scanner.nextLine().trim();
            MovimentacaoEstoque m = estoqueService.ajustarEstoque(id, nova, obs);
            System.out.println("✓ " + m);
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    // ==================== COMANDAS ====================
    static void menuComandas() {
        boolean sub = true;
        while (sub) {
            System.out.println("\n--- COMANDAS ---");
            System.out.println("1 - Listar abertas");
            System.out.println("2 - Criar comanda");
            System.out.println("3 - Adicionar produto");
            System.out.println("4 - Remover produto");
            System.out.println("5 - Adicionar pagamento");
            System.out.println("6 - Fechar comanda");
            System.out.println("7 - Juntar comandas");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");
            String op = scanner.nextLine().trim();
            switch (op) {
                case "1" -> comandaService.listarAbertas().forEach(System.out::println);
                case "2" -> { System.out.print("Mesa/Nome: "); try { System.out.println("✓ " + comandaService.criarComanda(scanner.nextLine())); } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); } }
                case "3" -> adicionarItemComanda();
                case "4" -> removerItemComanda();
                case "5" -> adicionarPagamentoComanda();
                case "6" -> fecharComanda();
                case "7" -> juntarComandas();
                case "0" -> sub = false;
                default  -> System.out.println("Opção inválida.");
            }
        }
    }

    static void adicionarItemComanda() {
        try {
            System.out.print("ID da comanda: ");  String cid = scanner.nextLine().trim();
            System.out.print("ID do produto: ");  String pid = scanner.nextLine().trim();
            System.out.print("Quantidade: ");     int qtd    = Integer.parseInt(scanner.nextLine().trim());
            Comanda c = comandaService.adicionarProduto(cid, pid, qtd);
            System.out.printf("✓ Total da comanda: R$%.2f%n", c.calcularTotal());
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    static void removerItemComanda() {
        try {
            System.out.print("ID da comanda: "); String cid = scanner.nextLine().trim();
            System.out.print("ID do produto: "); String pid = scanner.nextLine().trim();
            comandaService.removerProduto(cid, pid);
            System.out.println("✓ Produto removido da comanda.");
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    static void adicionarPagamentoComanda() {
        try {
            System.out.print("ID da comanda: "); String cid = scanner.nextLine().trim();
            System.out.println("Forma: 1-DINHEIRO 2-PIX 3-CARTAO");
            System.out.print("Opção: "); String f = scanner.nextLine().trim();
            Pagamento.Forma forma = switch (f) { case "1" -> Pagamento.Forma.DINHEIRO; case "2" -> Pagamento.Forma.PIX; default -> Pagamento.Forma.CARTAO; };
            System.out.print("Valor: "); double val = Double.parseDouble(scanner.nextLine().replace(",", "."));
            Comanda c = comandaService.adicionarPagamento(cid, forma, val);
            System.out.printf("✓ Pago: R$%.2f / Total: R$%.2f%n", c.calcularTotalPago(), c.calcularTotal());
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    static void fecharComanda() {
        try {
            System.out.print("ID da comanda: "); String cid = scanner.nextLine().trim();
            Venda v = comandaService.fecharComanda(cid);
            System.out.printf("✓ Comanda fechada! Venda registrada. Total: R$%.2f | Troco: R$%.2f%n",
                    v.getTotal(), 0.0);
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    static void juntarComandas() {
        try {
            System.out.print("ID da comanda de origem: ");  String ori = scanner.nextLine().trim();
            System.out.print("ID da comanda de destino: "); String dst = scanner.nextLine().trim();
            Comanda c = comandaService.juntarComandas(ori, dst);
            System.out.printf("✓ Comandas unidas. Novo total: R$%.2f%n", c.calcularTotal());
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    // ==================== CAIXA ====================
    static void menuCaixa() {
        boolean sub = true;
        while (sub) {
            System.out.println("\n--- CAIXA ---");
            System.out.println("1 - Ver caixa atual");
            System.out.println("2 - Abrir caixa");
            System.out.println("3 - Fechar caixa");
            System.out.println("4 - Entrada manual");
            System.out.println("5 - Saída manual");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");
            String op = scanner.nextLine().trim();
            switch (op) {
                case "1" -> verCaixa();
                case "2" -> abrirCaixa();
                case "3" -> fecharCaixa();
                case "4" -> movCaixa("entrada");
                case "5" -> movCaixa("saida");
                case "0" -> sub = false;
                default  -> System.out.println("Opção inválida.");
            }
        }
    }

    static void verCaixa() {
        Caixa c = caixaService.buscarAbertoOuNull();
        if (c == null) { System.out.println("Nenhum caixa aberto."); return; }
        System.out.printf("%nCaixa aberto em: %s%nSaldo inicial: R$%.2f%nTotal vendas: R$%.2f%nEntradas: R$%.2f%nSaídas: R$%.2f%nSaldo atual: R$%.2f%nLucro: R$%.2f%n",
                c.getDataAbertura(), c.getSaldoInicial(), c.getTotalVendas(),
                c.getTotalEntradas(), c.getTotalSaidas(),
                c.calcularSaldoAtual(), c.getLucroTotal());
    }

    static void abrirCaixa() {
        try {
            System.out.print("Saldo inicial: R$"); double saldo = Double.parseDouble(scanner.nextLine().replace(",", "."));
            caixaService.abrirCaixa(saldo);
            System.out.println("✓ Caixa aberto!");
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    static void fecharCaixa() {
        try {
            Caixa c = caixaService.fecharCaixa();
            System.out.printf("✓ Caixa fechado!%nTotal vendido: R$%.2f%nSaldo final: R$%.2f%nLucro: R$%.2f%n",
                    c.getTotalVendas(), c.getSaldoFinal(), c.getLucroTotal());
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    static void movCaixa(String tipo) {
        try {
            System.out.print("Descrição: "); String desc = scanner.nextLine().trim();
            System.out.print("Valor: R$");   double val  = Double.parseDouble(scanner.nextLine().replace(",", "."));
            if (tipo.equals("entrada")) caixaService.registrarEntradaManual(desc, val);
            else caixaService.registrarSaidaManual(desc, val);
            System.out.println("✓ Registrado.");
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    // ==================== RELATÓRIOS ====================
    static void menuRelatorios() {
        boolean sub = true;
        while (sub) {
            System.out.println("\n--- RELATÓRIOS ---");
            System.out.println("1 - Vendas de hoje");
            System.out.println("2 - Vendas do mês");
            System.out.println("3 - Total geral");
            System.out.println("4 - Estoque");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");
            String op = scanner.nextLine().trim();
            switch (op) {
                case "1" -> imprimirRelatorio(relatorioService.relatorioHoje());
                case "2" -> imprimirRelatorio(relatorioService.relatorioMesAtual());
                case "3" -> imprimirRelatorio(relatorioService.relatorioGeral());
                case "4" -> imprimirRelatorioEstoque(relatorioService.relatorioEstoque());
                case "0" -> sub = false;
                default  -> System.out.println("Opção inválida.");
            }
        }
    }

    @SuppressWarnings("unchecked")
    static void imprimirRelatorio(java.util.Map<String, Object> r) {
        System.out.printf("%n=== Relatório: %s ===%n", r.get("periodo"));
        System.out.printf("Quantidade de vendas: %s%n", r.get("quantidadeVendas"));
        System.out.printf("Total vendido:  R$%.2f%n", (double) r.get("totalVendas"));
        System.out.printf("Custo total:    R$%.2f%n", (double) r.get("totalCusto"));
        System.out.printf("Lucro total:    R$%.2f%n", (double) r.get("totalLucro"));
    }

    @SuppressWarnings("unchecked")
    static void imprimirRelatorioEstoque(java.util.Map<String, Object> r) {
        System.out.printf("%n=== Relatório de Estoque ===%n");
        System.out.printf("Total de produtos:     %s%n", r.get("totalProdutos"));
        System.out.printf("Produtos estoque baixo: %s%n", r.get("produtosComEstoqueBaixo"));
        System.out.printf("Valor total (venda):   R$%.2f%n", (double) r.get("valorTotalVenda"));
        System.out.printf("Custo total estoque:   R$%.2f%n", (double) r.get("custoTotalEstoque"));
        System.out.printf("Lucro estimado:        R$%.2f%n", (double) r.get("lucroEstimado"));
    }

    // ==================== EXPORTAR ====================
    static void menuExportar() {
        System.out.println("\n--- EXPORTAR CSV ---");
        System.out.println("1 - Exportar estoque (produtos)");
        System.out.println("2 - Exportar vendas");
        System.out.print("Opção: ");
        String op = scanner.nextLine().trim();
        if (op.equals("1")) {
            CsvUtil.exportarProdutos(produtoService.listarTodos(), "estoque_export.csv");
        } else if (op.equals("2")) {
            CsvUtil.exportarVendas(vendaRepo.listarTodas(), "vendas_export.csv");
        }
    }

    // ==================== SERVIDOR ====================
    static void iniciarServidor() {
        try {
            if (servidor != null) {
                System.out.println("Servidor já está rodando.");
                return;
            }

            servidor = new Servidor(
                    produtoService,
                    estoqueService,
                    caixaService,
                    comandaService,
                    relatorioService
            );

            servidor.iniciar();

            System.out.println("Servidor rodando em http://localhost:8080");

        } catch (Exception e) {
            System.out.println("Erro ao iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== HELPERS ====================
    static String lerOuManter(String atual) {
        String linha = scanner.nextLine().trim();
        return linha.isEmpty() ? atual : linha;
    }

    static double lerDoubleOuManter(double atual) {
        String linha = scanner.nextLine().trim();
        if (linha.isEmpty()) return atual;
        try { return Double.parseDouble(linha.replace(",", ".")); } catch (Exception e) { return atual; }
    }

    static int lerIntOuManter(int atual) {
        String linha = scanner.nextLine().trim();
        if (linha.isEmpty()) return atual;
        try { return Integer.parseInt(linha); } catch (Exception e) { return atual; }
    }
}
