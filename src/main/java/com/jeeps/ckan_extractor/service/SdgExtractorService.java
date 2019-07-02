package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.core.CkanExtractor;
import com.jeeps.ckan_extractor.core.SdgExtractor;
import com.jeeps.ckan_extractor.core.SdgSemanticCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SdgExtractorService {
    private Logger logger = LoggerFactory.getLogger(CkanExtractor.class);

    public static volatile boolean isExtractionRunning = false;

    @Async("asyncExecutor")
    public void beginExtraction() {
        logger.info("Began async extraction on SDG data and FRED transformation");
        if (!isExtractionRunning) {
            isExtractionRunning = true;
            try {
                // Extract SDG Data
                SdgExtractor sdgExtractor = new SdgExtractor();
                sdgExtractor.extract();
                // Generate triples from it
                SdgSemanticCreator sdgSemanticCreator = new SdgSemanticCreator();
                sdgSemanticCreator.generateTriples();
                sdgSemanticCreator.writeRdfFile();
            } catch (Exception e) {
                isExtractionRunning = false;
                logger.info("There was a problem when extracting and transforming the SDGs");
                e.printStackTrace();
            }
            isExtractionRunning = false;
        }
        logger.info("Finished SDG extraction and transformation");
    }
}
