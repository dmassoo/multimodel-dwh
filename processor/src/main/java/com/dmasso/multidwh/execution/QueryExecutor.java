package com.dmasso.multidwh.execution;

public interface QueryExecutor<QT> extends BaseQueryExecutor<QT, Iterable<?>>{

    @Override
    Iterable<?> execute(QT query);
}
