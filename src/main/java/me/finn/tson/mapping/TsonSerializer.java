package me.finn.tson.mapping;

import me.finn.tson.Tson;
import me.finn.tson.exception.TsonSerializeException;
import me.finn.tson.util.FieldUtil;
import me.finn.tson.util.FormatUtil;
import me.finn.tson.util.TypeUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Finn on 20.05.2021
 */
public class TsonSerializer {

    private final Tson tson;

    public TsonSerializer(Object object) {
        this.tson = new Tson(serializeString(object));
    }

    private String serializeString(Object obj) throws TsonSerializeException {
        StringBuilder jsonWriter = new StringBuilder();
        jsonWriter.append('{');

        final Class<?> clazz = obj.getClass();

        if (TypeUtil.getPrimitiveTypes().contains(clazz) || TypeUtil.getObjectTypes().contains(clazz)) {
            return getValue(obj);
        } else if (clazz.isArray()) {
            return parseArray(obj);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            return parseCollection(obj);
        } else if (clazz.isAssignableFrom(Map.class)) {
            return parseMapValues(obj);
        }

        List<Field> fields = FieldUtil.getFilteredFields(clazz, obj, true);

        for (Field field : fields) {
            Object value;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException e) {
                throw new TsonSerializeException("Failed to get Field-Object", e);
            }
            jsonWriter.append(FormatUtil.quote(FieldUtil.getFieldName(field)));
            jsonWriter.append(':');
            jsonWriter.append(getValue(value));
            if ((fields.indexOf(field) + 1) < fields.size()) {
                jsonWriter.append(',');
            }
        }

        jsonWriter.append('}');
        return jsonWriter.toString();
    }

    private String getValue(Object o) {
        if (o == null) {
            return "null";
        } else if (o instanceof String || o instanceof Character) {
            return FormatUtil.quote(o.toString());
        } else if (o instanceof Number || o instanceof Boolean) {
            return o.toString();
        } else if (o instanceof Collection) {
            return parseCollection(o);
        } else if (o instanceof Map) {
            return parseMapValues(o);
        } else if (o.getClass().isArray()) {
            return parseArray(o);
        }
        return serializeString(o);
    }

    private String parseArray(Object obj) {
        StringBuilder jsonWriter = new StringBuilder();
        jsonWriter.append('[');
        /*
          I don't know if it would improve the performance, to check if the Object is
          primitive and make a separate method for each primitive data type (array)
          which casts it for this, but the current method is much cleaner
         */
        int length = Array.getLength(obj);
        Object[] objects = new Object[length];
        for (int i = 0; i < length; i++) {
            objects[i] = Array.get(obj, i);
            jsonWriter.append(getValue(objects[i]));
            if ((i + 1) < length)
                jsonWriter.append(',');
        }
        jsonWriter.append(']');
        return jsonWriter.toString();
    }

    private String parseCollection(Object o) {
        return parseArray(((Collection<?>) o).toArray());
    }

    private String parseMapValues(Object o) {
        StringBuilder jsonWriter = new StringBuilder();
        jsonWriter.append('{');
        Iterator<?> entryIterator = ((Map<?, ?>) o).entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<?, ?> e = ((Map.Entry<?, ?>) entryIterator.next());
            jsonWriter.append(FormatUtil.quote(e.getKey().toString())).append(':').append(getValue(e.getValue()));
            if (entryIterator.hasNext())
                jsonWriter.append(',');
        }
        jsonWriter.append('}');
        return jsonWriter.toString();
    }

    public Tson getTson() {
        return tson;
    }
}
