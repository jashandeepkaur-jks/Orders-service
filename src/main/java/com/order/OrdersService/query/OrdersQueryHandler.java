package com.order.OrdersService.query;

import com.order.OrdersService.Entity.OrdersEntity;
import com.order.OrdersService.Model.OrderSummary;
import com.order.OrdersService.repository.OrdersRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrdersQueryHandler {
    private final OrdersRepository ordersRepository;

    public OrdersQueryHandler(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @QueryHandler
    public List<OrdersRestModel> getProducts(FindOrdersQuery query) {
        List<OrdersRestModel> ordersRestModels = new ArrayList<>();
        List<OrdersEntity> ordersEntities = ordersRepository.findAll();
        for (OrdersEntity ordersEntity : ordersEntities) {
            OrdersRestModel ordersRestModel = new OrdersRestModel();
            BeanUtils.copyProperties(ordersEntity, ordersRestModel);
            ordersRestModels.add(ordersRestModel);
        }
        return ordersRestModels;
    }

    @QueryHandler
    public OrderSummary findOrder(FindOrderQuery findOrderQuery) {
        return ordersRepository.findById(findOrderQuery.getOrderId()).map((e ->
        {
            return new OrderSummary(e.getOrderId(), e.getOrderStatus());
        })).get();


    }

}