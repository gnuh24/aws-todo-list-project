package aws.todolist.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account implements Serializable, UserDetails {
	
	@Serial
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(length = 36, nullable = false, updatable = false)
	private String id;
	
	@Column(nullable = false, unique = true, length = 255)
	private String email;
	
	@Column(nullable = false, length = 255)
	private String password;
	
	@Column(length = 512)
	private String avatar;
	
	@Column(name = "display_name", length = 255)
	private String displayName;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Role role;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Status status;
	
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
	
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
	
	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted;
	
	@Column(name = "receive_email", nullable = false)
	private boolean receiveEmail;
	
	// --- ENUMS ---
	public enum Role {
		ADMIN, USER
	}
	
	public enum Status {
		ACTIVE, INACTIVE, BANNED
	}
	
	// --- UserDetails implementation ---
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name()));
	}
	
	@Override
	public String getUsername() {
		return this.email;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return !isDeleted;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return this.status != Status.BANNED;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return !isDeleted;
	}
	
	@Override
	public boolean isEnabled() {
		return this.status == Status.ACTIVE && !isDeleted;
	}
	
	// --- Lifecycle hooks để set default ---
	@PrePersist
	public void prePersist() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
		if (updatedAt == null) {
			updatedAt = LocalDateTime.now();
		}
		if (role == null) {
			role = Role.USER;
		}
		if (status == null) {
			status = Status.ACTIVE;
		}
		isDeleted = false;
		receiveEmail = false;
	}
	
	@PreUpdate
	public void preUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
