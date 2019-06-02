package com.jeeps.ckan_extractor.utils;

public class StringUtils {

   public static String upperCaseFirst(String string) {
       if (string.length() > 1)
        return string.substring(0,1).toUpperCase() + string.substring(1);
       return string;
   }

    public static String urlify(String string) {
        return string.trim().toLowerCase()
                .replaceAll(" ", "_")
                .replaceAll("_", "_")
                .replaceAll("\"", "")
                .replaceAll("\\.", "_")
                .replaceAll("\n", "_")
                .replaceAll("\t", "_")
                .replaceAll("\\t", "_")
                .replaceAll("\\n", "_")
                .replaceAll("\\r", "_")
                .replaceAll(":", "_")
                .replaceAll("\\[", "_")
                .replaceAll("]", "_")
                .replaceAll("\\(", "_")
                .replaceAll("\\)", "_")
                .replaceAll("%", "_")
                .replaceAll("\\^", "_")
                .replaceAll(",", "_")
                .replaceAll("'", "_")
                .replaceAll("&", "_")
                .replaceAll("\\*", "_")
                .replaceAll("[^\\w\\s]", "_")
                .replaceAll("_+", "_");
    }

    public static String removeUrlProtocol(String url) {
       return url.replace("https://", "").replace("http://", "");
    }
}
