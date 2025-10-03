package com.ec.user.entity;

import com.ec.user.utils.IdGenerator;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;
	
	@Id
	private String id = IdGenerator.generateId();
	
	private String address;
	
	@Column(nullable = false)
	private Boolean isDefault = false;
	
	@Column(nullable = false)
	private Boolean isDeleted = false;
	
	private String fullName;
	
	private String phone;
	
	@ManyToOne
	@JoinColumn(name = "profileId", nullable = false)
	private Profile profile;
}
