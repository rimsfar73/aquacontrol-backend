package com.example.aquacontrol_backend.temperatura.hateoas;

import com.example.aquacontrol_backend.temperatura.dto.TemperaturaDTO;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

/**
 * Representación hipermedia de una temperatura.
 * Mantiene los campos JSON actuales para no romper los clientes Android
 * y agrega los enlaces HATEOAS en _links.
 */
public class TemperaturaResource extends RepresentationModel<TemperaturaResource> {

    private final Long id;
    private final Long lineaId;
    private final Double temperatura;
    private final LocalDateTime fechaHora;
    private final String observacion;

    public TemperaturaResource(TemperaturaDTO dto) {
        this.id = dto.id();
        this.lineaId = dto.lineaId();
        this.temperatura = dto.temperatura();
        this.fechaHora = dto.fechaHora();
        this.observacion = dto.observacion();
    }

    public Long getId() {
        return id;
    }

    public Long getLineaId() {
        return lineaId;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getObservacion() {
        return observacion;
    }
}
