package com.ote.crud.model;

public interface IPayload extends Identifiable {

    <TE extends IEntity> TE convert();

    interface CreatingValidationType {
    }

    interface ResettingValidationType {
    }

    interface MergingValidationType {
    }
}
