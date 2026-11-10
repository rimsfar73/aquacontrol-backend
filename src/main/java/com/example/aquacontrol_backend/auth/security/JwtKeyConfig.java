package com.example.aquacontrol_backend.auth.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.oauth2.jwt.*;

import java.nio.file.Files;
import java.nio.file.Path;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import java.util.Base64;

@Configuration
public class JwtKeyConfig {

    @Bean
    public RSAKey rsaKey(
            @Value("${auth.jwt.private-key-path}") String privatePath,
            @Value("${auth.jwt.public-key-path}") String publicPath
    ) throws Exception {

        KeyFactory factory = KeyFactory.getInstance("RSA");

        byte[] privateBytes = leerPem(
                privatePath,
                "PRIVATE KEY"
        );

        byte[] publicBytes = leerPem(
                publicPath,
                "PUBLIC KEY"
        );

        PrivateKey privateKey = factory.generatePrivate(
                new PKCS8EncodedKeySpec(privateBytes)
        );

        RSAPublicKey publicKey = (RSAPublicKey)
                factory.generatePublic(
                        new X509EncodedKeySpec(publicBytes)
                );

        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID("aquacontrol-local-1")
                .build();
    }

    @Bean
    public JwtEncoder jwtEncoder(RSAKey rsaKey) {

        return new NimbusJwtEncoder(
                new ImmutableJWKSet<>(
                        new JWKSet(rsaKey)
                )
        );
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAKey rsaKey) throws JOSEException {

        NimbusJwtDecoder decoder =
                NimbusJwtDecoder
                        .withPublicKey(rsaKey.toRSAPublicKey())
                        .build();

        decoder.setJwtValidator(
                JwtValidators.createDefaultWithIssuer(
                        "aquacontrol-backend"
                )
        );

        return decoder;
    }

    private byte[] leerPem(
            String ruta,
            String tipo
    ) throws Exception {

        String contenido = Files.readString(Path.of(ruta.trim()));

        String base64 = contenido
                .replace("-----BEGIN " + tipo + "-----", "")
                .replace("-----END " + tipo + "-----", "")
                .replaceAll("\\s", "");

        return Base64.getDecoder().decode(base64);
    }
}