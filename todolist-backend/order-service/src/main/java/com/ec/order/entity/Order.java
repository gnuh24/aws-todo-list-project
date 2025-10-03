package com.ec.order.entity;

import com.ec.order.utils.IdGenerator;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "`Order`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
	
	@Id
	@Builder.Default
	private String id = IdGenerator.generateId();
	
	@Column(nullable = false)
	private BigDecimal totalAmount;
	
	@Column(columnDefinition = "TEXT")
	private String note;
	
	@Column(nullable = false)
	private LocalDateTime orderTime;
	
	@Column(nullable = false)
	private String receiverName;
	
	@Column(nullable = false)
	private String receiverPhone;
	
	@Column(nullable = false)
	private String receiverAddress;
	
	@Column(nullable = false)
	@Builder.Default
	private boolean isTemp = false;
	
	@Column(nullable = false)
	private String accountId;
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Payment> payments;
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderDetail> orderDetails;
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderStatus> statuses;
}
