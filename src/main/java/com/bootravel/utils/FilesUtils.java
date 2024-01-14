package com.bootravel.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FilesUtils {

    public FilesUtils() {
    }

    public File createDocxFileFromBytes(byte[] bytes, String fileName) throws IOException {
        File tempFile = File.createTempFile(fileName, ".docx");
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(bytes);
        fos.close();
        return tempFile;
    }

    public File createPdfFileFromBytes(byte[] bytes, String fileName) throws IOException {
        File tempFile = File.createTempFile(fileName, ".pdf");
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(bytes);
        fos.close();
        return tempFile;
    }

}
