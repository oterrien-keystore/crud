package com.ote.crud.exception;

public class CreateException extends Exception {

    private final static String ErrorMessageTemplate = "Unable to create entity '%s'";

    public CreateException(String entityName, Exception e) {
        super(String.format(ErrorMessageTemplate, entityName), e);
    }
}
