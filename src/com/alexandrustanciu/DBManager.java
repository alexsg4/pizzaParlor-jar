package com.alexandrustanciu;

import java.sql.*;
import java.util.ArrayList;

public class DBManager {
    private static final String mDatabase = "main.db";
    private static Connection mConnection;
    private static DBManager mInstance;

    private void establishConnection() throws ClassNotFoundException, SQLException {
        // sqlite driver
        Class.forName("org.sqlite.JDBC");
        // database path, if it's new database, it will be created in the project folder
        mConnection = DriverManager.getConnection("jdbc:sqlite:" + mDatabase);
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        if(mConnection == null) { establishConnection(); }
        return mConnection;
    }

    private DBManager() throws ClassNotFoundException, SQLException {
        if(mConnection == null ) { establishConnection(); }
    }

    public static DBManager getInstance() throws ClassNotFoundException, SQLException {
        if (mInstance == null) {
            mInstance = new DBManager();
        }
        return mInstance;
    }

    public void addDBObject(DBObject toAdd) throws SQLException, ClassNotFoundException {
        toAdd.addToDB(mConnection);
    }

    public void addDBObjectsFrom(ArrayList<DBObject> arraySource) throws ClassNotFoundException, SQLException {
        if(arraySource.isEmpty()) { return; }

        for (DBObject toAdd : arraySource){
            toAdd.addToDB(mConnection);
        }
    }

    //Dangerous. TODO remove after testing is complete.
    public void clearDB() throws SQLException{

        PreparedStatement delStat = mConnection.prepareStatement("delete from \"Ingredients\";");
        delStat.execute();
        delStat = mConnection.prepareStatement("delete from \"ProductTypes\";");
        delStat.execute();
        delStat = mConnection.prepareStatement("delete from \"Products\";");
        delStat.execute();
        delStat = mConnection.prepareStatement("delete from \"Recipes\";");
        delStat.execute();
    }

    public int getDBObjectID(DBObject object) throws SQLException, ClassNotFoundException {
        return object.getDBID(mConnection);
    }

    public DBObject buildDBObjFromID(DBObject genericObject, int id) throws SQLException, ClassNotFoundException {
        return genericObject.buildFromID(mConnection, id);
    }

    public String getDBObjectColumn(DBObject object, String column) throws SQLException, ClassNotFoundException{
        String property = null;
        int id = getDBObjectID(object);
        PreparedStatement queryStatement = mConnection.prepareStatement(
                "SELECT ROWID, * FROM " + object.getTable() + " WHERE ROWID = ?;");
        queryStatement.setInt(1, id);
        try(ResultSet rs = queryStatement.executeQuery()){
            if(rs.next()){
                property = rs.getString(column);
            }
        }

        return property;
    }
}
