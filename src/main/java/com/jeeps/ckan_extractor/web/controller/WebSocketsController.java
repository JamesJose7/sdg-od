package com.jeeps.ckan_extractor.web.controller;

import com.jeeps.ckan_extractor.core.CkanSemanticCreator;
import com.jeeps.ckan_extractor.model.CkanPackage;
import com.jeeps.ckan_extractor.model.stomp.CkanUrlsStomp;
import com.jeeps.ckan_extractor.model.stomp.WebSocketResult;
import com.jeeps.ckan_extractor.service.CkanExtractorService;
import com.jeeps.ckan_extractor.service.CkanPackageService;
import com.jeeps.ckan_extractor.service.KnowledgeBaseService;
import com.jeeps.ckan_extractor.service.SemanticCreatorService;
import com.jeeps.ckan_extractor.utils.FileUtils;
import com.jeeps.ckan_extractor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Controller
public class WebSocketsController {
    @Autowired
    private CkanPackageService ckanPackageService;
    @Autowired
    private SemanticCreatorService semanticCreatorService;
    @Autowired
    private CkanExtractorService ckanExtractorService;
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    Logger logger = LoggerFactory.getLogger(WebSocketsController.class);

    @MessageMapping("/sendCkanUrls/{var}")
    @SendTo("/topic/transform-ckan/{var}")
    public WebSocketResult transformCkanToRdf(@DestinationVariable String var, CkanUrlsStomp ckanUrlsStomp) {
        // Log current socket
        logger.info("Socket connected on: " + var);
        if (ckanUrlsStomp.getCkanUrls().isEmpty()) {
            // At least one checkbox should be selected
            return new WebSocketResult("");
        }

        // File names for each repo
        List<String> fileNames = new ArrayList<>();
        // Get the package list for repos that haven't been transformed yet
        ckanUrlsStomp.getCkanUrls().forEach(url -> {
            String fileName = StringUtils.removeUrlProtocol(url).replaceAll("\\.", "-");
            fileNames.add(fileName);
            if (!FileUtils.isFilePresent("rdf/" + fileName + ".rdf")) {
                Collection<CkanPackage> ckanPackages = ckanPackageService.findAllByOriginUrl(url);

                // Generate missing triples files
                semanticCreatorService.createNewModel();
                semanticCreatorService.generateCkanTriples(ckanPackages);
                try {
                    semanticCreatorService.writeFile("rdf/", fileName, CkanSemanticCreator.RDF_XML);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Load existing models
        semanticCreatorService.createNewModel();
        fileNames.forEach(fileName -> semanticCreatorService.loadTriples("rdf/" + fileName + ".rdf"));

        // Upload if option selected
        if (ckanUrlsStomp.isUpload()) {
            knowledgeBaseService.uploadCatalogsModel(semanticCreatorService.getModel());
        }

        //Generate file name
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss-SSS");
        String fileName = dateFormat.format(new Date());
        try {
            semanticCreatorService.writeFile("temp/", fileName, ckanUrlsStomp.getFormat());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new WebSocketResult("/files/" +
                fileName + ckanUrlsStomp.getFormat().split("\\|")[1]);
    }

    @MessageMapping("/sendCkanApiUrls/")
    @SendTo("/topic/extract-ckan/")
    public WebSocketResult extractCkanData(CkanUrlsStomp ckanUrlsStomp) {
        if (ckanUrlsStomp.getCkanUrls().isEmpty()) {
            // At least one checkbox should be selected
            return new WebSocketResult("");
        }

        // Extract Datasets from each endpoint
        ckanUrlsStomp.getCkanUrls().forEach(url -> ckanExtractorService.beginExtraction(url));

        return new WebSocketResult("What");
    }
}
