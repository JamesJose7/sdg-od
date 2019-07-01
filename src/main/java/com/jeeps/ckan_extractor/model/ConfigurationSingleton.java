package com.jeeps.ckan_extractor.model;

import java.io.Serializable;

public class ConfigurationSingleton implements Serializable {

    private static volatile ConfigurationSingleton sSoleInstance;

    private ConfigurationRegistry configurationRegistry;

    //private constructor.
    private ConfigurationSingleton(){

        //Prevent form the reflection api.
        if (sSoleInstance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static ConfigurationSingleton getInstance() {
        if (sSoleInstance == null) { //if there is no instance available... create new one
            synchronized (ConfigurationSingleton.class) {
                if (sSoleInstance == null) sSoleInstance = new ConfigurationSingleton();
            }
        }

        return sSoleInstance;
    }

    public ConfigurationRegistry getConfigurationRegistry() {
        if (configurationRegistry != null) return configurationRegistry;
        return new ConfigurationRegistry();
    }

    public void setConfigurationRegistry(ConfigurationRegistry configurationRegistry) {
        this.configurationRegistry = configurationRegistry;
    }

    //Make singleton from serialize and deserialize operation.
    protected ConfigurationSingleton readResolve() {
        return getInstance();
    }
}
