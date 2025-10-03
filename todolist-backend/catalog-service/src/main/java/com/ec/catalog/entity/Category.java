package com.ec.catalog.entity;

import com.ec.catalog.utils.IdGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Category")
public class Category {
	
	@Id
	@Column(length = 10)
	private String id;
	
	@Column(length = 100, nullable = false, unique = true)
	private String categoryName;
	
	@Column(nullable = false)
	private Integer productCount;
	
	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@Column(nullable = false)
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	
	private LocalDateTime deletedAt;
	
	@Column(nullable = false)
	private Boolean isDeleted;
	
	// Tùy chọn: constructor riêng nếu muốn handle ID logic
	@PrePersist
	public void prePersist() {
		if (this.id == null) {
			this.id = IdGenerator.generateId();
		}
		if (this.productCount == null) {
			this.productCount = 0;
		}
		if (this.isDeleted == null) {
			this.isDeleted = false;
		}
	}
}
