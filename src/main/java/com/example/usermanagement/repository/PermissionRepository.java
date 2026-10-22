package com.example.usermanagement.repository;

import com.example.usermanagement.entity.SystemPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<SystemPermissionEntity, Long> {

    List<SystemPermissionEntity> findAllByPermissionCodeIn(Collection<String> permissions);

    boolean existsByPermissionCode(String permissionCode);

    Optional<SystemPermissionEntity> findByPermissionCode(String permissionCode);
}
