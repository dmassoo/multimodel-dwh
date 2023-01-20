package com.dmasso.routing;


import com.dmasso.routing.enums.DbType;

public interface BaseRouter<QT> {
    DbType route(QT query);
}
