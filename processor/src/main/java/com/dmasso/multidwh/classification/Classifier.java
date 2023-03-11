package com.dmasso.multidwh.classification;

public interface Classifier<Q, T> {

    T classify(Q query);
}
