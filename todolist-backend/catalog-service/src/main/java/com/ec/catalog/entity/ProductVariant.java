package com.ec.catalog.entity;

import com.ec.catalog.utils.IdGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ProductVariant")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariant {
	
	@Id
	@Column(length = 10)
	@Builder.Default
	private String id = IdGenerator.generateId();
	
	@Column(nullable = false)
	private Integer volume; // ml, immutable
	
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal price;
	
	@Column(nullable = false)
	private Integer quantity = 0;
	
	@Column(nullable = false)
	private Boolean isPublished = false;
	
	@Column(nullable = false)
	private Boolean isDeleted = false;
	
	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;
	
	private LocalDateTime deletedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "productId", nullable = false)
	private Product product;
}
