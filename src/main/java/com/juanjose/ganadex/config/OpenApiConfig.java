package com.juanjose.ganadex.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI/Swagger para la documentación de la API de Ganadex.
 * <p>
 * El esquema de seguridad "bearerAuth" se declara desde ya para que Swagger
 * UI muestre el botón "Authorize", aunque todavía no hay JWT implementado
 * (eso llega en el paso 9 del plan de desarrollo). Mientras tanto no tiene
 * efecto real porque {@code SecurityConfig} permite todas las peticiones.
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI ganadexOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ganadex API")
                        .description("Sistema Integral de Gestión Ganadera. API REST del backend de Ganadex.")
                        .version("v0.0.1")
                        .contact(new Contact()
                                .name("Juan Camilo")
                                .email("juancamilobernal4444@gmail.com")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME_NAME, new SecurityScheme()
                                .name(BEARER_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
