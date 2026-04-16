package http;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.CaixaService;
import util.JsonUtil;

import java.io.IOException;
import java.util.Map;

public class CaixaHandler extends BaseHandler implements HttpHandler {
    private final CaixaService service;

    public CaixaHandler(CaixaService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (isOptions(ex)) return;
        String method = ex.getRequestMethod();
        String path = ex.getRequestURI().getPath();

        try {
            if ("GET".equals(method) && path.equals("/caixa")) {
                var caixa = service.buscarAbertoOuNull();
                if (caixa == null) {
                    responderOk(ex, Map.of("status", "FECHADO", "mensagem", "Nenhum caixa aberto."));
                } else {
                    responderOk(ex, caixa);
                }
            } else if ("GET".equals(method) && path.equals("/caixa/historico")) {
                responderOk(ex, service.listarHistorico());
            } else if ("POST".equals(method) && path.equals("/caixa/abrir")) {
                JsonObject j = JsonUtil.parseJson(lerCorpo(ex));
                double saldo = j.has("saldoInicial") ? j.get("saldoInicial").getAsDouble() : 0.0;
                var caixa = service.abrirCaixa(saldo);
                responder(ex, 201, caixa);
            } else if ("POST".equals(method) && path.equals("/caixa/fechar")) {
                var caixa = service.fecharCaixa();
                responderOk(ex, caixa);
            } else if ("POST".equals(method) && path.equals("/caixa/entrada")) {
                JsonObject j = JsonUtil.parseJson(lerCorpo(ex));
                var caixa = service.registrarEntradaManual(
                        j.get("descricao").getAsString(),
                        j.get("valor").getAsDouble()
                );
                responderOk(ex, caixa);
            } else if ("POST".equals(method) && path.equals("/caixa/saida")) {
                JsonObject j = JsonUtil.parseJson(lerCorpo(ex));
                var caixa = service.registrarSaidaManual(
                        j.get("descricao").getAsString(),
                        j.get("valor").getAsDouble()
                );
                responderOk(ex, caixa);
            } else {
                responderErro(ex, 404, "Endpoint não encontrado.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            responderErro(ex, 400, e.getMessage());
        } catch (Exception e) {
            responderErro(ex, 500, "Erro interno: " + e.getMessage());
        }
    }
}
