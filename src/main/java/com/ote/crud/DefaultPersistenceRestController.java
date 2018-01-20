package com.ote.crud;

import com.ote.crud.model.IPayload;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultPersistenceRestController<TP extends IPayload> implements IPersistenceRestController<TP> {

    @Getter
    private final IPersistenceService<TP> persistenceService;

    @Getter
    private final String scope;
}
