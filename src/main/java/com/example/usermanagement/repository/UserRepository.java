package com.example.usermanagement.repository;

import com.example.usermanagement.entity.SystemUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<SystemUserEntity, Long> {

    Optional<SystemUserEntity> findByUsername(String username);
}
