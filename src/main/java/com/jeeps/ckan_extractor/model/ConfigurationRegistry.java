package com.jeeps.ckan_extractor.model;

import com.jeeps.ckan_extractor.utils.Encryptor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class ConfigurationRegistry {
    @Id
    private long id;

    /* SPARQL CONFIG */
    private String sparqlDBEndpoint;
    private String sparqlDBUser;
    private String sparqlDBPass;
    private String sparqlWebEndpoint;

    @Transient
    private String secret = "itsASecretToEverybody";

    public ConfigurationRegistry() {}

    public ConfigurationRegistry(String sparqlDBEndpoint, String sparqlDBUser, String sparqlDBPass, String sparqlWebEndpoint) {
        setId(1);
        this.sparqlDBEndpoint = sparqlDBEndpoint;
        setSparqlDBUser(sparqlDBUser);
        setSparqlDBPass(sparqlDBPass);
        this.sparqlWebEndpoint = sparqlWebEndpoint;
    }

    public long getId() {
        return 1;
    }

    public void setId(long id) {
        this.id = 1;
    }

    public String getSparqlDBEndpoint() {
        return sparqlDBEndpoint;
    }

    public void setSparqlDBEndpoint(String sparqlDBEndpoint) {
        this.sparqlDBEndpoint = sparqlDBEndpoint;
    }

    public String getSparqlDBUser() {
        return Encryptor.decrypt(sparqlDBUser, secret);
    }

    public void setSparqlDBUser(String sparqlDBUser) {
        // Encrypt
        this.sparqlDBUser = Encryptor.encrypt(sparqlDBUser, secret);
    }

    public String getSparqlDBPass() {
        return Encryptor.decrypt(sparqlDBPass, secret);
    }

    public void setSparqlDBPass(String sparqlDBPass) {
        this.sparqlDBPass = Encryptor.encrypt(sparqlDBPass, secret);
    }

    public String getSparqlWebEndpoint() {
        return sparqlWebEndpoint;
    }

    public void setSparqlWebEndpoint(String sparqlWebEndpoint) {
        this.sparqlWebEndpoint = sparqlWebEndpoint;
    }
}
