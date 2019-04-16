package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.utils.FileUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class FredService {

    public static final String FRED_TOKEN = "Bearer f028d74a-87dd-346e-af2a-4c3b8daa40cd";
    private final String BASE_URL = "http://wit.istc.cnr.it/stlab-tools/fred?text=";
    private HttpService httpService;

    public FredService() {
        httpService = new HttpService();
    }

    public void fredActivate(String text) {
        try {
            Thread.sleep(60100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String encodedText = URLEncoder.encode("\"" + text + "\"", StandardCharsets.UTF_8);
        httpService.sendRequestWithHeaders(this::writeRdfToFile,
                BASE_URL + encodedText,
                "Accept", "application/rdf+xml",
                "Authorization", FRED_TOKEN);
    }

    private void writeRdfToFile(String rdf) {
        Random random = new Random();
        String fileName = String.format("fred\\File_%03d.rdf", random.nextInt(1000));
        try {
            FileUtils.writeContentsToFile(fileName, rdf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
