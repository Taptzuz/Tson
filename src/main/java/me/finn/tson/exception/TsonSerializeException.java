package me.finn.tson.exception;

/**
 * @author Finn on 20.05.2021
 */
public class TsonSerializeException extends RuntimeException {

    public TsonSerializeException(String msg) {
        super(msg);
    }

    public TsonSerializeException(String msg, Exception e) {
        super(msg, e);
    }
}
