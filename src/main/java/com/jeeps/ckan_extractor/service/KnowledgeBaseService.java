package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.model.SdgRelatedDataset;
import org.apache.jena.rdf.model.Model;

import java.util.List;
import java.util.Map;

public interface KnowledgeBaseService {
    List<SdgRelatedDataset> findAllCatalogsRelatedToOds();
    Map<String, Integer> howManyDatasetsRelateToEachGoal();
    List<SdgRelatedDataset> getRelatedOdsByDatasetId(Long id);
    void uploadCatalogsModel(Model model);
    void uploadSdgModel(Model model);
}
