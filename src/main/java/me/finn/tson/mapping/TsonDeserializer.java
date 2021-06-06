package me.finn.tson.mapping;

import me.finn.tson.Tson;
import me.finn.tson.exception.TsonDeserializeException;
import me.finn.tson.mapping.wrapper.ArrayWrapper;
import me.finn.tson.parse.JsonParse;
import me.finn.tson.util.FieldUtil;
import me.finn.tson.util.TypeUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * @author Finn on 21.05.2021
 */
public class TsonDeserializer {

    private final Tson tson;
    
    public TsonDeserializer(final String json) {
        this.tson = new Tson(json, JsonParse.parse(json));
    }

    @SuppressWarnings("unchecked") //Because convertToObject gives us an Object
    public <T> T getAs(Class<T> clazz) {
        if (this.tson.getJsonObject() == null)
            throw new TsonDeserializeException("Failed to parse Json-String to Object");

        if (this.tson.getJsonObject() instanceof Map) {
            return this.getTsonifiedInstance(clazz, getNewClassInstance(clazz));
        } else {
            return (T) this.convertToObject(clazz, null, this.tson.getJsonObject());
        }
    }

    public <T> T getAsInstance(T instance) {
        Class<?> clazz = instance.getClass();
        return this.getTsonifiedInstance(clazz, instance);
    }

    private Object convertToObject(Class<?> clazz, Field field, Object value) {
        if (value == null)
            return null;

        if (TypeUtil.getPrimitiveTypes().contains(clazz) || TypeUtil.getObjectTypes().contains(clazz)) {
            return parseObject(clazz, value.toString());
        } else if (clazz.isArray()) {
            Class<?> componentType = clazz.getComponentType();
            //Please tell me a other way with primitive-arrays
            if (TypeUtil.getPrimitiveTypes().contains(componentType)) {
                if (clazz == char[].class) {
                    return new ArrayWrapper(value).asCharArray();
                } else if (clazz == boolean[].class) {
                    return new ArrayWrapper(value).asBooleanArray();
                } else if (clazz == byte[].class) {
                    return new ArrayWrapper(value).asByteArray();
                } else if (clazz == short[].class) {
                    return new ArrayWrapper(value).asShortArray();
                } else if (clazz == int[].class) {
                    return new ArrayWrapper(value).asIntArray();
                } else if (clazz == long[].class) {
                    return new ArrayWrapper(value).asLongArray();
                } else if (clazz == float[].class) {
                    return new ArrayWrapper(value).asFloatArray();
                } else if (clazz == double[].class) {
                    return new ArrayWrapper(value).asDoubleArray();
                }

                //Can't actually happen
                return new ArrayWrapper(value).asObjectArray();
            }

            List<?> objList = (List<?>) value;
            Object newArrayInstance = Array.newInstance(componentType, objList.size());
            for (int i = 0; i < objList.size(); i++) {
                Array.set(newArrayInstance, i, convertToObject(componentType, null, objList.get(i)));
            }
            return newArrayInstance;
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Class<?> parameterizedType = getParameterizedType(field, 0);
            if (parameterizedType == null) {
                throw new TsonDeserializeException("Failed to fetch Parameterized-Type of (List) Field \"" + field.getName() + "\"!");
            }

            List<Object> localList = new ArrayList<>();
            for (Object o : (List<?>) value) {
                localList.add(convertToObject(parameterizedType, null, o));
            }

            if (Set.class.isAssignableFrom(clazz)) {
                return new HashSet<>(localList);
            }

            return localList;
        } else if (clazz.isAssignableFrom(Map.class)) {
            Class<?> keyClass = getParameterizedType(field, 0);
            Class<?> valueClass = getParameterizedType(field, 1);
            if (keyClass == null || valueClass == null) {
                throw new TsonDeserializeException("Failed to fetch Parameterized-Type of (Map) Field \"" + field.getName() + "\"!");
            }

            Map<Object, Object> localMap = new HashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                localMap.put(convertToObject(keyClass, null, entry.getKey()), convertToObject(valueClass, null, entry.getValue()));
            }
            return localMap;
        } else {
            //Unknown Object-Class
            Object instance = getNewClassInstance(clazz);
            for (Field unknownField : FieldUtil.getFilteredFields(clazz, instance, false)) {
                String fieldName = FieldUtil.getFieldName(unknownField);
                Object unknownValue = ((Map<?, ?>) value).get(fieldName);
                Class<?> unknownFieldType = unknownField.getType();
                try {
                    unknownField.set(instance, convertToObject(unknownFieldType, unknownField, unknownValue));
                } catch (IllegalAccessException e) {
                    throw new TsonDeserializeException("Failed to set Field (\"" + fieldName + "\") in unknown-object-instance Class(\"" + clazz + "\")", e);
                }
            }
            return instance;
        }
    }

    private <T> T getTsonifiedInstance(Class<?> clazz, T instance) {
        for (Field filteredField : FieldUtil.getFilteredFields(clazz, instance, false)) {
            String fieldName = FieldUtil.getFieldName(filteredField);

            if (this.tson.getJsonMap().containsKey(fieldName)) {
                try {
                    filteredField.set(instance, convertToObject(filteredField.getType(), filteredField, this.tson.getJsonMap().get(fieldName)));
                } catch (IllegalAccessException e) {
                    throw new TsonDeserializeException("Failed to set Field-Value", e);
                }
            }
        }
        return instance;
    }

    private Object parseObject(Class<?> clazz, String value) {
        if (value == null) {
            return null;
        } else if (Character.class.equals(clazz) || char.class.equals(clazz)) {
            return value.charAt(0);
        } else if (Boolean.class.equals(clazz) || boolean.class.equals(clazz)) {
            return Boolean.parseBoolean(value);
        } else if (Byte.class.equals(clazz) || byte.class.equals(clazz)) {
            return Byte.parseByte(value);
        } else if (Short.class.equals(clazz) || short.class.equals(clazz)) {
            return Short.parseShort(value);
        } else if (Integer.class.equals(clazz) || int.class.equals(clazz)) {
            return Integer.parseInt(value);
        } else if (Long.class.equals(clazz) || long.class.equals(clazz)) {
            return Long.parseLong(value);
        } else if (Float.class.equals(clazz) || float.class.equals(clazz)) {
            return Float.parseFloat(value);
        } else if (Double.class.equals(clazz) || double.class.equals(clazz)) {
            return Double.parseDouble(value);
        }
        return value;
    }

    private Class<?> getParameterizedType(Field field, int index) {
        Object object = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[index];
        if (object instanceof Class)
            return (Class<?>) object;
        else if (object != null)
            return object.getClass();
        return null;
    }

    private <T> T getNewClassInstance(Class<T> clazz) {
        T instance;
        try {
            instance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new TsonDeserializeException("Please ensure that Class (" + clazz + ") contains a PUBLIC zero-arg Constructor", e);
        }
        return instance;
    }

    public Tson getTson() {
        return tson;
    }
}
