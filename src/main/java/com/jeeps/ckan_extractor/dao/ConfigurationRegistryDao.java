package com.jeeps.ckan_extractor.dao;

import com.jeeps.ckan_extractor.model.ConfigurationRegistry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationRegistryDao extends CrudRepository<ConfigurationRegistry, Long> {
    ConfigurationRegistry findFirstById(Long id);
}
