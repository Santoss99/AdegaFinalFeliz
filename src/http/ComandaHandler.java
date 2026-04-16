package http;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Pagamento;
import service.ComandaService;
import util.JsonUtil;

import java.io.IOException;

public class ComandaHandler extends BaseHandler implements HttpHandler {
    private final ComandaService service;

    public ComandaHandler(ComandaService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (isOptions(ex)) return;
        String method = ex.getRequestMethod();
        String path = ex.getRequestURI().getPath();

        try {
            // GET /comandas
            if ("GET".equals(method) && path.equals("/comandas")) {
                String query = ex.getRequestURI().getQuery();
                if ("abertas".equals(query)) {
                    responderOk(ex, service.listarAbertas());
                } else {
                    responderOk(ex, service.listarTodas());
                }
            }
            // GET /comandas/{id}
            else if ("GET".equals(method) && path.startsWith("/comandas/")) {
                String[] partes = path.split("/");

                if (partes.length == 3) { // /comandas/{id}
                    String id = partes[2];
                    responderOk(ex, service.buscarPorId(id));
                } else {
                    responderErro(ex, 400, "ID inválido");
                }
            }
            // POST /comandas — criar
            else if ("POST".equals(method) && path.equals("/comandas")) {
                JsonObject j = JsonUtil.parseJson(lerCorpo(ex));
                var comanda = service.criarComanda(j.get("identificacao").getAsString());
                responder(ex, 201, comanda);
            }
            // POST /comandas/adicionar-produto
            else if ("POST".equals(method) && path.equals("/comandas/adicionar-produto")) {
                JsonObject j = JsonUtil.parseJson(lerCorpo(ex));
                var comanda = service.adicionarProduto(
                        j.get("comandaId").getAsString(),
                        j.get("produtoId").getAsString(),
                        j.get("quantidade").getAsInt()
                );
                responderOk(ex, comanda);
            }
            // POST /comandas/remover-produto
            else if ("POST".equals(method) && path.equals("/comandas/remover-produto")) {
                JsonObject j = JsonUtil.parseJson(lerCorpo(ex));
                var comanda = service.removerProduto(
                        j.get("comandaId").getAsString(),
                        j.get("produtoId").getAsString()
                );
                responderOk(ex, comanda);
            }
            // POST /comandas/pagamento
            else if ("POST".equals(method) && path.equals("/comandas/pagamento")) {
                JsonObject j = JsonUtil.parseJson(lerCorpo(ex));
                Pagamento.Forma forma = Pagamento.Forma.valueOf(j.get("forma").getAsString().toUpperCase());
                var comanda = service.adicionarPagamento(
                        j.get("comandaId").getAsString(),
                        forma,
                        j.get("valor").getAsDouble()
                );
                responderOk(ex, comanda);
            }
            // POST /comandas/limpar-pagamentos
            else if ("POST".equals(method) && path.equals("/comandas/limpar-pagamentos")) {
                JsonObject j = JsonUtil.parseJson(lerCorpo(ex));
                var comanda = service.removerPagamentos(j.get("comandaId").getAsString());
                responderOk(ex, comanda);
            }
            // POST /comandas/fechar
            else if ("POST".equals(method) && path.equals("/comandas/fechar")) {
                JsonObject j = JsonUtil.parseJson(lerCorpo(ex));
                var venda = service.fecharComanda(j.get("comandaId").getAsString());
                responderOk(ex, venda);
            }
            // POST /comandas/juntar
            else if ("POST".equals(method) && path.equals("/comandas/juntar")) {
                JsonObject j = JsonUtil.parseJson(lerCorpo(ex));
                var comanda = service.juntarComandas(
                        j.get("origemId").getAsString(),
                        j.get("destinoId").getAsString()
                );
                responderOk(ex, comanda);
            }
            else {
                responderErro(ex, 404, "Endpoint não encontrado.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            responderErro(ex, 400, e.getMessage());
        } catch (Exception e) {
            responderErro(ex, 500, "Erro interno: " + e.getMessage());
        }
    }
}
