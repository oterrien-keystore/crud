package com.ote.crud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortingParameter {

    @NotNull
    private String property;
    private Direction direction;

    public enum Direction {
        ASC, DESC;

        public static Direction of(String direction) {
            return Stream.of(Direction.values()).
                    filter(p -> p.name().equalsIgnoreCase(direction)).
                    findFirst().
                    orElseThrow(() -> new RuntimeException("Direction " + direction + " not found"));
        }
    }

    public Direction getDirection() {
        if (direction == null) {
            this.direction = Direction.ASC;
        }
        return this.direction;
    }
}