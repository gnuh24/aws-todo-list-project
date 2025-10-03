package com.ec.user.entity;

import com.ec.user.utils.IdGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.io.Serial;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account implements Serializable,  UserDetails {
	
	@Serial
	private static final long serialVersionUID = 1L;
	
	@Id
	private String id;
	
	@Column(nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();
	
	@Column(nullable = false)
	private LocalDateTime updatedAt  = LocalDateTime.now();
	
	@Column(nullable = false, unique = true)
	private String username;
	
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role = Role.USER;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status = Status.INACTIVE;
	
	// Enum Role (Viết hoa toàn bộ)
	public enum Role {
		ADMIN, USER
	}
	
	// Enum Status (Viết hoa toàn bộ)
	public enum Status {
		ACTIVE, INACTIVE, BANNED
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name()) );
	}
	
	@Override
	public String getPassword() {
		return this.password;
	}
	
	@OneToOne
	@JoinColumn(name = "ProfileId", nullable = false)
	private Profile profile;
	
	
	@Override
	public String getUsername() {
		return this.username;
	}
}


