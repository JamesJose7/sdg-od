package com.jeeps.ckan_extractor.model.stomp;

import java.util.List;

public class CkanUrlsStomp {
    private List<String> ckanUrls;
    private String format;
    private boolean upload;
    private boolean noCache;

    public List<String> getCkanUrls() {
        return ckanUrls;
    }

    public void setCkanUrls(List<String> ckanUrls) {
        this.ckanUrls = ckanUrls;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    public boolean isNoCache() {
        return noCache;
    }

    public void setNoCache(boolean noCache) {
        this.noCache = noCache;
    }
}
