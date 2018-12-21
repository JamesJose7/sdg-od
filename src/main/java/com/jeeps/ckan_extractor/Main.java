package com.jeeps.ckan_extractor;

import com.jeeps.ckan_extractor.controller.CkanExtractor;

public class Main {
    public static void main(String[] args) {
        CkanExtractor ckanExtractor = new CkanExtractor();
        ckanExtractor.extract("http://ambar.utpl.edu.ec/api/action/");
    }
}
