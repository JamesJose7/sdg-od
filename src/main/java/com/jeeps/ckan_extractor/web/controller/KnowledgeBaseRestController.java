package com.jeeps.ckan_extractor.web.controller;

import com.jeeps.ckan_extractor.model.SdgConceptScheme;
import com.jeeps.ckan_extractor.model.SdgConceptTree;
import com.jeeps.ckan_extractor.model.SdgRelatedDataset;
import com.jeeps.ckan_extractor.service.KnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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

    @RequestMapping(value = "/api/sdg/concepts/{sdg_number}", method = RequestMethod.GET)
    public SdgConceptScheme getSdgConcepts(@PathVariable("sdg_number") int sdg) {
        return  knowledgeBaseService.getSdgConcepts(sdg);
    }

    @RequestMapping(value = "/api/sdg/concept-tree/{sdg_number}", method = RequestMethod.GET)
    public SdgConceptTree getSdgConceptTree(@PathVariable("sdg_number") int sdg) {
        return knowledgeBaseService.getSdgConceptTree(sdg);
    }
}
