package util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final String PASTA_DADOS = "dados";

    public static <T> List<T> lerLista(String nomeArquivo, Class<T> tipo) {
        File arquivo = new File(PASTA_DADOS, nomeArquivo);
        if (!arquivo.exists()) {
            return new ArrayList<>();
        }
        try (Reader reader = new FileReader(arquivo, StandardCharsets.UTF_8)) {
            Type listType = TypeToken.getParameterized(List.class, tipo).getType();
            List<T> lista = GSON.fromJson(reader, listType);
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Erro ao ler " + nomeArquivo + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static <T> void salvarLista(String nomeArquivo, List<T> lista) {
        File pasta = new File(PASTA_DADOS);
        if (!pasta.exists()) pasta.mkdirs();
        File arquivo = new File(pasta, nomeArquivo);
        try (Writer writer = new FileWriter(arquivo, StandardCharsets.UTF_8)) {
            GSON.toJson(lista, writer);
        } catch (Exception e) {
            System.err.println("Erro ao salvar " + nomeArquivo + ": " + e.getMessage());
        }
    }

    public static <T> T lerObjeto(String nomeArquivo, Class<T> tipo) {
        File arquivo = new File(PASTA_DADOS, nomeArquivo);
        if (!arquivo.exists()) return null;
        try (Reader reader = new FileReader(arquivo, StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, tipo);
        } catch (Exception e) {
            System.err.println("Erro ao ler " + nomeArquivo + ": " + e.getMessage());
            return null;
        }
    }

    public static <T> void salvarObjeto(String nomeArquivo, T objeto) {
        File pasta = new File(PASTA_DADOS);
        if (!pasta.exists()) pasta.mkdirs();
        File arquivo = new File(pasta, nomeArquivo);
        try (Writer writer = new FileWriter(arquivo, StandardCharsets.UTF_8)) {
            GSON.toJson(objeto, writer);
        } catch (Exception e) {
            System.err.println("Erro ao salvar " + nomeArquivo + ": " + e.getMessage());
        }
    }

    public static String toJson(Object objeto) {
        return GSON.toJson(objeto);
    }

    public static <T> T fromJson(String json, Class<T> tipo) {
        return GSON.fromJson(json, tipo);
    }

    public static JsonObject parseJson(String json) {
        return JsonParser.parseString(json).getAsJsonObject();
    }
}
