package com.order.OrdersService.command;

import com.order.OrdersService.Model.CreateOrderRestModel;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class CreateOrderCommandController {


    private final Environment environment;

    private final CommandGateway commandGateway;

    @Autowired
    CreateOrderCommandController(Environment environment, CommandGateway commandGateway) {
        this.environment = environment;
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String createProduct(@RequestBody CreateOrderRestModel createOrderRestModel) {
        //db persist
        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder().productId(createOrderRestModel.getProductId())
                .orderStatus(createOrderRestModel.getOrderStatus())
                .addressId(createOrderRestModel.getAddressId())
                .orderId(UUID.randomUUID().toString())
                .userId(createOrderRestModel.getUserId())
                .quantity(createOrderRestModel.getQuantity()).build();

        String value = commandGateway.sendAndWait(createOrderCommand);
        return value;

    }

}
