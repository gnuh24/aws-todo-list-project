package com.ec.order.dto.order;

import com.ec.order.entity.Payment;
import com.ec.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponseDTO {
    private String id;
    private BigDecimal totalAmount;
    private String note;
    private LocalDateTime orderTime;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;

    private List<OrderDetailDTO> orderDetails;
    private List<OrderStatusDTO> statusHistory;
    private PaymentDTO payment;
}
