package com.dmasso.multidwh.metadata;

import lombok.Data;

import java.util.List;

@Data
public class Metadata {
    public String version;
    public List<Entity> entities;
}






