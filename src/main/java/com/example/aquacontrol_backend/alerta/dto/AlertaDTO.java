package com.example.aquacontrol_backend.alerta.dto;

import java.time.LocalDateTime;

public record AlertaDTO(
        Long medicionId,
        Long lineaId,
        Double temperatura,
        LocalDateTime fechaHora,
        String nivel,
        String mensaje
) {
}
