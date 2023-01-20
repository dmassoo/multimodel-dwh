package com.dmasso.translation;

/**
 * The most basic translator interface
 * @param <QT> Query Type
 * @param <RT> Result (or prepared query type)
 */
public interface BaseTranslator<QT, RT> {
    RT translate(QT query);
}
