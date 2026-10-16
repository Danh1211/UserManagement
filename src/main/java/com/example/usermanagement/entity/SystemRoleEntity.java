package com.example.usermanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "ROLES")
public class SystemRoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ROLE")
    @SequenceGenerator(name = "SEQ_ROLE", sequenceName = "SEQ_ROLE", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ROLE_CODE")
    private String roleCode;

    @Column(name = "ROLE_NAME")
    private String roleName;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToMany
    @JoinTable(
            name = "ROLE_PERMISSIONS",
            joinColumns = {@JoinColumn(name = "ROLE_CODE", referencedColumnName = "ROLE_CODE")},
            inverseJoinColumns = {@JoinColumn(name = "PERMISSION_CODE", referencedColumnName = "PERMISSION_CODE")}
    )
    private Set<SystemPermissionEntity> permissions = new HashSet<>();
}
