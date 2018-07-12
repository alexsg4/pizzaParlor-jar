package app;

import java.sql.Connection;
import java.sql.SQLException;

public interface DBObject{
    int ID_NOTFOUND = -1;
    String TABLE_UNUSED = "NOTABLE";

    int getID() throws SQLException, ClassNotFoundException;
    String getTable();
    void addToDB(Connection con) throws SQLException, ClassNotFoundException;
    
}
