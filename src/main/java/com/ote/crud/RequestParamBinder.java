package com.ote.crud;

import com.ote.common.JsonUtils;
import com.ote.crud.model.Filters;
import com.ote.crud.model.SortingParameters;
import com.ote.crud.model.SplitListParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;

@ControllerAdvice
@Slf4j
public class RequestParamBinder {

    private static final String ErrorMessageTemplate = "Unable to parse %s into %s";

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        registerCustomEditor(binder, SplitListParameter.class);
        registerCustomEditor(binder, Filters.class);
        registerCustomEditor(binder, SortingParameters.class);
    }

    private <T> void registerCustomEditor(WebDataBinder binder, Class<T> clazz) {
        binder.registerCustomEditor(clazz,
                getPropertyEditorSupport(text -> JsonUtils.parse(text, clazz), clazz));
    }

    private <T> PropertyEditorSupport getPropertyEditorSupport(CustomFunction<T> parser, Class<T> clazz) {

        return new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    T param = parser.parse(text);
                    this.setValue(param);
                } catch (Exception e) {
                    throw new IllegalArgumentException(String.format(ErrorMessageTemplate, text, clazz.getSimpleName()), e);
                }
            }
        };
    }

    @FunctionalInterface
    private interface CustomFunction<T> {
        T parse(String text) throws Exception;
    }
}