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
                // Nuestra API recibe tokens y no utiliza sesiones con cookies.
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(auth -> auth

                        // Permitimos iniciar sesión y consultar el estado.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/login"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/"
                        ).permitAll()

                        // METEOROLOGIA - OPEN-METEO
                        // Consulta publica de temperatura y humedad exterior
                        .requestMatchers(
                        HttpMethod.GET,
                        "/api/weather"
                        ).permitAll()

                       // Cada usuario autenticado puede consultar su perfil.
                       .requestMatchers(
                       HttpMethod.GET,
                       "/api/v1/usuarios/me"
                       ).authenticated()
                   
                        // Ambos roles pueden consultar registros y sus fotos.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/flushing",
                                "/api/v1/flushing/**",
                                "/api/v1/temperatura",
                                "/api/v1/temperatura/**"
                        ).hasAnyRole("OPERARIO", "SUPERVISOR")

                        // Solo el operario puede crear registros.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/flushing",
                                "/api/v1/temperatura"
                        ).hasRole("OPERARIO")

                        // Solo el operario puede editar o subir la evidencia.
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/flushing/*",
                                "/api/v1/flushing/*/foto",
                                "/api/v1/temperatura/*"
                        ).hasRole("OPERARIO")

                        // Solo el supervisor elimina registros.
                        // La foto se elimina junto con el flushing desde el servicio.
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/flushing/*",
                                "/api/v1/temperatura/*"
                        ).hasRole("SUPERVISOR")

                        // Conservamos la consulta de la estructura para ambos roles.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/granjas",
                                "/api/v1/granjas/**",
                                "/api/v1/galpones",
                                "/api/v1/galpones/**",
                                "/api/v1/lineas",
                                "/api/v1/lineas/**"
                        ).hasAnyRole("OPERARIO", "SUPERVISOR")

                        // Conservamos los permisos de administración de Rimsky
                        // para granjas, galpones y líneas.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/granjas",
                                "/api/v1/galpones",
                                "/api/v1/lineas"
                        ).hasRole("SUPERVISOR")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/granjas/**",
                                "/api/v1/galpones/**",
                                "/api/v1/lineas/**"
                        ).hasRole("SUPERVISOR")
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/granjas/**",
                                "/api/v1/galpones/**",
                                "/api/v1/lineas/**"
                        ).hasRole("SUPERVISOR")

                        // Las rutas que no definimos explícitamente quedan cerradas.
                        .anyRequest().denyAll()
                )

                // Conservamos la validación JWT incorporada por Rimsky.
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtConverter)
                        )
                )
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .build();
    }
}