package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.model.Role;

public interface RoleService {
    void save(Role role);
    Role findByName(String name);
}
