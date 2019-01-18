package com.jeeps.ckan_extractor.model;

public class CkanResource {
    private String id;
    private String package_id;
    private String description;
    private String format;
    private String name;
    private String created;
    private String last_modified;
    private String url;

    public CkanResource(CkanResourceBuilder builder) {
        this.id = builder.id;
        this.package_id = builder.package_id;
        this.description = builder.description;
        this.format = builder.format;
        this.name = builder.name;
        this.created = builder.created;
        this.last_modified = builder.last_modified;
        this.url = builder.url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormat() {
        return format != null ? format : "";
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(String last_modified) {
        this.last_modified = last_modified;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static class CkanResourceBuilder {
        private String id;
        private String package_id;
        private String description;
        private String format;
        private String name;
        private String created;
        private String last_modified;
        private String url;

        public CkanResourceBuilder(String id, String package_id) {
            this.id = id;
            this.package_id = package_id;
        }

        public CkanResourceBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public CkanResourceBuilder withFormat(String format) {
            this.format = format;
            return this;
        }

        public CkanResourceBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public CkanResourceBuilder withCreated(String created) {
            this.created = created;
            return this;
        }

        public CkanResourceBuilder withLastModified(String last_modified) {
            this.last_modified = last_modified;
            return this;
        }

        public CkanResourceBuilder withUrl(String url) {
            this.url = url;
            return this;
        }

        public CkanResource build() {
            return new CkanResource(this);
        }
    }
}
