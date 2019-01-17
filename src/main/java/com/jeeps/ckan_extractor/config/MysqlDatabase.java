package com.jeeps.ckan_extractor.config;

import com.jeeps.ckan_extractor.model.CkanPackage;
import com.jeeps.ckan_extractor.model.CkanResource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class MysqlDatabase {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/ckan?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "";

    public MysqlDatabase() {
        // Create package table
        //language=SQL
        String sql = "CREATE TABLE IF NOT EXISTS PACKAGE " +
                "(id INTEGER not NULL AUTO_INCREMENT, " +
                " package_id VARCHAR(255), " +
                " name VARCHAR(255), " +
                " title TEXT, " +
                " license_title VARCHAR(255), " +
                " metadata_created VARCHAR(255)," +
                " metadata_modified VARCHAR(255)," +
                " author VARCHAR(255)," +
                " notes TEXT," +
                " type VARCHAR(255)," +
                " origin_url text," +
                " PRIMARY KEY ( id ));";
        executeSql(sql);

        // Create table resource
        sql = "CREATE TABLE IF NOT EXISTS RESOURCE " +
                "(id INTEGER not NULL AUTO_INCREMENT, " +
                " resource_id VARCHAR(255), " +
                " package_id VARCHAR(255), " +
                " description TEXT, " +
                " format VARCHAR(255), " +
                " name VARCHAR(255)," +
                " created VARCHAR(255)," +
                " last_modified VARCHAR(255)," +
                " url text," +
                " PRIMARY KEY ( id ));";
        executeSql(sql);
    }

    public void savePackage(CkanPackage aPackage, CkanResource[] resources) {
        //language=SQL
        String sql = "INSERT INTO PACKAGE(package_id, author, license_title, name, metadata_created, metadata_modified, title, type, notes, origin_url) " +
                "VALUES (" +
                String.format("'%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s'",
                        dQts(aPackage.getId()), dQts(aPackage.getAuthor()), dQts(aPackage.getLicense_title()),
                        dQts(aPackage.getName()), dQts(aPackage.getMetadata_created()), dQts(aPackage.getMetadata_modified()),
                        dQts(aPackage.getTitle()), dQts(aPackage.getType()), dQts(aPackage.getNotes()), dQts(aPackage.getOriginUrl())) +
                ")";
        executeSql(sql);

        List<CkanResource> resourceList = Arrays.asList(resources);
        resourceList.parallelStream().forEach(resource -> {
            //language=SQL
            String resourceSql = "INSERT INTO RESOURCE(resource_id, package_id, description, format, name, created, last_modified, url) " +
                    "VALUES (" +
                    String.format("'%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s'",
                            dQts(resource.getId()), dQts(resource.getPackage_id()), dQts(resource.getDescription()), dQts(resource.getFormat()),
                            dQts(resource.getName()), dQts(resource.getCreated()), dQts(resource.getLast_modified()), dQts(resource.getUrl())) +
                    ")";
            executeSql(resourceSql);
        });
    }

    private void executeSql(String sql) {
        Connection conn = null;
        Statement stmt = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();

            stmt.executeUpdate(sql);

        } catch(Exception se){
            se.printStackTrace();
        }
        finally{
            try{
                if(stmt!=null)
                    conn.close();
            }catch(SQLException se){
            }
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
    }

    private String dQts(String value) {
        if (value != null)
            if (value.contains("'"))
                return value.replaceAll("'", "''");
        return value;
    }
}
