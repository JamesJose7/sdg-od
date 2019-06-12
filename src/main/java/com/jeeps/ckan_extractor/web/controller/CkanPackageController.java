package com.jeeps.ckan_extractor.web.controller;

import com.jeeps.ckan_extractor.model.CkanPackage;
import com.jeeps.ckan_extractor.service.CkanPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CkanPackageController {
    @Autowired
    private CkanPackageService ckanPackageService;

    @RequestMapping("/datasets")
    public String showDatasets(@PageableDefault(size = 30)Pageable pageable,
                               @RequestParam(name = "q", required = false) String q,
                               @RequestParam(name = "originUrl", required = false) String originUrl,
                               @RequestParam(name = "filterType", required = false) String filterType,
                               @RequestParam(name = "filter", required = false) String filter,
                               Model model) {
        // Prevent nulls for less logic checks
        q = q == null ? "" : q;
        filter = filter == null ? "" : filter;
        originUrl = originUrl == null || originUrl.equals("none") ? "" : originUrl;
        Page<CkanPackage> page;

        // Filter according to parameters received
        if (filterType == null)
            page = ckanPackageService.findAllByTitleContainsAndOriginUrlContains(q, originUrl, pageable);
        else if (filterType.equals("tag"))
            page = ckanPackageService.findAllByTitleContainsAndOriginUrlContainsAndPackageTagsEquals(q, originUrl, filter, pageable);
        else if (filterType.equals("group"))
            page = ckanPackageService.findAllByTitleContainsAndOriginUrlContainsAndPackageGroupsEquals(q, originUrl, filter, pageable);
        else page = ckanPackageService.findAllByTitleContaining(q, pageable);

        model.addAttribute("originUrls", ckanPackageService.getOriginUrls());
        model.addAttribute("selectedUrl", originUrl);
        model.addAttribute("q", q);
        model.addAttribute("filterType", filterType);
        model.addAttribute("filter", filter);
        model.addAttribute("page", page);
        model.addAttribute("action", "/datasets");
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
