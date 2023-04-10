package com.dmasso.multidwh.metadata;

import lombok.Data;

import java.util.List;

@Data
public final class Engine {
    private String type;
    private String orientation;
    private List<String> index;

}