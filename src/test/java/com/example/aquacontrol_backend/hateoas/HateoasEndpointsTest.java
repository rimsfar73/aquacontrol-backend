package com.example.aquacontrol_backend.hateoas;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class HateoasEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void temperaturaDebeIncluirEnlacesHateoas() throws Exception {
        mockMvc.perform(get("/api/v1/temperatura?lineaId=1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].links").isArray())
                .andExpect(jsonPath("$[0].links[*].rel")
                        .value(hasItems("self", "linea", "collection")));
    }

    @Test
    void flushingDebeIncluirEnlacesHateoas() throws Exception {
        mockMvc.perform(get("/api/v1/flushing?lineaId=1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].links").isArray())
                .andExpect(jsonPath("$[0].links[*].rel")
                        .value(hasItems("self", "linea", "foto", "collection")));
    }
}