
package com.example.aquacontrol_backend.meteorologia.servicios;

import com.example.aquacontrol_backend.meteorologia.dto.WeatherResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class WeatherService {

    private final RestClient weatherRestClient;

    public WeatherService(
            @Qualifier("weatherRestClient") RestClient weatherRestClient
    ) {
        this.weatherRestClient = weatherRestClient;
    }

    public WeatherResponseDto obtenerClima(
            double latitude,
            double longitude
    ) {
        Map<?, ?> respuesta;

        try {
            respuesta = weatherRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/forecast")
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam(
                                    "current",
                                    "temperature_2m,relative_humidity_2m"
                            )
                            .queryParam("timezone", "auto")
                            .build())
                    .retrieve()
                    .body(Map.class);

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "No fue posible consultar Open-Meteo",
                    e
            );
        }

        if (respuesta == null ||
                !(respuesta.get("current") instanceof Map<?, ?> actual)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Respuesta meteorologica incompleta"
            );
        }

        try {
            String fechaHora = (String) actual.get("time");

            double temperatura = ((Number)
                    actual.get("temperature_2m")).doubleValue();

            int humedad = ((Number)
                    actual.get("relative_humidity_2m")).intValue();

            return new WeatherResponseDto(
                    latitude,
                    longitude,
                    fechaHora,
                    temperatura,
                    humedad,
                    "Open-Meteo"
            );

        } catch (RuntimeException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Datos meteorologicos invalidos",
                    e
            );
        }
    }
}
