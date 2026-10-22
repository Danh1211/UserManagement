package com.example.usermanagement.repository;

import com.example.usermanagement.entity.SystemRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<SystemRoleEntity, Long> {

    @Query(nativeQuery = true, value = """
    SELECT rp.PERMISSION_CODE FROM ROLE_PERMISSIONS rp
        WHERE rp.ROLE_CODE = :roleCode
    """)
    List<String> findAllPermissionByRoleCode(@Param("roleCode") String roleCode);

    Optional<SystemRoleEntity> findByRoleCode(String roleCode);

    boolean existsByRoleCode(String roleCode);

}
