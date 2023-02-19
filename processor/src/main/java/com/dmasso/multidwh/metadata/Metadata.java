package com.dmasso.multidwh.metadata;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Metadata {
    public List<Map<String, Entity>> entities;
}






