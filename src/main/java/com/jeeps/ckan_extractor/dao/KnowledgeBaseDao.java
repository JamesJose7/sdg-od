package com.jeeps.ckan_extractor.dao;

import com.jeeps.ckan_extractor.model.SdgRelatedDataset;

import java.util.List;
import java.util.Map;

public interface KnowledgeBaseDao {
    List<SdgRelatedDataset> findAllCatalogsRelatedToOds();
    Map<String, Integer> howManyDatasetsRelateToEachGoal();
}
