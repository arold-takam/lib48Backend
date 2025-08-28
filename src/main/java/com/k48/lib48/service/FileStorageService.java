package com.k48.lib48.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    private  final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload.directory:./uploads/imagesCouvertures}") String uploadDirectory) {
        this.fileStorageLocation = Paths.get(uploadDirectory).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
            logger.info("Created a new directory: {}", this.fileStorageLocation);
        } catch (IOException e) {
            logger.error("Could not create a new directory", this.fileStorageLocation,e);
            throw new RuntimeException("Impossible de créer le dossier de stockage",e);
        }
    }

    public String storeFile(MultipartFile file, Long IdLivre) {

      // Générer un nom de fichier unique
      String originalFilename = file.getOriginalFilename();
      String fileExtension = "";
      if (originalFilename != null && originalFilename.contains(".")) {
          fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") );
      }
      String fileName = "livre_" + IdLivre + "_" + UUID.randomUUID().toString() + fileExtension;
      logger.debug("début de stockage du fichier . Nom originl:{}, id livre:{}", originalFilename, IdLivre);
      logger.debug("Nom de fichier généré:{}", fileName);

      try {
          //vérifier que le fichier n'est pas vide
          if (file.isEmpty()) {
              logger.warn("Tentative de stockage d'un fichier vide pour le livre id:{}", IdLivre);
              throw new RuntimeException("Le fichier est vide !");
          }

          logger.debug("Taille du fichier :{} bytes", file.getSize());
          logger.debug("Type MIME du fichier :{}", file.getContentType());

          // vérifier les types de fichiers (sécurité)
          if(!isImageFile(file)){
              logger.warn("Tentative de stockage d'un fichier non-image .Type:{} pour le livre id:{}", file.getContentType(), IdLivre);
              throw new RuntimeException("Seules les images sont autorisées!");
          }

          logger.debug("validation du type réussie");

          //copier le fichier vers le dossier de destination
          Path targetLocation = this.fileStorageLocation.resolve(fileName);
          logger.debug("Emplacement cible:{}", targetLocation);

          Files.copy(file.getInputStream(),targetLocation, StandardCopyOption.REPLACE_EXISTING);
          logger.debug("Fichier stocké avec succès:{} pour le livre id:{}", fileName, IdLivre);
          return fileName;
      }catch (IOException e){
          logger.error("Erreur IOException lors du stockage du fichier: {}", fileName, e);
          throw new RuntimeException("Erreur lors du stockage du fichier"+ fileName,e);
      }catch (Exception e){
          logger.error("Erreur inattendue lors du stockage du fichier: {}", fileName, e);
          throw new RuntimeException("Erreur lors du stockage du fichier " + fileName, e);
      }

    }

    public void deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        }catch (IOException e){
            throw new RuntimeException("Erreur lors de la suppresion du fichier"+fileName,e);
        }
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    public Path loadFile(String fileName) {
        return this.fileStorageLocation.resolve(fileName).normalize();
    }

}
