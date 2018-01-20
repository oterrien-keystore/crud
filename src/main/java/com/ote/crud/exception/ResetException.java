package com.ote.crud.exception;

public class ResetException extends Exception {

    private final static String ErrorMessageWithReasonTemplate = "Unable to create reset '%s' from id '%s' : %s";

    public ResetException(String entityName, long id, String reason, Exception e) {
        super(String.format(ErrorMessageWithReasonTemplate, entityName, id, reason), e);
    }
}
