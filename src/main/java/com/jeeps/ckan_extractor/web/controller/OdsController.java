package com.jeeps.ckan_extractor.web.controller;

import com.jeeps.ckan_extractor.model.SdgRelatedDataset;
import com.jeeps.ckan_extractor.service.KnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jeeps.ckan_extractor.dao.KnowledgeBaseDaoImpl.SPARQL_ENDPOINT;

@Controller
public class OdsController {
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @RequestMapping("/sdg/overview")
    public String odsOverview(Model model) {
        model.addAttribute("sparqlEndpoint", SPARQL_ENDPOINT);
        try {
            Map<String, Integer> datasetsPerOds = knowledgeBaseService.howManyDatasetsRelateToEachGoal();
            model.addAttribute("odsLabels", datasetsPerOds.keySet());
            model.addAttribute("datasetsCounts", datasetsPerOds.values());
        } catch (Exception e) {
            model.addAttribute("odsLabels", new ArrayList<String>());
            model.addAttribute("datasetsCounts", new ArrayList<Integer>());
        }
        return "ods/ods-od-overview";
    }

    @RequestMapping("/sdg/datasets")
    public String datasetsRelatedToOds(Model model) {
        List<SdgRelatedDataset> datasets = knowledgeBaseService.findAllCatalogsRelatedToOds();
        model.addAttribute("datasets", datasets);
        return "ods/datasets-list";
    }
}
