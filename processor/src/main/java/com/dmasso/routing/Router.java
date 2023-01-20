package com.dmasso.routing;


public interface Router<QT> {
    DbType route(QT query);
}
