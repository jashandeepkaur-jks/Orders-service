package com.order.OrdersService.Entity;

import com.order.OrdersService.enums.OrderStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class OrdersEntity {

    @Id
    public String orderId;
    private String userId;
    private String productId;
    private  int quantity;
    private  String addressId;
    private OrderStatus orderStatus;
}
