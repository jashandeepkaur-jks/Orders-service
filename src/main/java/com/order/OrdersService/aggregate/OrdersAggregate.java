package com.order.OrdersService.aggregate;

import com.order.OrdersService.Event.OrderApprovedEvent;
import com.order.OrdersService.Event.OrderCreatedEvent;
import com.order.OrdersService.Event.OrderRejectedEvent;
import com.order.OrdersService.command.ApprovedOrderCommand;
import com.order.OrdersService.command.CreateOrderCommand;
import com.order.OrdersService.command.RejectOrderCommand;
import com.order.OrdersService.enums.OrderStatus;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;


@Aggregate
public class OrdersAggregate {

    @AggregateIdentifier
    public String orderId;
    private String userId;
    private String productId;
    private  int quantity;
    private  String addressId;
    private OrderStatus orderStatus;

    public OrdersAggregate() {
    }

    @CommandHandler
    public OrdersAggregate(CreateOrderCommand createOrderCommand) throws Exception{

        if(createOrderCommand.getOrderId()==null || createOrderCommand.getOrderId().trim().isEmpty())
        {
            throw new RuntimeException(" Order id can,t be empty");
        }
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();

        BeanUtils.copyProperties(createOrderCommand,orderCreatedEvent);

        AggregateLifecycle.apply(orderCreatedEvent);


    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent orderCreatedEvent)
    {
        this.orderId = orderCreatedEvent.getOrderId();
        this.addressId = orderCreatedEvent.getAddressId();
        this.orderStatus = orderCreatedEvent.getOrderStatus();
        this.productId = orderCreatedEvent.getProductId();
        this.quantity = orderCreatedEvent.getQuantity();
        this.userId = orderCreatedEvent.getUserId();
        this.orderStatus = orderCreatedEvent.getOrderStatus();
    }

    @CommandHandler
    public void handle(ApprovedOrderCommand approvedOrderCommand) throws Exception{

        OrderApprovedEvent orderApprovedEvent = new OrderApprovedEvent(approvedOrderCommand.getOrderId());


        AggregateLifecycle.apply(orderApprovedEvent);


    }

    @EventSourcingHandler
    public void on(OrderApprovedEvent orderApprovedEvent)
    {
        this.orderStatus = orderApprovedEvent.getOrderStatus();


    }

    @CommandHandler
    public void handle(RejectOrderCommand rejectOrderCommand) throws Exception{

        OrderRejectedEvent orderRejectedEvent = new OrderRejectedEvent(rejectOrderCommand.getOrderId(), rejectOrderCommand.getReason());

        AggregateLifecycle.apply(orderRejectedEvent);
    }

    @EventSourcingHandler
    public void on(OrderRejectedEvent orderRejectedEvent)
    {
        this.orderStatus = orderRejectedEvent.getOrderStatus();


    }
}