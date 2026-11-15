package com.example.aquacontrol_backend.temperatura.repository;

import com.example.aquacontrol_backend.temperatura.entity.TemperaturaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TemperaturaRepository extends JpaRepository<TemperaturaEntity, Long> {

    List<TemperaturaEntity> findByLineaIdOrderByFechaHoraDesc(Long lineaId);

    @Query("""
            SELECT t
            FROM TemperaturaEntity t
            WHERE NOT EXISTS (
                SELECT t2.id
                FROM TemperaturaEntity t2
                WHERE t2.lineaId = t.lineaId
                  AND (
                      t2.fechaHora > t.fechaHora
                      OR (t2.fechaHora = t.fechaHora AND t2.id > t.id)
                  )
            )
            ORDER BY t.lineaId
            """)
    List<TemperaturaEntity> findUltimasPorLinea();
}