package com.example.aquacontrol_backend.flushing.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// Representamos cada flushing como una fila de la tabla en PostgreSQL.
// FlushingService utiliza esta clase para guardar y consultar los datos.
@Entity
@Table(
        name = "flushing",
        indexes = @Index(
                name = "idx_flushing_linea",
                columnList = "linea_id"
        )
)
public class FlushingEntity {

    // Dejamos que PostgreSQL genere el identificador de cada registro.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Asociamos el flushing con la línea donde se realizó.
    @Column(name = "linea_id", nullable = false)
    private Long lineaId;

    // Conservamos la fecha y hora del evento.
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    // Guardamos la duración en segundos, igual que en Android.
    @Column(name = "duracion_segundos", nullable = false)
    private Integer duracionSegundos;

    // Admitimos hasta 500 caracteres, como permite FlushingDTO.
    @Column(name = "observacion", length = 500)
    private String observacion;

    // Guardamos las temperaturas para comparar antes y después del flushing.
    // Permitimos null para conservar registros antiguos que no tenían estos datos.
    @Column(name = "temperatura_antes")
    private Double temperaturaAntes;

    @Column(name = "temperatura_despues")
    private Double temperaturaDespues;

    // Estos métodos permiten que el servicio consulte y complete el registro.
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLineaId() {
        return lineaId;
    }

    public void setLineaId(Long lineaId) {
        this.lineaId = lineaId;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Integer getDuracionSegundos() {
        return duracionSegundos;
    }

    public void setDuracionSegundos(Integer duracionSegundos) {
        this.duracionSegundos = duracionSegundos;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Double getTemperaturaAntes() {
        return temperaturaAntes;
    }

    public void setTemperaturaAntes(Double temperaturaAntes) {
        this.temperaturaAntes = temperaturaAntes;
    }

    public Double getTemperaturaDespues() {
        return temperaturaDespues;
    }

    public void setTemperaturaDespues(Double temperaturaDespues) {
        this.temperaturaDespues = temperaturaDespues;
    }
}