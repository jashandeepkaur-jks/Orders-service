package com.order.OrdersService.query;

import com.order.OrdersService.Model.OrderSummary;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrdersQueryController {

    @Autowired
    private QueryGateway queryGateway;

    @GetMapping
    public List<OrdersRestModel> getProducts(){

        FindOrdersQuery findOrdersQuery = new FindOrdersQuery();
        List<OrdersRestModel> ordersRestModels = queryGateway.query(findOrdersQuery
                , ResponseTypes.multipleInstancesOf(OrdersRestModel.class)).join();

        return ordersRestModels;
    }

    @GetMapping("/orderSummary")
    public OrderSummary getOrderSummary(){
        String orderId = UUID.randomUUID().toString();
        SubscriptionQueryResult<OrderSummary, OrderSummary> queryResult = queryGateway
                .subscriptionQuery(new FindOrderQuery(orderId),
                ResponseTypes.instanceOf(OrderSummary.class)
                ,ResponseTypes.instanceOf(OrderSummary.class) );
         return queryResult.updates().blockFirst();
    }
}
