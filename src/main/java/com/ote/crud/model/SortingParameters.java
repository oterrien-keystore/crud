package com.ote.crud.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
public class SortingParameters {

    @NotNull
    private List<SortingParameter> content;

    public SortingParameters(SortingParameter... content) {
        this.content = Arrays.asList(content);
    }

    public boolean isEmpty() {
        return content == null || content.isEmpty();
    }
}