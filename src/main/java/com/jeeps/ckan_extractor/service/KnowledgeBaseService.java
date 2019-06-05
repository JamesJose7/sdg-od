package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.model.SdgRelatedDataset;

import java.util.List;
import java.util.Map;

public interface KnowledgeBaseService {
    List<SdgRelatedDataset> findAllCatalogsRelatedToOds();
    Map<String, Integer> howManyDatasetsRelateToEachGoal();
}
