
package com.example.aquacontrol_backend.linea.repository;

import com.example.aquacontrol_backend.linea.entity.LineaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LineaRepository
        extends JpaRepository<LineaEntity, Long> {

    List<LineaEntity> findByGalpon_Id(Long galponId);

    boolean existsByGalpon_Id(Long galponId);
}
