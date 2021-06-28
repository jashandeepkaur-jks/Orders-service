package com.order.OrdersService.query;

import com.order.OrdersService.Entity.OrdersEntity;
import com.order.OrdersService.Event.OrderApprovedEvent;
import com.order.OrdersService.Event.OrderCreatedEvent;
import com.order.OrdersService.Event.OrderRejectedEvent;
import com.order.OrdersService.enums.OrderStatus;
import com.order.OrdersService.repository.OrdersRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@ProcessingGroup("orders-group")
public class OrdersEventHandler {

    @Autowired
    private OrdersRepository ordersRepository;

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException argumentException){
//
    }

    @ExceptionHandler(resultType = Exception.class)
    public void handle(Exception ex) throws Exception {
        throw ex;
    }

    @EventHandler
    public void on(OrderCreatedEvent orderCreatedEvent) throws Exception {
        OrdersEntity ordersEntity = new OrdersEntity();
        BeanUtils.copyProperties(orderCreatedEvent, ordersEntity);
        try {
            ordersRepository.save(ordersEntity);
        }
        catch (IllegalArgumentException exception){
            exception.getMessage();
        }

        /*if(true)
            throw new Exception("Error happened in eventHandler");*/


    }

    @EventHandler
    public void on(OrderApprovedEvent orderApprovedEvent) throws Exception {
        Optional<OrdersEntity> ordersEntity = ordersRepository.findById(orderApprovedEvent.getOrderId());
        OrdersEntity o = ordersEntity.get();
        o.setOrderStatus(orderApprovedEvent.getOrderStatus());
        ordersRepository.save(o);

    }

    @EventHandler
    public void on(OrderRejectedEvent orderRejectedEvent) throws Exception {
        Optional<OrdersEntity> ordersEntity = ordersRepository.findById(orderRejectedEvent.getOrderId());
        OrdersEntity o = ordersEntity.get();
        o.setOrderStatus(orderRejectedEvent.getOrderStatus());
        ordersRepository.save(o);

    }
}
