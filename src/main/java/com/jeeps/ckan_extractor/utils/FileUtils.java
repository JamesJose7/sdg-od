package com.jeeps.ckan_extractor.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    public static void writeContentsToFile(String fileName, String content) throws IOException {
        Path path = Paths.get(fileName);
        byte[] strToBytes = content.getBytes();
        Files.createDirectories(path.getParent());
        Files.write(path, strToBytes);
    }
}
