package com.ec.order.dto.order;

import com.ec.order.entity.OrderStatus.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusDTO {
    private OrderStatusEnum status;
    private LocalDateTime updateTime;
}
