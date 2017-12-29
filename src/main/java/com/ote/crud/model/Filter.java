package com.ote.crud.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class Filter {

    @NotNull
    private String property;

    @NotNull
    private String value;

    private Operator operator;

    public enum Operator {
        EQUALS,
        DIFFERENT,
        LIKE,
        GREATER_THAN,
        GREATER_THAN_OR_EQUALS,
        LESS_THAN,
        LESS_THAN_OR_EQUALS,
        IN;
    }

    public Operator getOperator() {
        if (this.operator == null) {
            this.operator = Operator.EQUALS;
        }
        return this.operator;
    }
}
