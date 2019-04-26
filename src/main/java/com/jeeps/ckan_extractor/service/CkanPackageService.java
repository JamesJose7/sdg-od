package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.model.CkanPackage;

public interface CkanPackageService {
    Iterable<CkanPackage> findAll();
    CkanPackage findOne(Long id);
    void save(CkanPackage ckanPackage);
}
