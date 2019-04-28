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

//    @Async("asyncExecutor")
    public Long generateCkanTriples(Collection<CkanPackage> ckanPackages) {
        Instant start = Instant.now();

        // Generate triples
        semanticCreator = new CkanSemanticCreator();
        ckanPackages.forEach(ckanPackage -> semanticCreator.generateTriples(ckanPackage));

        Instant finish = Instant.now();
        return Duration.between(start, finish).toMillis();
    }

    public void writeFile(String fileName) throws IOException {
        semanticCreator.writeRdfFile(fileName);
    }
}
