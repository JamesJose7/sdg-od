package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.core.CkanExtractor;
import com.jeeps.ckan_extractor.core.SdgExtractor;
import com.jeeps.ckan_extractor.core.SdgSemanticCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SdgExtractorService {
    private Logger logger = LoggerFactory.getLogger(CkanExtractor.class);

    public static volatile boolean isProcessRunning = false;

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @Async("asyncExecutor")
    public void extractAndTransformWithFRED() {
        logger.info("Began async extraction on SDG data and FRED transformation");
        if (!isProcessRunning) {
            isProcessRunning = true;
            try {
                // Extract SDG Data
                SdgExtractor sdgExtractor = new SdgExtractor();
                sdgExtractor.extract();
                logger.info("Finished SDG extraction and transformation");
            } catch (Exception e) {
                isProcessRunning = false;
                logger.info("There was a problem when extracting and transforming the SDGs");
                e.printStackTrace();
            }
            isProcessRunning = false;
        }
    }

//    @Async("asyncExecutor")
    public void transformFredIntoSKOS() {
        logger.info("Began FRED transformation into SKOS taxonomy");
        if (!isProcessRunning) {
            isProcessRunning = true;
            try {
                // Generate triples from it
                SdgSemanticCreator sdgSemanticCreator = new SdgSemanticCreator();
                sdgSemanticCreator.generateTriples();
                sdgSemanticCreator.writeRdfFile();
                logger.info("Finished FRED's transformation into SKOS");
            } catch (Exception e) {
                isProcessRunning = false;
                logger.info("There was a problem when transforming FRED's result into a SKOS taxonomy");
                e.printStackTrace();
            }
            isProcessRunning = false;
        }
    }

    @Async("asyncExecutor")
    public void uploadModelToVirtuoso() {
        logger.info("Began uploading SDG to Virtuoso");
        if (!isProcessRunning) {
            isProcessRunning = true;
            try {
                // Load generated model
                SdgSemanticCreator sdgSemanticCreator = new SdgSemanticCreator();
                sdgSemanticCreator.loadTriples("rdf/" + SdgSemanticCreator.SDG_TRIPLES_FILE_NAME);
                //Upload it
                knowledgeBaseService.uploadSdgModel(sdgSemanticCreator.getModel());
                logger.info("Finished Uploading model to Virtuoso");
            } catch (Exception e) {
                isProcessRunning = false;
                logger.info("There was a problem when uploading SDG model into Virtuoso");
                e.printStackTrace();
            }
            isProcessRunning = false;
        }
    }
}
