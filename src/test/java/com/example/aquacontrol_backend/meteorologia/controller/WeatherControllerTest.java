package com.example.aquacontrol_backend.meteorologia.controller;

import com.example.aquacontrol_backend.meteorologia.dto.WeatherResponseDto;
import com.example.aquacontrol_backend.meteorologia.servicios.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherControllerTest {
    private WeatherService servicio;
    private WeatherController controlador;
    @BeforeEach void preparar() {
        servicio=mock(WeatherService.class); controlador=new WeatherController(servicio);
    }
    @Test void coordenadasValidasDevuelvenClima() {
        WeatherResponseDto clima=new WeatherResponseDto(-33.7,-71.2,"2026-11-16T12:00",23.5,60,"Open-Meteo");
        when(servicio.obtenerClima(-33.7,-71.2)).thenReturn(clima);
        var respuesta=controlador.obtenerClima(-33.7,-71.2);
        assertEquals(HttpStatus.OK,respuesta.getStatusCode()); assertSame(clima,respuesta.getBody());
    }
    @Test void latitudFueraDeRango() {
        assertEquals(HttpStatus.BAD_REQUEST,controlador.obtenerClima(90.1,0).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST,controlador.obtenerClima(-90.1,0).getStatusCode());
        verifyNoInteractions(servicio);
    }
    @Test void longitudFueraDeRango() {
        assertEquals(HttpStatus.BAD_REQUEST,controlador.obtenerClima(0,180.1).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST,controlador.obtenerClima(0,-180.1).getStatusCode());
        verifyNoInteractions(servicio);
    }
    @Test void coordenadasNaN() {
        assertEquals(HttpStatus.BAD_REQUEST,controlador.obtenerClima(Double.NaN,0).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST,controlador.obtenerClima(0,Double.NaN).getStatusCode());
        verifyNoInteractions(servicio);
    }
    @Test void coordenadasInfinitas() {
        assertEquals(HttpStatus.BAD_REQUEST,controlador.obtenerClima(Double.POSITIVE_INFINITY,0).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST,controlador.obtenerClima(0,Double.NEGATIVE_INFINITY).getStatusCode());
        verifyNoInteractions(servicio);
    }
    @Test void limitesSonValidos() {
        controlador.obtenerClima(90,180);
        controlador.obtenerClima(-90,-180);
        verify(servicio).obtenerClima(90,180);
        verify(servicio).obtenerClima(-90,-180);
    }
}
