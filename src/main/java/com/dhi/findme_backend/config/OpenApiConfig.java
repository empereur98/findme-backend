package com.dhi.findme_backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI findmeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FindMe API")
                        .description("FindMe Backend REST API - Contract-first design following Spring Boot best practices")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("FindMe Team")
                                .email("contact@findme.com")
                                .url("https://findme.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://choosealicense.com/licenses/mit/")))
                .externalDocs(new ExternalDocumentation()
                        .description("FindMe Documentation")
                        .url("https://docs.findme.com"))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development Server"),
                        new Server().url("https://api.staging.findme.com").description("Staging Server"),
                        new Server().url("https://api.findme.com").description("Production Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token for authentication"))
                        .addSchemas("ErrorResponse", new Schema<>()
                                .type("object")
                                .addProperties("type", new Schema<>().type("string").description("Error type URI"))
                                .addProperties("title", new Schema<>().type("string").description("Error title"))
                                .addProperties("status", new Schema<>().type("integer").description("HTTP status code"))
                                .addProperties("detail", new Schema<>().type("string").description("Error detail"))
                                .addProperties("instance", new Schema<>().type("string").description("Request path"))
                                .addProperties("timestamp", new Schema<>().type("string").format("date-time").description("Error timestamp"))
                                .addProperties("errors", new Schema<>().type("object").description("Validation errors (if applicable)")))
                );
    }

    // @Bean
    // public OpenApiCustomizer openApiCustomizer() {
    //     return openApi -> {
    //         openApi.getPaths().forEach((path, pathItem) -> {
    //             if (pathItem.getGet() != null) {
    //                 pathItem.getGet().addTagsItem(path.split("/")[2]);
    //             }
    //             if (pathItem.getPost() != null) {
    //                 pathItem.getPost().addTagsItem(path.split("/")[2]);
    //             }
    //             if (pathItem.getPut() != null) {
    //                 pathItem.getPut().addTagsItem(path.split("/")[2]);
    //             }
    //             if (pathItem.getDelete() != null) {
    //                 pathItem.getDelete().addTagsItem(path.split("/")[2]);
    //             }
    //             if (pathItem.getPatch() != null) {
    //                 pathItem.getPatch().addTagsItem(path.split("/")[2]);
    //             }
    //         });
    //     };
    // }
}