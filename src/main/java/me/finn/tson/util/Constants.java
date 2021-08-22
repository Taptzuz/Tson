package me.finn.tson.util;

/**
 * @author mitch
 * @since 30/12/15
 */
public class Constants {

    public static boolean isLetter(char c) {
        return c >= 'a' && c <= 'z';
    }

    public static boolean isNumberStart(char c) {
        return (c >= '0' && c <= '9') || c == '-';
    }
}