package com.ote.crud.exception;

public class CreateException extends Exception {

    private final static String ErrorMessageWithReasonTemplate = "Unable to create entity '%s' : %s";

    public CreateException(String entityName, String reason, Exception e) {
        super(String.format(ErrorMessageWithReasonTemplate, entityName, reason), e);
    }

}
