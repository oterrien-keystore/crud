package com.ote.crud;

import com.ote.crud.exception.NotFoundException;
import lombok.Getter;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    default Error handle(MethodArgumentNotValidException exception) {
        if (getLogger().isDebugEnabled()) {
            getLogger().error(exception.getMessage(), exception);
        } else {
            getLogger().error(exception.getMessage());
        }
        Error error = new Error();
        exception.getBindingResult().getFieldErrors().stream().
                map(p -> p.getObjectName() + ": " + p.getField() + " " + p.getDefaultMessage()).
                forEach(m -> error.getMessages().add(m));
        return error;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    default Error handle(NotFoundException exception) {
        if (getLogger().isDebugEnabled()) {
            getLogger().error(exception.getMessage(), exception);
        } else {
            getLogger().error(exception.getMessage());
        }
        return new Error(exception.getMessage());
    }

    class Error {
        @Getter
        private final List<String> messages = new ArrayList<>();

        Error(String... msg) {
            messages.addAll(Arrays.asList(msg));
        }
    }
}
