package app;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public interface DBObject{
    int ID_NOTFOUND = -1;
    String TABLE_UNUSED = "NO_TABLE";

    int getID() throws SQLException, ClassNotFoundException;
    String getTable();
    void addToDB(Connection con) throws SQLException, ClassNotFoundException;

    ArrayList<Object> loadFromFile(String path);

    DBObject buildFromID(int id) throws SQLException, ClassNotFoundException;

}
