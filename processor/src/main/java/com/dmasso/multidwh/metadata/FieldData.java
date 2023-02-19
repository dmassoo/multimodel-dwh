package com.dmasso.multidwh.metadata;

public class FieldData {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCardinality() {
        return cardinality;
    }

    public void setCardinality(int cardinality) {
        this.cardinality = cardinality;
    }

    private String name;
    private String type;
    private int cardinality;

    public FieldData(String name, String type, int cardinality) {
        this.name = name;
        this.type = type;
        this.cardinality = cardinality;
    }
}