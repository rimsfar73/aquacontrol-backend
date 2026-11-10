
package com.example.aquacontrol_backend.galpon.entity;

import com.example.aquacontrol_backend.granja.entity.GranjaEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "galpon")
public class GalponEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "granja_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_galpon_granja")
    )
    private GranjaEntity granja;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public GranjaEntity getGranja() {
        return granja;
    }

    public void setGranja(GranjaEntity granja) {
        this.granja = granja;
    }
}
