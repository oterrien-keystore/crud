package com.ote.crud.exception;

public class MergeException extends Exception {

    private final static String ErrorMessageTemplate = "Unable to merge entity '%s' for id '%s'";

    public MergeException(String entityName, long id, Exception e) {
        super(String.format(ErrorMessageTemplate, entityName, id), e);
    }
}
