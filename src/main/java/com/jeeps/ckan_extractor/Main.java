package com.jeeps.ckan_extractor;

import com.jeeps.ckan_extractor.controller.CkanExtractor;

public class Main {
    public static void main(String[] args) {
        CkanExtractor ckanExtractor = new CkanExtractor();
        ckanExtractor.extract("http://ambar.utpl.edu.ec/api/action/");

        /*MysqlDatabase db = new MysqlDatabase();
        CkanPackage ckanPackage = new CkanPackage();
        ckanPackage.setTitle("jj");
        ckanPackage.setMetadata_modified("");
        ckanPackage.setMetadata_created("");
        ckanPackage.setLicense_title("");
        ckanPackage.setName("");
        ckanPackage.setAuthor("");
        db.savePackage(ckanPackage);*/
    }
}
