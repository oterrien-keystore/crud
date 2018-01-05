package com.ote.crud;

import com.ote.crud.exception.NotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

public interface IExceptionRestControllerAdvice {

    Logger getLogger();

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    default Error handle(Throwable exception) {
        getLogger().error(exception.getMessage(), exception);
        return new Error(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    default List<Error> handle(MethodArgumentNotValidException exception) {
        getLogger().error(exception.getMessage(), exception);
        return exception.getBindingResult().getFieldErrors().stream().
                map(p -> p.getObjectName() + ": " + p.getField() + " " + p.getDefaultMessage()).
                map(Error::new).
                collect(Collectors.toList());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    default Error handle(NotFoundException exception) {
        if (getLogger().isDebugEnabled()) {
            getLogger().info(exception.getMessage(), exception);
        } else {
            getLogger().info(exception.getMessage());
        }
        return new Error(exception.getMessage());
    }

    @RequiredArgsConstructor
    class Error {
        @Getter
        private final String message;
    }
}
