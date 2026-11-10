
package com.example.aquacontrol_backend.galpon.repository;

import com.example.aquacontrol_backend.galpon.entity.GalponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GalponRepository
        extends JpaRepository<GalponEntity, Long> {

    List<GalponEntity> findByGranja_Id(Long granjaId);

    boolean existsByGranja_Id(Long granjaId);
}
