package me.finn.tson.exception;

import me.finn.tson.parse.JsonParse;

import java.util.List;
import java.util.Stack;

/**
 * @author mitch Finn (edited on 20.05.2021)
 * @since 30/12/15
 */
public class JsonParseException extends RuntimeException {

    private final String message;

    public JsonParseException(String message) {
        this.message = message;
    }

    public JsonParseException(Stack<JsonParse.State> stateStack, String message) {
        StringBuilder jsonTrace = new StringBuilder();
        for (int i = 0; i < stateStack.size(); i++) {
            String name = stateStack.get(i).propertyName;
            if (name == null) {
                // Fill in array index
                List<Object> list = (List<Object>) stateStack.get(i).container;
                name = String.format("[%d]", list.size());
            }
            jsonTrace.append(name).append(i != stateStack.size() - 1 ? "." : "");
        }

        jsonTrace = new StringBuilder(jsonTrace.toString().equals("") ? "<root>" : "<root>." + jsonTrace);

        this.message = jsonTrace + ": " + message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}