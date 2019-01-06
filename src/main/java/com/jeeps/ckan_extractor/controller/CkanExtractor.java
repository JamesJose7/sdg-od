package com.jeeps.ckan_extractor.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jeeps.ckan_extractor.config.MysqlDatabase;
import com.jeeps.ckan_extractor.model.CkanContent;
import com.jeeps.ckan_extractor.model.CkanPackage;
import com.jeeps.ckan_extractor.model.CkanResource;
import com.jeeps.ckan_extractor.service.HttpService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        ckanDatasets.parallelStream().forEach(dataset -> mHttpService.
                        sendRequest(this::extractDatasetDetails, (mListPackageDetailsUrl + dataset)));
    }

    private void extractDatasetDetails(String result) {
        // Get details from dataset
        JSONObject body = new JSONObject(result);
        // Dataset info
        JSONObject resultJson = body.getJSONObject("result");
        CkanPackage aPackage = null;
        try {
            aPackage = mGson.fromJson(resultJson.toString(), CkanPackage.class);
        } catch (JsonSyntaxException e) {
            System.out.println("-->Complex json");
            aPackage = buildComplexPackage(resultJson);
        }
        // Resource info
        JSONArray packageResource = resultJson.getJSONArray("resources");
        CkanResource[] resourcesCkan = null;
        try {
            resourcesCkan = mGson.fromJson(packageResource.toString(), CkanResource[].class);
        } catch (JsonSyntaxException e) {
            resourcesCkan = buildComplexResources(packageResource);
        }
        // Set origin URL
        aPackage.setOriginUrl(mBaseUrl);
        System.out.println(aPackage);
        mDatabase.savePackage(aPackage, resourcesCkan);
    }

    private CkanPackage buildComplexPackage(JSONObject resultJson) {
        // Get complex attributes
        Optional title = resultJson.getJSONObject("title").toMap().values().stream().findFirst();

        return new CkanPackage.CkanPackageBuilder(resultJson.optString("id"))
                .withTitle(title.isPresent() ? title.get().toString() : "")
                .withName(resultJson.optString("name"))
                .withLicense(resultJson.optString("license_title"))
                .withMetadataCreated(resultJson.optString("metadata_created"))
                .withMetadataModified(resultJson.optString("metadata_modified"))
                .withAuthor(resultJson.optString("author"))
                .withNotes(resultJson.optString("notes"))
                .withType(resultJson.optString("type"))
                .build();
    }

    private CkanResource[] buildComplexResources(JSONArray packageResources) {
        List<CkanResource> ckanResources = new ArrayList<>();
        for (int i = 0; i < packageResources.length(); i++) {
            JSONObject resourceJson = packageResources.getJSONObject(i);
            // Get complex attributes
            Optional description = resourceJson.getJSONObject("description").toMap().values().stream().findFirst();
            Optional name = resourceJson.getJSONObject("name").toMap().values().stream().findFirst();

            ckanResources.add(
                    new CkanResource.CkanResourceBuilder(resourceJson.optString("id"),
                            resourceJson.optString("package_id"))
                            .withDescription(description.isPresent() ? description.get().toString() : "")
                            .withName(name.isPresent() ? name.get().toString() : "")
                            .withFormat(resourceJson.optString("format"))
                            .withCreated(resourceJson.optString("created"))
                            .withLastModified(resourceJson.optString("last_modified"))
                            .withUrl(resourceJson.optString("url"))
                            .build());
        }

        // Convert list to array
        CkanResource[] ckanResourcesArray = new CkanResource[ckanResources.size()];
        ckanResourcesArray = ckanResources.toArray(ckanResourcesArray);
        return ckanResourcesArray;
    }
}
