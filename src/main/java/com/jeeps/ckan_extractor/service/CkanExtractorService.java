package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.core.CkanExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class CkanExtractorService {
    @Autowired
    private CkanPackageService ckanPackageService;

    public void beginExtraction(String url) {
        // Delete previously saved resources
        ckanPackageService.deleteAllByOriginUrl(url.split("api")[0]);
        // Begin extraction
        CkanExtractor ckanExtractor = new CkanExtractor(ckanPackageService);
        ckanExtractor.extract(url);
    }
}
