package com.ote.crud;

import com.ote.common.Error;
import com.ote.crud.exception.CreateException;
import com.ote.crud.exception.MergeException;
import com.ote.crud.exception.NotFoundException;
import com.ote.crud.exception.ResetException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class PersistenceExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error handle(NotFoundException exception) {
        log.info(exception.getMessage());
        String message = exception.getMessage();
        return new Error(message);
    }

    @ExceptionHandler(CreateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handle(CreateException exception) {
        log.info(exception.getMessage(), exception);
        String message = exception.getMessage();
        return new Error(message);
    }

    @ExceptionHandler(MergeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handle(MergeException exception) {
        log.info(exception.getMessage(), exception);
        String message = exception.getMessage();
        return new Error(message);
    }

    @ExceptionHandler(ResetException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handle(ResetException exception) {
        log.info(exception.getMessage(), exception);
        String message = exception.getMessage();
        return new Error(message);
    }
}
