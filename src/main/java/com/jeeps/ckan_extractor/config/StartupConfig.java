package com.jeeps.ckan_extractor.config;

import com.jeeps.ckan_extractor.model.CkanRepository;
import com.jeeps.ckan_extractor.model.Role;
import com.jeeps.ckan_extractor.model.User;
import com.jeeps.ckan_extractor.service.CkanPackageService;
import com.jeeps.ckan_extractor.service.CkanRepositoryService;
import com.jeeps.ckan_extractor.service.RoleService;
import com.jeeps.ckan_extractor.service.UserService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Transactional
public class StartupConfig {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private CkanRepositoryService ckanRepositoryService;
    @Autowired
    private CkanPackageService ckanPackageService;

    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshedEvent() {
        // Create roles
        Role userRole = roleService.findByName("ROLE_USER");
        if (userRole == null) {
            userRole = new Role(1L, "ROLE_USER");
            roleService.save(userRole);
        }
        Role adminRole = roleService.findByName("ROLE_ADMIN");
        if (adminRole == null) {
            adminRole = new Role(2L, "ROLE_ADMIN");
            roleService.save(adminRole);
        }

        // TODO: Remove, this is just for testing
        // Create admin user if it doesn't exist
        User adminUser = userService.findByUsername("admin");
        if (adminUser == null) {
            adminUser = new User("admin",
                    BCrypt.hashpw("admin", BCrypt.gensalt(10)),
                    true, adminRole);
            userService.save(adminUser);
        }

        // Delete temp files and create directory
        File temp = new File("temp/");
        try {
            if (!(temp.exists()))
                Files.createDirectories(temp.toPath()); // Create temp directory if it doesn't exis
            FileUtils.cleanDirectory(new File("temp/"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load initial CKAN repos
        List<CkanRepository> repos = new ArrayList<>(Arrays.asList(
                new CkanRepository("Ambar", "http://ambar.utpl.edu.ec/api/3/action/"),
                new CkanRepository("Europe OD Portal", "http://data.europa.eu/euodp/data/api/3/action/"),
                new CkanRepository("Humanitarian Data Exchange", "https://data.humdata.org/api/3/action/"),
                new CkanRepository("OD Swiss", "https://opendata.swiss/api/3/action/"),
                new CkanRepository("Australian Government", "https://data.gov.au/api/3/action/")
                ));
        repos.forEach(repo -> {
            if (ckanRepositoryService.findByUrl(repo.getUrl()) == null)
                ckanRepositoryService.save(repo);
        });

        // Test erase repos
       ckanPackageService.deleteAllByOriginUrl("http://ambar.utpl.edu.ec/");
        /*ckanPackageService.deleteAllByOriginUrl("https://data.gov.au/");
        ckanPackageService.deleteAllByOriginUrl("https://data.humdata.org/");
        ckanPackageService.deleteAllByOriginUrl("http://data.europa.eu/euodp/data/");
        ckanPackageService.deleteAllByOriginUrl("https://opendata.swiss/");*/
    }
}
