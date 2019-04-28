package com.jeeps.ckan_extractor.web.controller;

import com.jeeps.ckan_extractor.model.CkanPackage;
import com.jeeps.ckan_extractor.model.stomp.CkanUrlsStomp;
import com.jeeps.ckan_extractor.model.stomp.WebSocketResult;
import com.jeeps.ckan_extractor.service.CkanPackageService;
import com.jeeps.ckan_extractor.service.SemanticCreatorService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @MessageMapping("/sendCkanUrls")
    @SendTo("/topic/transform-ckan")
    public WebSocketResult transformCkanToRdf(CkanUrlsStomp ckanUrlsStomp) throws Exception {

        /*if (ckanUrls == null) {
            // At least one checkbox should be selected
            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage("Please select at least one repository", FlashMessage.Status.FAILURE));
            return "redirect:/admin";
        }
        Arrays.stream(ckanUrls).forEach(System.out::println);*/

        List<CkanPackage> ckanPackageList = new ArrayList<>();
        ckanUrlsStomp.getCkanUrls().forEach(url -> {
            Collection<CkanPackage> ckanPackages = ckanPackageService.findAllByOriginUrl(url);
            ckanPackageList.addAll(ckanPackages);
        });

        semanticCreatorService.generateCkanTriples(ckanPackageList);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss-SSS");
        String fileName = dateFormat.format(new Date());
        try {
            semanticCreatorService.writeFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new WebSocketResult("http://localhost:8080/files/" + fileName + ".rdf");
    }
}
