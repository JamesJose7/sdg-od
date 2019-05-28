package com.jeeps.ckan_extractor.web.controller;

import com.jeeps.ckan_extractor.model.CkanPackage;
import com.jeeps.ckan_extractor.model.CkanRepository;
import com.jeeps.ckan_extractor.service.CkanExtractorService;
import com.jeeps.ckan_extractor.service.CkanPackageService;
import com.jeeps.ckan_extractor.service.CkanRepositoryService;
import com.jeeps.ckan_extractor.web.FlashMessage;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CkanPackageController {
    @Autowired
    private CkanPackageService ckanPackageService;
    @Autowired
    private CkanExtractorService ckanExtractorService;
    @Autowired
    private CkanRepositoryService ckanRepositoryService;

    @RequestMapping("/admin/datasets/extractor")
    public String extractor(Model model) {
        List<CkanRepository> ckanRepos = Lists.newArrayList(ckanRepositoryService.findAll().iterator());
        List<Boolean> availableRepos = new ArrayList<>();
        ckanRepos.forEach(repo -> availableRepos.add(ckanPackageService.existsByOriginUrl(repo.getUrl().split("api")[0])));
        model.addAttribute("ckanRepos", ckanRepos);
        model.addAttribute("availableRepos", availableRepos);

        return "ckanPackages/extractor";
    }

    @RequestMapping("/admin/datasets/extractor/edit")
    public String editCkanRepos(Model model) {
        List<CkanRepository> ckanRepos = Lists.newArrayList(ckanRepositoryService.findAll().iterator());
        model.addAttribute("ckanRepos", ckanRepos);
        model.addAttribute("action", "/admin/datasets/extractor/delete");
        model.addAttribute("actionEdit", "/admin/datasets/extractor/save");
        return "ckanPackages/extractor-editor";
    }

    @RequestMapping(value = "/admin/datasets/extractor/delete", method = RequestMethod.POST)
    public String deleteCkanRepo(@RequestParam("url") String url, RedirectAttributes redirectAttributes) {
        // Check if url exists
        CkanRepository ckanRepository = ckanRepositoryService.findByUrl(url);
        if (ckanRepository == null) {
            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage("There was an error when deleting that repo", FlashMessage.Status.FAILURE));
            return "redirect:/admin/datasets/extractor/edit";
        }
        ckanRepositoryService.delete(ckanRepository);
        redirectAttributes.addFlashAttribute("flash",
                new FlashMessage("Deleted correctly", FlashMessage.Status.SUCCESS));
        return "redirect:/admin/datasets/extractor/edit";
    }

    @RequestMapping(value = "/admin/datasets/extractor/save", method = RequestMethod.POST)
    public String saveCkanRepo(RedirectAttributes redirectAttributes,
                               @RequestParam("name") String name,
                               @RequestParam("url") String url,
                               @RequestParam("id") String id) {
        CkanRepository repo = new CkanRepository(name, url);
        if (!id.equals("none"))
            repo.setId(Long.parseLong(id));
        ckanRepositoryService.save(repo);
        return "redirect:/admin/datasets/extractor/edit";
    }

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
