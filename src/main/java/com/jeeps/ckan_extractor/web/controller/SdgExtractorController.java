package com.jeeps.ckan_extractor.web.controller;

import com.jeeps.ckan_extractor.core.SdgSemanticCreator;
import com.jeeps.ckan_extractor.service.SdgExtractorService;
import com.jeeps.ckan_extractor.utils.FileUtils;
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
        model.addAttribute("isExtractionRunning", SdgExtractorService.isProcessRunning);
        model.addAttribute("actionStg1", "/admin/sdg-extractor/stage1");
        model.addAttribute("actionStg2", "/admin/sdg-extractor/stage2");
        model.addAttribute("actionStg3", "/admin/sdg-extractor/stage3");
        // Check if SKOS model exists
        model.addAttribute("modelExists", FileUtils.isFilePresent("rdf/" +
                SdgSemanticCreator.SDG_TRIPLES_FILE_NAME));
        return "ods/sdg-extractor";
    }

    @RequestMapping(value = "/admin/sdg-extractor/stage1", method = RequestMethod.POST)
    public String beginExtraction(RedirectAttributes redirectAttributes) {
        sdgExtractorService.extractAndTransformWithFRED();
        return "redirect:/admin/sdg-extractor";
    }

    @RequestMapping(value = "/admin/sdg-extractor/stage2", method = RequestMethod.POST)
    public String transformToSkos(RedirectAttributes redirectAttributes) {
        sdgExtractorService.transformFredIntoSKOS();
        return "redirect:/admin/sdg-extractor";
    }

    @RequestMapping(value = "/admin/sdg-extractor/stage3", method = RequestMethod.POST)
    public String uploadToVirtuoso(RedirectAttributes redirectAttributes) {
        sdgExtractorService.uploadModelToVirtuoso();
        return "redirect:/admin/sdg-extractor";
    }
}
