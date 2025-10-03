package com.ec.order.entity;

import com.ec.order.utils.IdGenerator;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
	
	@Id
	@Builder.Default
	private String id = IdGenerator.generateId();
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus paymentStatus;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentMethod paymentMethod;
	
	@ManyToOne
	@JoinColumn(name = "orderId", nullable = false)
	private Order order;
	
	
	public static enum PaymentStatus {
		PENDING,
		SUCCESS,
		FAILED,
		CANCELLED,
		REFUNDED
	}
	
	public static enum PaymentMethod {
		COD,
		VNPAY,
		MOMO
	}
	
	
}
