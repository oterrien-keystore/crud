package com.ote.crud.exception;

public class NotFoundException extends Exception {

    private final static String ErrorMessageTemplate = "Unable to find '%s' for id '%s'";

    public NotFoundException(String entityName, long id) {
        super(String.format(ErrorMessageTemplate, entityName, id));
    }
}
