package com.k48.lib48.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService implements FileServiceInt {

    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        //get the name of the file
        String fileName = file.getOriginalFilename();
        String fileExtension = "";

        if (fileName != null && fileName.contains(".")) {
            fileExtension = fileName.substring(fileName.lastIndexOf("."));
        }

        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;


        // Créer le répertoire s'il n'existe pas
        Path uploadPath = Paths.get(path);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        //to get the file path
        String filePath = path + File.separator + uniqueFileName;

        //create a file object
        File f = new File(path);
        if(!f.exists()){
            f.mkdirs();
        }

        //copy the file or upload the file to the path
        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

        return uniqueFileName;
    }

    @Override
    public InputStream getResourceFile(String path, String filename) throws  IOException {
        String filePath = path + File.separator + filename;
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }
        return new FileInputStream(file);

    }
    @Override
    public boolean fileExists(String path, String filename) {
        String filePath = path + File.separator + filename;
        return Files.exists(Paths.get(filePath));
    }

    public void deleteFile(String path, String filename) throws IOException {
        String filePath = path + File.separator + filename;
        Files.deleteIfExists(Paths.get(filePath));
    }

}
