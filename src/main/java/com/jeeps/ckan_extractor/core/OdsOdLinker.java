package com.jeeps.ckan_extractor.core;

import com.jeeps.ckan_extractor.service.SparqlService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFWriter;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.SKOS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.jeeps.ckan_extractor.dao.KnowledgeBaseDaoImpl.*;

public class OdsOdLinker {
    private Model model;
    private FileOutputStream os;

    public static final String DATA_PREFIX = "http://ods-od.org/data/";
    public static final String SCHEMA_PREFIX = "http://ods-od.org/schema/";
    private static final String DBR_PREFIX = "http://dbpedia.org/resource/";
    private static final int QUERY_LIMIT = 10000;

    private static final String FIND_ALL_CATALOGS_Q = "PREFIX dcat: <http://www.w3.org/ns/dcat#>\n" +
            "select ?catalog where { \n" +
            "\t?catalog a dcat:Catalog .\n" +
            "}";

    private Property automaticallyAnnotagedSubject;

    public OdsOdLinker() throws FileNotFoundException {
        // Create model
        model = ModelFactory.createDefaultModel();
        File fos = new File("ods-od-links.rdf");
        os = new FileOutputStream(fos);
        initializeVocabs();
    }

    private void initializeVocabs() {
        // Data prefix
        model.setNsPrefix("odsdata", DATA_PREFIX);

        // SKOS prefix
        model.setNsPrefix("skos", SKOS.getURI());

        // DCTerms prefix
        model.setNsPrefix("dct", DCTerms.getURI());

        // OD Data prefix
        model.setNsPrefix("oddata", CkanSemanticCreator.DATA_PREFIX);

        // Dcat prefix
        model.setNsPrefix("dcat", DCAT.getURI());

        // Create custom property
        model.setNsPrefix("ods", SCHEMA_PREFIX);
        automaticallyAnnotagedSubject =
                model.createProperty(SCHEMA_PREFIX + "automaticallyAnnotatedSubject");
    }

    public void annotateOdAndOds() {
           // Process to find similarities between OD and ODS goals
        List<String> catalogs = findAllCatalogs();
        findSimilarTagsInOds(catalogs);
    }

    private void findSimilarTagsInOds(List<String> catalogs) {
        catalogs
                .forEach(catalog -> {
                    List<List<String>> result = SparqlService.queryEndpoint(SPARQL_ENDPOINT,
                            getSimilarityQuery(catalog), "concept", "scheme");
                    // Remove variables
                    result.remove(0);
                    // Add link between the catalog and each ODS
                    result.forEach(link ->
                            model.createResource(catalog)
                                    .addProperty(automaticallyAnnotagedSubject,
                                            model.createResource(link.get(1))));
        });
    }

    private List<String> findAllCatalogs() {
        // Repeat the query offsetting the result until all triples are returned
        int offset = 0;
        int results;
        List<String> catalogs = new ArrayList<>();
        do {
            List<String> tempResult =
                    SparqlService.queryEndpoint(SPARQL_ENDPOINT,
                    getAllCatalogsQueryWithOffset(QUERY_LIMIT, offset),
                    "catalog").stream()
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
            // First result is the variable
            tempResult.remove(0);
            // check the # of returned results
            results = tempResult.size();
            offset += QUERY_LIMIT;
            // Add the resulting triples
            catalogs.addAll(tempResult);
        } while (results != 0);
        return catalogs;
    }

    private String getSimilarityQuery(String catalog) {
        return String.format("PREFIX data: <http://opendata.org/resource/>\n" +
                        "PREFIX dcat: <http://www.w3.org/ns/dcat#>\n" +
                        "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
                        "select ?concept ?scheme where { \n" +
                        "\t<%s> dcat:themeTaxonomy ?taxonomy .\n" +
                        "    ?concept a skos:Concept ;\n" +
                        "             skos:inScheme ?taxonomy ;\n" +
                        "             skos:prefLabel ?conceptLabel .\n" +
                        "    ?sdgConcept skos:inScheme ?scheme ;\n" +
                        "                skos:prefLabel ?sdgLabel .\n" +
                        "    FILTER regex(str(?scheme), \"SDG_Goal\")\n" +
                        "    filter(?conceptLabel=?sdgLabel)\n" +
                        "}",
                catalog);
    }

    private String getAllCatalogsQueryWithOffset(int limit, int offset) {
        return String.format("%s \nLIMIT %d\nOFFSET %d",
                FIND_ALL_CATALOGS_Q, limit, offset);
    }

    public void writeRdfFile() {
        // File dump
        // Write model to file
        RDFWriter writer = model.getWriter("RDF/XML");
        writer.write(model, os,  "");
    }
}
