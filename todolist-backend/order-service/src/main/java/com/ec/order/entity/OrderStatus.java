package com.ec.order.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "OrderStatus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatus {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "orderId", nullable = false)
	private Order order;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatusEnum status;
	
	@Column(nullable = false)
	@CreationTimestamp
	private LocalDateTime updateTime;
	
	public static enum OrderStatusEnum {
		PENDING,
		PROCESSING,
		SHIPPING,
		COMPLETE,
		CANCELED
	}
	
}
