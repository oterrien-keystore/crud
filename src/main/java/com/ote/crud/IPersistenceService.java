package com.ote.crud;

import com.ote.crud.exception.CreateException;
import com.ote.crud.exception.MergeException;
import com.ote.crud.exception.ResetException;
import com.ote.crud.model.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface IPersistenceService<TP extends IPayload> {

    Optional<TP> findOne(long id);

    List<TP> findMany(Filters filters);

    SplitList<TP> findMany(Filters filters, SortingParameters sortingParam, SplitListParameter splitListParam);

    default List<TP> findAll() {
        return findMany(null);
    }

    default SplitList<TP> findAll(SortingParameters sortingParam, SplitListParameter splitListParam) {
        return findMany(null, sortingParam, splitListParam);
    }

    TP create(TP payload) throws CreateException;

    Optional<TP> reset(long id, TP payload) throws ResetException;

    Optional<TP> merge(long id, TP payload) throws MergeException;

    void deleteOne(long id);

    void deleteMany(Filters filters);

    default void deleteAll() {
        deleteMany(null);
    }
}
