package com.jeeps.ckan_extractor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class Dcat {
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
                                String dName = String.format(":dataset-%03d", i);
                                if (i < numberOfDatasets - 1)
                                    dName += separator;
                                return dName;
                            })
                            .reduce(String::concat);
            String allDatasets = datasetsOptional.orElse("");

            String cleanName = aPackage.getName().trim().replaceAll(" ", "-");
            String cleanNameLower = aPackage.getName().toLowerCase().trim().replaceAll(" ", "");
            return String.format(
                    ":%s-catalog\n" +
                            "\ta dcat:Catalog ;\n" +
                            "\tdct:title \"%s\" ;\n" +
                            "\trdfs:label \"%s\" ;\n" +
                            "\tfoaf:homepage <%s> ;\n" +
                            "\tdct:publisher :%s-publisher ;\n" +
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
                    ":%s-publisher\n" +
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
                                Object cleanDescription = resource.getDescription().replaceAll("\n", " ").replaceAll("\r", " ");
                                String cleanName = mPackage.getName().toLowerCase().trim().replaceAll(" ", "");
                                String cleanFormat = resource.getFormat().trim().replaceAll(" ", "-");
                                return String.format(
                                        ":dataset-%03d\n" +
                                                "\ta dcat:Dataset ;\n" +
                                                "\tdct:title \"%s\" ;\n" +
                                                "\tdct:description \"%s\"\n" +
                                                "\t#dcat:keyword \"accountability\",\"transparency\" ,\"payments\" ; # maybe\n" +
                                                "\tdct:issued \"%s\"^^xsd:date ;\n" +
                                                "\tdct:modified \"%s\"^^xsd:date ;\n" +
                                                "\t#dcat:contactPoint <http://example.org/transparency-office/contact> ;\n" +
                                                "\t#dct:temporal <http://reference.data.gov.uk/id/quarter/2006-Q1> ;\n" +
                                                "\t#dct:spatial <http://www.geonames.org/6695072> ;\n" +
                                                "\tdct:publisher :%s-publisher ;\n" +
                                                "\tdct:language <http://id.loc.gov/vocabulary/iso639-1/en>  ;\n" +
                                                "\t#dct:accrualPeriodicity <http://purl.org/linked-data/sdmx/2009/code#freq-W>  ;\n" +
                                                "\tdcat:distribution :dataset-%03d-%s ;\n" +
                                                "\t.\n" +
                                                "\n" +
                                        ":dataset-%03d-%s\n" +
                                                "\ta dcat:Distribution ;\n" +
                                                "\tdcat:downloadURL <%s> ;\n" +
                                                "\tdct:title \"%s\" ;\n" +
                                                "\tdcat:mediaType \"%s\" ;\n" +
                                                "\t#dcat:byteSize \"5120\"^^xsd:decimal ;\n" +
                                                "\t.\n\n",
                                        i,
                                        resource.getName(),
                                        cleanDescription,
                                        resource.getCreated(),
                                        resource.getLast_modified(),
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
