package com.order.OrdersService.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@AllArgsConstructor
@Data
public class ApprovedOrderCommand {

    @TargetAggregateIdentifier
    private final String orderId;
}
