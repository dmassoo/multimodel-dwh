package com.dmasso.multidwh_datagen.datagen;

import com.dmasso.multidwh.data.ConnectionProperties;
import com.dmasso.multidwh.data.OltpConnectionProperties;

public class PgDataGenerator implements RelationalDataGenerator {

    @Override
    public ConnectionProperties getConnectionProperties() {
        return new OltpConnectionProperties();
    }

    public static void main(String[] args) {
        new PgDataGenerator().generateData();
    }
}
