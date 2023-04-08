package com.dmasso.multidwh_datagen.datagen.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Movie {
    long id;
    String title;
    int released;
    String tagline;
}
