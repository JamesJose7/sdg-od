package com.jeeps.ckan_extractor.dao;

import com.jeeps.ckan_extractor.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDao extends CrudRepository<Role, Long> {
    Role findByName(String name);
}
