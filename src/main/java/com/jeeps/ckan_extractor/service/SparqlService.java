package com.jeeps.ckan_extractor.service;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SparqlService {

    public static List<List<String>> queryEndpoint(String endpoint, String query, String... vars) {
        QueryExecution queryExecution = QueryExecutionFactory.sparqlService(endpoint, query);
        return query(queryExecution, vars);
    }

    public static List<List<String>> queryModel(Model model, String query, String... vars) {
        QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
        return query(queryExecution, vars);
    }

    private static List<List<String>> query(QueryExecution qE, String... vars) {
        List<List<String>> resultSet = new ArrayList<>();
        resultSet.add(new ArrayList<>(Arrays.asList(vars)));

        ResultSet results = qE.execSelect();

        while (results.hasNext()) {
            List<String> row = new ArrayList<>();
            QuerySolution sol = results.nextSolution();

            // get every var
            for (String var : vars) {
                RDFNode node = sol.get(var);
                if (var.equals("count"))
                    row.add(node.toString().split("\\^")[0]);
                else
                    row.add(node.toString());
            }
            resultSet.add(row);
        }
        return resultSet;
    }
}
