package com.jeeps.ckan_extractor.config;

import com.jeeps.ckan_extractor.model.CkanPackage;
import com.jeeps.ckan_extractor.model.CkanResource;

import java.sql.*;
import java.util.*;

public class MysqlDatabase {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
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

    public MysqlDatabase(boolean createTables) {}

    public void savePackage(CkanPackage aPackage, CkanResource[] resources) {
        //language=SQL
        String sql = "INSERT INTO PACKAGE(package_id, author, license_title, name, metadata_created, metadata_modified, title, type, notes, origin_url) " +
                "VALUES (" +
                //language=
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
                    //language=
                    String.format("'%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s'",
                            dQts(resource.getId()), dQts(resource.getPackage_id()), dQts(resource.getDescription()), dQts(resource.getFormat()),
                            dQts(resource.getName()), dQts(resource.getCreated()), dQts(resource.getLast_modified()), dQts(resource.getUrl())) +
                    ")";
            executeSql(resourceSql);
        });
    }

    public Map<CkanPackage, List<CkanResource>> retrieveDatasets() {
        Map<CkanPackage, List<CkanResource>> datasets = new HashMap<>();

        Connection conn = null;
        Statement stmt = null;
        try{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();

            String sql = "select * from PACKAGE p left join RESOURCE r on p.package_id = r.package_id order by p.package_id;";
            ResultSet rs = stmt.executeQuery(sql);
            //STEP 5: Extract data from result set
            // Track previous package id
            String previousPackageId = "";
            int counter = 0;
            List<CkanResource> resources = new ArrayList<>();
            while(rs.next()){
                if (counter == 0)
                    previousPackageId = rs.getString("package_id");
                // Package details
                String p_package_id = rs.getString("package_id");
                String p_name = rs.getString("name");
                String p_title = rs.getString("title");
                String p_license_title = rs.getString("license_title");
                String p_metadata_created = rs.getString("metadata_created");
                String p_metadata_modified = rs.getString("metadata_modified");
                String p_author = rs.getString("author");
                String p_origin_url = rs.getString("origin_url");

                CkanPackage ckanPackage = new CkanPackage.CkanPackageBuilder(p_package_id)
                        .withName(p_name)
                        .withTitle(p_title)
                        .withLicense(p_license_title)
                        .withMetadataCreated(p_metadata_created)
                        .withMetadataCreated(p_metadata_modified)
                        .withAuthor(p_author)
                        .withOriginUrl(p_origin_url)
                        .build();

                // Resource details
                String description = rs.getString("description");
                String format = rs.getString("format");
                String name = rs.getString("name");
                String created = rs.getString("created");
                String last_modified = rs.getString("last_modified");
                String url = rs.getString("url");

                CkanResource ckanResource = new CkanResource.CkanResourceBuilder("", p_package_id)
                        .withDescription(description)
                        .withFormat(format)
                        .withName(name)
                        .withCreated(created)
                        .withLastModified(last_modified)
                        .withUrl(url)
                        .build();

                resources.add(ckanResource);

                if (!previousPackageId.equals(p_package_id)) {
                    datasets.put(ckanPackage, resources);
                    resources = new ArrayList<>();
                }

                previousPackageId = rs.getString("package_id");
                counter++;
            }
            rs.close();
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
        }//end try

        return datasets;
    }

    private void executeSql(String sql) {
        Connection conn = null;
        Statement stmt = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
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
