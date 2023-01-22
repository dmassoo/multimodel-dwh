package com.dmasso.multidwh.translation;

import com.dmasso.multidwh.common.interfaces.DbTyped;

/**
 * The most basic translator interface
 * @param <QT> Query Type
 * @param <RT> Result (or prepared query type)
 */
public interface BaseTranslator<QT, RT> extends DbTyped {
    RT translate(QT query);
}
