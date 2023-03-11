package com.dmasso.multidwh.metadata;

import java.util.List;

public final class Engine {
    private String type;
    private String orientation;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public List<String> getIndex() {
        return index;
    }

    public void setIndex(List<String> index) {
        this.index = index;
    }

    private List<String> index;

    public Engine(String type, String orientation, List<String> index) {
        this.type = type;
        this.orientation = orientation;
        this.index = index;
    }
}