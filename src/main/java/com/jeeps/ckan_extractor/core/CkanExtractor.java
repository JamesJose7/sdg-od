package com.jeeps.ckan_extractor.core;

import com.google.gson.*;
import com.jeeps.ckan_extractor.model.CkanContent;
import com.jeeps.ckan_extractor.model.CkanPackage;
import com.jeeps.ckan_extractor.model.CkanResource;
import com.jeeps.ckan_extractor.service.CkanPackageService;
import com.jeeps.ckan_extractor.service.HttpService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CkanExtractor {
    private final CkanPackageService ckanPackageService;

    public static final int MAX_SIZE = 100;
    private Gson mGson;
    private HttpService mHttpService;
    private String mBaseUrl;
    private String mListPackageDetailsUrl;
    private Logger logger = LoggerFactory.getLogger(CkanExtractor.class);

    public CkanExtractor(CkanPackageService ckanPackageService) {
        mGson = new GsonBuilder()
                .addSerializationExclusionStrategy(getGsonStrategy())
                .create();
        mHttpService = new HttpService();
        this.ckanPackageService = ckanPackageService;
    }

    private ExclusionStrategy getGsonStrategy() {
        // Ignore database ID fields on JSON deserialization
        return new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getName().equals("idDB");
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        };
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
        ckanDatasets = findMissingDatasets(ckanDatasets);
        ckanDatasets.parallelStream()
//                .limit(MAX_SIZE)
                .forEach(dataset -> mHttpService.
                        sendRequest(this::extractDatasetDetails, (mListPackageDetailsUrl + dataset)));
    }

    private void extractDataSetsByPost(String json) {
        List<String> ckanDatasets = parseCkanContent(json);
        ckanDatasets.parallelStream()
//                .limit(MAX_SIZE)
                .forEach(dataset -> mHttpService.
                        sendPostRequest(this::extractDatasetDetails, (mListPackageDetailsUrl), String.format("{\"id\": \"%s\"}", dataset)));
    }

    private List<String> findMissingDatasets(List<String> ckanDatasets) {
        List<String> currentlySavedNames = ckanPackageService.findIdsByOriginUrl(mBaseUrl.split("api")[0]);
        List<String> missingNames = ckanDatasets.stream()
                .filter(name -> !currentlySavedNames.contains(name))
                .collect(Collectors.toCollection(ArrayList::new));
        return missingNames;
    }

    private List<String> parseCkanContent(String json) {
        CkanContent ckanContent = mGson.fromJson(json, CkanContent.class);
        logger.info(String.format("Packages found in %s: %d", mBaseUrl, ckanContent.getResult().length));
        return Arrays.asList(ckanContent.getResult());
    }

    private void extractDatasetDetails(String result) {
        // Get details from dataset
        try {
            JSONObject body = new JSONObject(result);

            // Dataset info
            JSONObject resultJson = body.getJSONObject("result");
            CkanPackage aPackage;
            try {
                aPackage = mGson.fromJson(resultJson.toString(), CkanPackage.class);
                // Extract tags and groups
                aPackage.setPackageTags(extractTagsOrGroupsFromJson(resultJson, "tags"));
                aPackage.setPackageGroups(extractTagsOrGroupsFromJson(resultJson, "groups"));
            } catch (JsonSyntaxException e) {
                logger.info("--> Complex json");
                aPackage = buildComplexPackage(resultJson);
            }
            // Resource info
            JSONArray packageResource = resultJson.getJSONArray("resources");
            CkanResource[] resourcesCkan;
            try {
                resourcesCkan = mGson.fromJson(packageResource.toString(), CkanResource[].class);
            } catch (JsonSyntaxException e) {
                resourcesCkan = buildComplexResources(packageResource);
            }
            // Set origin URL
            aPackage.setOriginUrl(mBaseUrl.split("api")[0]);
            logger.debug(aPackage.toString());

            // Set resources parent to store on DB
            List<CkanResource> resources = Arrays.asList(resourcesCkan);
            CkanPackage finalAPackage = aPackage;
            resources.forEach(resource -> resource.setCkanPackage(finalAPackage));
            aPackage.setResources(resources);

            ckanPackageService.save(aPackage);
        } catch (JSONException e) {
            logger.debug("Could not extract dataset details");
            e.printStackTrace();
        }
    }

    private List<String> extractTagsOrGroupsFromJson(JSONObject resultJson, String type) {
        List<String> types = new ArrayList<>();
        if (!resultJson.has(type)) return types;
        JSONArray typesArray = resultJson.getJSONArray(type);
        for (int i = 0; i < typesArray.length(); i++) {
            types.add(typesArray.getJSONObject(i).optString("display_name"));
        }
        return types;
    }

    private CkanPackage buildComplexPackage(JSONObject resultJson) {
        // Get complex attributes
        Optional title = resultJson.getJSONObject("title").toMap().values().stream()
                .filter(o -> !((String) o).isEmpty())
                .findFirst();

        Optional description = resultJson.getJSONObject("description").toMap().values().stream()
                .filter(o -> !((String) o).isEmpty())
                .findFirst();

        // Transform Groups and Tags from org.json to gson.json
        JSONArray groups = resultJson.getJSONArray("groups");
        JsonArray transformedGroups = orgToGson(groups);
        List<String> packageGroups = new ArrayList<>();
        transformedGroups.forEach(group -> {
            if (group.getAsJsonObject().get("display_name") != null) {
                String groupName = group.getAsJsonObject().get("display_name").getAsString();
                packageGroups.add(groupName);
            }
        });

        JSONArray tags = resultJson.getJSONArray("tags");
        JsonArray transformedTags = new JsonArray();
        List<String> packageTags = new ArrayList<>();
        try {
            transformedTags = orgToGson(tags);
            transformedTags.forEach(tag -> {
                String tagName = tag.getAsJsonObject().get("display_name").getAsString();
                packageTags.add(tagName);
            });
        } catch (NullPointerException e) {
            logger.debug("cant find tags");
        }

        JSONObject organization = resultJson.getJSONObject("organization");
        JsonObject transformedOrg = new JsonObject();
        transformedOrg.addProperty("title", organization.getJSONObject("title").getString("en"));
        transformedOrg.addProperty("political_level", organization.getString("political_level"));
        transformedOrg.addProperty("state", organization.getString("state"));

        return new CkanPackage.CkanPackageBuilder(resultJson.optString("id"))
                .withTitle(title.isPresent() ? title.get().toString() : "No title")
                .withName(resultJson.optString("name"))
                .withLicense(resultJson.optString("license_title"))
                .withMetadataCreated(resultJson.optString("metadata_created"))
                .withMetadataModified(resultJson.optString("metadata_modified"))
                .withAuthor(resultJson.optString("author"))
                .withNotes(resultJson.optString("notes"))
                .withType(resultJson.optString("type"))
                .withIssued(resultJson.optString("issued"))
                .withVersion(resultJson.optString("version"))
                .withDescription(description.isPresent() ? description.get().toString() : "No description")
                .isPrivate(resultJson.optBoolean("private"))
                .withState(resultJson.optString("state"))
                .withModified(resultJson.optString("modified"))
                .withGropus(transformedGroups)
                .withTags(transformedTags)
                .withPackageTags(packageTags)
                .withPackageGroups(packageGroups)
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
           Optional name = resourceJson.getJSONObject("name").toMap().values().stream()
                    .filter(o -> !((String) o).isEmpty())
                    .findFirst();

            Optional description = resourceJson.getJSONObject("description").toMap().values().stream()
                    .filter(o -> !((String) o).isEmpty())
                    .findFirst();

            ckanResources.add(
                    new CkanResource.CkanResourceBuilder(resourceJson.optString("id"),
                            resourceJson.optString("package_id"))
                            .withDescription(description.isPresent() ? description.get().toString() : "No description")
                            .withName(name.isPresent() ? name.get().toString() : "No name")
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
