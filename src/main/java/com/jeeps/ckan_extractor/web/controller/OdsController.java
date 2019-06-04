package com.jeeps.ckan_extractor.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class OdsController {

    @RequestMapping("/ods/overview")
    public String odsOverview(Model model) {
        return "/ods/ods-od-overview";
    }
}
