package me.finn.tson.mapping;

import me.finn.tson.Tson;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Finn on 22.05.2021
 */
public class TsonMapper {

    public static Tson getSerializer(Object object) {
        return new TsonSerializer(object).getTson();
    }

    public static TsonDeserializer getDeserializer(String json) {
        return new TsonDeserializer(json);
    }

    public static TsonDeserializer getDeserializer(Tson tson) {
        return getDeserializer(tson.toJsonString());
    }

    public static TsonDeserializer getDeserializer(InputStream inputStream) throws IOException {
        return getDeserializer(new Tson(inputStream).toJsonString());
    }
}
