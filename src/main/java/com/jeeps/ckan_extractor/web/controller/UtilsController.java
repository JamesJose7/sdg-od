package com.jeeps.ckan_extractor.web.controller;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class UtilsController {
    @RequestMapping(value = "/files/{file_name}", method = RequestMethod.GET)
    public void getTempFile(@PathVariable("file_name") String fileName,
                        HttpServletResponse response) {
        try {
            // Add temp dir
            fileName = "temp/" + fileName;
            // get your file as InputStream
            InputStream is = new FileInputStream(fileName);

            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setContentType("application/xml");
            FileCopyUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
            deleteTempFile(fileName);
        } catch (IOException ex) {
//            Log.info("Error writing file to output stream. Filename was '{}'", fileName, ex);
            throw new RuntimeException("IOError writing file to output stream");
        }
        System.out.println("Finished");
    }

    @RequestMapping(value = "/rdf-cache/{file_name}", method = RequestMethod.GET)
    public void getFile(@PathVariable("file_name") String fileName,
                        HttpServletResponse response) {
        try {
            // Add temp dir
            fileName = "rdf/" + fileName;
            // get your file as InputStream
            InputStream is = new FileInputStream(fileName);

            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setContentType("application/xml");
            FileCopyUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
//            Log.info("Error writing file to output stream. Filename was '{}'", fileName, ex);
            throw new RuntimeException("IOError writing file to output stream");
        }
        System.out.println("Finished");
    }

    // TODO: Scheduled task to delete files that weren't opened
    @Async("asyncExecutor")
    public void deleteTempFile(String tempFile) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Delete file after 1 minute of clicking the download link
        File file = new File(tempFile);
        if (file.delete())
            System.out.println("File delete successfully");
        else
            System.out.println("Could not delete file");
    }
}
