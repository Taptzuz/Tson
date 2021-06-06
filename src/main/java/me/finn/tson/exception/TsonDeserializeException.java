package me.finn.tson.exception;

/**
 * @author Finn on 20.05.2021
 */
public class TsonDeserializeException extends RuntimeException {

    public TsonDeserializeException(String msg) {
        super(msg);
    }

    public TsonDeserializeException(String msg, Exception e) {
        super(msg, e);
    }
}
