package com.jeeps.ckan_extractor.controller;

import com.jeeps.ckan_extractor.config.MysqlDatabase;
import com.jeeps.ckan_extractor.model.CkanPackage;
import com.jeeps.ckan_extractor.model.CkanResource;
import com.jeeps.ckan_extractor.model.Dcat;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SemanticCreator {
    private MysqlDatabase mMysqlDatabase;

    public SemanticCreator() {
        mMysqlDatabase = new MysqlDatabase(false);
    }

    public void convertToDcat() {
        Map<CkanPackage, List<CkanResource>> datasets = mMysqlDatabase.retrieveDatasets();
        Optional<Integer> sum = datasets.entrySet().stream()
                .map(entry -> entry.getValue().size())
                .reduce((integer, integer2) -> integer + integer2);
        System.out.printf("Packages: %d\nResources: %d\n", datasets.size(), sum.orElse(0));

        // Convert to dcat
        Optional<String> completeDcat = datasets.entrySet().stream()
                .map(this::datasetToDcat)
                .reduce(String::concat);
        //System.out.println(completeDcat);

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("dcat-representation.ttl"), "utf-8"))) {
            writer.write(Dcat.NAMESPACES + completeDcat.orElse(""));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String datasetToDcat(Map.Entry<CkanPackage, List<CkanResource>> dataset) {
        return new Dcat.DcatBuilder(dataset.getKey())
                .withResources(dataset.getValue())
                .build()
                .getTurtleRepresentation();
    }
}
