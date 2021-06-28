package com.order.OrdersService.Model;

import com.order.OrdersService.enums.OrderStatus;
import lombok.Value;

@Value
public class OrderSummary {

    private String orderId;

    private OrderStatus status;
}
