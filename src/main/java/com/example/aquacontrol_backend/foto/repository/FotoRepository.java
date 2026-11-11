package com.example.aquacontrol_backend.foto.repository;

import com.example.aquacontrol_backend.foto.entity.FotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Accedemos a las imágenes almacenadas en PostgreSQL.
// Cada foto comparte el identificador de su flushing.
public interface FotoRepository extends JpaRepository<FotoEntity, Long> {

    // Heredamos las operaciones de guardar y consultar por identificador.

    // Eliminamos la evidencia directamente, sin descargar sus bytes.
    // FlushingService llamará este método dentro de la misma transacción
    // que elimina el flushing, para que ambas operaciones se confirmen juntas.
    @Modifying
    @Query("delete from FotoEntity foto where foto.id = :flushingId")
    int eliminarPorFlushingId(@Param("flushingId") Long flushingId);
}