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
    private HttpService mHttpService;
    private String mBaseUrl;
    private String mListPackageDetailsUrl;

    public CkanExtractor() {
        mDatabase = new MysqlDatabase();
    }

    public void extract(String baseUrl) {
        mBaseUrl = baseUrl;
        mListPackageDetailsUrl = baseUrl + "package_show?id=";
        String listPackagesUrl = baseUrl + "package_list";

        mGson = new Gson();
        mHttpService = new HttpService();
        mHttpService.sendRequest(this::extractDatasets, listPackagesUrl);
    }

    private void extractDatasets(String json) {
        System.out.println(json);
        CkanContent ckanContent = mGson.fromJson(json, CkanContent.class);
        System.out.println(ckanContent.getResult().length);

        List<String> ckanDatasets = Arrays.asList(ckanContent.getResult());
        ckanDatasets.forEach(dataset -> mHttpService.
                        sendRequest(this::extractDatasetDetails, (mListPackageDetailsUrl + dataset)));
    }

    private void extractDatasetDetails(String result) {
        // Get details from dataset
        JSONObject body = new JSONObject(result);
        // Dataset info
        JSONObject resultJson = body.getJSONObject("result");
        CkanPackage aPackage = mGson.fromJson(resultJson.toString(), CkanPackage.class);
        // Resource info
        JSONArray packageResource = resultJson.getJSONArray("resources");
        CkanResource[] resourcesCkan = mGson.fromJson(packageResource.toString(), CkanResource[].class);
        // Set origin URL
        aPackage.setOriginUrl(mBaseUrl);
        System.out.println(aPackage);
        mDatabase.savePackage(aPackage, resourcesCkan);
    }
}
