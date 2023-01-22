package com.dmasso.multidwh.routing;


import com.dmasso.multidwh.common.enums.DbType;

public interface BaseRouter<QT> {
    DbType route(QT query);
}
