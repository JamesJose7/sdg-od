package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.core.CkanExtractor;
import com.jeeps.ckan_extractor.model.ExtractionHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Transactional
public class CkanExtractorService {
    @Autowired
    private CkanPackageService ckanPackageService;
    @Autowired
    private ExtractionHistoryService extractionHistoryService;

    public void beginExtraction(String url) {
        // Get number of existing resources before extraction
        int existingResourcesCount = ckanPackageService.countAllByOriginUrl(url.split("api")[0]);
        // Begin extraction
        CkanExtractor ckanExtractor = new CkanExtractor(ckanPackageService);
        ckanExtractor.extract(url);
        // Amount of newly extracted resources
        Integer newTotal = ckanPackageService.countAllByOriginUrl(url.split("api")[0]);
        int newResourcesCount = newTotal - existingResourcesCount;
        // Save history based on extraction
        extractionHistoryService.save(new ExtractionHistory(new Date(), url, newResourcesCount,
                String.format("Extracted %d new datasets, current total is %d", newResourcesCount, newTotal)));
    }

    public void deleteDatasets(String url) {
        int existingResourcesCount = ckanPackageService.countAllByOriginUrl(url);
        // Delete previously saved resources
        ckanPackageService.deleteAllByOriginUrl(url.split("api")[0]);
        // Save history
        Integer newTotal = ckanPackageService.countAllByOriginUrl(url);
        int newResourcesCount = newTotal - existingResourcesCount;
        extractionHistoryService.save(new ExtractionHistory(new Date(), url, newResourcesCount,
                String.format("Deleted %d datasets", newResourcesCount)));
    }
}
