package com.alexandrustanciu.DB;

import java.sql.Connection;
import java.sql.SQLException;

public interface DBObject{
    int ID_UNUSED = -1;
    String TABLE_UNUSED = "NO_TABLE";

    String getTable();
    boolean canAdd(Connection con) throws SQLException;
    int getDBID(Connection connection) throws SQLException, ClassNotFoundException;
    void addToDB(Connection connection) throws SQLException, ClassNotFoundException;
    DBObject buildFromID(Connection con, int id) throws SQLException, ClassNotFoundException;

}
