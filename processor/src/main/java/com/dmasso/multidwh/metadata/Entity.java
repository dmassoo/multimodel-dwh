package com.dmasso.multidwh.metadata;

import java.util.List;

public class Entity {
    private String name;
    private int cardinality;
    private List<FieldData> schema;
    private List<Engine> engines;

    public Entity(String name, int cardinality, List<FieldData> schema, List<Engine> engines) {
        this.name = name;
        this.cardinality = cardinality;
        this.schema = schema;
        this.engines = engines;
    }

    public String getName() {
        return name;
    }

    public int getCardinality() {
        return cardinality;
    }

    public List<FieldData> getSchema() {
        return schema;
    }

    public List<Engine> getEngines() {
        return engines;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCardinality(int cardinality) {
        this.cardinality = cardinality;
    }

    public void setSchema(List<FieldData> schema) {
        this.schema = schema;
    }

    public void setEngines(List<Engine> engines) {
        this.engines = engines;
    }
}
