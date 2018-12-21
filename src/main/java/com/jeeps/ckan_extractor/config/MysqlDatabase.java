package com.jeeps.ckan_extractor.config;

import com.jeeps.ckan_extractor.model.CkanPackage;

import java.sql.*;

public class MysqlDatabase {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/ckan";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "";

    public MysqlDatabase() {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            stmt = conn.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS PACKAGE " +
                    "(id INTEGER not NULL AUTO_INCREMENT, " +
                    " name VARCHAR(255), " +
                    " title VARCHAR(255), " +
                    " license_title VARCHAR(255), " +
                    " metadata_created VARCHAR(255)," +
                    " metadata_modified VARCHAR(255)," +
                    " author VARCHAR(255)," +
                    " PRIMARY KEY ( id ))";

            stmt.executeUpdate(sql);
        } catch (Exception se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }//Handle errors for Class.forName
        finally {
            try {
                if (stmt != null)
                    conn.close();
            } catch (SQLException se) {
            }// do nothing
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public void savePackage(CkanPackage aPackage) {
        Connection conn = null;
        Statement stmt = null;
        try{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //STEP 4: Execute a query
            stmt = conn.createStatement();

            String sql = "INSERT INTO package(author, license_title, name, metadata_created, metadata_modified, title) " +
                    "VALUES (" +
                    String.format("'%s', '%s', '%s', '%s', '%s', '%s'",
                            aPackage.getAuthor(), aPackage.getLicense_title(), aPackage.getName(), aPackage.getMetadata_created(),
                            aPackage.getMetadata_modified(), aPackage.getTitle()) +
                    ")";
            stmt.executeUpdate(sql);

        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    conn.close();
            }catch(SQLException se){
            }// do nothing
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }
    }
}
