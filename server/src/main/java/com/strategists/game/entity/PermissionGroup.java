package com.strategists.game.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name = "permission_groups")
public class PermissionGroup implements Serializable {

    @Serial
    private static final long serialVersionUID = -2390513110898034348L;

    public enum PermissionStatus {
        ENABLED, DISABLED;
    }

    @Id
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionStatus gameCreationPermissionStatus;

}
