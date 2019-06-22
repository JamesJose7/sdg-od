package com.jeeps.ckan_extractor.web.controller;

import com.jeeps.ckan_extractor.service.CkanPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.jeeps.ckan_extractor.core.CkanSemanticCreator.SERIALIZATION_FORMATS;

@Controller
public class ToolsController {
    @Autowired
    private CkanPackageService ckanPackageService;

    @RequestMapping("/tools/converter")
    public String rdfConverter(Model model) {
        List<String> originUrls = ckanPackageService.getOriginUrls();
        model.addAttribute("ckanUrls", originUrls);

        // Packages count
        List<Integer> ckanPackageCount = new ArrayList<>();
        originUrls.forEach(url -> {
            Integer originUrlCount = ckanPackageService.countDistinctByOriginUrl(url);
            ckanPackageCount.add(originUrlCount);
        });
        model.addAttribute("urlCount", ckanPackageCount);

        // Serialization formats
        model.addAttribute("formats", SERIALIZATION_FORMATS);

        // Create a random socket for each client
        Random random = new Random();
        String socketUri = "socket-" + random.nextInt(10000);
        model.addAttribute("socketUri", socketUri);
        return "converter";
    }
}
