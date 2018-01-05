package com.ote.crud;

import com.ote.crud.exception.CreateException;
import com.ote.crud.exception.MergeException;
import com.ote.crud.exception.NotFoundException;
import com.ote.crud.exception.ResetException;
import com.ote.crud.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
public abstract class AdtPersistenceRestController<TP extends IPayload> implements IPersistenceRestController<TP> {

    protected final IPersistenceService<TP> persistenceService;

    protected final String entityName;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TP get(@PathVariable("id") int id) throws NotFoundException {
        return persistenceService.findOne(id).orElseThrow(() -> new NotFoundException(entityName, id));
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public SplitList<TP> get(@RequestParam(required = false) Filters filters,
                             @RequestParam(required = false) SortingParameters sortingParameters,
                             @RequestParam(required = false) SplitListParameter splitListParam) {
        return persistenceService.findMany(filters, sortingParameters, splitListParam);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TP reset(@PathVariable("id") int id,
                    @RequestBody @NotNull @Validated(IPayload.ResettingValidationType.class) TP payload) throws ResetException, NotFoundException {
        return persistenceService.reset(id, payload).orElseThrow(() -> new NotFoundException(entityName, id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TP merge(@PathVariable("id") int id,
                    @RequestBody @NotNull @Validated(IPayload.MergingValidationType.class) TP payload) throws MergeException, NotFoundException {
        return persistenceService.merge(id, payload).orElseThrow(() -> new NotFoundException(entityName, id));
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TP create(@RequestBody @NotNull @Validated(IPayload.CreatingValidationType.class) TP payload) throws CreateException {
        return persistenceService.create(payload);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") int id) {
        persistenceService.deleteOne(id);
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam(required = false) Filters filters) {
        persistenceService.deleteMany(filters);
    }
}
