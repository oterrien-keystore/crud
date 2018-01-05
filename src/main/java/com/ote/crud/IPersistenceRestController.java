package com.ote.crud;

import com.ote.crud.exception.CreateException;
import com.ote.crud.exception.MergeException;
import com.ote.crud.exception.NotFoundException;
import com.ote.crud.exception.ResetException;
import com.ote.crud.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

public interface IPersistenceRestController<TP extends IPayload> {

    IPersistenceService<TP> getPersistenceService();

    String getEntityName();

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    default TP get(@PathVariable("id") int id) throws NotFoundException {
        return getPersistenceService().findOne(id).orElseThrow(() -> new NotFoundException(getEntityName(), id));
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    default SplitList<TP> get(@RequestParam(required = false) Filters filters,
                             @RequestParam(required = false) SortingParameters sortingParameters,
                             @RequestParam(required = false) SplitListParameter splitListParam) {
        return getPersistenceService().findMany(filters, sortingParameters, splitListParam);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    default TP reset(@PathVariable("id") int id,
                    @RequestBody @NotNull @Validated(IPayload.ResettingValidationType.class) TP payload) throws ResetException, NotFoundException {
        return getPersistenceService().reset(id, payload).orElseThrow(() -> new NotFoundException(getEntityName(), id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    default TP merge(@PathVariable("id") int id,
                    @RequestBody @NotNull @Validated(IPayload.MergingValidationType.class) TP payload) throws MergeException, NotFoundException {
        return getPersistenceService().merge(id, payload).orElseThrow(() -> new NotFoundException(getEntityName(), id));
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    default TP create(@RequestBody @NotNull @Validated(IPayload.CreatingValidationType.class) TP payload) throws CreateException {
        return getPersistenceService().create(payload);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    default void delete(@PathVariable("id") int id) {
        getPersistenceService().deleteOne(id);
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    default void delete(@RequestParam(required = false) Filters filters) {
        getPersistenceService().deleteMany(filters);
    }
}
