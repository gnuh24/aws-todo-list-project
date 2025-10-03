package com.ec.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "VnPayPayment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VnPayPayment {
	
	@Id
	private String id; // Trùng với Payment.id
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "id")
	private Payment payment;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private VnPayResponseCode vnpResponseCode; // ✅ enum hóa mã response
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private VnPayTransactionStatusCode vnpTransactionStatusCode; // ✅ enum hóa mã transaction
	
	private String transactionId;
	
	private LocalDateTime paymentTime;
	
	private String vnpSecureHash;
	
	private String bankCode;
	
	private String cardType;
	
	@Column(length = 255)
	private String vnpResponseStatus; // ✅ Mô tả theo responseCode
	
	@Column(length = 255)
	private String vnpTransactionStatus; // ✅ Mô tả theo responseCode
}
