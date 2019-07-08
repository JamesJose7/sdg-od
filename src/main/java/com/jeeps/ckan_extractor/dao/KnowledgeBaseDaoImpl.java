package com.jeeps.ckan_extractor.dao;

import com.jeeps.ckan_extractor.model.ConfigurationSingleton;
import com.jeeps.ckan_extractor.model.SdgRelatedDataset;
import com.jeeps.ckan_extractor.service.SparqlService;
import org.apache.jena.rdf.model.Model;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
