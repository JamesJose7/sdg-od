package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.model.CkanRepository;

public interface CkanRepositoryService {
    void delete(CkanRepository ckanRepository);
    void save(CkanRepository ckanRepository);
    Iterable<CkanRepository> findAll();
    CkanRepository findByUrl(String url);
}
