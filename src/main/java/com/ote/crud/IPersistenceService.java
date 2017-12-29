package com.ote.crud;

import com.ote.crud.exception.CreateException;
import com.ote.crud.exception.MergeException;
import com.ote.crud.exception.ResetException;
import com.ote.crud.model.*;

import java.util.List;
import java.util.Optional;

public interface IPersistenceService<TP extends IPayload> {

    //region READER
    Optional<TP> findOne(long id);

    List<TP> findMany(Filters filters);

    SplitList<TP> findMany(Filters filters, SortingParameters sortingParam, SplitListParameter splitListParam);

    default List<TP> findAll() {
        return findMany(null);
    }

    default SplitList<TP> findAll(SortingParameters sortingParam, SplitListParameter splitListParam) {
        return findMany(null, sortingParam, splitListParam);
    }
    //endregion

    //region CREATE
    TP create(TP payload) throws CreateException;
    //endregion

    //region UPDATE
    Optional<TP> reset(long id, TP payload) throws ResetException;

    Optional<TP> merge(long id, TP payload) throws MergeException;
    //endregion

    //region DELETE
    void deleteOne(long id);

    void deleteMany(Filters filters);

    default void deleteAll() {
        deleteMany(null);
    }
    //endregion
}
