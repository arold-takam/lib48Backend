package com.k48.lib48.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface FileServiceInt {
    String uploadFile(String path , MultipartFile file)throws IOException;

    InputStream getResourceFile(String path, String filename)throws IOException;

    boolean fileExists(String path, String filename);

    void deleteFile(String path, String filename) throws IOException;
}
