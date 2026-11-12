
package com.example.aquacontrol_backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI aquaControlOpenAPI() {

        return new OpenAPI()

                // Información general de la API.
                .info(new Info()
                        .title("AquaControl API REST")
                        .version("1.0.0")
                        .description(
                                "API REST para la gestión de AquaControl. " +
                                        "Permite administrar granjas, galpones, líneas, " +
                                        "temperaturas, registros de flushing, " +
                                        "evidencias fotográficas y usuarios. " +
                                        "Incluye autenticación JWT y consulta meteorológica."
                        )
                )

                // Configuración de autenticación JWT Bearer.
                .components(new Components()
                        .addSecuritySchemes(
                                SECURITY_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description(
                                                "Ingrese el token JWT obtenido " +
                                                        "desde POST /api/v1/auth/login."
                                        )
                        )
                )

                // Declara JWT como seguridad predeterminada
                // en la documentación OpenAPI.
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(SECURITY_SCHEME)
                );
    }
}
