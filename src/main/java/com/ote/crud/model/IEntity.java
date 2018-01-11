package com.ote.crud.model;

public interface IEntity extends Identifiable {

    <TP extends IPayload> TP convert();
}
