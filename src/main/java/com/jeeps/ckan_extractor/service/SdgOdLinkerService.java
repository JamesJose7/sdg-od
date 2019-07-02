package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.core.CkanExtractor;
import com.jeeps.ckan_extractor.core.OdsOdLinker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SdgOdLinkerService {
    private Logger logger = LoggerFactory.getLogger(CkanExtractor.class);

    public static volatile boolean isLinkingRunning = false;

    @Async("asyncExecutor")
    public void beginLinking() {
        logger.info("Began async linking between SDG and OD");
        if (!isLinkingRunning) {
            isLinkingRunning = true;
            try {
                OdsOdLinker odsOdLinker = new OdsOdLinker();
                odsOdLinker.annotateOdAndOds();
                odsOdLinker.writeRdfFile();
            } catch (Exception e) {
                isLinkingRunning = false;
                logger.info("There was a problem when linking SDG and OD");
                e.printStackTrace();
            }
            isLinkingRunning = false;
        }
        logger.info("Finished SDG and OD linking");
    }
}
