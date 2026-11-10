
package com.example.aquacontrol_backend.granja.service;

import com.example.aquacontrol_backend.common.exception.ResourceNotFoundException;
import com.example.aquacontrol_backend.granja.dto.GranjaDTO;
import com.example.aquacontrol_backend.granja.entity.GranjaEntity;
import com.example.aquacontrol_backend.granja.repository.GranjaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GranjaService {

    private final GranjaRepository repository;

    public GranjaService(GranjaRepository repository) {
        this.repository = repository;
    }

    public List<GranjaDTO> listar() {
        return repository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public GranjaDTO obtener(Long id) {
        return toDto(buscar(id));
    }

    @Transactional
    public GranjaDTO crear(GranjaDTO dto) {
        GranjaEntity entity = new GranjaEntity();
        copiar(dto, entity);

        return toDto(repository.save(entity));
    }

    @Transactional
    public GranjaDTO actualizar(Long id, GranjaDTO dto) {
        GranjaEntity entity = buscar(id);
        copiar(dto, entity);

        return toDto(repository.save(entity));
    }

    @Transactional
    public void eliminar(Long id) {
        GranjaEntity entity = buscar(id);
        repository.delete(entity);
    }

    private GranjaEntity buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Granja no encontrada: " + id
                        )
                );
    }

    private void copiar(GranjaDTO dto, GranjaEntity entity) {
        entity.setNombre(dto.nombre().trim());
        entity.setUbicacion(dto.ubicacion());
    }

    private GranjaDTO toDto(GranjaEntity entity) {
        return new GranjaDTO(
                entity.getId(),
                entity.getNombre(),
                entity.getUbicacion()
        );
    }
}

