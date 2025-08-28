package com.k48.lib48.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Lib48 API")
                        .version("1.0")
                        .description("API pour la gestion de la bibliothèque Lib48")
                        .contact(new Contact()
                                .name("Support")
                                .email("support@lib48.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .components(new Components()
                        .addSchemas("MultipartFile", new Schema()
                                .type("string")
                                .format("binary")
                                .description("Fichier uploadé")));
    }
}