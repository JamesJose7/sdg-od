package com.jeeps.ckan_extractor.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    public static String GLOBAL_PATH;

    public static void writeContentsToFile(String fileName, String content) throws IOException {
        Path path = Paths.get(fileName);
        byte[] strToBytes = content.getBytes();
        Files.createDirectories(path.getParent());
        Files.write(path, strToBytes);
    }

    public static boolean isFilePresent(String fileName) {
        return new File(GLOBAL_PATH + fileName).exists();
    }

    public static FileOutputStream createFOS(String fileName, String path) throws IOException {
        File temp = new File(GLOBAL_PATH + path);
        if (!(temp.exists()))
            Files.createDirectories(temp.toPath()); // Create temp directory if id doesn't exist
        File fos = new File(GLOBAL_PATH + path + fileName);
        return new FileOutputStream(fos);
    }
}
