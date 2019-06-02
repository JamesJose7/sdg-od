package com.jeeps.ckan_extractor.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ExtractionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date timestamp;
    private String url;
    private int datasetsExtracted;
    private String description;

    public ExtractionHistory() {
    }

    public ExtractionHistory(Date timestamp, String url, int datasetsExtracted, String description) {
        this.timestamp = timestamp;
        this.url = url;
        this.datasetsExtracted = datasetsExtracted;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDatasetsExtracted() {
        return datasetsExtracted;
    }

    public void setDatasetsExtracted(int datasetsExtracted) {
        this.datasetsExtracted = datasetsExtracted;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
