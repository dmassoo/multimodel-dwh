package com.dmasso.data;


/**
 * Base interface for actual query/command/etc executors versus DBMS
 * @param <QT> input query type
 * @param <RT> type of result
 */
public interface BaseQueryExecutor<QT, RT> {
    RT execute(QT query);
}
