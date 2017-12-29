package com.ote.crud.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Iterator;
import java.util.List;

@Data
@NoArgsConstructor
public class SplitList<T> implements Iterable<T> {

    private SplitListParameter splitListParam;
    private long totalElements;
    private int totalPages;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<SortingParameter> sort;

    private List<T> content;

    @Override
    public Iterator<T> iterator() {
        return content.iterator();
    }

    public int getNumberOfElements() {
        return content.size();
    }
}
