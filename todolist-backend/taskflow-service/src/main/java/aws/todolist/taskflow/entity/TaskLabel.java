package aws.todolist.taskflow.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "task_label")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskLabel {
	
	@Id
	@Column(length = 36)
	private String id = UUID.randomUUID().toString();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_id", nullable = false)
	private Task task;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "label_id", nullable = false)
	private ProjectLabel projectLabel; // Luôn liên kết với ProjectLabel
	
	@Column(name = "is_ai_generated")
	private Boolean isAiGenerated = false;
	
	@Column
	private Float confidence;
	
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();
}
