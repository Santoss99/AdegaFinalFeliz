package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class StaticHandler implements HttpHandler {

    private static final Map<String, String> MIME = Map.of(
            "html", "text/html; charset=UTF-8",
            "css",  "text/css",
            "js",   "application/javascript",
            "json", "application/json",
            "png",  "image/png",
            "ico",  "image/x-icon"
    );

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        if (path.equals("/") || path.equals("")) path = "/index.html";

        // Remove leading slash and resolve inside web folder
        String relative = path.startsWith("/") ? path.substring(1) : path;
        Path file = Path.of("web", relative);

        if (!Files.exists(file) || Files.isDirectory(file)) {
            // Fallback to index.html for SPA-style navigation
            file = Path.of("web", "index.html");
        }

        if (!Files.exists(file)) {
            byte[] msg = "404 Not Found".getBytes();
            ex.sendResponseHeaders(404, msg.length);
            ex.getResponseBody().write(msg);
            ex.getResponseBody().close();
            return;
        }

        String ext = "";
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        if (dot >= 0) ext = name.substring(dot + 1);

        String contentType = MIME.getOrDefault(ext, "application/octet-stream");
        byte[] bytes = Files.readAllBytes(file);

        ex.getResponseHeaders().set("Content-Type", contentType);
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}
