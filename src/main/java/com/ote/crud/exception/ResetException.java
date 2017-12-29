package com.ote.crud.exception;

public class ResetException extends Exception {

    private final static String ErrorMessageTemplate = "Unable to create reset '%s' from id '%s'";

    public ResetException(String entityName, long id, Exception e) {
        super(String.format(ErrorMessageTemplate, entityName, id), e);
    }
}
