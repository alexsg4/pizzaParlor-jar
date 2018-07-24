package com.alexandrustanciu.DB;

import java.sql.*;
import java.util.ArrayList;

public class DBManager {
    private static final String mDatabase = "main.db";
    private static Connection mConnection = null;
    private static DBManager mInstance;

    static {
        try {
            mInstance = new DBManager();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private DBManager() throws SQLException {
        this.getConnection();
    }

    public static DBManager getInstance() throws SQLException {
        if (mInstance == null) {
            mInstance = new DBManager();
        }
        return mInstance;
    }

    private void establishConnection(){
        try {
            // sqlite driver
            Class.forName("org.sqlite.JDBC");
            mConnection = DriverManager.getConnection("jdbc:sqlite:" + mDatabase);
        } catch (SQLException | ClassNotFoundException ex){
            ex.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        if(mConnection == null || mConnection.isClosed()) { establishConnection(); }
        return mConnection;
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
    private void clearDB() throws SQLException{

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
        return object.getIDfromDB(mConnection);
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
