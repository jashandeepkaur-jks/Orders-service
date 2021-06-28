package com.order.OrdersService.Event;

import com.order.OrdersService.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class OrderCreatedEvent {

    public String orderId;
    private String userId;
    private String productId;
    private  int quantity;
    private  String addressId;
    private  OrderStatus orderStatus;
}
