package fr.univ.lille.s4a021.dao.impl;

import fr.univ.lille.s4a021.Config;
import fr.univ.lille.s4a021.exception.ConfigErrorException;

import java.sql.Connection;

public class DaoSql {

    protected Connection connection;

    public DaoSql(Connection con) {
        this.connection = con;
    }
}
