package http;

import com.sun.net.httpserver.HttpExchange;
import util.JsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseHandler {

    protected void responder(HttpExchange ex, int status, Object objeto) throws IOException {
        String json = JsonUtil.toJson(objeto);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    protected void responderOk(HttpExchange ex, Object objeto) throws IOException {
        responder(ex, 200, objeto);
    }

    protected void responderErro(HttpExchange ex, int status, String mensagem) throws IOException {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", mensagem);
        responder(ex, status, erro);
    }

    protected String lerCorpo(HttpExchange ex) throws IOException {
        try (InputStream is = ex.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    protected boolean isOptions(HttpExchange ex) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
            ex.sendResponseHeaders(204, -1);
            return true;
        }
        return false;
    }

    protected String extrairPathParam(String path, String prefixo) {
        // Ex: path = "/produtos/ABC123", prefixo = "/produtos/" → retorna "ABC123"
        if (path.startsWith(prefixo) && path.length() > prefixo.length()) {
            return path.substring(prefixo.length());
        }
        return null;
    }

    protected Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) return params;
        for (String par : query.split("&")) {
            String[] kv = par.split("=", 2);
            if (kv.length == 2) params.put(kv[0], kv[1]);
        }
        return params;
    }
}
