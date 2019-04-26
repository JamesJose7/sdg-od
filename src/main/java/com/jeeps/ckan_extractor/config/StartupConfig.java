package com.jeeps.ckan_extractor.config;

import com.jeeps.ckan_extractor.core.CkanExtractor;
import com.jeeps.ckan_extractor.service.CkanPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;

import static com.jeeps.ckan_extractor.core.CkanSemanticCreator.CURRENT_COUNTRY;

@Component
public class StartupConfig {
    @Autowired
    private CkanPackageService ckanPackageService;

    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshedEvent() {
        Instant start = Instant.now();

        try {
            CkanExtractor ckanExtractor = new CkanExtractor(ckanPackageService);

//        CURRENT_COUNTRY = "Ecuador";
            ckanExtractor.extract("http://ambar.utpl.edu.ec/api/action/");

//        CURRENT_COUNTRY = "";
            ckanExtractor.extractByPost("http://data.europa.eu/euodp/data/api/3/action/");

//        CURRENT_COUNTRY = "Netherlands";
            ckanExtractor.extract("https://data.humdata.org/api/3/action/");

            CURRENT_COUNTRY = "Switzerland";
            ckanExtractor.extract("https://opendata.swiss/api/3/action/");

//        CURRENT_COUNTRY = "Australia";
            ckanExtractor.extract("https://data.gov.au/api/3/action/");

            ckanExtractor.writeFile();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.printf("Elapsed time:\n" +
                "millis: %d\n" +
                "seconds: %d\n", timeElapsed, (timeElapsed / 1000));
    }
}
