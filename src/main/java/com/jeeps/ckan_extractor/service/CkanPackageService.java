package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.model.CkanPackage;

import java.util.Collection;
import java.util.List;

public interface CkanPackageService {
    Iterable<CkanPackage> findAll();
    CkanPackage findOne(Long id);
    void save(CkanPackage ckanPackage);
    List<String> getOriginUrls();
    Integer countDistinctByOriginUrl(String url);
    Collection<CkanPackage> findAllByOriginUrl(String url);
}
