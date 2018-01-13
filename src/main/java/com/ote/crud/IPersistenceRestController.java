package com.ote.crud;

import com.ote.crud.exception.CreateException;
import com.ote.crud.exception.MergeException;
import com.ote.crud.exception.NotFoundException;
import com.ote.crud.exception.ResetException;
import com.ote.crud.model.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.constraints.NotNull;

public interface IPersistenceRestController<TP extends IPayload> {

    IPersistenceService<TP> getPersistenceService();

    String getEntityName();

    default TP get(long id) throws NotFoundException {
        return getPersistenceService().findOne(id).orElseThrow(() -> new NotFoundException(getEntityName(), id));
    }

    default SplitList<TP> get(Filters filters,
                              SortingParameters sortingParameters,
                              SplitListParameter splitListParam) {
        return getPersistenceService().findMany(filters, sortingParameters, splitListParam);
    }

    default TP reset(long id, @NotNull @Validated(IPayload.CreatingValidationType.class) TP payload) throws ResetException, NotFoundException {
        return getPersistenceService().reset(id, payload).orElseThrow(() -> new NotFoundException(getEntityName(), id));
    }

    default TP merge(long id, @NotNull @Validated(IPayload.CreatingValidationType.class) TP payload) throws MergeException, NotFoundException {
        return getPersistenceService().merge(id, payload).orElseThrow(() -> new NotFoundException(getEntityName(), id));
    }

    default TP create(@NotNull @Validated(IPayload.CreatingValidationType.class) TP payload) throws CreateException {
        return getPersistenceService().create(payload);
    }

    default void delete(long id) {
        getPersistenceService().deleteOne(id);
    }

    default void delete(Filters filters) {
        getPersistenceService().deleteMany(filters);
    }
}
