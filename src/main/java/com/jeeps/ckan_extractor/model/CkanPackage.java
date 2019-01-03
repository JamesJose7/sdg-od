package com.jeeps.ckan_extractor.model;

public class CkanPackage {
    private String id;
    private String name;
    private String title;
    private String license_title;
    private String metadata_created;
    private String metadata_modified;
    private String author;
    private String notes;
    private String type;
    private String originUrl;

    public CkanPackage(CkanPackageBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.title = builder.title;
        this.license_title = builder.license_title;
        this.metadata_created = builder.metadata_created;
        this.metadata_modified = builder.metadata_modified;
        this.author = builder.author;
        this.notes = builder.notes;
        this.type = builder.type;
        this.originUrl = builder.originUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    @Override
    public String toString() {
        return String.format("License: %s\n" +
                "Created: %s\n" +
                "Modified: %s\n" +
                "Author: %s", license_title, metadata_created, metadata_modified, author);
    }

    public static class CkanPackageBuilder {
        private String id;
        private String name;
        private String title;
        private String license_title;
        private String metadata_created;
        private String metadata_modified;
        private String author;
        private String notes;
        private String type;
        private String originUrl;

        public CkanPackageBuilder(String id) {
            this.id = id;
        }

        public CkanPackageBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public CkanPackageBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public CkanPackageBuilder withLicense(String license) {
            this.license_title = license;
            return this;
        }

        public CkanPackageBuilder withMetadataCreated(String created) {
            this.metadata_created = created;
            return this;
        }

        public CkanPackageBuilder withMetadataModified(String modified) {
            this.metadata_modified = modified;
            return this;
        }

        public CkanPackageBuilder withAuthor(String author) {
            this.author = author;
            return this;
        }

        public CkanPackageBuilder withNotes(String notes) {
            this.notes = notes;
            return this;
        }

        public CkanPackageBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public CkanPackageBuilder withOriginUrl(String url) {
            this.originUrl = url;
            return this;
        }

        public CkanPackage build() {
            return new CkanPackage(this);
        }
    }
}
