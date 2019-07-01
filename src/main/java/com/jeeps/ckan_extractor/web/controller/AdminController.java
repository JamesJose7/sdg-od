package com.jeeps.ckan_extractor.web.controller;

import com.jeeps.ckan_extractor.model.ConfigurationRegistry;
import com.jeeps.ckan_extractor.model.ConfigurationSingleton;
import com.jeeps.ckan_extractor.service.ConfigurationRegistryService;
import com.jeeps.ckan_extractor.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {
    @Autowired
    private ConfigurationRegistryService configurationRegistryService;

    @RequestMapping(value = "/admin/configuration")
    public String configurationPanel(Model model) {
        model.addAttribute("action", "/admin/configuration/edit");
        model.addAttribute("configuration",
                ConfigurationSingleton.getInstance().getConfigurationRegistry());
        return "admin/configuration-panel";
    }

    @RequestMapping(value = "/admin/configuration/edit", method = RequestMethod.POST)
    public String updateConfigurationParameters(RedirectAttributes redirectAttributes,
                                                ConfigurationRegistry configurationRegistry) {
        configurationRegistryService.save(configurationRegistry);
        if (configurationRegistry != null)
            redirectAttributes.addFlashAttribute("flash",
                new FlashMessage("Configuration updated correctly", FlashMessage.Status.SUCCESS));
        else
            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage("There was a problem when saving the new configuration", FlashMessage.Status.FAILURE));
        return "redirect:/admin/configuration";
    }
}
