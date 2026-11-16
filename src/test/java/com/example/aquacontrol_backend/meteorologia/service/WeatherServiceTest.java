package com.example.aquacontrol_backend.meteorologia.service;

import com.example.aquacontrol_backend.meteorologia.servicios.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import org.springframework.http.MediaType;

class WeatherServiceTest {
    private record Fixture(WeatherService service, MockRestServiceServer server) {}
    private Fixture preparar() {
        RestClient.Builder builder=RestClient.builder().baseUrl("https://api.open-meteo.com");
        MockRestServiceServer server=MockRestServiceServer.bindTo(builder).build();
        return new Fixture(new WeatherService(builder.build()),server);
    }
    @Test void respuestaValida() {
        Fixture f=preparar();
        f.server.expect(requestTo(org.hamcrest.Matchers.containsString("/v1/forecast")))
          .andRespond(withSuccess("{\"current\":{\"time\":\"2026-11-16T12:00\",\"temperature_2m\":22.5,\"relative_humidity_2m\":65}}",MediaType.APPLICATION_JSON));
        var clima=f.service.obtenerClima(-33.7,-71.2);
        assertEquals(22.5,clima.temperaturaExterior()); assertEquals(65,clima.humedadRelativa());
        assertEquals("Open-Meteo",clima.fuente()); f.server.verify();
    }
    @Test void respuestaSinCurrent() {
        Fixture f=preparar(); f.server.expect(requestTo(org.hamcrest.Matchers.containsString("/v1/forecast")))
          .andRespond(withSuccess("{}",MediaType.APPLICATION_JSON));
        var ex=assertThrows(ResponseStatusException.class,()->f.service.obtenerClima(0,0));
        assertEquals(HttpStatus.BAD_GATEWAY,ex.getStatusCode()); f.server.verify();
    }
    @Test void currentNoEsObjeto() {
        Fixture f=preparar(); f.server.expect(requestTo(org.hamcrest.Matchers.containsString("/v1/forecast")))
          .andRespond(withSuccess("{\"current\":123}",MediaType.APPLICATION_JSON));
        assertEquals(HttpStatus.BAD_GATEWAY,assertThrows(ResponseStatusException.class,()->f.service.obtenerClima(0,0)).getStatusCode());
        f.server.verify();
    }
    @Test void currentIncompleto() {
        Fixture f=preparar(); f.server.expect(requestTo(org.hamcrest.Matchers.containsString("/v1/forecast")))
          .andRespond(withSuccess("{\"current\":{\"time\":\"hoy\"}}",MediaType.APPLICATION_JSON));
        var ex=assertThrows(ResponseStatusException.class,()->f.service.obtenerClima(0,0));
        assertEquals(HttpStatus.BAD_GATEWAY,ex.getStatusCode()); f.server.verify();
    }
    @Test void errorRemotoSeConvierteEn502() {
        Fixture f=preparar(); f.server.expect(requestTo(org.hamcrest.Matchers.containsString("/v1/forecast")))
          .andRespond(withServerError());
        assertEquals(HttpStatus.BAD_GATEWAY,assertThrows(ResponseStatusException.class,()->f.service.obtenerClima(0,0)).getStatusCode());
        f.server.verify();
    }
}
