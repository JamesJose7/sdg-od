package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.dao.KnowledgeBaseDao;
import com.jeeps.ckan_extractor.model.SdgConceptScheme;
import com.jeeps.ckan_extractor.model.SdgConceptTree;
import com.jeeps.ckan_extractor.model.SdgRelatedDataset;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {
    @Autowired
    private KnowledgeBaseDao knowledgeBaseDao;

    @Override
    public List<SdgRelatedDataset> findAllCatalogsRelatedToOds() {
        return knowledgeBaseDao.findAllCatalogsRelatedToOds();
    }

    @Override
    public Map<String, Integer> howManyDatasetsRelateToEachGoal() {
        return knowledgeBaseDao.howManyDatasetsRelateToEachGoal();
    }

    @Override
    public List<SdgRelatedDataset> getRelatedOdsByDatasetId(Long id) {
        return knowledgeBaseDao.getRelatedOdsByDatasetId(id);
    }

    @Override
    public SdgConceptScheme getSdgConcepts(int sdg) {
        return knowledgeBaseDao.getSdgConcepts(sdg);
    }

    @Override
    public SdgConceptTree getSdgConceptTree(int sdg) {
        return knowledgeBaseDao.getSdgConceptTree(sdg);
    }

    @Override
    public void uploadCatalogsModel(Model model) {
        knowledgeBaseDao.uploadCatalogModel(model);
    }

    @Override
    public void uploadSdgModel(Model model) {
        knowledgeBaseDao.uploadSdgModel(model);
    }

    @Override
    public void uploadSdgOdLinks(Model model) {
        knowledgeBaseDao.uploadSdgOdLinks(model);
    }
}
