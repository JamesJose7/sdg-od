package com.jeeps.ckan_extractor.controller;

import com.jeeps.ckan_extractor.model.CkanPackage;
import com.jeeps.ckan_extractor.model.CkanResource;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.jeeps.ckan_extractor.util.StringUtils.upperCaseFirst;
import static com.jeeps.ckan_extractor.util.StringUtils.urlify;

public class SemanticCreator {
    private Model mModel;
    private final FileOutputStream os;
    public static final String DATA_PREFIX = "http://example.org/data/";

    public static String CURRENT_PLATFORM;
    public static String CURRENT_COUNTRY;
    private Property dboCountryP;
    private Resource dboCountryC;
    private String dbo;
    private String dbr;
    private final Random random;

    public SemanticCreator() throws FileNotFoundException {
        random = new Random();
        // Create model
        mModel = ModelFactory.createDefaultModel();
        // File dump
        File fos = new File("triples.rdf");
        os = new FileOutputStream(fos);
        initializeVocabs();
    }

    private void initializeVocabs() {
        // Data prefix
        mModel.setNsPrefix("data", DATA_PREFIX);

        // Foaf prefix
        String foaf = FOAF.getURI();
        mModel.setNsPrefix("foaf",foaf);

        // Dcat prefix
        String dcat = DCAT.getURI();
        mModel.setNsPrefix("dcat", dcat);

        // DCTerms prefix
        String dct = DCTerms.getURI();
        mModel.setNsPrefix("dct", dct);

        // SKOS prefix
        String skos = SKOS.getURI();
        mModel.setNsPrefix("skos", skos);

        // DBO prefix
        dbo = "http://dbpedia.org/ontology/";
        dbr = "http://dbpedia.org/resource/";
        mModel.setNsPrefix("dbo", dbo);
        mModel.setNsPrefix("dbr", dbr);
        dboCountryP = ResourceFactory.createProperty(dbo + "country");
        dboCountryC = ResourceFactory.createResource(dbo + "Country");
    }

