package com.dmasso.multidwh_datagen.datagen;

import com.dmasso.multidwh.data.ConnectionProperties;
import com.dmasso.multidwh.data.OlapConnectionProperties;

public class ChDataGenerator implements RelationalDataGenerator {
    public static final int BATCH_SIZE = 500000; //CH works best with big batches

    @Override
    public ConnectionProperties getConnectionProperties() {
        return new OlapConnectionProperties();
    }

    @Override
    public int getBatchSize() {
        return BATCH_SIZE;
    }

    public static void main(String[] args) {
        new ChDataGenerator().generateData();
    }
}
