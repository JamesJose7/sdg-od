package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.core.CkanExtractor;
import com.jeeps.ckan_extractor.core.OdsOdLinker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SdgOdLinkerService {
    private Logger logger = LoggerFactory.getLogger(CkanExtractor.class);

    public static volatile boolean isLinkingRunning = false;

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

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

    @Async("asyncExecutor")
    public void uploadToTriplestore() {
        logger.info("Began uploading SDG-OD links to virtuoso");
        if (!isLinkingRunning) {
            isLinkingRunning = true;
            try {
                OdsOdLinker odsOdLinker = new OdsOdLinker();
                odsOdLinker.loadTriples("rdf/" + OdsOdLinker.SDG_OD_LINKS_FILE_NAME);
                // Upload to virtuoso
                knowledgeBaseService.uploadCatalogsModel(odsOdLinker.getModel());
            } catch (Exception e) {
                isLinkingRunning = false;
                logger.info("There was a problem when uploading SDG-OD links");
                e.printStackTrace();
            }
            isLinkingRunning = false;
        }
        logger.info("Finished Uploading SDG-OD links");
    }
}
