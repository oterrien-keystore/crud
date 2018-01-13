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

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(SplitListParameter.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    SplitListParameter param = JsonUtils.parse(text, SplitListParameter.class);
                    this.setValue(param);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        });
        binder.registerCustomEditor(Filters.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    Filters param = JsonUtils.parse(text, Filters.class);
                    this.setValue(param);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        });
        binder.registerCustomEditor(SortingParameters.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    SortingParameters param = JsonUtils.parse(text, SortingParameters.class);
                    this.setValue(param);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        });
    }
}