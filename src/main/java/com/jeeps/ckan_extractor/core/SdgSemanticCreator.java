package com.jeeps.ckan_extractor.core;

import com.jeeps.ckan_extractor.service.SparqlService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFWriter;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.List;

public class SdgSemanticCreator {
    private Model model;
    private FileOutputStream os;

    public static final String DATA_PREFIX = "http://example.org/data/";
    private static final String FRED_PREFIX = "http://www.ontologydesignpatterns.org/ont/fred/domain.owl#";
    private static final String DBR_PREFIX = "http://dbpedia.org/resource/";

    private static final String queryClasses = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
            "select * where { \n" +
            "\t?s a owl:Class .\n" +
            "} limit 100 \n";

    private static final String queryAssociations = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
            "PREFIX dul: <http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#>\n" +
            "select * where { \n" +
            "\t?s a owl:Class ;\n" +
            "       dul:associatedWith ?associated .\n" +
            "} limit 100 \n";

    private static final String queryEquivalents = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
            "select * where { \n" +
            "\t?s a owl:Class ;\n" +
            "    \towl:equivalentClass ?equivalent .\n" +
            "} limit 100 \n";

    public SdgSemanticCreator() throws FileNotFoundException {
        // Create model
        model = ModelFactory.createDefaultModel();
        File fos = new File("sdgTriples.rdf");
        os = new FileOutputStream(fos);
        initializeVocabs();
    }

    private void initializeVocabs() {
        // Data prefix
        model.setNsPrefix("data", DATA_PREFIX);

        // SKOS prefix
        model.setNsPrefix("skos", SKOS.getURI());
    }

    public synchronized void generateTriples() {
        Model fredModel = FileManager.get().loadModel("fred/1/1.1.1.rdf", null, "RDF/XML");

        // Create concept scheme
        Resource conceptScheme = model.createResource(DATA_PREFIX + "SDG_Indicator_1.1.1")
                .addProperty(RDF.type, SKOS.ConceptScheme);
        // Get all owl:Class
        List<List<String>> resultStatements = SparqlService.queryModel(fredModel, queryClasses, "s");
        // Create a skos:Concept out of every class
        resultStatements.stream()
                .flatMap(Collection::stream)
                .forEach(result -> {
                    // Replace FRED prefix
                    if (result.contains(FRED_PREFIX)) {
                        String conceptUri = result.replace(FRED_PREFIX, DATA_PREFIX);
                        // Create resource
                        // TODO: Test method createResource(Uri, Type)
                        model.createResource(conceptUri)
                                .addProperty(RDF.type, SKOS.Concept)
                                .addProperty(SKOS.prefLabel, result.replace(FRED_PREFIX, ""))
                                .addProperty(SKOS.inScheme, conceptScheme);
                    }
                });

        // Find class associations
        resultStatements = SparqlService.queryModel(fredModel, queryAssociations, "s", "associated");
        // Create a skos:related between concepts
        resultStatements.forEach(statement -> {
            if (statement.size() == 2) {
                String s = statement.get(0);
                String o = statement.get(1);
                if (s.contains(FRED_PREFIX) && o.contains(FRED_PREFIX)) {
                    String sUri = s.replace(FRED_PREFIX, DATA_PREFIX);
                    String oUri = o.replace(FRED_PREFIX, DATA_PREFIX);
                    Resource oResource = model.createResource(oUri)
                            .addProperty(RDF.type, SKOS.Concept)
                            .addProperty(SKOS.inScheme, conceptScheme);

                    model.createResource(sUri)
                            .addProperty(RDF.type, SKOS.Concept)
                            .addProperty(SKOS.inScheme, conceptScheme)
                            .addProperty(SKOS.related, oResource);
                }
            }
        });

        // Find subjects
        resultStatements = SparqlService.queryModel(fredModel, queryEquivalents, "s", "equivalent");
        // Create a subject for each concept that has one
        resultStatements.forEach(statement -> {
            if (statement.size() == 2) {
                String s = statement.get(0);
                String equivalent = statement.get(1);
                if (equivalent.contains(DBR_PREFIX) && s.contains(FRED_PREFIX)) {
                    String sUri = s.replace(FRED_PREFIX, DATA_PREFIX);
                    // Concept
                    Resource concept = model.createResource(sUri)
                            .addProperty(RDF.type, SKOS.Concept)
                            .addProperty(SKOS.inScheme, conceptScheme);
                    // Create dbr resource and set the concept as its subject
                    model.createResource(equivalent)
                            .addProperty(DCTerms.subject, concept);
                }
            }
        });
    }

    public void writeRdfFile() {
        // File dump
        // Write model to file
        RDFWriter writer = model.getWriter("RDF/XML");
        writer.write(model, os,  "");
    }
}
