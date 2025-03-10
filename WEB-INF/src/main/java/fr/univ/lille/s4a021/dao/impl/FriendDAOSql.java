package fr.univ.lille.s4a021.dao.impl;

import fr.univ.lille.s4a021.dao.FriendDAO;

import java.sql.Connection;

public class FriendDAOSql extends DaoSql implements FriendDAO {
    public FriendDAOSql(Connection con) {
        super(con);
    }
}
