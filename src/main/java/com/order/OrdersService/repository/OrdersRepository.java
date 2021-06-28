package com.order.OrdersService.repository;

import com.order.OrdersService.Entity.OrdersEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends MongoRepository<OrdersEntity, String> {
}
