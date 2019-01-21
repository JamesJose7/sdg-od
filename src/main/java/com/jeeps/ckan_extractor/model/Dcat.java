package com.jeeps.ckan_extractor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class Dcat {
    public static final String NAMESPACES =
            "@prefix rdfs:     <http://www.w3.org/2000/01/rdf-schema#> .\n" +
            "@prefix rdf:      <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
            "@prefix dcat:     <http://www.w3.org/ns/dcat#> .\n" +
            "@prefix dct:      <http://purl.org/dc/terms/> .\n" +
            "@prefix dctype:   <http://purl.org/dc/dcmitype/> .\n" +
            "@prefix foaf:     <http://xmlns.com/foaf/0.1/> .\n" +
            "@prefix skos:     <http://www.w3.org/2004/02/skos/core#> .\n" +
            "@prefix vcard:    <http://www.w3.org/2006/vcard/ns#> .\n" +
            "@prefix xsd:      <http://www.w3.org/2001/XMLSchema#> .\n" +
            "@prefix eg:       <http://example.org/ns#> .\n\n";

    private CkanPackage mPackage;
    private List<CkanResource> mResources;
    private String mTurtleRepresentation;

    public Dcat(DcatBuilder dcatBuilder) {
        mPackage = dcatBuilder.mPackage;
        mResources = dcatBuilder.mResources;
        mTurtleRepresentation = dcatBuilder.mTurtleRepresentation;
    }

    public CkanPackage getPackage() {
        return mPackage;
    }

    public List<CkanResource> getResources() {
        return mResources;
    }

    public String getTurtleRepresentation() {
        return mTurtleRepresentation;
    }

    public static class DcatBuilder {
        private CkanPackage mPackage;
        private List<CkanResource> mResources;
        private String mTurtleRepresentation;

        public DcatBuilder(CkanPackage aPackage) {
            mPackage = aPackage;
            mResources = new ArrayList<>();
        }

        public DcatBuilder addResource(CkanResource resource) {
            mResources.add(resource);
            return this;
        }

        public DcatBuilder withResources(List<CkanResource> resources) {
            mResources = resources;
            return this;
        }

        public Dcat build() {
            String catalog = buildCatalog(mPackage, mResources.size());
            String organization = buildOrganization(mPackage);
            String datasets = buildDatasets(mResources);

            mTurtleRepresentation = String.format(
                            "%s\n\n" +
                            "%s\n\n" +
                            "%s\n\n"
                    , catalog, organization, datasets
            );
            return new Dcat(this);
        }

        private String buildCatalog(CkanPackage aPackage, int numberOfDatasets) {
            Optional<String> datasetsOptional =
                    IntStream.range(0, numberOfDatasets)
                            .boxed()
                            .map(i -> {
                                String separator = " , ";
                                String dName = String.format("eg:dataset-%03d", i);
                                if (i < numberOfDatasets - 1)
                                    dName += separator;
                                return dName;
                            })
                            .reduce(String::concat);
            String allDatasets = datasetsOptional.orElse("");

            String cleanName = aPackage.getName().trim().replaceAll(" ", "-");
            String cleanNameLower = aPackage.getName().toLowerCase().trim().replaceAll(" ", "");
            return String.format(
                    "eg:%s-catalog\n" +
                            "\ta dcat:Catalog ;\n" +
                            "\tdct:title \"%s\" ;\n" +
                            "\trdfs:label \"%s\" ;\n" +
                            "\tfoaf:homepage <%s> ;\n" +
                            "\tdct:publisher eg:%s-publisher ;\n" +
                            "\tdct:language <http://id.loc.gov/vocabulary/iso639-1/en>  ; # pending\n" +
                            "\tdct:license \"%s\" ;\n" +
                            "\tdcat:dataset %s ; \n" +
                            "\t.",
                    cleanName,
                    aPackage.getTitle(),
                    aPackage.getTitle(),
                    aPackage.getOriginUrl(),
                    cleanNameLower,
                    aPackage.getLicense_title(),
                    allDatasets
            );
        }

        private String buildOrganization(CkanPackage aPackage) {
            String cleanNameLower = aPackage.getName().toLowerCase().trim().replaceAll(" ", "");
            return String.format(
                    "eg:%s-publisher\n" +
                            "\ta foaf:Organization ;\n" +
                            "\trdfs:label \"%s\" ;\n" +
                            "\t.",
                    cleanNameLower,
                    aPackage.getAuthor()
            );
        }

        private String buildDatasets(List<CkanResource> resources) {
            Optional<String> allDatasetsOptional =
                    IntStream.range(0, resources.size())
                            .boxed()
                            .map(i -> {
                                CkanResource resource = resources.get(i);
                                Object cleanDescription = resource.getDescription().replaceAll("\n", " ").replaceAll("\r", " ").replaceAll("\"", "'").replaceAll("[.]", "");
                                String cleanName = mPackage.getName().toLowerCase().trim().replaceAll(" ", "");
                                String cleanFormat = resource.getFormat().trim().replaceAll(" ", "-");
                                String created = resource.getCreated() != null ? resource.getCreated().replaceAll("[.]", ",") : "null";
                                String last_modified = resource.getLast_modified() != null ? resource.getLast_modified().replaceAll("[.]", ",") : "null";
                                return String.format(
                                        "eg:dataset-%03d\n" +
                                                "\ta dcat:Dataset ;\n" +
                                                "\tdct:title \"%s\" ;\n" +
                                                "\tdct:description \"%s\" ;\n" +
                                                //"\t#dcat:keyword \"accountability\",\"transparency\" ,\"payments\" ; # maybe\n" +
                                                "\tdct:issued \"%s\"^^xsd:date ;\n" +
                                                "\tdct:modified \"%s\"^^xsd:date ;\n" +
                                                //"\t#dcat:contactPoint <http://example.org/transparency-office/contact> ;\n" +
                                                //"\t#dct:temporal <http://reference.data.gov.uk/id/quarter/2006-Q1> ;\n" +
                                                //"\t#dct:spatial <http://www.geonames.org/6695072> ;\n" +
                                                "\tdct:publisher eg:%s-publisher ;\n" +
                                                "\tdct:language <http://id.loc.gov/vocabulary/iso639-1/en>  ;\n" +
                                                //"\t#dct:accrualPeriodicity <http://purl.org/linked-data/sdmx/2009/code#freq-W>  ;\n" +
                                                "\tdcat:distribution eg:dataset-%03d-%s ;\n" +
                                                "\t.\n" +
                                                "\n" +
                                        "eg:dataset-%03d-%s\n" +
                                                "\ta dcat:Distribution ;\n" +
                                                "\tdcat:downloadURL <%s> ;\n" +
                                                "\tdct:title \"%s\" ;\n" +
                                                "\tdcat:mediaType \"%s\" ;\n" +
                                                //"\t#dcat:byteSize \"5120\"^^xsd:decimal ;\n" +
                                                "\t.\n\n",
                                        i,
                                        resource.getName(),
                                        cleanDescription,
                                        created,
                                        last_modified,
                                        cleanName,
                                        i, cleanFormat,
                                        i, cleanFormat,
                                        resource.getUrl(),
                                        resource.getName(),
                                        resource.getFormat()
                                );
                            })
                            .reduce(String::concat);
            return allDatasetsOptional.orElse("");
        }
    }
}
