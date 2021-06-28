package com.order.OrdersService.query;

import com.order.OrdersService.enums.OrderStatus;
import lombok.Data;

@Data
public class OrdersRestModel {

    public String orderId;
    private String userId;
    private String productId;
    private  int quantity;
    private  String addressId;
    private OrderStatus orderStatus;
}
