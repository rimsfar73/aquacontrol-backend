package com.example.aquacontrol_backend.flushing.hateoas;

import com.example.aquacontrol_backend.flushing.dto.FlushingDTO;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

/**
 * Representación hipermedia de un registro de flushing.
 * Conserva los campos JSON actuales y añade los enlaces en _links.
 */
public class FlushingResource extends RepresentationModel<FlushingResource> {

    private final Long id;
    private final Long lineaId;
    private final LocalDateTime fechaHora;
    private final Integer duracionSegundos;
    private final String observacion;
    private final Double temperaturaAntes;
    private final Double temperaturaDespues;

    public FlushingResource(FlushingDTO dto) {
        this.id = dto.id();
        this.lineaId = dto.lineaId();
        this.fechaHora = dto.fechaHora();
        this.duracionSegundos = dto.duracionSegundos();
        this.observacion = dto.observacion();
        this.temperaturaAntes = dto.temperaturaAntes();
        this.temperaturaDespues = dto.temperaturaDespues();
    }

    public Long getId() {
        return id;
    }

    public Long getLineaId() {
        return lineaId;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public Integer getDuracionSegundos() {
        return duracionSegundos;
    }

    public String getObservacion() {
        return observacion;
    }

    public Double getTemperaturaAntes() {
        return temperaturaAntes;
    }

    public Double getTemperaturaDespues() {
        return temperaturaDespues;
    }
}
