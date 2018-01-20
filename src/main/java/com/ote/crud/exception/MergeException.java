package com.ote.crud.exception;

public class MergeException extends Exception {

    private final static String ErrorMessageWithReasonTemplate = "Unable to merge entity '%s' for id '%s' : %s";

    public MergeException(String entityName, long id, String reason, Exception e) {
        super(String.format(ErrorMessageWithReasonTemplate, entityName, id, reason), e);
    }
}
