package com.jeeps.ckan_extractor.controller;

import com.google.gson.Gson;
import com.jeeps.ckan_extractor.model.CkanContent;
import com.jeeps.ckan_extractor.service.HttpService;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class CkanExtractor {


    private Gson mGson;

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
                //Get details from dataset
                httpService.sendRequest(result -> {
                    JSONObject body = new JSONObject(result);
                    JSONObject resultJson = body.getJSONObject("result");
                    Package aPackage = mGson.fromJson(resultJson.toString(), Package.class);
                    System.out.println(resultJson.toString());
                }, (listPackageDetailsUrl + dataset));
            });
        }, listPackagesUrl);


    }
}
