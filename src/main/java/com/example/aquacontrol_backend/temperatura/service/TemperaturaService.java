
package com.example.aquacontrol_backend.temperatura.service;

import com.example.aquacontrol_backend.common.exception.ResourceNotFoundException;
import com.example.aquacontrol_backend.linea.repository.LineaRepository;
import com.example.aquacontrol_backend.temperatura.dto.TemperaturaDTO;
import com.example.aquacontrol_backend.temperatura.entity.TemperaturaEntity;
import com.example.aquacontrol_backend.temperatura.repository.TemperaturaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TemperaturaService {

    private final TemperaturaRepository repository;
    private final LineaRepository lineaRepository;

    public TemperaturaService(
            TemperaturaRepository repository,
            LineaRepository lineaRepository
    ) {
        this.repository = repository;
        this.lineaRepository = lineaRepository;
    }

    public List<TemperaturaDTO> listar(Long lineaId) {
        List<TemperaturaEntity> entities = lineaId == null
                ? repository.findAll()
                : repository.findByLineaIdOrderByFechaHoraDesc(lineaId);

        return entities.stream().map(this::toDto).toList();
    }

    public TemperaturaDTO obtener(Long id) {
        return toDto(buscar(id));
    }

    @Transactional
    public TemperaturaDTO crear(TemperaturaDTO dto) {
        TemperaturaEntity entity = new TemperaturaEntity();
        copiar(dto, entity);
        return toDto(repository.save(entity));
    }

    @Transactional
    public TemperaturaDTO actualizar(Long id, TemperaturaDTO dto) {
        TemperaturaEntity entity = buscar(id);
        copiar(dto, entity);
        return toDto(repository.save(entity));
    }

    @Transactional
    public void eliminar(Long id) {
        TemperaturaEntity entity = buscar(id);
        repository.delete(entity);
    }

    private TemperaturaEntity buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Temperatura no encontrada: " + id
                )
        );
    }

    private void validarLinea(Long lineaId) {
        if (lineaId == null || !lineaRepository.existsById(lineaId)) {
            throw new ResourceNotFoundException(
                    "Línea no encontrada: " + lineaId
            );
        }
    }

    private void copiar(TemperaturaDTO dto, TemperaturaEntity e) {
        validarLinea(dto.lineaId());

        e.setLineaId(dto.lineaId());
        e.setTemperatura(dto.temperatura());
        e.setFechaHora(dto.fechaHora());
        e.setObservacion(dto.observacion());
    }

    private TemperaturaDTO toDto(TemperaturaEntity e) {
        return new TemperaturaDTO(
                e.getId(),
                e.getLineaId(),
                e.getTemperatura(),
                e.getFechaHora(),
                e.getObservacion()
        );
    }
}
