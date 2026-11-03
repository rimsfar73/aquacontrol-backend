package com.example.aquacontrol_backend.flushing.service;

import com.example.aquacontrol_backend.common.exception.ResourceNotFoundException;
import com.example.aquacontrol_backend.flushing.dto.FlushingDTO;
import com.example.aquacontrol_backend.flushing.entity.FlushingEntity;
import com.example.aquacontrol_backend.flushing.repository.FlushingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class FlushingService {
    private final FlushingRepository repository;

    public FlushingService(FlushingRepository repository) {
        this.repository = repository;
    }

    public List<FlushingDTO> listar(Long lineaId) {
        List<FlushingEntity> entities = lineaId == null
                ? repository.findAll()
                : repository.findByLineaIdOrderByFechaHoraDesc(lineaId);
        return entities.stream().map(this::toDto).toList();
    }

    public FlushingDTO obtener(Long id) {
        return toDto(buscar(id));
    }

    @Transactional
    public FlushingDTO crear(FlushingDTO dto) {
        FlushingEntity entity = new FlushingEntity();
        copiar(dto, entity);
        return toDto(repository.save(entity));
    }

    @Transactional
    public FlushingDTO actualizar(Long id, FlushingDTO dto) {
        FlushingEntity entity = buscar(id);
        copiar(dto, entity);
        return toDto(repository.save(entity));
    }

    @Transactional
    public void eliminar(Long id) {
        FlushingEntity entity = buscar(id);
        repository.delete(entity);
    }

    private FlushingEntity buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Flushing no encontrado: " + id));
    }

    private void copiar(FlushingDTO dto, FlushingEntity e) {
        e.setLineaId(dto.lineaId());
        e.setFechaHora(dto.fechaHora());
        e.setDuracionSegundos(dto.duracionSegundos());
        e.setObservacion(dto.observacion());
    }

    private FlushingDTO toDto(FlushingEntity e) {
        return new FlushingDTO(
                e.getId(),
                e.getLineaId(),
                e.getFechaHora(),
                e.getDuracionSegundos(),
                e.getObservacion()
        );
    }
}
