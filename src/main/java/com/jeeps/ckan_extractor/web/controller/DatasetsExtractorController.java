package com.jeeps.ckan_extractor.web.controller;

import com.jeeps.ckan_extractor.model.CkanRepository;
import com.jeeps.ckan_extractor.model.ExtractionHistory;
import com.jeeps.ckan_extractor.service.CkanExtractorService;
import com.jeeps.ckan_extractor.service.CkanPackageService;
import com.jeeps.ckan_extractor.service.CkanRepositoryService;
import com.jeeps.ckan_extractor.service.ExtractionHistoryService;
import com.jeeps.ckan_extractor.web.FlashMessage;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DatasetsExtractorController {
    @Autowired
    private CkanPackageService ckanPackageService;
    @Autowired
    private CkanExtractorService ckanExtractorService;
    @Autowired
    private CkanRepositoryService ckanRepositoryService;
    @Autowired
    private ExtractionHistoryService extractionHistoryService;

    @RequestMapping("/")
    public String homePage(Model model) {
        return "index";
    }

    @RequestMapping("/admin/datasets/extractor")
    public String extractor(Model model) {
        List<CkanRepository> ckanRepos = Lists.newArrayList(ckanRepositoryService.findAll().iterator());
        List<Boolean> availableRepos = new ArrayList<>();
        ckanRepos.forEach(repo -> availableRepos.add(ckanPackageService.existsByOriginUrl(repo.getUrl().split("api")[0])));
        model.addAttribute("ckanRepos", ckanRepos);
        model.addAttribute("availableRepos", availableRepos);
        model.addAttribute("actionDelete", "/admin/datasets/extractor/repository/delete");

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

    @RequestMapping(value = "/admin/datasets/extractor/history/{repository}")
    public String viewHistory(@PathVariable("repository") String repository,
                              Model model) {
        List<ExtractionHistory> extractionHistoryList = extractionHistoryService.findAllByUrl(repository);
        model.addAttribute("historyList", extractionHistoryList);
        model.addAttribute("url", repository);
        return "ckanPackages/extractor-history";
    }

    @RequestMapping(value = "/admin/datasets/extractor/repository/delete", method = RequestMethod.POST)
    public String deleteRepository(@RequestParam("url") String url, RedirectAttributes redirectAttributes) {
        ckanExtractorService.deleteDatasets(url);
        redirectAttributes.addFlashAttribute("flash",
                new FlashMessage("Repository deleted successfully", FlashMessage.Status.SUCCESS));
        return "redirect:/admin/datasets/extractor";
    }
}
