
package com.example.aquacontrol_backend.meteorologia.dto;

public record WeatherResponseDto(
        double latitude,
        double longitude,
        String fechaHora,
        double temperaturaExterior,
        int humedadRelativa,
        String fuente
) {
}
