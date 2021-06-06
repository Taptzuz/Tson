package me.finn.tson.util;

import me.finn.tson.annotation.Serialize;
import me.finn.tson.annotation.all.Ignore;
import me.finn.tson.annotation.all.SerializeAll;
import me.finn.tson.exception.TsonDeserializeException;
import me.finn.tson.exception.TsonSerializeException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Finn on 20.05.2021
 */
public class FieldUtil {

    public static boolean isFieldAllowed(Class<?> clazz, Field field) {
        //Skip field if transient or final
        if (Modifier.isTransient(field.getModifiers())) {
            return false;
        }

        boolean serializeAllPresent = clazz.isAnnotationPresent(SerializeAll.class);
        boolean serializePresent = field.isAnnotationPresent(Serialize.class);

        /*
         If @SerializeAll is not present on current class, we will check
         if field is annotated by @Serialize - if not skip
        */
        if (!serializeAllPresent && !serializePresent) return false;

        if (serializeAllPresent && field.isAnnotationPresent(Ignore.class)) {
            /*
              If @SerializeAll is present on current Class, we check whether the current field is annotated
              with @Ignore, if so skip
             */
            return false;
        }

        return true;
    }

    public static boolean isSerializationAllowed(Class<?> clazz, Object instance, Field field) {
        // If @Serialize is present on field
        if (field.isAnnotationPresent(Serialize.class)) {
            // If we do a deserialization this block is unnecessary
            Serialize serializeAnnotation = field.getAnnotation(Serialize.class);

            // If skip is true, we will skip the field.
            if (serializeAnnotation.skip()) return false;

            //Check if skipNull is true or skipIfEquals is not empty to get the field object
            if (serializeAnnotation.skipNull() || !serializeAnnotation.skipIfEquals().isEmpty()) {
                try {
                    Object fieldObject = field.get(instance);

                    // If skipNull is true and field-object is null we will skip the field
                    if (serializeAnnotation.skipNull() && fieldObject == null) return false;

                    if (!serializeAnnotation.skipIfEquals().isEmpty()) {
                        String skipString = serializeAnnotation.skipIfEquals();

                        // Check whether our object is numeric, and if it should be, whether our skip-string can also be formatted numerically, if not we throw an exception
                        if (fieldObject instanceof Number) {
                            if (!isNumeric(skipString))
                                throw new TsonSerializeException("Failed to Skip-String, Field (Name:" + field.getName() + ", Value:" + fieldObject + " in Class:" + clazz.getName() + ") is numeric but skip-string (\"" + skipString + "\") not");
                        } else if (!(fieldObject instanceof String)) {
                            String msg = (fieldObject == null) ? "is a null-object, if you want to skip null objects (too) use skipNull" : "is neither a String nor a Numeric Object, other objects are not supported";

                            throw new TsonSerializeException("Failed to Skip-String, Field (Name:" + field.getName() + " in Class:" + clazz.getName() + ") " + msg);
                        }

                        // If our Field-Object equals the Skip-String we will skip the Field
                        if (fieldObject.toString().equals(skipString)) return false;
                    }
                } catch (TsonSerializeException | IllegalAccessException e) {
                    throw new TsonDeserializeException("Failed to fetch value of Field: \"" + field.getName() + "\"", e);
                }
            }
        }
        return true;
    }

    public static List<Field> getFilteredFields(Class<?> clazz, Object instance, boolean serialize) {
        List<Field> localFieldList = new ArrayList<>();
        Class<?> current = clazz;

        while (current.getSuperclass() != null && !TypeUtil.getPrimitiveTypes().contains(current) && !TypeUtil.getObjectTypes().contains(current)) {
            for (Field field : current.getDeclaredFields()) {
                field.setAccessible(true);

                // Check if field is allowed to be accessed
                if (!isFieldAllowed(clazz, field)) continue;
                // Check if we want fields for serialization (isSerializationAllowed implements skip checks - useless for deserialization)
                if (serialize)
                    if (!isSerializationAllowed(clazz, instance, field)) continue;
                localFieldList.add(field);
            }
            //getDeclaredFields ignores Superclasses so we need to do this
            current = current.getSuperclass();
        }
        return localFieldList;
    }

    /**
     * Checks if @Serialize is/not present on field or value is empty
     * if so then fetch normal field-name
     */
    public static String getFieldName(Field field) {
        String fieldName = field.getName();
        if (field.isAnnotationPresent(Serialize.class)) {
            String serializeValue = field.getAnnotation(Serialize.class).value();
            if (!serializeValue.isEmpty()) fieldName = serializeValue;
        }
        return fieldName;
    }

    private static boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
