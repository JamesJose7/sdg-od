package com.jeeps.ckan_extractor.web.controller;

import com.jeeps.ckan_extractor.core.OdsOdLinker;
import com.jeeps.ckan_extractor.service.SdgOdLinkerService;
import com.jeeps.ckan_extractor.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SdgOdLinkerController {
    @Autowired
    private SdgOdLinkerService sdgOdLinkerService;

    @RequestMapping(value = "/admin/sdg-od-linker")
    public String sdgOdLinker(Model model) {
        model.addAttribute("actionStg1", "/admin/sdg-od-linker/stage1");
        model.addAttribute("actionStg2", "/admin/sdg-od-linker/stage2");
        model.addAttribute("isLinkingRunning", SdgOdLinkerService.isLinkingRunning);
        model.addAttribute("rdfFile", OdsOdLinker.SDG_OD_LINKS_FILE_NAME);
        model.addAttribute("modelExists", FileUtils.isFilePresent("rdf/" +
                OdsOdLinker.SDG_OD_LINKS_FILE_NAME));
        return "ods/sdg-od-linker";
    }

    @RequestMapping(value = "/admin/sdg-od-linker/stage1", method = RequestMethod.POST)
    public String beginLinking(RedirectAttributes redirectAttributes) {
        sdgOdLinkerService.beginLinking();
        return "redirect:/admin/sdg-od-linker";
    }

    @RequestMapping(value = "/admin/sdg-od-linker/stage2", method = RequestMethod.POST)
    public String uploadTriplestore(RedirectAttributes redirectAttributes) {
        sdgOdLinkerService.uploadToTriplestore();
        return "redirect:/admin/sdg-od-linker";
    }
}
