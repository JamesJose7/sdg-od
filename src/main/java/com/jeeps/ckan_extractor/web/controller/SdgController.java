package com.jeeps.ckan_extractor.web.controller;

import com.jeeps.ckan_extractor.model.ConfigurationSingleton;
import com.jeeps.ckan_extractor.model.SdgRelatedDataset;
import com.jeeps.ckan_extractor.service.KnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class SdgController {
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @RequestMapping("/sdg/overview")
    public String sdgOverview(Model model) {
        model.addAttribute("sparqlEndpoint", ConfigurationSingleton.getInstance()
                .getConfigurationRegistry().getSparqlWebEndpoint());
        return "ods/ods-od-overview";
    }

    @RequestMapping("/sdg/{goal}")
    public String sdgPage(Model model, @PathVariable("goal") int goal) {
        if (goal > 17 || goal < 1)
            return "redirect:/sdg/overview";
        model.addAttribute("sdg", goal);
        return "ods/sdg_page";
    }

    @RequestMapping("/sdg/datasets")
    public String datasetsRelatedToOds(Model model) {
        List<SdgRelatedDataset> datasets = knowledgeBaseService.findAllCatalogsRelatedToOds();
        model.addAttribute("datasets", datasets);
        try {
            Map<String, Integer> datasetsPerOds = knowledgeBaseService.howManyDatasetsRelateToEachGoal();
            model.addAttribute("odsLabels", datasetsPerOds.keySet());
            model.addAttribute("datasetsCounts", datasetsPerOds.values());
        } catch (Exception e) {
            model.addAttribute("odsLabels", new ArrayList<String>());
            model.addAttribute("datasetsCounts", new ArrayList<Integer>());
        }
        return "ods/datasets-list";
    }
}
