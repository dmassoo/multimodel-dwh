package com.dmasso.multidwh.metadata;

import lombok.Data;

import java.util.List;


@Data
public class Entity {
    private String name;
    private int cardinality;
    private List<FieldData> schema;
    private List<Engine> engines;
}

