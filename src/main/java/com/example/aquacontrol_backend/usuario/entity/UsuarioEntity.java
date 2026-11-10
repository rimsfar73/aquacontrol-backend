package com.example.aquacontrol_backend.usuario.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "usuario",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_usuario_username",
                columnNames = "username"
        )
)
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String username;

    @Column(
            name = "password_hash",
            nullable = false,
            length = 100
    )
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Column(nullable = false)
    private boolean activo = true;

    protected UsuarioEntity() {
    }

    public UsuarioEntity(
            String username,
            String passwordHash,
            Rol rol
    ) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.activo = true;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Rol getRol() {
        return rol;
    }

    public boolean isActivo() {
        return activo;
    }
}
