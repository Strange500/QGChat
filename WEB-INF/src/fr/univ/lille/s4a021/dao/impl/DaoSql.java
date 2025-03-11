package fr.univ.lille.s4a021.dao.impl;

import java.sql.Connection;

public class DaoSql {

    protected Connection connection;

    public DaoSql(Connection con) {
        this.connection = con;
    }
}
