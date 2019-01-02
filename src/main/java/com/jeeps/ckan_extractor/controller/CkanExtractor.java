package com.jeeps.ckan_extractor.controller;

import com.google.gson.Gson;
import com.jeeps.ckan_extractor.config.MysqlDatabase;
import com.jeeps.ckan_extractor.model.CkanContent;
import com.jeeps.ckan_extractor.model.CkanPackage;
import com.jeeps.ckan_extractor.model.CkanResource;
import com.jeeps.ckan_extractor.service.HttpService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class CkanExtractor {

    private Gson mGson;
    private MysqlDatabase mDatabase;

    public CkanExtractor() {
        mDatabase = new MysqlDatabase();
    }

    public void extract(String baseUrl) {
        String listPackagesUrl = baseUrl + "package_list";
        String listPackageDetailsUrl = baseUrl + "package_show?id=";

        mGson = new Gson();
        HttpService httpService = new HttpService();
        httpService.sendRequest(s -> {
            System.out.println(s);
            CkanContent ckanContent = mGson.fromJson(s, CkanContent.class);
            System.out.println(ckanContent.getResult().length);

            List<String> ckanDatasets = Arrays.asList(ckanContent.getResult());
            ckanDatasets.forEach(dataset -> {
                // Get details from dataset
                httpService.sendRequest(result -> {
                    JSONObject body = new JSONObject(result);
                    // Dataset info
                    JSONObject resultJson = body.getJSONObject("result");
                    CkanPackage aPackage = mGson.fromJson(resultJson.toString(), CkanPackage.class);
                    // Resource info
                    JSONArray packageResource = resultJson.getJSONArray("resources");
                    CkanResource[] resourcesCkan = mGson.fromJson(packageResource.toString(), CkanResource[].class);
                    // Set origin URL
                    aPackage.setOriginUrl(baseUrl);
                    if (aPackage != null) {
                        System.out.println(aPackage);
                        mDatabase.savePackage(aPackage, resourcesCkan);
                    }
                }, (listPackageDetailsUrl + dataset));
            });
        }, listPackagesUrl);
    }
}
