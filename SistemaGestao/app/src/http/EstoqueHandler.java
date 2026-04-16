package http;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.EstoqueService;
import util.JsonUtil;

import java.io.IOException;

public class EstoqueHandler extends BaseHandler implements HttpHandler {
    private final EstoqueService service;

    public EstoqueHandler(EstoqueService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (isOptions(ex)) return;
        String method = ex.getRequestMethod();
        String path = ex.getRequestURI().getPath();

        try {
            if ("GET".equals(method) && path.equals("/estoque")) {
                responderOk(ex, service.listarHistorico());
            } else if ("GET".equals(method) && path.startsWith("/estoque/produto/")) {
                String produtoId = extrairPathParam(path, "/estoque/produto/");
                responderOk(ex, service.listarPorProduto(produtoId));
            } else if ("POST".equals(method) && path.equals("/estoque/entrada")) {
                String corpo = lerCorpo(ex);
                JsonObject j = JsonUtil.parseJson(corpo);
                var mov = service.registrarEntrada(
                        j.get("produtoId").getAsString(),
                        j.get("quantidade").getAsInt(),
                        j.has("observacao") ? j.get("observacao").getAsString() : ""
                );
                responder(ex, 201, mov);
            } else if ("POST".equals(method) && path.equals("/estoque/saida")) {
                String corpo = lerCorpo(ex);
                JsonObject j = JsonUtil.parseJson(corpo);
                var mov = service.registrarSaida(
                        j.get("produtoId").getAsString(),
                        j.get("quantidade").getAsInt(),
                        j.has("observacao") ? j.get("observacao").getAsString() : ""
                );
                responder(ex, 201, mov);
            } else if ("POST".equals(method) && path.equals("/estoque/ajuste")) {
                String corpo = lerCorpo(ex);
                JsonObject j = JsonUtil.parseJson(corpo);
                var mov = service.ajustarEstoque(
                        j.get("produtoId").getAsString(),
                        j.get("novaQuantidade").getAsInt(),
                        j.has("observacao") ? j.get("observacao").getAsString() : ""
                );
                responder(ex, 201, mov);
            } else {
                responderErro(ex, 404, "Endpoint não encontrado.");
            }
        } catch (IllegalArgumentException e) {
            responderErro(ex, 400, e.getMessage());
        } catch (Exception e) {
            responderErro(ex, 500, "Erro interno: " + e.getMessage());
        }
    }
}
