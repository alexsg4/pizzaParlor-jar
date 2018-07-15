package app;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public interface DBObject{
    int ID_UNUSED = -1;
    String TABLE_UNUSED = "NO_TABLE";

    String getTable();
    ArrayList<DBObject> loadFromFile(String path);

    void addToDB(Connection connection) throws SQLException, ClassNotFoundException;
    int getDBID(Connection connection) throws SQLException, ClassNotFoundException;
    DBObject buildFromID(Connection con, int id) throws SQLException, ClassNotFoundException;

}
