package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User findByUsername(String username);
    void save(User user);
    void delete(User user);
    User findById(Long id);
}
