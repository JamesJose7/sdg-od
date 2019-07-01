package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.dao.ConfigurationRegistryDao;
import com.jeeps.ckan_extractor.model.ConfigurationRegistry;
import com.jeeps.ckan_extractor.model.ConfigurationSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class ConfigurationRegistryServiceImpl implements ConfigurationRegistryService {
    @Autowired
    private ConfigurationRegistryDao configurationRegistryDao;

    @Override
    public void save(ConfigurationRegistry configurationRegistry) {
        // Delete previous configuration
        configurationRegistryDao.deleteAll();
        // Save the new configuration and reload the singleton in memory
        configurationRegistryDao.save(configurationRegistry);
        ConfigurationSingleton.getInstance().setConfigurationRegistry(configurationRegistry);
    }

    @Override
    public ConfigurationRegistry getConfiguration() {
        // Get previous configuration
        Iterator<ConfigurationRegistry> iterator = configurationRegistryDao.findAll().iterator();
        if (iterator.hasNext()) return iterator.next();
        return null;
    }
}
