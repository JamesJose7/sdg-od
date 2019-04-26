package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.model.CkanResource;

public interface CkanResourceService {
    Iterable<CkanResource> findAll();
    CkanResource findOne(Long id);
    void save(CkanResource ckanResource);
}
