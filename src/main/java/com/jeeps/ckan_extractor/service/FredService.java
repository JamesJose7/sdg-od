package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.utils.FileUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class FredService {

    public static final String FRED_TOKEN = "Bearer 6388bda1-9bdd-304e-bcd7-f193463037ee";
    private final String BASE_URL = "http://wit.istc.cnr.it/stlab-tools/fred?text=";
    private HttpService httpService;

    private String fileName;
    private String path;

    public FredService(String path) {
        httpService = new HttpService();
        this.path = path;
    }

    public void fredActivate(String text, String fileName) {
        this.fileName = fileName;
        String encodedText = URLEncoder.encode("\"" + text + "\"", StandardCharsets.UTF_8);
        httpService.sendRequestWithHeaders(this::writeRdfToFile,
                BASE_URL + encodedText,
                "Accept", "application/rdf+xml",
                "Authorization", FRED_TOKEN);
    }

    private void writeRdfToFile(String rdf) {
        String name = String.format("%s\\%s.rdf", path, fileName);
        try {
            FileUtils.writeContentsToFile(name, rdf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
