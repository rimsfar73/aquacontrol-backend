
package com.example.aquacontrol_backend.meteorologia.controller;

import com.example.aquacontrol_backend.meteorologia.dto.WeatherResponseDto;
import com.example.aquacontrol_backend.meteorologia.servicios.WeatherService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<WeatherResponseDto> obtenerClima(
            @RequestParam double latitude,
            @RequestParam double longitude
    ) {
        if (!Double.isFinite(latitude)
                || !Double.isFinite(longitude)
                || latitude < -90 || latitude > 90
                || longitude < -180 || longitude > 180) {

            return ResponseEntity.badRequest().build();
        }

        WeatherResponseDto respuesta =
                weatherService.obtenerClima(latitude, longitude);

        return ResponseEntity.ok(respuesta);
    }
}
