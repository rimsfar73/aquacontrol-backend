package com.example.aquacontrol_backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class EstadoController {

    @GetMapping("/")
    public Map<String, String> estado() {
        return Map.of(
                "aplicacion", "AquaControl Backend",
                "estado", "OPERATIVO"
        );
    }
}
