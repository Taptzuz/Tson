package me.finn.tson.annotation.all;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Finn on 19.05.2021
 * @apiNote If you annotate a class with @SerializeAll, you will serialize ALL fields in the class
 * (functions of the @Serialize annotation are retained)
 * if you want to ignore a field in a @SerializeAll annotated class you can use the @Ignore annotation on a Field
 * (transient fields and skip-options from @Serialize will be also ignored)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SerializeAll {
}
