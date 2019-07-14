package com.jeeps.ckan_extractor.model;

import java.util.List;

public class SdgConceptScheme {
    private String sdg;
    private List<String> concepts;

    public SdgConceptScheme(String sdg, List<String> concepts) {
        this.sdg = sdg;
        this.concepts = concepts;
    }

    public SdgConceptScheme() {}

    public String getSdg() {
        return sdg;
    }

    public void setSdg(String sdg) {
        this.sdg = sdg;
    }

    public List<String> getConcepts() {
        return concepts;
    }

    public void setConcepts(List<String> concepts) {
        this.concepts = concepts;
    }
}
