
package com.example.aquacontrol_backend.linea.entity;

import com.example.aquacontrol_backend.galpon.entity.GalponEntity;
import jakarta.persistence.*;

@Entity
@Table(
        name = "linea",
        indexes = @Index(
                name = "idx_linea_galpon",
                columnList = "galpon_id"
        )
)
public class LineaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "galpon_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_linea_galpon"
            )
    )
    private GalponEntity galpon;

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

    public GalponEntity getGalpon() {
        return galpon;
    }

    public void setGalpon(GalponEntity galpon) {
        this.galpon = galpon;
    }
}

