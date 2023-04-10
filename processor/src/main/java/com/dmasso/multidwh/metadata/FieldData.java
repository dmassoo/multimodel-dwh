package com.dmasso.multidwh.metadata;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FieldData {
    private String name;
    private String type;
    private int cardinality;
}