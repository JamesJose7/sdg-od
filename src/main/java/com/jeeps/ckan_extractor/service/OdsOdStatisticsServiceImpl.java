package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.core.OdsOdLinker;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class OdsOdStatisticsServiceImpl implements OdsOdStatisticsService {

    @Override
    public Map<String, Integer> howManyDatasetsRelateToEachGoal() {
        String query = "PREFIX ods: <http://ods-od.org/schema/>\n" +
                "select (count(?catalog) as ?count) ?goal where { \n" +
                "\t?catalog ods:automaticallyAnnotatedSubject ?goal .\n" +
                "} group by ?goal";
        List<List<String>> result = SparqlService.queryEndpoint(OdsOdLinker.SPARQL_ENDPOINT,
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
}
