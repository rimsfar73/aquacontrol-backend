
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // AUTH
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/login"
                        ).permitAll()

                        // ESTADO DEL BACKEND
                        .requestMatchers(
                                HttpMethod.GET,
                                "/"
                        ).permitAll()

                        // PERFIL DEL USUARIO
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/usuarios/me"
                        ).authenticated()

                        // FLUSHING Y TEMPERATURA
                        // DELETE: solo SUPERVISOR
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/flushing/**",
                                "/api/v1/temperatura/**"
                        ).hasRole("SUPERVISOR")

                        // Operaciones restantes: ambos roles
                        .requestMatchers(
                                "/api/v1/flushing/**",
                                "/api/v1/temperatura/**"
                        ).hasAnyRole("OPERARIO", "SUPERVISOR")

                        // GRANJAS
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/granjas"
                        ).hasRole("SUPERVISOR")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/granjas/**"
                        ).hasRole("SUPERVISOR")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/granjas/**"
                        ).hasRole("SUPERVISOR")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/granjas",
                                "/api/v1/granjas/**"
                        ).hasAnyRole("OPERARIO", "SUPERVISOR")

                        // GALPONES
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/galpones"
                        ).hasRole("SUPERVISOR")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/galpones/**"
                        ).hasRole("SUPERVISOR")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/galpones/**"
                        ).hasRole("SUPERVISOR")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/galpones",
                                "/api/v1/galpones/**"
                        ).hasAnyRole("OPERARIO", "SUPERVISOR")

                        // LINEAS
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/lineas"
                        ).hasRole("SUPERVISOR")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/lineas/**"
                        ).hasRole("SUPERVISOR")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/lineas/**"
                        ).hasRole("SUPERVISOR")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/lineas",
                                "/api/v1/lineas/**"
                        ).hasAnyRole("OPERARIO", "SUPERVISOR")

                        // FOTOS
                        // GET: ambos roles
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/fotos",
                                "/api/v1/fotos/**"
                        ).hasAnyRole("SUPERVISOR", "OPERARIO")

                        // POST: ambos roles
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/fotos"
                        ).hasAnyRole("SUPERVISOR", "OPERARIO")

                        // PUT: ambos roles
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/fotos/**"
                        ).hasAnyRole("SUPERVISOR", "OPERARIO")

                        // DELETE: solo SUPERVISOR
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/fotos/**"
                        ).hasRole("SUPERVISOR")

                        // DENEGAR TODAS LAS DEMAS RUTAS
                        .anyRequest().denyAll()
                )

                .oauth2ResourceServer(oauth ->
                        oauth.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtConverter
                                )
                        )
                )

                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())

                .build();
    }
}
