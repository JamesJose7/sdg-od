package com.jeeps.ckan_extractor.web.controller;

import com.jeeps.ckan_extractor.model.CkanPackage;
import com.jeeps.ckan_extractor.service.CkanExtractorService;
import com.jeeps.ckan_extractor.service.CkanPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CkanPackageController {
    @Autowired
    private CkanPackageService ckanPackageService;
    @Autowired
    private CkanExtractorService ckanExtractorService;

    @RequestMapping("/admin/datasets/extractor")
    public String transformer(Model model) {
        List<String> ckanUrls = ckanExtractorService.getCkanUrls();
        List<Boolean> availableRepos = new ArrayList<>();
        ckanUrls.forEach(url -> availableRepos.add(ckanPackageService.existsByOriginUrl(url.split("api")[0])));
        model.addAttribute("ckanUrls", ckanUrls);
        model.addAttribute("availableRepos", availableRepos);

        return "extractor";
    }

    @RequestMapping("/admin/datasets")
    public String showDatasets(@PageableDefault(size = 30)Pageable pageable,
                               @RequestParam(name = "q", required = false) String q,
                               Model model) {
        q = q == null ? "" : q;
        Page<CkanPackage> page = ckanPackageService.findAllByTitleContaining(q, pageable);
        model.addAttribute("page", page);
        model.addAttribute("action", "/admin/datasets");
        model.addAttribute("q", q);
        return "ckanPackages/ckan-packages-list";
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    @ResponseBody
    public List<String> search(HttpServletRequest request) {
        return ckanPackageService.search(request.getParameter("term"));
    }
}
