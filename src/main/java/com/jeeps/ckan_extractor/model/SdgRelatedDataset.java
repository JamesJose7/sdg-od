package com.jeeps.ckan_extractor.model;

public class SdgRelatedDataset {
    private String datasetUri;
    private String dataset;
    private String sdgUri;
    private String sdg;

    public SdgRelatedDataset() {}

    public SdgRelatedDataset(String datasetUri, String dataset, String sdgUri, String sdg) {
        this.datasetUri = datasetUri;
        this.dataset = dataset;
        this.sdgUri = sdgUri;
        this.sdg = sdg;
    }

    public String getDatasetUri() {
        return datasetUri;
    }

    public void setDatasetUri(String datasetUri) {
        this.datasetUri = datasetUri;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public String getSdgUri() {
        return sdgUri;
    }

    public void setSdgUri(String sdgUri) {
        this.sdgUri = sdgUri;
    }

    public String getSdg() {
        return sdg;
    }

    public void setSdg(String sdg) {
        this.sdg = sdg;
    }
}
