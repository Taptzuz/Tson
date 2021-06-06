package me.finn.tson.annotation.all;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Finn on 19.05.2021
 * @apiNote Use @Ignore in a @SerializeAll annotated class to ignore a Field
 * from De/Serialization (this annotation is only useful for @SerializeAll classes)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Ignore {
}
