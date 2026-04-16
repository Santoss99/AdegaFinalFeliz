package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.RelatorioService;

import java.io.IOException;

public class RelatorioHandler extends BaseHandler implements HttpHandler {
    private final RelatorioService service;

    public RelatorioHandler(RelatorioService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (isOptions(ex)) return;
        String path = ex.getRequestURI().getPath();

        try {
            if (path.equals("/relatorios/hoje")) {
                responderOk(ex, service.relatorioHoje());
            } else if (path.equals("/relatorios/mes")) {
                responderOk(ex, service.relatorioMesAtual());
            } else if (path.equals("/relatorios/geral")) {
                responderOk(ex, service.relatorioGeral());
            } else if (path.equals("/relatorios/estoque")) {
                responderOk(ex, service.relatorioEstoque());
            } else {
                responderErro(ex, 404, "Relatório não encontrado.");
            }
        } catch (Exception e) {
            responderErro(ex, 500, "Erro interno: " + e.getMessage());
        }
    }
}
