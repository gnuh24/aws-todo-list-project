package com.ec.catalog.entity;

import com.ec.catalog.utils.IdGenerator;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;
	
	@Id
	private String id = IdGenerator.generateId();
	
	@Column(nullable = false, unique = true)
	private String email;
	
	@Column(unique = true)
	private String phone;
	
	private String fullName;
	
	private LocalDate birthday;
	
	private String gender;
	
	@OneToOne(mappedBy = "profile")
	private Account account;
	
	@OneToMany(mappedBy = "profile")
	private java.util.List<Address> addresses;
}


