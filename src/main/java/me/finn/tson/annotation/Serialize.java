package me.finn.tson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Finn on 19.05.2021
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Serialize {

    /**
     * @return returns the de/serialization name for the current field
     * (if it is empty, it takes the original field name)
     */
    String value() default "";

    /**
     * @return returns the String (can also be a number on numeric fields) which will be skipped if
     * field is equals that (deserialization is not affected)
     * <p>
     * (Example: You want to serialize a int field, but if your int-value is 0 and you dont need a 0 value to serialize,
     * you would safe unnecessary String-Size of your JSON, so you can use this option to declare what kind of value you want to skip)
     */
    String skipIfEquals() default "";

    /**
     * @return returns if you want to skip serialization for a null object
     * like String "example" is null it will be skipped but kept for deserialization
     */
    boolean skipNull() default false;

    /**
     * @return returns if you want to skip the serialization for the current field
     * (deserialization is not affected)
     */
    boolean skip() default false;

}
