package com.example.aquacontrol_backend.config;

import com.example.aquacontrol_backend.usuario.entity.Rol;
import com.example.aquacontrol_backend.usuario.entity.UsuarioEntity;
import com.example.aquacontrol_backend.usuario.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@Profile("demo")
public class UsuariosDemoConfig {

    private static final Logger log =
            LoggerFactory.getLogger(UsuariosDemoConfig.class);

    @Bean
    CommandLineRunner inicializarUsuariosDemo(
            UsuarioRepository usuarios,
            PasswordEncoder passwordEncoder,
            TransactionTemplate transacciones,
            @Value("${aqua.demo.operario-password}") String claveOperario,
            @Value("${aqua.demo.supervisor-password}") String claveSupervisor
    ) {
        return args -> {
            validarClave(claveOperario, "operario");
            validarClave(claveSupervisor, "supervisor");

            transacciones.executeWithoutResult(status -> {
                crearSiFalta(
                        usuarios,
                        passwordEncoder,
                        "operario",
                        claveOperario,
                        Rol.OPERARIO
                );

                crearSiFalta(
                        usuarios,
                        passwordEncoder,
                        "supervisor",
                        claveSupervisor,
                        Rol.SUPERVISOR
                );
            });
        };
    }

    private static void crearSiFalta(
            UsuarioRepository usuarios,
            PasswordEncoder passwordEncoder,
            String username,
            String clave,
            Rol rol
    ) {
        var existente = usuarios.findByUsername(username);

        if (existente.isPresent()) {
            var usuario = existente.get();

            if (!usuario.isActivo() || usuario.getRol() != rol) {
                throw new IllegalStateException(
                        "La cuenta " + username
                                + " existe, pero su estado o rol no coincide. "
                                + "Revísala antes de continuar."
                );
            }

            log.info(
                    "Cuenta {} existente: se conserva su contraseña",
                    username
            );
            return;
        }

        usuarios.save(
                new UsuarioEntity(
                        username,
                        passwordEncoder.encode(clave),
                        rol
                )
        );

        log.info("Cuenta {} creada con rol {}", username, rol);
    }

    private static void validarClave(String clave, String username) {
        if (clave == null || clave.isBlank()) {
            throw new IllegalStateException(
                    "Falta configurar la contraseña demo de " + username
            );
        }
    }
}