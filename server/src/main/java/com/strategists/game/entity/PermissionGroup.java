package com.strategists.game.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "permission_groups")
public class PermissionGroup implements Serializable {

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
