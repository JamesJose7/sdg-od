package com.jeeps.ckan_extractor.web.controller;

import com.jeeps.ckan_extractor.core.OdsOdLinker;
import com.jeeps.ckan_extractor.service.OdsOdStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Map;

@Controller
public class OdsController {
    @Autowired
    private OdsOdStatisticsService odsOdStatisticsService;

    @RequestMapping("/sdg/overview")
    public String odsOverview(Model model) {
        model.addAttribute("sparqlEndpoint", OdsOdLinker.SPARQL_ENDPOINT);
        try {
            Map<String, Integer> datasetsPerOds = odsOdStatisticsService.howManyDatasetsRelateToEachGoal();
            model.addAttribute("odsLabels", datasetsPerOds.keySet());
            model.addAttribute("datasetsCounts", datasetsPerOds.values());
        } catch (Exception e) {
            model.addAttribute("odsLabels", new ArrayList<String>());
            model.addAttribute("datasetsCounts", new ArrayList<Integer>());
        }
        return "/ods/ods-od-overview";
    }
}
