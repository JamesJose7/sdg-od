package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.model.ConfigurationRegistry;

public interface ConfigurationRegistryService {
    void save(ConfigurationRegistry configurationRegistry);
    ConfigurationRegistry getConfiguration();
}
