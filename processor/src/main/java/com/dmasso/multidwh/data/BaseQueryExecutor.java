package com.dmasso.multidwh.data;


import com.dmasso.multidwh.common.interfaces.DbTyped;

/**
 * Base interface for actual query/command/etc executors versus DBMS
 * @param <QT> input query type
 * @param <RT> type of result
 */
public interface BaseQueryExecutor<QT, RT> extends DbTyped {
    RT execute(QT query);
}
