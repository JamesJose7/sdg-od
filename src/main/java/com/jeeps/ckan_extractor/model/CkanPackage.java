package com.jeeps.ckan_extractor.model;

public class CkanPackage {
    private String name;
    private String title;
    private String license_title;
    private String metadata_created;
    private String metadata_modified;
    private String author;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLicense_title() {
        return license_title;
    }

    public void setLicense_title(String license_title) {
        this.license_title = license_title;
    }

    public String getMetadata_created() {
        return metadata_created;
    }

    public void setMetadata_created(String metadata_created) {
        this.metadata_created = metadata_created;
    }

    public String getMetadata_modified() {
        return metadata_modified;
    }

    public void setMetadata_modified(String metadata_modified) {
        this.metadata_modified = metadata_modified;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return String.format("License: %s\n" +
                "Created: %s\n" +
                "Modified: %s\n" +
                "Author: %s", license_title, metadata_created, metadata_modified, author);
    }
}
