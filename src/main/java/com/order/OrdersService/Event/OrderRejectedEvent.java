package com.order.OrdersService.Event;

import com.order.OrdersService.enums.OrderStatus;
import lombok.Value;

@Value
public class OrderRejectedEvent {

    public String orderId;
    private final OrderStatus orderStatus = OrderStatus.REJECTED;
    public String reason;
}
