package com.ec.order.dto.order;

import com.ec.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyOrderResponseDTO {
    private String id;
    private BigDecimal totalAmount;
    private LocalDateTime orderTime;
    private OrderStatus.OrderStatusEnum latestStatus;
    private List<OrderDetailDTO> orderDetails;  // Danh sách sản phẩm trong đơn
}
