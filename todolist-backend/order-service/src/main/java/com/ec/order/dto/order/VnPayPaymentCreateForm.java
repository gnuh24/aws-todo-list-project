package com.ec.order.dto.order;

import com.ec.order.entity.Payment;
import com.ec.order.entity.VnPayResponseCode;
import com.ec.order.entity.VnPayTransactionStatusCode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VnPayPaymentCreateForm {
	private String orderId;
	private String transactionId;
	private LocalDateTime paymentTime;
	private String vnpSecureHash;
	private String bankCode;
	private String cardType;
	private VnPayResponseCode vnpResponseCode;
	private VnPayTransactionStatusCode vnpTransactionStatusCode;
	private String vnpResponseStatus;
	private String vnpTransactionStatus;
	private Payment.PaymentStatus paymentStatus;
}
