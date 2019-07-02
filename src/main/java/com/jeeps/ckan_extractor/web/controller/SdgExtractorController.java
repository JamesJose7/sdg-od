package com.jeeps.ckan_extractor.web.controller;

import com.jeeps.ckan_extractor.service.SdgExtractorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SdgExtractorController {
    @Autowired
    private SdgExtractorService sdgExtractorService;

    @RequestMapping(value = "/admin/sdg-extractor")
    public String sdgExtractor(Model model) {
        model.addAttribute("isExtractionRunning", SdgExtractorService.isExtractionRunning);
        model.addAttribute("action", "/admin/sdg-extractor/begin");
        return "ods/sdg-extractor";
    }

    @RequestMapping(value = "/admin/sdg-extractor/begin", method = RequestMethod.POST)
    public String beginExtraction(RedirectAttributes redirectAttributes) {
        sdgExtractorService.beginExtraction();
        return "redirect:/admin/sdg-extractor";
    }
}
