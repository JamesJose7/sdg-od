package com.jeeps.ckan_extractor.web.controller;

import com.jeeps.ckan_extractor.service.SdgOdLinkerService;
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
        model.addAttribute("action", "/admin/sdg-od-linker/begin");
        model.addAttribute("isLinkingRunning", SdgOdLinkerService.isLinkingRunning);
        return "ods/sdg-od-linker";
    }

    @RequestMapping(value = "/admin/sdg-od-linker/begin", method = RequestMethod.POST)
    public String beginLinking(RedirectAttributes redirectAttributes) {
        sdgOdLinkerService.beginLinking();
        return "redirect:/admin/sdg-od-linker";
    }
}
