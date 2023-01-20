package com.dmasso.processing;

/**
 * Base Query processor
 * @param <SQT> source query type
 * @param <TQT> target query type
 * @param <RT> type of result
 */
public interface QueryProcessor<SQT,TQT, RT> {
    RT execute(SQT query);
}
