package com.dmasso.data;

public interface QueryExecutor<QT> extends BaseQueryExecutor<QT, Iterable<?>>{

    @Override
    Iterable<?> execute(QT query);
}
