package aws.todolist.taskflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "personal_label")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalLabel {
	
	@Id
	@Column(length = 36)
	private String id = UUID.randomUUID().toString();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", nullable = false)
	private Account account;
	
	@Column(nullable = false)
	private String name;
	
	@Column(columnDefinition = "TEXT")
	private String description;
	
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();
	
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt = LocalDateTime.now();
	
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
	
	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted = false;
}
