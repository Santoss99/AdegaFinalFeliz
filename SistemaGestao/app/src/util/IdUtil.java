package util;

import java.util.UUID;

public class IdUtil {
    public static String novoId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
