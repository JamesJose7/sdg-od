package com.jeeps.ckan_extractor.dao;

import com.jeeps.ckan_extractor.model.ConfigurationSingleton;
import com.jeeps.ckan_extractor.model.SdgConceptScheme;
import com.jeeps.ckan_extractor.model.SdgRelatedDataset;
import com.jeeps.ckan_extractor.service.SparqlService;
import org.apache.jena.rdf.model.Model;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class KnowledgeBaseDaoImpl implements KnowledgeBaseDao {

    public static final String SPARQL_ENDPOINT = ConfigurationSingleton.getInstance()
                                                    .getConfigurationRegistry().getSparqlWebEndpoint();

    @Override
    public List<SdgRelatedDataset> findAllCatalogsRelatedToOds() {
        String query = "PREFIX ods: <http://ods-od.org/schema/>\n" +
                "PREFIX dcat: <http://www.w3.org/ns/dcat#>\n" +
                "PREFIX dct: <http://purl.org/dc/terms/>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "select ?catalog ?title ?id ?ods ?odsLabel where { \n" +
                "\t?catalog ods:automaticallyAnnotatedSubject ?ods ;\n" +
                "          a dcat:Catalog ;\n" +
                "          dct:identifier ?id ;\n" +
                "          dct:title ?title .\n" +
                "    ?ods rdfs:label ?odsLabel\n" +
                "}";
        // Get sparql result
        String sparqlEndpoint = ConfigurationSingleton.getInstance()
                .getConfigurationRegistry().getSparqlWebEndpoint();
        List<List<String>> result = SparqlService.queryEndpoint(sparqlEndpoint, query,
                "catalog", "title", "id", "ods", "odsLabel");
        result.remove(0);
        List<SdgRelatedDataset> sdgRelatedDatasets = result.stream()
                .map(l -> new SdgRelatedDataset(l.get(0), l.get(1), Long.parseLong(l.get(2)), l.get(3), l.get(4)))
                .collect(Collectors.toList());
        return sdgRelatedDatasets;
    }

    @Override
    public Map<String, Integer> howManyDatasetsRelateToEachGoal() {
        String query = "PREFIX ods: <http://ods-od.org/schema/>\n" +
                "select (count(?catalog) as ?count) ?goal where { \n" +
                "\t?catalog ods:automaticallyAnnotatedSubject ?goal .\n" +
                "} group by ?goal";
        String sparqlEndpoint = ConfigurationSingleton.getInstance()
                .getConfigurationRegistry().getSparqlWebEndpoint();
        List<List<String>> result = SparqlService.queryEndpoint(sparqlEndpoint,
                query, "count", "goal");
        result.remove(0);
        // Transform into map
        Map<String, Integer> countResult = result.stream()
                .collect(Collectors.toMap(o -> o.get(1).split("SDG_")[1].replace("_", " "),
                        o -> Integer.parseInt(o.get(0))));
        // sort it
        Map<String, Integer> sortedCountResult = new TreeMap<>(countResult);
        return sortedCountResult;
    }

    @Override
    public List<SdgRelatedDataset> getRelatedOdsByDatasetId(Long id) {
        String query = "PREFIX dcat: <http://www.w3.org/ns/dcat#>\n" +
                "PREFIX dct: <http://purl.org/dc/terms/>\n" +
                "PREFIX ods: <http://ods-od.org/schema/>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "\n" +
                "select ?catalog ?id ?ods ?odsLabel where {\n" +
                " ?catalog a dcat:Catalog ;\n" +
                "          dct:identifier ?id ;\n" +
                "          ods:automaticallyAnnotatedSubject ?ods .\n" +
                " ?ods rdfs:label ?odsLabel .\n" +
                " FILTER(str(?id)=str(\"" + id  + "\"))\n" +
                "}";
        String sparqlEndpoint = ConfigurationSingleton.getInstance()
                .getConfigurationRegistry().getSparqlWebEndpoint();
        List<List<String>> result = SparqlService.queryEndpoint(sparqlEndpoint,
                query, "catalog", "id", "ods", "odsLabel");
        result.remove(0);
        // Transform into Object list
        List<SdgRelatedDataset> sdgRelatedDatasets = result.stream()
                .map(o -> new SdgRelatedDataset(o.get(0), Long.parseLong(o.get(1)), o.get(2), o.get(3)))
                .collect(Collectors.toCollection(ArrayList::new));
        return sdgRelatedDatasets;
    }

    @Override
    public SdgConceptScheme getSdgConcepts(int sdg) {
        String sparqlEndpoint = ConfigurationSingleton.getInstance()
                .getConfigurationRegistry().getSparqlWebEndpoint();
        // Get Goal concepts
        String goalQuery = String.format("PREFIX skos: <http://www.w3.org/2004/02/skos/core#>" +
                "PREFIX ods: <http://ods-od.org/data/>\n" +
                "\n" +
                "select ?conceptLabel where {\n" +
                " ?concept skos:inScheme ods:SDG_Goal_%d ;\n" +
                "          skos:prefLabel ?conceptLabel . \n" +
                "}", sdg);
        List<List<String>> goalConcepts = SparqlService.queryEndpoint(sparqlEndpoint,
                goalQuery, "conceptLabel");
        goalConcepts.remove(0);

        // Get Targets concepts
        String targetsQuery = String.format("PREFIX skos: <http://www.w3.org/2004/02/skos/core#>" +
                "select ?conceptLabel where {\n" +
                " ?scheme a skos:ConceptScheme .\n" +
                " ?concept skos:inScheme ?scheme ;\n" +
                "          skos:prefLabel ?conceptLabel . \n" +
                " FILTER regex(str(?scheme), \"SDG_Target_%d\\\\.\")\n" +
                "}", sdg);
        List<List<String>> targetConcepts = SparqlService.queryEndpoint(sparqlEndpoint,
                targetsQuery, "conceptLabel");
        targetConcepts.remove(0);

        // Get Indicators concepts
        String indicatorsQuery = String.format("PREFIX skos: <http://www.w3.org/2004/02/skos/core#>" +
                "select ?conceptLabel where {\n" +
                " ?scheme a skos:ConceptScheme .\n" +
                " ?concept skos:inScheme ?scheme ;\n" +
                "          skos:prefLabel ?conceptLabel . \n" +
                " FILTER regex(str(?scheme), \"SDG_Indicator_%d\\\\.\")\n" +
                "}", sdg);
        List<List<String>> indicatorsConcepts = SparqlService.queryEndpoint(sparqlEndpoint,
                indicatorsQuery, "conceptLabel");
        indicatorsConcepts.remove(0);

        List<String> concepts = new ArrayList<>();
        goalConcepts.parallelStream().flatMap(Collection::parallelStream)
                .forEach(concepts::add);
        targetConcepts.parallelStream().flatMap(Collection::parallelStream)
                .forEach(concepts::add);
        indicatorsConcepts.parallelStream().flatMap(Collection::parallelStream)
                .forEach(concepts::add);
        return new SdgConceptScheme(sdg + "", concepts);
    }

    @Override
    public void uploadCatalogModel(Model model) {
        String graph = "http://opendata.org/resource/";
        SparqlService.uploadModelToTriplestore(model, graph);
    }

    @Override
    public void uploadSdgModel(Model model) {
        String graph = "http://ods-od.org/data";
        SparqlService.uploadModelToTriplestore(model, graph);
    }

    @Override
    public void uploadSdgOdLinks(Model model) {
        String graph = "http://ods-od.org/data";
        SparqlService.uploadModelToTriplestore(model, graph);
    }
}
