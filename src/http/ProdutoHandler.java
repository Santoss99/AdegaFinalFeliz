package http;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Produto;
import service.ProdutoService;
import util.JsonUtil;

import java.io.IOException;
import java.util.List;

public class ProdutoHandler extends BaseHandler implements HttpHandler {
    private final ProdutoService service;

    public ProdutoHandler(ProdutoService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (isOptions(ex)) return;
        String method = ex.getRequestMethod();
        String path = ex.getRequestURI().getPath();

        try {
            switch (method) {
                case "GET"    -> handleGet(ex, path);
                case "POST"   -> handlePost(ex);
                case "PUT"    -> handlePut(ex, path);
                case "DELETE" -> handleDelete(ex, path);
                default       -> responderErro(ex, 405, "Método não permitido.");
            }
        } catch (IllegalArgumentException e) {
            responderErro(ex, 400, e.getMessage());
        } catch (Exception e) {
            responderErro(ex, 500, "Erro interno: " + e.getMessage());
        }
    }

    private void handleGet(HttpExchange ex, String path) throws IOException {
        String query = ex.getRequestURI().getQuery();
        if (query != null && query.startsWith("codigo=")) {
            String codigo = query.substring(7);
            service.buscarPorCodigo(codigo)
                    .ifPresentOrElse(
                            p -> { try { responderOk(ex, p); } catch (IOException e) { throw new RuntimeException(e); } },
                            () -> { try { responderErro(ex, 404, "Produto não encontrado."); } catch (IOException e) { throw new RuntimeException(e); } }
                    );
            return;
        }
        if (query != null && query.startsWith("nome=")) {
            String nome = query.substring(5);
            List<Produto> lista = service.buscarPorNome(nome);
            responderOk(ex, lista);
            return;
        }
        if (query != null && query.equals("estoque-baixo")) {
            responderOk(ex, service.produtosComEstoqueBaixo());
            return;
        }
        // GET /produtos/{id}
        String id = extrairPathParam(path, "/produtos/");
        if (id != null) {
            service.buscarPorId(id)
                    .ifPresentOrElse(
                            p -> { try { responderOk(ex, p); } catch (IOException e) { throw new RuntimeException(e); } },
                            () -> { try { responderErro(ex, 404, "Produto não encontrado."); } catch (IOException e) { throw new RuntimeException(e); } }
                    );
            return;
        }
        // GET /produtos
        responderOk(ex, service.listarTodos());
    }

    private void handlePost(HttpExchange ex) throws IOException {
        String corpo = lerCorpo(ex);
        JsonObject j = JsonUtil.parseJson(corpo);
        Produto p = service.cadastrar(
                j.get("nome").getAsString(),
                j.get("codigo").getAsString(),
                j.get("precoCompra").getAsDouble(),
                j.get("precoVenda").getAsDouble(),
                j.has("quantidadeEstoque") ? j.get("quantidadeEstoque").getAsInt() : 0,
                j.has("estoqueMinimo") ? j.get("estoqueMinimo").getAsInt() : 0
        );
        responder(ex, 201, p);
    }

    private void handlePut(HttpExchange ex, String path) throws IOException {
        String id = extrairPathParam(path, "/produtos/");
        if (id == null) { responderErro(ex, 400, "ID obrigatório."); return; }
        String corpo = lerCorpo(ex);
        JsonObject j = JsonUtil.parseJson(corpo);
        Produto p = service.editar(
                id,
                j.get("nome").getAsString(),
                j.get("codigo").getAsString(),
                j.get("precoCompra").getAsDouble(),
                j.get("precoVenda").getAsDouble(),
                j.has("estoqueMinimo") ? j.get("estoqueMinimo").getAsInt() : 0
        );
        responderOk(ex, p);
    }

    private void handleDelete(HttpExchange ex, String path) throws IOException {
        String id = extrairPathParam(path, "/produtos/");
        if (id == null) { responderErro(ex, 400, "ID obrigatório."); return; }
        service.remover(id);
        responderOk(ex, java.util.Map.of("mensagem", "Produto removido com sucesso."));
    }
}
