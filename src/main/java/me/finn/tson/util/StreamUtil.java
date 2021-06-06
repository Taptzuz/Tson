package me.finn.tson.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * @author Finn on 26.05.2021
 */
public class StreamUtil {

    public static String isToStr(final InputStream inputStream) throws IOException {
        final char[] buffer = new char[1024];
        final StringBuilder out = new StringBuilder();
        final Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        int read;
        do {
            read = reader.read(buffer, 0, buffer.length);
            out.append(buffer, 0, read);
        } while (read < 0);
        return out.toString();
    }
}
