package com.jeeps.ckan_extractor.utils;

public class WebUtils {

    public static String getFileTypeIcon(String type) {
        type = type.toLowerCase();
        if (type.contains("zip")) return "zip";
        if (type.contains("rar")) return "zip";
        if (type.contains("tsv")) return "csv";
        if (type.contains("html")) return "html";
        if (type.contains("xls")) return "xls";
        if (type.contains("xml")) return "xml";
        if (type.contains("pdf")) return "pdf";
        if (type.contains("png")) return "png";
        if (type.contains("csv")) return "csv";
        if (type.contains("json")) return "json-file";
        if (type.contains("txt")) return "txt";
        if (type.contains("rdf")) return "rdf"; // TODO: Find an icon for RDF
        if (type.contains("doc")) return "doc";
        if (type.contains("dbf")) return "dbf";
        if (type.contains("sparql")) return "dbf";
        if (type.contains("gif")) return "png"; // TODO: Find an icon for GIF
        if (type.contains("ppt")) return "ppt";
        if (type.contains("powerpoint")) return "ppt";
        if (type.contains("jpeg")) return "png"; // TODO: Find an icon for JPEG
        if (type.contains("tar")) return "zip-1";
        if (type.contains("excel")) return "xls";
        if (type.contains("spreadsheet")) return "xls";
        if (type.contains("sheet")) return "xls";
        if (type.contains("url")) return "search";
        if (type.contains("web")) return "search";
        /*TODO: Get more icons:
        *  ttl, rest api?, yaml*/
        // default
        return "file";
    }
}
