package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class FredService {

    private static final Object FRED_FOLDER_PATH = "fred\\";

    private HttpService httpService;

    @Value("${fred.api.url}")
    private String url;
    @Value("${fred.api.key}")
    private String token;

    private String fileName;
    private String path;

    @Autowired
    public FredService() {
        httpService = new HttpService();
    }

    public void fredActivate(String text, String fileName) {
        this.fileName = fileName;
        String encodedText = URLEncoder.encode("\"" + text + "\"", StandardCharsets.UTF_8);
        httpService.sendRequestWithHeaders(this::writeRdfToFile,
                url + encodedText,
                "Accept", "application/rdf+xml",
                "Authorization", token);
    }

    private void writeRdfToFile(String rdf) {
        String name = String.format("%s\\%s.rdf", getPath(), fileName);
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
        this.path = FRED_FOLDER_PATH + path;
    }
}
