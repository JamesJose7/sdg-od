package com.jeeps.ckan_extractor.model;

public class CkanContent {
    private String success;
    private String[] result;


    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String[] getResult() {
        return result;
    }

    public void setResult(String[] result) {
        this.result = result;
    }
}
