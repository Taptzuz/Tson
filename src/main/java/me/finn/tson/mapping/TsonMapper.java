package me.finn.tson.mapping;

import me.finn.tson.Tson;

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
}
