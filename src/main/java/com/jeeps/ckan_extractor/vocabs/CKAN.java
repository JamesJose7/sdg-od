package com.jeeps.ckan_extractor.vocabs;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class CKAN {
    private static final Model M_MODEL = ModelFactory.createDefaultModel();

    // Namespace
    public static final String NS = "http://ckan-vocab.org/ontology#";

    public static String getURI() {return NS;}

    // Namespace as a resource
    public static final Resource NAMESPACE = M_MODEL.createResource( NS );

    /* Properties */
    public static final Property version = M_MODEL.createProperty(NS + "version");
    public static final Property isPrivate = M_MODEL.createProperty(NS + "isPrivate");
    public static final Property status = M_MODEL.createProperty(NS + "status");
    public static final Property license = M_MODEL.createProperty(NS + "license");
    public static final Property provenance = M_MODEL.createProperty(NS + "provenance");
    public static final Property organization = M_MODEL.createProperty(NS + "organization");
    public static final Property tag = M_MODEL.createProperty(NS + "tag");
    public static final Property group = M_MODEL.createProperty(NS + "group");
    public static final Property title = M_MODEL.createProperty(NS + "title");
    public static final Property description = M_MODEL.createProperty(NS + "description");
    public static final Property politicalLevel = M_MODEL.createProperty(NS + "politicalLevel");
    public static final Property mediaType = M_MODEL.createProperty(NS + "mediaType");

    /* Classes */
    public static final Resource CkanCatalog = M_MODEL.createResource(NS + "CkanCatalog");
    public static final Resource Group = M_MODEL.createResource(NS + "Group");
    public static final Resource Tag = M_MODEL.createResource(NS + "Tag");
    public static final Resource Organization = M_MODEL.createResource(NS + "Organization");
    public static final Resource CkanDataset = M_MODEL.createResource(NS + "CkanDataset");
    public static final Resource CkanPlatform = M_MODEL.createResource(NS + "CkanPlatform");
}
