package com.ecore.roles.exception;

import static java.lang.String.format;

public class InvalidArgumentException extends RuntimeException {

    public <T> InvalidArgumentException(Class<T> resource, String additional) {
        super(format("Invalid '%s' object. %s", resource.getSimpleName(), additional));
    }

    public <T> InvalidArgumentException(Class<T> resource) {
        this(resource, "");
    }
}
