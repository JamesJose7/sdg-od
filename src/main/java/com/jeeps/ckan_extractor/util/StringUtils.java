package com.jeeps.ckan_extractor.util;

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
                .replaceAll("_+", "_");
    }
}
