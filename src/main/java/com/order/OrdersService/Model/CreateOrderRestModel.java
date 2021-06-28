package com.order.OrdersService.Model;

import com.order.OrdersService.enums.OrderStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CreateOrderRestModel {

    @Id
    public String orderId;
    private String userId;
    private String productId;
    private  int quantity;
    private  String addressId;
    private  OrderStatus orderStatus;
}
