package com.jeeps.ckan_extractor.model;

import java.util.List;

public class SdgTarget {
    private String code;
    private String title;
    private String description;
    private List<SdgIndicator> indicators;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SdgIndicator> getIndicators() {
        return indicators;
    }

    public void setIndicators(List<SdgIndicator> indicators) {
        this.indicators = indicators;
    }
}
