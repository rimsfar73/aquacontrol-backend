
package com.example.aquacontrol_backend.auth.security;

import com.example.aquacontrol_backend.usuario.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
public class SecurityConfig {

    // Conservamos BCrypt para comprobar las contraseñas almacenadas.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Conectamos la autenticación con los usuarios de nuestra base.
    @Bean
    public AuthenticationManager authenticationManager(
            UsuarioService usuarioService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(usuarioService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    // Obtenemos el rol del token validado y aceptamos solo nuestros dos roles.
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String rol = jwt.getClaimAsString("rol");

            if (!"ROLE_OPERARIO".equals(rol)
                    && !"ROLE_SUPERVISOR".equals(rol)) {
                return List.of();
            }

            return List.of(new SimpleGrantedAuthority(rol));
        });

        return converter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtConverter
    ) throws Exception {
        return http
                // API REST sin sesiones de servidor.
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(auth -> auth

                        // SWAGGER / OPENAPI
                        // Acceso a la documentación sin iniciar sesión.
                        // Las operaciones protegidas siguen requiriendo JWT.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // AUTENTICACIÓN
                        // Permitimos iniciar sesión.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/login"
                        ).permitAll()

                        // ESTADO DEL SERVIDOR
                        .requestMatchers(
                                HttpMethod.GET,
                                "/"
                        ).permitAll()

                        // METEOROLOGÍA - OPEN-METEO
                        // Consulta pública de temperatura y humedad exterior.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/weather"
                        ).permitAll()

                        // PERFIL DE USUARIO
                        // Cada usuario autenticado puede consultar su perfil.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/usuarios/me"
                        ).authenticated()

                        // FLUSHING Y TEMPERATURA - CONSULTAS
                        // Operario y Supervisor pueden consultar registros.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/flushing",
                                "/api/v1/flushing/**",
                                "/api/v1/temperatura",
                                "/api/v1/temperatura/**"
                        ).hasAnyRole("OPERARIO", "SUPERVISOR")

                        // FLUSHING Y TEMPERATURA - CREACIÓN
                        // Solo Operario puede crear registros.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/flushing",
                                "/api/v1/temperatura"
                        ).hasRole("OPERARIO")

                        // FLUSHING Y TEMPERATURA - EDICIÓN
                        // Solo Operario puede editar o subir evidencias.
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/flushing/*",
                                "/api/v1/flushing/*/foto",
                                "/api/v1/temperatura/*"
                        ).hasRole("OPERARIO")

                        // FLUSHING Y TEMPERATURA - ELIMINACIÓN
                        // Solo Supervisor puede eliminar registros.
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/flushing/*",
                                "/api/v1/temperatura/*"
                        ).hasRole("SUPERVISOR")

                        // ESTRUCTURA - CONSULTAS
                        // Ambos roles pueden consultar granjas, galpones y líneas.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/granjas",
                                "/api/v1/granjas/**",
                                "/api/v1/galpones",
                                "/api/v1/galpones/**",
                                "/api/v1/lineas",
                                "/api/v1/lineas/**"
                        ).hasAnyRole("OPERARIO", "SUPERVISOR")

                        // ESTRUCTURA - CREACIÓN
                        // Solo Supervisor administra granjas, galpones y líneas.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/granjas",
                                "/api/v1/galpones",
                                "/api/v1/lineas"
                        ).hasRole("SUPERVISOR")

                        // ESTRUCTURA - EDICIÓN
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/granjas/**",
                                "/api/v1/galpones/**",
                                "/api/v1/lineas/**"
                        ).hasRole("SUPERVISOR")

                        // ESTRUCTURA - ELIMINACIÓN
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/granjas/**",
                                "/api/v1/galpones/**",
                                "/api/v1/lineas/**"
                        ).hasRole("SUPERVISOR")

                        // SEGURIDAD POR DEFECTO
                        // Toda ruta no declarada queda bloqueada.
                        .anyRequest().denyAll()
                )

                // Validación JWT Bearer.
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtConverter)
                        )
                )

                // Deshabilitamos autenticación Basic y formularios.
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .build();
    }
}
