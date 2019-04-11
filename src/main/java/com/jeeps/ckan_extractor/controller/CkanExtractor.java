package com.jeeps.ckan_extractor.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.jeeps.ckan_extractor.config.MysqlDatabase;
import com.jeeps.ckan_extractor.model.CkanContent;
import com.jeeps.ckan_extractor.model.CkanPackage;
import com.jeeps.ckan_extractor.model.CkanResource;
import com.jeeps.ckan_extractor.service.HttpService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CkanExtractor {

    public static final int MAX_SIZE = 100;
    private Gson mGson;
    private MysqlDatabase mDatabase;
    private SemanticCreator mSemanticCreator;
    private HttpService mHttpService;
    private String mBaseUrl;
    private String mListPackageDetailsUrl;

    public CkanExtractor() throws FileNotFoundException {
//        mDatabase = new MysqlDatabase();
        mSemanticCreator = new SemanticCreator();
        mGson = new Gson();
        mHttpService = new HttpService();
    }

    public void extract(String baseUrl) {
        mBaseUrl = baseUrl;
        mListPackageDetailsUrl = baseUrl + "package_show?id=";
        String listPackagesUrl = baseUrl + "package_list";

        mHttpService.sendRequest(this::extractDatasets, listPackagesUrl);
    }

    public void extractByPost(String baseUrl) {
        mBaseUrl = baseUrl;
        mListPackageDetailsUrl = baseUrl + "package_show";
        String listPackagesUrl = baseUrl + "package_list";

        mHttpService.sendRequest(this::extractDataSetsByPost, listPackagesUrl);
    }

    private void extractDatasets(String json) {
        List<String> ckanDatasets = parseCkanContent(json);
        ckanDatasets.parallelStream()
//                .limit(MAX_SIZE)
                .forEach(dataset -> mHttpService.
                        sendRequest(this::extractDatasetDetails, (mListPackageDetailsUrl + dataset)));
//        mSemanticCreator.writeRdfFile();
    }

    private void extractDataSetsByPost(String json) {
        List<String> ckanDatasets = parseCkanContent(json);
        ckanDatasets.parallelStream()
//                .limit(MAX_SIZE)
                .forEach(dataset -> mHttpService.
                        sendPostRequest(this::extractDatasetDetails, (mListPackageDetailsUrl), String.format("{\"id\": \"%s\"}", dataset)));
//        mSemanticCreator.writeRdfFile();
    }

    public void writeFile() {
        mSemanticCreator.writeRdfFile();
    }

    private List<String> parseCkanContent(String json) {
        System.out.println(json);
        CkanContent ckanContent = mGson.fromJson(json, CkanContent.class);
        System.out.println(ckanContent.getResult().length);
        return Arrays.asList(ckanContent.getResult());
    }

    private void extractDatasetDetails(String result) {
        // Get details from dataset
        try {
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
            aPackage.setOriginUrl(mBaseUrl.split("api")[0]);
            System.out.println(aPackage);
            mSemanticCreator.generateTriples(aPackage, resourcesCkan);
//            mDatabase.savePackage(aPackage, resourcesCkan);
        } catch (JSONException e) {}
    }

    private CkanPackage buildComplexPackage(JSONObject resultJson) {
        // Get complex attributes
        Optional title = resultJson.getJSONObject("title").toMap().entrySet().stream()
                .filter(e -> e.getKey().equals("en"))
                .map(e -> e.getValue().toString())
                .findFirst();

        Optional description = resultJson.getJSONObject("description").toMap().entrySet().stream()
                .filter(e -> e.getKey().equals("en"))
                .map(e -> e.getValue().toString())
                .findFirst();

        // Transform Groups and Tags from org.json to gson.json
        JSONArray groups = resultJson.getJSONArray("groups");
        JsonArray transformedGroups = orgToGson(groups);

        JSONArray tags = resultJson.getJSONArray("tags");
        JsonArray transformedTags = new JsonArray();
        try {
            transformedTags = orgToGson(tags);
        } catch (NullPointerException e) {
            System.out.println("cant find tags");
        }

        JSONObject organization = resultJson.getJSONObject("organization");
        JsonObject transformedOrg = new JsonObject();
        transformedOrg.addProperty("title", organization.getJSONObject("title").getString("en"));
        transformedOrg.addProperty("political_level", organization.getString("political_level"));
        transformedOrg.addProperty("state", organization.getString("state"));

        return new CkanPackage.CkanPackageBuilder(resultJson.optString("id"))
                .withTitle(title.isPresent() ? title.get().toString() : resultJson.getJSONObject("title").toMap().values().stream().findFirst().orElse("").toString())
                .withName(resultJson.optString("name"))
                .withLicense(resultJson.optString("license_title"))
                .withMetadataCreated(resultJson.optString("metadata_created"))
                .withMetadataModified(resultJson.optString("metadata_modified"))
                .withAuthor(resultJson.optString("author"))
                .withNotes(resultJson.optString("notes"))
                .withType(resultJson.optString("type"))
                .withIssued(resultJson.optString("issued"))
                .withVersion(resultJson.optString("version"))
                .withDescription(description.isPresent() ? description.get().toString() : resultJson.getJSONObject("description").toMap().values().stream().findFirst().orElse("").toString())
                .isPrivate(resultJson.optBoolean("private"))
                .withState(resultJson.optString("state"))
                .withModified(resultJson.optString("modified"))
                .withGropus(transformedGroups)
                .withTags(transformedTags)
                .withOrganization(transformedOrg)
                .build();
    }

    private JsonArray orgToGson(JSONArray groups) {
        JsonArray transformedGson = new JsonArray();
        if (groups == null)
            return transformedGson;
        for (int i = 0; i < groups.length(); i++) {
            String groupName = groups.getJSONObject(i).optJSONObject("display_name").optString("en");
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("display_name", groupName);
            transformedGson.add(jsonObject);
        }
        return transformedGson;
    }

    private CkanResource[] buildComplexResources(JSONArray packageResources) {
        List<CkanResource> ckanResources = new ArrayList<>();
        for (int i = 0; i < packageResources.length(); i++) {
            JSONObject resourceJson = packageResources.getJSONObject(i);
            // Get complex attributes
            Optional name = resourceJson.getJSONObject("name").toMap().entrySet().stream()
                    .filter(e -> e.getKey().equals("en"))
                    .map(e -> e.getValue().toString())
                    .findFirst();
            Optional description = resourceJson.getJSONObject("description").toMap().entrySet().stream()
                    .filter(e -> e.getKey().equals("en"))
                    .map(e -> e.getValue().toString())
                    .findFirst();

            ckanResources.add(
                    new CkanResource.CkanResourceBuilder(resourceJson.optString("id"),
                            resourceJson.optString("package_id"))
                            .withDescription(description.isPresent() ? description.get().toString() : "")
                            .withName(name.isPresent() ? name.get().toString() : "")
                            .withFormat(resourceJson.optString("format"))
                            .withCreated(resourceJson.optString("created"))
                            .withLastModified(resourceJson.optString("last_modified"))
                            .withLastModified(resourceJson.optString("modified"))
                            .withLastModified(resourceJson.optString("state"))
                            .withLastModified(resourceJson.optString("license"))
                            .withUrl(resourceJson.optString("url"))
                            .build());
        }

        // Convert list to array
        CkanResource[] ckanResourcesArray = new CkanResource[ckanResources.size()];
        ckanResourcesArray = ckanResources.toArray(ckanResourcesArray);
        return ckanResourcesArray;
    }
}
