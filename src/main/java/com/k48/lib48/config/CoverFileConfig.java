package com.k48.lib48.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CoverFileConfig implements WebMvcConfigurer {

    private static final String UPLOAD_DIR=System.getProperty("user.dir") + "/uploads/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        // Le chemin d'accès public que l'utilisateur va utiliser (par exemple, http://localhost:8080/uploads/image.jpg)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + UPLOAD_DIR);

        // Mappe aussi l'URL si vous utilisez un préfixe d'API (server.servlet.context-path=/api)
        registry.addResourceHandler("/api/uploads/**")
                .addResourceLocations("file:" + UPLOAD_DIR);
    }

}
