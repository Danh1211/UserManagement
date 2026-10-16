package com.example.usermanagement.entity;

import com.example.usermanagement.enums.ModuleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PERMISSIONS")
public class SystemPermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PERMISSION")
    @SequenceGenerator(name = "SEQ_PERMISSION", sequenceName = "SEQ_PERMISSION", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PERMISSION_CODE")
    private String permissionCode;

    @Column(name = "PERMISSION_NAME")
    private String permissionName;

    @Column(name = "MODULE")
    @Enumerated(EnumType.STRING)
    private ModuleEnum module;

    @Column(name = "DESCRIPTION")
    private String description;
}
