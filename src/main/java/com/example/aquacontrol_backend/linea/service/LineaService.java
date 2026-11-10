
package com.example.aquacontrol_backend.linea.service;

import com.example.aquacontrol_backend.common.exception.ResourceNotFoundException;
import com.example.aquacontrol_backend.galpon.entity.GalponEntity;
import com.example.aquacontrol_backend.galpon.repository.GalponRepository;
import com.example.aquacontrol_backend.linea.dto.LineaDTO;
import com.example.aquacontrol_backend.linea.entity.LineaEntity;
import com.example.aquacontrol_backend.linea.repository.LineaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LineaService {

    private final LineaRepository lineaRepository;
    private final GalponRepository galponRepository;

    public LineaService(
            LineaRepository lineaRepository,
            GalponRepository galponRepository
    ) {
        this.lineaRepository = lineaRepository;
        this.galponRepository = galponRepository;
    }

    // Consultar todas las líneas
    public List<LineaDTO> listar() {
        return lineaRepository.findAll()
                .stream()
                .map(this::convertirDTO)
                .toList();
    }

    // Consultar una línea por ID
    public LineaDTO obtenerPorId(Long id) {
        LineaEntity linea = buscarLinea(id);
        return convertirDTO(linea);
    }

    // Consultar líneas pertenecientes a un galpón
    public List<LineaDTO> listarPorGalpon(Long galponId) {

        if (!galponRepository.existsById(galponId)) {
            throw new ResourceNotFoundException(
                    "No existe el galpón con ID: " + galponId
            );
        }

        return lineaRepository.findByGalpon_Id(galponId)
                .stream()
                .map(this::convertirDTO)
                .toList();
    }

    // Crear una línea
    @Transactional
    public LineaDTO crear(LineaDTO dto) {

        GalponEntity galpon = buscarGalpon(dto.galponId());

        LineaEntity linea = new LineaEntity();
        linea.setNombre(dto.nombre());
        linea.setGalpon(galpon);

        LineaEntity guardada = lineaRepository.save(linea);

        return convertirDTO(guardada);
    }

    // Actualizar una línea existente
    @Transactional
    public LineaDTO actualizar(Long id, LineaDTO dto) {

        LineaEntity linea = buscarLinea(id);
        GalponEntity galpon = buscarGalpon(dto.galponId());

        linea.setNombre(dto.nombre());
        linea.setGalpon(galpon);

        LineaEntity actualizada = lineaRepository.save(linea);

        return convertirDTO(actualizada);
    }

    // Eliminar una línea
    @Transactional
    public void eliminar(Long id) {

        LineaEntity linea = buscarLinea(id);

        lineaRepository.delete(linea);
    }

    // Buscar una línea o informar que no existe
    private LineaEntity buscarLinea(Long id) {
        return lineaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe la línea con ID: " + id
                        )
                );
    }

    // Buscar un galpón o informar que no existe
    private GalponEntity buscarGalpon(Long id) {
        return galponRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe el galpón con ID: " + id
                        )
                );
    }

    // Convertir entidad JPA a DTO
    private LineaDTO convertirDTO(LineaEntity linea) {

        return new LineaDTO(
                linea.getId(),
                linea.getNombre(),
                linea.getGalpon().getId()
        );
    }
}
