package com.dmasso.multidwh.translation;

/**
 * Interface for query traslators from source plain text query to some object DBs Java client can work with
 * @param <RT> stands for Resulting Query Type which means the type of object
 *            some java lib/client for DB connectivity can work with
 */
public interface Preparer<RT> extends BaseTranslator<String, RT> {
}
