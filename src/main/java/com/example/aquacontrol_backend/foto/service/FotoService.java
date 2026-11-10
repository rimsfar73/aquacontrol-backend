
package com.example.aquacontrol_backend.foto.service;

import com.example.aquacontrol_backend.common.exception.ResourceNotFoundException;
import com.example.aquacontrol_backend.flushing.repository.FlushingRepository;
import com.example.aquacontrol_backend.foto.dto.FotoDTO;
import com.example.aquacontrol_backend.foto.entity.FotoEntity;
import com.example.aquacontrol_backend.foto.repository.FotoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class FotoService {

    private final FotoRepository repository;
    private final FlushingRepository flushingRepository;

    public FotoService(
            FotoRepository repository,
            FlushingRepository flushingRepository
    ) {
        this.repository = repository;
        this.flushingRepository = flushingRepository;
    }

    public List<FotoDTO> listar(Long flushingId) {
        List<FotoEntity> entities = flushingId == null
                ? repository.findAll()
                : repository.findByFlushingIdOrderByIdDesc(flushingId);

        return entities.stream()
                .map(this::toDto)
                .toList();
    }

    public FotoDTO obtener(Long id) {
        return toDto(buscar(id));
    }

    @Transactional
    public FotoDTO crear(FotoDTO dto) {
        validarFlushing(dto.flushingId());
        validarRuta(dto.ruta());

        FotoEntity entity = new FotoEntity();
        copiar(dto, entity);

        return toDto(repository.save(entity));
    }

    @Transactional
    public FotoDTO actualizar(Long id, FotoDTO dto) {
        FotoEntity entity = buscar(id);

        validarFlushing(dto.flushingId());
        validarRuta(dto.ruta());

        copiar(dto, entity);

        return toDto(repository.save(entity));
    }

    @Transactional
    public void eliminar(Long id) {
        FotoEntity entity = buscar(id);
        repository.delete(entity);
    }

    private FotoEntity buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Foto no encontrada: " + id
                )
        );
    }

    private void validarFlushing(Long flushingId) {
        if (flushingId == null
                || !flushingRepository.existsById(flushingId)) {
            throw new ResourceNotFoundException(
                    "Flushing no encontrado: " + flushingId
            );
        }
    }

    private void validarRuta(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            throw new IllegalArgumentException(
                    "La ruta de la fotografía es obligatoria."
            );
        }

        if (ruta.length() > 500) {
            throw new IllegalArgumentException(
                    "La ruta no puede superar 500 caracteres."
            );
        }
    }

    private void copiar(FotoDTO dto, FotoEntity entity) {
        entity.setFlushingId(dto.flushingId());
        entity.setRuta(dto.ruta());
        entity.setDescripcion(dto.descripcion());
    }

    private FotoDTO toDto(FotoEntity entity) {
        return new FotoDTO(
                entity.getId(),
                entity.getFlushingId(),
                entity.getRuta(),
                entity.getDescripcion()
        );
    }
}
