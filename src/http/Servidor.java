package http;

import com.sun.net.httpserver.HttpServer;
import service.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Servidor {
    // Em vez de: private static final int PORTA = 8080;
// Use isso para aceitar a porta que o Render mandar:
    private static final int PORTA = System.getenv("PORT") != null
            ? Integer.parseInt(System.getenv("PORT"))
            : 8080;
    private HttpServer server;

    private final ProdutoService produtoService;
    private final EstoqueService estoqueService;
    private final CaixaService caixaService;
    private final ComandaService comandaService;
    private final RelatorioService relatorioService;

    public Servidor(ProdutoService produtoService, EstoqueService estoqueService,
                    CaixaService caixaService, ComandaService comandaService,
                    RelatorioService relatorioService) {
        this.produtoService = produtoService;
        this.estoqueService = estoqueService;
        this.caixaService = caixaService;
        this.comandaService = comandaService;
        this.relatorioService = relatorioService;
    }

    public void iniciar() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORTA), 0);

        // Rotas da API
        server.createContext("/produtos", new ProdutoHandler(produtoService));
        server.createContext("/estoque", new EstoqueHandler(estoqueService));
        server.createContext("/comandas", new ComandaHandler(comandaService));
        server.createContext("/caixa", new CaixaHandler(caixaService));
        server.createContext("/relatorios", new RelatorioHandler(relatorioService));

        // Arquivos estáticos (frontend)
        server.createContext("/", new StaticHandler());

        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();

        System.out.println("====================================");
        System.out.println("  Servidor iniciado na porta " + PORTA);
        System.out.println("  Acesse: http://localhost:" + PORTA);
        System.out.println("====================================");
    }

    public void parar() {
        if (server != null) {
            server.stop(0);
            System.out.println("Servidor parado.");
        }
    }
}
