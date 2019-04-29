package com.jeeps.ckan_extractor.config;

import com.jeeps.ckan_extractor.model.Role;
import com.jeeps.ckan_extractor.model.User;
import com.jeeps.ckan_extractor.service.RoleService;
import com.jeeps.ckan_extractor.service.UserService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
public class StartupConfig {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

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
    }
}
