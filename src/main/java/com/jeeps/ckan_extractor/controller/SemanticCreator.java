package com.jeeps.ckan_extractor.controller;

import com.jeeps.ckan_extractor.model.CkanPackage;
import com.jeeps.ckan_extractor.model.CkanResource;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;

import static com.jeeps.ckan_extractor.util.StringUtils.upperCaseFirst;
import static com.jeeps.ckan_extractor.util.StringUtils.urlify;

public class SemanticCreator {
    private Model mModel;
    private final FileOutputStream os;
    public static final String DATA_PREFIX = "http://example.org/data/";

    public static String CURRENT_PLATFORM;
    public static String CURRENT_COUNTRY;
    private Property mDboCountryP;
    private Resource mDboCountryC;
    private String mDbo;
    private String mDbr;

    public SemanticCreator() throws FileNotFoundException {
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
        mDbo = "http://dbpedia.org/ontology/";
        mDbr = "http://dbpedia.org/resource/";
        mModel.setNsPrefix("dbo", mDbo);
        mModel.setNsPrefix("dbr", mDbr);
        mDboCountryP = ResourceFactory.createProperty(mDbo + "country");
        mDboCountryC = ResourceFactory.createResource(mDbo + "Country");

        // Our Ontology prefix
//        String vocabPrefix = CKAN.getURI();
//        mModel.setNsPrefix("ckan",vocabPrefix);
    }

    public void generateTriples(CkanPackage aPackage, CkanResource[] resourcesCkan) {
        // Create package as Catalog
        Resource catalog = mModel.createResource(DATA_PREFIX + upperCaseFirst(urlify(aPackage.getName())))
                .addProperty(RDF.type, DCAT.Catalog)
                .addProperty(FOAF.homepage, mModel.createResource(aPackage.getOriginUrl()));

        if (exists(aPackage.getAuthor()))
//            catalog.addProperty(DCTerms.publisher, mModel.createResource(DATA_PREFIX + upperCaseFirst(urlify(aPackage.getName()) + "_publisher"))
            catalog.addProperty(DCTerms.publisher, mModel.createResource(DATA_PREFIX + upperCaseFirst(urlify(aPackage.getAuthor())))
                    .addProperty(RDF.type, FOAF.Agent)
                    .addProperty(FOAF.name, aPackage.getAuthor()));
        if (exists(aPackage.getTitle()))
            catalog.addProperty(DCTerms.title, aPackage.getTitle());
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
        /*aPackage.getGroups().forEach(tag -> {
            String groupName = tag.getAsJsonObject().get("display_name").getAsString();
            catalog.addProperty(CKAN.group, mModel.createResource(DATA_PREFIX + urlify(groupName))
                    .addProperty(RDF.type, CKAN.Group)
                    .addProperty(CKAN.title, groupName));
        });*/

        // Add Tags
        /*aPackage.getTags().forEach(tag -> {
            String tagName = tag.getAsJsonObject().get("display_name").getAsString();
            catalog.addProperty(CKAN.tag, mModel.createResource(DATA_PREFIX + urlify(tagName))
                    .addProperty(RDF.type, CKAN.Tag)
                    .addProperty(SKOS.prefLabel, tagName));
        });*/

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
                    // Dataset
                    Resource dataset = mModel.createResource(DATA_PREFIX + urlify(resource.getName()))
                            .addProperty(RDF.type, DCAT.Dataset)
                            .addProperty(DCTerms.title, resource.getName());
                    if (exists(resource.getDescription()))
                        dataset.addProperty(DCTerms.description, resource.getDescription());
                    if (exists(resource.getCreated()))
                        dataset.addProperty(DCTerms.issued, resource.getCreated());
                    if (exists(resource.getModified()))
                        dataset.addProperty(DCTerms.modified, resource.getModified());

                    // Distribution
                    Resource distribution = mModel.createResource(DATA_PREFIX + "dist_" + urlify(resource.getName()))
                            .addProperty(RDF.type, DCAT.Distribution)
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
