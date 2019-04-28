package com.jeeps.ckan_extractor.web.controller;

import com.jeeps.ckan_extractor.service.CkanPackageService;
import com.jeeps.ckan_extractor.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class AdminController {
    @Autowired
    private CkanPackageService ckanPackageService;

    @RequestMapping("/admin")
    public String adminDashboard(Model model) {
        List<String> originUrls = ckanPackageService.getOriginUrls();
        model.addAttribute("ckanUrls", originUrls);

        // Packages count
        List<Integer> ckanPackageCount = new ArrayList<>();
        List<Long> resourcesCount = new ArrayList<>();
        originUrls.forEach(url -> {
            Integer originUrlCount = ckanPackageService.countDistinctByOriginUrl(url);
            ckanPackageCount.add(originUrlCount);

            // Resources count
            // TODO: Too resource intensive
            /*Collection<CkanPackage> ckanPackages = ckanPackageService.findAllByOriginUrl(url);
            Long count = ckanPackages.stream()
                    .map(CkanPackage::getResources)
                    .mapToLong(Collection::size)
                    .sum();
            resourcesCount.add(count);*/
        });
        model.addAttribute("urlCount", ckanPackageCount);
        /*model.addAttribute("resourcesCount", resourcesCount);*/
        return "admin";
    }

    @RequestMapping(value = "/admin/extract", method = RequestMethod.POST)
    public String extractCkanData(@RequestParam(value = "ckanURL", required = false) String[] ckanUrls,
                                  RedirectAttributes redirectAttributes) {
        if (ckanUrls == null) {
            // At least one checkbox should be selected
            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage("Please select at least one repository", FlashMessage.Status.FAILURE));
            return "redirect:/admin";
        }
        Arrays.stream(ckanUrls).forEach(System.out::println);

        /*Collection<CkanPackage> ckanPackages = ckanPackageService.findAllByOriginUrl(ckanUrls[0]);

        CompletableFuture<Long> task = semanticCreatorService.generateCkanTriples(ckanPackages);
        task.whenComplete((aLong, throwable) -> System.out.println("The result is: " + aLong));
        CompletableFuture.allOf(task).join();
        try {
            semanticCreatorService.writeFile("testTriples");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("What what");*/

        return "redirect:/admin";
    }
}
