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
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping("/datasets")
    public String showDatasets(@PageableDefault(size = 30)Pageable pageable,
                               @RequestParam(name = "q", required = false) String q,
                               @RequestParam(name = "originUrl", required = false) String originUrl,
                               @RequestParam(name = "tag", required = false) String tag,
                               @RequestParam(name = "group", required = false) String group,
                               Model model) {
        q = q == null ? "" : q;
        Page<CkanPackage> page;
        model.addAttribute("selectedUrl", "");
        if (tag == null && group == null)
            if (originUrl == null || originUrl.equals("none")) page = ckanPackageService.findAllByTitleContaining(q, pageable);
            else {
                model.addAttribute("selectedUrl", originUrl);
                page = ckanPackageService.findAllByTitleContainsAndOriginUrlEquals(q, originUrl, pageable);
            }
        else if (group == null) page = ckanPackageService.findAllByPackageTagsEquals(tag, pageable);
        else page = ckanPackageService.findAllByPackageGroupsEquals(group, pageable);
        model.addAttribute("originUrls", ckanPackageService.getOriginUrls());
        model.addAttribute("page", page);
        model.addAttribute("action", "/datasets");
        model.addAttribute("q", q);
        return "ckanPackages/ckan-packages-list";
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    @ResponseBody
    public List<String> search(HttpServletRequest request) {
        return ckanPackageService.search(request.getParameter("term"));
    }

    @RequestMapping("/datasets/{package}")
    public String showDatasetInfo(@PathVariable("package") String packageName,
                                  Model model) {
        CkanPackage ckanPackage = ckanPackageService.findByName(packageName);
        model.addAttribute("package", ckanPackage);
        return "ckanPackages/ckan-package";
    }
}
