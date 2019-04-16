package com.jeeps.ckan_extractor.core;

import com.google.gson.Gson;
import com.jeeps.ckan_extractor.model.SdgTarget;
import com.jeeps.ckan_extractor.model.SustainableGoal;
import com.jeeps.ckan_extractor.service.HttpService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

public class SdgExtractor {
    private final String BASE_URL = "https://unstats.un.org/SDGAPI/v1/sdg/Goal/";

    private final Gson gson;
    private final HttpService httpService;
    private SustainableGoal[] sustainableGoals;
    private int counter;

    public SdgExtractor() {
        gson = new Gson();
        httpService = new HttpService();
        counter = 0;
    }

    public void extract() {
        String sdgGoalsListUrl = BASE_URL + "List?includechildren=false";
        httpService.sendRequest(this::extractGoals, sdgGoalsListUrl);
    }

    private void extractGoals(String json) {
        String goalDetailsUrl = "/Target/List?includechildren=true";
        sustainableGoals = gson.fromJson(json, SustainableGoal[].class);
        Arrays.asList(sustainableGoals).forEach(goal ->
            httpService.sendRequest(this::extractGoalDetails, BASE_URL + goal.getCode() + goalDetailsUrl));

        // Analyze with FRED
        /*FredService fredService = new FredService();
        Arrays.asList(sustainableGoals).forEach(goal -> {
            fredService.fredActivate(goal.getTitle());
        });*/
    }

    private void extractGoalDetails(String json) {
        JSONObject goalJson = (new JSONArray(json)).getJSONObject(0);
        JSONArray targetsJson = goalJson.getJSONArray("targets");
        // Build targets array
        SdgTarget[] sdgTargets = gson.fromJson(targetsJson.toString(), SdgTarget[].class);
        sustainableGoals[counter].setTargets(Arrays.asList(sdgTargets));
        Arrays.asList(sdgTargets).forEach(target -> System.out.println("Extracted target: " + target.getCode()));
        counter++;
    }
}
