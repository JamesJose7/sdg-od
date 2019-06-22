package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.core.CkanSemanticCreator;
import com.jeeps.ckan_extractor.model.CkanPackage;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;

@Service
public class SemanticCreatorService {
    private CkanSemanticCreator semanticCreator;

    public void createNewModel() {
        semanticCreator = new CkanSemanticCreator();
    }

//    @Async("asyncExecutor")
    public Long generateCkanTriples(Collection<CkanPackage> ckanPackages) {
        Instant start = Instant.now();

        // Generate triples
        ckanPackages.forEach(ckanPackage -> semanticCreator.generateTriples(ckanPackage));

        Instant finish = Instant.now();
        return Duration.between(start, finish).toMillis();
    }

    public void writeFile(String path, String fileName, String serializeFormat) throws IOException {
        semanticCreator.writeRdfFile(path, fileName, serializeFormat);
    }

    public void loadTriples(String fileName) {
        semanticCreator.loadTriples(fileName);
    }
}
