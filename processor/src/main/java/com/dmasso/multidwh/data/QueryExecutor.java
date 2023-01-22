package com.dmasso.multidwh.data;

public interface QueryExecutor<QT> extends BaseQueryExecutor<QT, Iterable<?>>{

    @Override
    Iterable<?> execute(QT query);
}
