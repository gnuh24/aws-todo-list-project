package com.ec.news.entity;

import com.ec.news.utils.IdGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "News")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class News {
	
	@Id
	@Column(length = 10)
	@Builder.Default
	private String id = IdGenerator.generateId();
	
	@Column(nullable = false)
	private String title;
	
	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;
	
	@Column(nullable = false)
	private Boolean highlight = false;
	
	@Column(length = 500)
	private String thumbnail;
	
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	private LocalDateTime deletedAt;
	
	@Column(nullable = false)
	private Boolean isDeleted = false;
	
	@Column(nullable = false, length = 10)
	private String accountId;
	
	@Column(nullable = false)
	private Boolean isPublished = false;
	
	@PrePersist
	public void prePersist() {
		createdAt = LocalDateTime.now();
	}
	
	@PreUpdate
	public void preUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
