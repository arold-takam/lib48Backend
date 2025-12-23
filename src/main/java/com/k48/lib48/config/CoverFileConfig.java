package com.k48.lib48.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CoverFileConfig implements WebMvcConfigurer {
    
    // Chemin absolu vers le dossier uploads à la racine du projet
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        // Mappe l'URL /api/uploads/** vers le dossier physique
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + UPLOAD_DIR + "/");
    }
    
    @PostConstruct
    public void logUploadDir() {
        System.out.println("[CONFIG] Dossier Uploads : " + UPLOAD_DIR);
    }
}