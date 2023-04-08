package com.dmasso.multidwh_datagen.datagen.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Person {
    long id;
    String name;
    int born;
}
