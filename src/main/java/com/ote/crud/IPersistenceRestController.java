package com.ote.crud;

import com.ote.crud.exception.CreateException;
import com.ote.crud.exception.MergeException;
import com.ote.crud.exception.NotFoundException;
import com.ote.crud.exception.ResetException;
import com.ote.crud.model.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;

public interface IPersistenceRestController<TP extends IPayload> {

    TP get(@PathVariable("id") int id) throws NotFoundException;

    SplitList<TP> get(@RequestParam(required = false) Filters filters,
                      @RequestParam(required = false) SortingParameters sortingParameters,
                      @RequestParam(required = false) SplitListParameter splitListParam);

    TP reset(@PathVariable("id") int id,
             @RequestBody @NotNull @Validated(IPayload.ResettingValidationType.class) TP payload) throws ResetException, NotFoundException;

    TP merge(@PathVariable("id") int id,
             @RequestBody @NotNull @Validated(IPayload.MergingValidationType.class) TP payload) throws MergeException, NotFoundException;

    TP create(@RequestBody @NotNull @Validated(IPayload.CreatingValidationType.class) TP payload) throws CreateException;

    void delete(@PathVariable("id") int id);

    void delete(@RequestParam(required = false) Filters filters);
}
