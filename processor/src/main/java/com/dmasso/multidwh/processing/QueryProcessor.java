package com.dmasso.multidwh.processing;

/**
 * Base Query processor
 * @param <QT> source query type
 * @param <RT> type of result
 */
public interface QueryProcessor<QT, RT> {
    RT execute(QT query);
}
