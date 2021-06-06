package me.finn.tson.util;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Finn on 25.05.2021
 */
public class TypeUtil {

    private static final Set<Class<?>> primitiveTypes = new HashSet<>(), objectTypes = new HashSet<>();

    static {
        objectTypes.add(String.class);
        objectTypes.add(Boolean.class);
        objectTypes.add(Character.class);
        objectTypes.add(Number.class);
        objectTypes.add(Byte.class);
        objectTypes.add(Short.class);
        objectTypes.add(Integer.class);
        objectTypes.add(Long.class);
        objectTypes.add(Float.class);
        objectTypes.add(Double.class);

        primitiveTypes.add(boolean.class);
        primitiveTypes.add(char.class);
        primitiveTypes.add(byte.class);
        primitiveTypes.add(short.class);
        primitiveTypes.add(int.class);
        primitiveTypes.add(long.class);
        primitiveTypes.add(float.class);
        primitiveTypes.add(double.class);
    }

    public static Set<Class<?>> getPrimitiveTypes() {
        return primitiveTypes;
    }

    public static Set<Class<?>> getObjectTypes() {
        return objectTypes;
    }
}
