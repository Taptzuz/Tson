package me.finn.tson;

import me.finn.tson.exception.TsonDeserializeException;
import me.finn.tson.mapping.wrapper.ArrayWrapper;
import me.finn.tson.parse.JsonParse;
import me.finn.tson.util.FormatUtil;
import me.finn.tson.util.StreamUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Finn on 22.05.2021
 */
public class Tson {

    private final String json;
    private Map<String, Object> jsonMap;
    private Object jsonObject;

    public Tson(final String json) {
        this.json = json;
    }

    public Tson(String json, Object jsonObject) {
        this.json = json;
        this.jsonObject = jsonObject;

    }

    public Tson(final InputStream inputStream) throws IOException {
        this.json = StreamUtil.isToStr(inputStream);
    }

    public ArrayWrapper getArray(String key) {
        Object jsonObject = getObject(key);
        if (!(jsonObject instanceof ArrayList))
            throw new TsonDeserializeException("Failed to deserialize key: \"" + key + "\" to Array (value is not a array)");
        return new ArrayWrapper(jsonObject);
    }

    public List<?> getList(String key) {
        Object jsonObject = getObject(key);
        if (!(jsonObject instanceof ArrayList))
            throw new TsonDeserializeException("Failed to deserialize key: \"" + key + "\" to List (value is not a list)");
        return (List<?>) jsonObject;
    }

    public Map<?, ?> getMap(String key) {
        Object jsonObject = getObject(key);
        if (!(jsonObject instanceof Map))
            throw new TsonDeserializeException("Failed to deserialize key: \"" + key + "\" to Map (value is not a Map)");
        return (Map<?, ?>) jsonObject;
    }

    public Object getObject(String key) {
        return getJsonMap().get(key);
    }

    //String & Char
    public String getString(String key) {
        return (String) getJsonMap().get(key);
    }

    public char getChar(String key) {
        String string = getString(key);
        if (string.length() > 1)
            throw new TsonDeserializeException("Failed to deserialize key: \"" + key + "\" to Char (value contains more than 1 character)");
        return string.charAt(0);
    }

    //Primitives
    public boolean getBoolean(String key) {
        return (boolean) getJsonMap().get(key);
    }

    public byte getByte(String key) {
        return getNumber(key).byteValue();
    }

    public short getShort(String key) {
        return getNumber(key).shortValue();
    }

    public int getInt(String key) {
        return getNumber(key).intValue();
    }

    public long getLong(String key) {
        return getNumber(key).longValue();
    }

    public float getFloat(String key) {
        return getNumber(key).floatValue();
    }

    public double getDouble(String key) {
        return getNumber(key).doubleValue();
    }

    private Number getNumber(String key) {
        try {
            return (Number) getJsonMap().get(key);
        } catch (ClassCastException e) {
            throw new TsonDeserializeException("Failed to deserialize key: \"" + key + "\" to Number", e);
        }
    }

    public boolean hasKey(String key) {
        return getJsonMap().containsKey(key);
    }

    public boolean hasValue(String value) {
        return getJsonMap().containsValue(value);
    }

    @SuppressWarnings("unchecked") //Unchecked because Map-Casting
    public Map<String, Object> getJsonMap() {
        if (this.jsonMap == null) {
            if (this.jsonObject != null && (this.jsonObject instanceof Map)) {
                this.jsonMap = (Map<String, Object>) this.jsonObject;
            } else
                this.jsonMap = JsonParse.map(this.json);
        }
        return this.jsonMap;
    }

    public String toJsonString() {
        return toJsonString(false);
    }

    public String toJsonPrettyString() {
        return toJsonString(true);
    }

    private String toJsonString(boolean pretty) {
        return pretty ? FormatUtil.prettyPrintJSON(this.json) : this.json;
    }

    public Object getJsonObject() {
        return jsonObject;
    }

    @Override
    public String toString() {
        return toJsonString();
    }
}
