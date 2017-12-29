package com.ote.crud.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
public class Filters {

    @NotNull
    private List<Filter> content;

    public boolean isEmpty() {
        return content == null || content.isEmpty();
    }
}
