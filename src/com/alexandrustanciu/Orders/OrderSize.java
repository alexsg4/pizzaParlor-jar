package com.alexandrustanciu.Orders;

import com.alexandrustanciu.DB.DBObject;
import com.alexandrustanciu.FileLoadable;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderSize implements DBObject, FileLoadable<DBObject> {

    private int id = ID_UNUSED;
    private String name = "Regular";
    private double priceMod = 1.;


    OrderSize() { }

    OrderSize(int id, String name, double priceMod){
        setId(id);
        setName(name);
        setPriceMod(priceMod);
    }

    OrderSize(String name, double priceMod){
        setName(name);
        setPriceMod(priceMod);
    }

    public static OrderSize getGeneric() { return new OrderSize(); }

    @Override
    public String getTable(){ return "OrderSizes"; }

    @Override
    public int getIDfromDB(Connection connection) throws SQLException{

        int idToGet = ID_UNUSED;

        if(connection!=null) {
            String table = getTable();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT id FROM " + table + " WHERE name = ?;"
            );
            ps.setString(1, this.name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idToGet = rs.getInt(1);
                }
            }
        }

        return idToGet;
    }

    @Override
    public boolean canAdd(Connection con) throws SQLException {
        return (this.getIDfromDB(con) == ID_UNUSED);
    }

    @Override
    public void addToDB(Connection connection) throws SQLException{
        if(canAdd(connection)){

            String table = getTable();
            PreparedStatement add = connection.prepareStatement(
                    "INSERT INTO " + table + " (name, priceMod) VALUES (?, ?);"
            );
            add.setString(1, this.name);
            add.setDouble(2, this.priceMod);

            add.executeUpdate();
        }
    }

    @Override
    public DBObject buildFromID(Connection con, int id) throws SQLException{
        OrderSize toBuild = null;

        if(con != null && id > ID_UNUSED){
            String table = getTable();
            PreparedStatement queryObjectData = con.prepareStatement(
                    "SELECT * FROM " + table + " WHERE id = ?;"
            );
            queryObjectData.setInt(1, id);

            try(ResultSet rs = queryObjectData.executeQuery()){
                while (rs.next()){
                    toBuild = new OrderSize(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("priceMod")
                    );
                }
            }
        }

        return toBuild;
    }

    void setId(int id){
        if(id > ID_UNUSED){
            this.id = id;
        }
    }

    String getName(){ return this.name; }

    void setName(String name){
        if(!name.isEmpty()){
            this.name = name;
        }
    }

    double getPriceMod() { return this.priceMod; }

    void setPriceMod(double priceMod){
        if(priceMod > 1.){
            this.priceMod= priceMod;
        }
    }

    private OrderSize fromString(String source){
        OrderSize toBuild = null;

        if(source.matches("((\\w ?))+( */ *)(\\d+(\\.\\d{1,5})?)\\s*$")){
            String[] data = source.split(" */ *");
            toBuild = new OrderSize(
                    data[0],
                    Double.parseDouble(data[1])
            );
        }

        return toBuild;
    }

    @Override
    public ArrayList<DBObject> loadFromFile(String path) {

        ArrayList<DBObject> loadedSizes = new ArrayList<>();

        try {
            File file = new File(path);
            BufferedReader fin = new BufferedReader(new FileReader(file));

            String lineToProcess;
            try {
                while ((lineToProcess = fin.readLine()) != null ) {
                    OrderSize toAdd = fromString(lineToProcess);
                    if (toAdd != null) {
                        loadedSizes.add(toAdd);
                    }
                }
                fin.close();
            } catch (IOException ex) { ex.printStackTrace(); }
        } catch (FileNotFoundException fex) {
            fex.printStackTrace();
        }

        return loadedSizes;
    }

    @Override
    public ArrayList<DBObject> loadFromFile(File file) {

        ArrayList<DBObject> loadedSizes = new ArrayList<>();

        try {
            BufferedReader fin = new BufferedReader(new FileReader(file));

            String lineToProcess;
            try {
                while ((lineToProcess = fin.readLine()) != null ) {
                    OrderSize toAdd = fromString(lineToProcess);
                    if (toAdd != null) {
                        loadedSizes.add(toAdd);
                    }
                }
                fin.close();
            } catch (IOException ex) { ex.printStackTrace(); }
        } catch (FileNotFoundException fex) {
            fex.printStackTrace();
        }

        return loadedSizes;
    }


}
