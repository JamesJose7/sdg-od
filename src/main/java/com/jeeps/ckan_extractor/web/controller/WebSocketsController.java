package com.jeeps.ckan_extractor.web.controller;

import com.jeeps.ckan_extractor.model.CkanPackage;
import com.jeeps.ckan_extractor.model.stomp.CkanUrlsStomp;
import com.jeeps.ckan_extractor.model.stomp.WebSocketResult;
import com.jeeps.ckan_extractor.service.CkanPackageService;
import com.jeeps.ckan_extractor.service.SemanticCreatorService;
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

        List<CkanPackage> ckanPackageList = new ArrayList<>();
        ckanUrlsStomp.getCkanUrls().forEach(url -> {
            Collection<CkanPackage> ckanPackages = ckanPackageService.findAllByOriginUrl(url);
            ckanPackageList.addAll(ckanPackages);
        });

        semanticCreatorService.generateCkanTriples(ckanPackageList);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss-SSS");
        String fileName = dateFormat.format(new Date());
        try {
            semanticCreatorService.writeFile(fileName, ckanUrlsStomp.getFormat());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new WebSocketResult("http://localhost:8080/files/" +
                fileName + ckanUrlsStomp.getFormat().split("\\|")[1]);
    }
}