    public synchronized void generateTriples(CkanPackage aPackage, CkanResource[] resourcesCkan) {
        // Create package as Catalog
        Resource catalog = mModel.createResource(DATA_PREFIX + upperCaseFirst(urlify(aPackage.getTitle())))
                .addProperty(RDF.type, DCAT.Catalog)
                .addProperty(FOAF.homepage, mModel.createResource(aPackage.getOriginUrl()));

        if (exists(aPackage.getAuthor()))
//            catalog.addProperty(DCTerms.publisher, mModel.createResource(DATA_PREFIX + upperCaseFirst(urlify(aPackage.getName()) + "_publisher"))
            catalog.addProperty(DCTerms.publisher, mModel.createResource(DATA_PREFIX + upperCaseFirst(urlify(aPackage.getAuthor())))
                    .addProperty(RDF.type, FOAF.Agent)
                    .addProperty(FOAF.name, aPackage.getAuthor()));
        if (exists(aPackage.getTitle())) {
            catalog.addProperty(DCTerms.title, aPackage.getTitle());
            catalog.addProperty(RDFS.label, aPackage.getTitle());
        }
        if (exists(aPackage.getDescription()))
            catalog.addProperty(DCTerms.description, aPackage.getDescription());
        if (exists(aPackage.getIssued()))
            catalog.addProperty(DCTerms.issued, aPackage.getIssued());
        if (exists(aPackage.getModified()))
            catalog.addProperty(DCTerms.modified, aPackage.getModified());
        if (exists(aPackage.getLicense_title()))
            catalog.addProperty(DCTerms.license, mModel.createResource(DATA_PREFIX + upperCaseFirst(urlify(aPackage.getLicense_title())))
                    .addProperty(RDF.type, DCTerms.LicenseDocument)
                    .addProperty(DCTerms.title, aPackage.getLicense_title()));


        // Add Groups
        List<Resource> groupList = new ArrayList<>();
        if (aPackage.getGroups() != null) {
            Resource groupConceptScheme = mModel.createResource(DATA_PREFIX + "Groups_from_" + urlify(aPackage.getTitle()))
                    .addProperty(RDF.type, SKOS.ConceptScheme)
                    .addProperty(RDFS.label, "Groups from " + aPackage.getTitle())
                    .addProperty(DCTerms.title, "Groups from " + aPackage.getTitle());
            aPackage.getGroups().forEach(tag -> {
                if (tag.getAsJsonObject().get("display_name") != null) {
                    String groupName = tag.getAsJsonObject().get("display_name").getAsString();
                    Resource groupRes = mModel.createResource(DATA_PREFIX + upperCaseFirst(urlify(groupName)))
                            .addProperty(RDF.type, SKOS.Concept)
                            .addProperty(SKOS.prefLabel, groupName)
                            .addProperty(SKOS.inScheme, groupConceptScheme);
                    groupList.add(groupRes);
                }
            });
            catalog.addProperty(DCAT.themeTaxonomy, groupConceptScheme);
        }

        // Add Tags
        // Create concept scheme for catalog
        List<Resource> tagList = new ArrayList<>();
        if (aPackage.getTags() != null) {
            Resource tagConceptScheme = mModel.createResource(DATA_PREFIX + "Tags_from_" + urlify(aPackage.getTitle()))
                    .addProperty(RDF.type, SKOS.ConceptScheme)
                    .addProperty(RDFS.label, "Tags from " + aPackage.getTitle())
                    .addProperty(DCTerms.title, "Tags from " + aPackage.getTitle());
            aPackage.getTags().forEach(tag -> {
                String tagName = tag.getAsJsonObject().get("display_name").getAsString();
                Resource tagRes = mModel.createResource(DATA_PREFIX + upperCaseFirst(urlify(tagName)))
                        .addProperty(RDF.type, SKOS.Concept)
                        .addProperty(SKOS.prefLabel, tagName)
                        .addProperty(SKOS.inScheme, tagConceptScheme);
                tagList.add(tagRes);
            });
            catalog.addProperty(DCAT.themeTaxonomy, tagConceptScheme);
        }

        // Add Organization
        /*String orgName = aPackage.getOrganization().has("title") ? aPackage.getOrganization().get("title").getAsString() : "org_" + urlify(aPackage.getName());
        String orgStatus = aPackage.getOrganization().has("state") ? aPackage.getOrganization().get("state").getAsString() : "";
        String orgPoliticalLevel = aPackage.getOrganization().has("political_level") ? aPackage.getOrganization().get("political_level").getAsString() : "";
        Resource organization = mModel.createResource(DATA_PREFIX + urlify(orgName))
                .addProperty(RDF.type, CKAN.Organization)
                .addProperty(FOAF.name, orgName);
        if (exists(orgStatus))
            organization.addProperty(CKAN.status, orgStatus);
        if (exists(orgPoliticalLevel))
            organization.addProperty(CKAN.politicalLevel, orgPoliticalLevel);
        catalog.addProperty(CKAN.organization, organization);*/

        // Add resources
        Arrays.stream(resourcesCkan)
                .forEach(resource -> {
                    // Generate a random number to differentiate datasets and distributions with the same name
                    String randomId = String.format("_%03d", random.nextInt(1000));
                    // Dataset
                    Resource dataset = mModel.createResource(DATA_PREFIX + upperCaseFirst(urlify(resource.getName())) + randomId)
                            .addProperty(RDF.type, DCAT.Dataset)
                            .addProperty(RDFS.label, resource.getName())
                            .addProperty(DCTerms.title, resource.getName());
                    if (exists(resource.getDescription()))
                        dataset.addProperty(DCTerms.description, resource.getDescription());
                    if (exists(resource.getCreated()))
                        dataset.addProperty(DCTerms.issued, resource.getCreated());
                    if (exists(resource.getModified()))
                        dataset.addProperty(DCTerms.modified, resource.getModified());
                    tagList.forEach(tag -> dataset.addProperty(DCAT.theme, tag));
                    groupList.forEach(tag -> dataset.addProperty(DCAT.theme, tag));

                    // Distribution
                    Resource distribution = mModel.createResource(DATA_PREFIX + "dist_" + upperCaseFirst(urlify(resource.getName())) + randomId)
                            .addProperty(RDF.type, DCAT.Distribution)
                            .addProperty(RDFS.label, "Distribution of: " + resource.getName())
                            .addProperty(DCTerms.title, "Distribution of: " + resource.getName())
                            .addProperty(DCAT.downloadURL, resource.getUrl());
                    if (exists(resource.getByteSize()))
                        distribution.addProperty(DCAT.byteSize, resource.getByteSize());
                    if (exists(resource.getLicense()))
                        distribution.addProperty(DCTerms.license, mModel.createResource(DATA_PREFIX + upperCaseFirst(urlify(resource.getLicense())))
                                .addProperty(RDF.type, DCTerms.LicenseDocument)
                                .addProperty(DCTerms.title, resource.getLicense()));
                    if (exists(resource.getFormat()))
                        distribution.addProperty(DCAT.mediaType, resource.getFormat());

                    // Add relations
                    dataset.addProperty(DCAT.distribution, distribution);
                    catalog.addProperty(DCAT.dataset, dataset);
                });
    }

    public void writeRdfFile() {
        // Write model to file
        RDFWriter writer = mModel.getWriter("RDF/XML");
        writer.write(mModel, os,  "");
    }

    private boolean exists(String string) {
        if (string == null)
            return false;
        return !string.isBlank() && !string.isEmpty();
    }
}
