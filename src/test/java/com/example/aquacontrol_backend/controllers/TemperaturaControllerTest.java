
package com.example.aquacontrol_backend.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class TemperaturaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void debeConsultarTemperaturasPorLinea() throws Exception {
        mockMvc.perform(get("/api/v1/temperatura")
                        .param("lineaId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void cadaTemperaturaDebeIncluirEnlacesHateoas() throws Exception {
        mockMvc.perform(get("/api/v1/temperatura")
                        .param("lineaId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].links").isArray())
                .andExpect(jsonPath("$[0].links[*].rel")
                        .value(hasItems(
                                "self",
                                "linea",
                                "collection"
                        )));
    }

    @Test
    void enlaceLineaDebeApuntarAlRecursoDeLaLinea() throws Exception {
        mockMvc.perform(get("/api/v1/temperatura")
                        .param("lineaId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].links[*].rel")
                        .value(hasItems("linea")))
                .andExpect(jsonPath("$[0].links[*].href")
                        .value(hasItems(
                                containsString("/api/v1/lineas/1")
                        )));
    }
}
