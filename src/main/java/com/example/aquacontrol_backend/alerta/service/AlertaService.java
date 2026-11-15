package com.example.aquacontrol_backend.alerta.service;

import com.example.aquacontrol_backend.alerta.dto.AlertaDTO;
import com.example.aquacontrol_backend.common.exception.ResourceNotFoundException;
import com.example.aquacontrol_backend.temperatura.entity.TemperaturaEntity;
import com.example.aquacontrol_backend.temperatura.repository.TemperaturaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class AlertaService {

    private final TemperaturaRepository temperaturaRepository;

    public AlertaService(TemperaturaRepository temperaturaRepository) {
        this.temperaturaRepository = temperaturaRepository;
    }

    public List<AlertaDTO> listar(Long lineaId) {
        return temperaturaRepository.findUltimasPorLinea()
                .stream()
                .filter(medicion ->
                        lineaId == null || lineaId.equals(medicion.getLineaId())
                )
                .map(this::convertirSiEsAlerta)
                .filter(Objects::nonNull)
                .toList();
    }

    public AlertaDTO obtener(Long medicionId) {
        return temperaturaRepository.findUltimasPorLinea()
                .stream()
                .filter(medicion -> medicionId.equals(medicion.getId()))
                .map(this::convertirSiEsAlerta)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Alerta no encontrada para la medición: " + medicionId
                ));
    }

    private AlertaDTO convertirSiEsAlerta(TemperaturaEntity medicion) {
        double valor = medicion.getTemperatura();

        if (valor < 5.0 || valor >= 30.0) {
            return new AlertaDTO(
                    medicion.getId(),
                    medicion.getLineaId(),
                    valor,
                    medicion.getFechaHora(),
                    "CRITICO",
                    "Temperatura crítica: " + valor + " °C"
            );
        }

        if (valor >= 18.0 && valor <= 21.0) {
            return null;
        }

        return new AlertaDTO(
                medicion.getId(),
                medicion.getLineaId(),
                valor,
                medicion.getFechaHora(),
                "ADVERTENCIA",
                "Temperatura fuera del rango normal: " + valor + " °C"
        );
    }
}