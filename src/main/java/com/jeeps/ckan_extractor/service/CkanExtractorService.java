package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.core.CkanExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class CkanExtractorService {
    @Autowired
    private CkanPackageService ckanPackageService;

    private final List<String> ckanUrls = Arrays.asList(
            "http://ambar.utpl.edu.ec/api/3/action/",
            "http://data.europa.eu/euodp/data/api/3/action/",
            "https://data.humdata.org/api/3/action/",
            "https://opendata.swiss/api/3/action/",
            "https://data.gov.au/api/3/action/"
    );

    public List<String> getCkanUrls() {
        return ckanUrls;
    }

    public void beginExtraction(String url) {
        // Delete previously saved resources
        ckanPackageService.deleteAllByOriginUrl(url.split("api")[0]);
        // Begin extraction
        CkanExtractor ckanExtractor = new CkanExtractor(ckanPackageService);
        ckanExtractor.extract(url);
    }
}
