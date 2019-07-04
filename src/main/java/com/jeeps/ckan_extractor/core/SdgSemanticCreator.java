package com.jeeps.ckan_extractor.core;

import com.jeeps.ckan_extractor.service.SparqlService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFWriter;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RiotException;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SdgSemanticCreator {
    public static final String SDG_TRIPLES_FILE_NAME = "sdg-triples-gen.rdf";
    private Model model;
    private FileOutputStream os;

    public static final String DATA_PREFIX = "http://ods-od.org/data/";
    public static final String SCHEMA_PREFIX = "http://ods-od.org/schema/";
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
        initializeVocabs();
    }

    private void initializeVocabs() {
        // Data prefix
        model.setNsPrefix("odsdata", DATA_PREFIX);

        // SKOS prefix
        model.setNsPrefix("skos", SKOS.getURI());

        // DCTerms prefix
        model.setNsPrefix("dct", DCTerms.getURI());

        // Create custom property
        model.setNsPrefix("ods", SCHEMA_PREFIX);
        model.createProperty("ods", "automaticallyAnnotatedSubject");
    }

    public synchronized void generateTriples() {
        // Get SDGs directories
        File fredFolder = new File("fred");
        File[] fredFolderFiles = fredFolder.listFiles();

        Arrays.stream(fredFolderFiles)
//                .limit(1)
                .forEach(folder -> {
                    // Get SDG RDF files
                    File sdgFolder = new File("fred/" + folder.getName());
                    File[] sdgFolderFiles = sdgFolder.listFiles();
                    Arrays.stream(sdgFolderFiles)
                            .map(File::getName)
                            .map(file -> String.format("%s/%s", folder.getName(), file))
                            .forEach(this::transformFredIntoSkos);
                });
    }

    private void transformFredIntoSkos(String fileName) {
        // Get the name for the ConceptScheme based on the structure
        String sdgElement = getConceptSchemeName(fileName);
        Model fredModel;
        try { // Some results of FRED failed
            fredModel = FileManager.get().loadModel("fred/" + fileName, null, "RDF/XML");
        } catch (RiotException e) {
            return;
        }

        // Create concept scheme
        Resource conceptScheme = model.createResource(DATA_PREFIX + sdgElement)
                .addProperty(RDF.type, SKOS.ConceptScheme)
                .addProperty(RDFS.label, getSDGLabel(sdgElement));
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

    private String getSDGLabel(String sdgElement) {
        return sdgElement.replaceAll("_", " ").replace("SDG ", "");
    }

    private String getConceptSchemeName(String fileName) {
        String sdgElement = fileName.replace(".rdf", "")
                .replace("_description", "");
        // Remove folder
        sdgElement = sdgElement.split("/")[1];
        int hierarchy = sdgElement.split("\\.").length;
        if (hierarchy == 1)
            return "SDG_Goal_" + sdgElement;
        else if (hierarchy == 2)
            return  "SDG_Target_" + sdgElement;
        return "SDG_Indicator_" + sdgElement;
    }

    public void writeRdfFile() throws IOException {
        // File dump
        String path = "rdf/";
        File temp = new File(path);
        if (!(temp.exists()))
            Files.createDirectories(temp.toPath()); // Create directory if it doesn't exist
        File fos = new File(path + SDG_TRIPLES_FILE_NAME);
        os = new FileOutputStream(fos);
        // Write model to file
        RDFWriter writer = model.getWriter("RDF/XML");
        writer.write(model, os,  "");
    }

    public void loadTriples(String fileName) {
        model.read(fileName);
    }

    public Model getModel() {
        return model;
    }
}
