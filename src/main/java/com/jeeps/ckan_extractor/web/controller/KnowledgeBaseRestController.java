package com.jeeps.ckan_extractor.web.controller;

import com.jeeps.ckan_extractor.model.SdgRelatedDataset;
import com.jeeps.ckan_extractor.service.KnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class KnowledgeBaseRestController {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @RequestMapping(value = "/api/sdg/datasets", method = RequestMethod.GET)
    public List<SdgRelatedDataset> getDatasetsRelatedToOds() {
        return  knowledgeBaseService.findAllCatalogsRelatedToOds();
    }
}
