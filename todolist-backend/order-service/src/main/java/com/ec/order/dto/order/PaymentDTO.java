package com.ec.order.dto.order;

import com.ec.order.entity.Payment.PaymentMethod;
import com.ec.order.entity.Payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private String id;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
}
