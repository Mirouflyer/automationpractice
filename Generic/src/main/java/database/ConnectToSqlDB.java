package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;



public class ConnectToSqlDB {

    public static Connection connect = null;
    public static Statement statement = null;
    public static PreparedStatement ps = null;
    public static ResultSet resultSet = null;

    //****************************************//
    //This method is used to get  the propperties from secret.properties file (driver url,database name ,username, password)
    public static Properties loadProperties(String filePath) throws IOException {
        Properties prop = new Properties();
        InputStream ism = new FileInputStream(filePath);
        prop.load(ism);
        ism.close();
        return prop;
    }

    //*******************************************************************//
    // This method is used to connect to  MYSQL databases.
    public static Connection connectToSqlDatabase(String filePath) throws IOException, SQLException, ClassNotFoundException {
        Properties prop = loadProperties(filePath);
        String driverClass = prop.getProperty("MYSQLJDBC.driver");
        String url = prop.getProperty("MYSQLJDBC.url");
        String userName = prop.getProperty("MYSQLJDBC.userName");
        String password = prop.getProperty("MYSQLJDBC.password");
        Class.forName(driverClass);
        connect = DriverManager.getConnection(url, userName, password);
        System.out.println("Database is connected");
        return connect;
    }

    //*******************************************************************//
    // this method is used to read from MYSQL databases.
    public List<String> readDataBase(String tableName, String columnName,String filePath) throws Exception {
        List<String> data = new ArrayList<String>();

        try {
            connectToSqlDatabase(filePath);
            statement = connect.createStatement();
            resultSet = statement.executeQuery("select * from " + tableName);
            data = getResultSetData(resultSet, columnName);
        } catch (ClassNotFoundException e) {
            throw e;
        } finally {
            close();
        }
        return data;
    }

    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {

        }
    }

    private List<String> getResultSetData(ResultSet resultSet2, String columnName) throws SQLException {
        List<String> dataList = new ArrayList<String>();
        while (resultSet.next()) {
            String itemName = resultSet.getString(columnName);
            dataList.add(itemName);
        }
        return dataList;
    }
//***********************************************************************************************************
    //This method is used to insert data to MYSQL databases.
    public void insertDataFromArrayListToSqlTable(List<String> list, String tableName, String columnName, String filePath) {
        try {
            connectToSqlDatabase(filePath);
            ps = connect.prepareStatement("DROP TABLE IF EXISTS `" + tableName + "`;");
            ps.executeUpdate();
            ps = connect.prepareStatement("CREATE TABLE `" + tableName + "` (`items` VARCHAR(1000) NOT NULL );");
            ;
            ps.executeUpdate();
            for (String st : list) {
                ps = connect.prepareStatement("INSERT INTO " + tableName + " ( " + columnName + " ) VALUES(?)");
                ps.setObject(1, st);
                ps.executeUpdate();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
