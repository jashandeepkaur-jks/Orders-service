package com.order.OrdersService.saga;

import com.order.OrdersService.Event.OrderApprovedEvent;
import com.order.OrdersService.Event.OrderCreatedEvent;
import com.order.OrdersService.Event.OrderRejectedEvent;
import com.order.OrdersService.Model.OrderSummary;
import com.order.OrdersService.command.ApprovedOrderCommand;
import com.order.OrdersService.command.RejectOrderCommand;
import com.order.OrdersService.query.FindOrderQuery;
import com.service.core.command.CancelProductReservationCommand;
import com.service.core.command.ProcessPaymentCommand;
import com.service.core.command.ReserveProductCommand;
import com.service.core.event.PaymentProcessedEvent;
import com.service.core.event.ProductReservationCancelEvent;
import com.service.core.event.ProductReservedEvent;
import com.service.core.model.User;
import com.service.core.query.FetchUserPaymentDetailsQuery;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Saga
public class OrderSaga {

    private  static  final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);


    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient QueryUpdateEmitter queryUpdateEmitter;

   /* @Autowired
    private transient DeadlineManager deadlineManager;*/

    private String deadId;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void  handle(OrderCreatedEvent orderCreatedEvent)
    {
        LOGGER.info("order created"+ orderCreatedEvent.getOrderId());
        ReserveProductCommand reserveProductCommand
                = ReserveProductCommand.builder().
                 orderId(orderCreatedEvent.getOrderId())
                .productId(orderCreatedEvent.getProductId())
                .quantity(orderCreatedEvent.getQuantity())
                .userId(orderCreatedEvent.getUserId()).build();

        commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {
            @Override
            public void onResult(CommandMessage<? extends ReserveProductCommand> commandMessage, CommandResultMessage<?> commandResultMessage) {
                if(commandResultMessage.isExceptional()){



                }
            }
        });
    }


    @SagaEventHandler(associationProperty = "orderId")
    public void  handle(ProductReservedEvent productReservedEvent)
    {
        LOGGER.info("product reserved"+ productReservedEvent.getOrderId());

        FetchUserPaymentDetailsQuery query = new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());

        User user = queryGateway.query(query, ResponseTypes.instanceOf(User.class)).join();

        if(true){
            LOGGER.info("revoking transactions");
         cancelProductReservation(productReservedEvent, "userPaymentdetails are null");
         return;
        }

       /* deadId = deadlineManager.schedule(Duration.of(10, ChronoUnit.SECONDS)
                ,"payment-processing-deadline",productReservedEvent);

        if(true) return;*/

        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .paymentDetails(user.getPaymentDetails())
                .orderId(productReservedEvent.getOrderId())
                .paymentId(UUID.randomUUID().toString()).build();
        commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);

        LOGGER.info("successfully got userPayment details" + user.getFirstName());

    }

    private void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason){
        //cancelDeadline();
        CancelProductReservationCommand cancelProductReservationCommand = CancelProductReservationCommand.builder()
                .productId(productReservedEvent.getProductId())
                .orderId(productReservedEvent.getOrderId())
                .quantity(productReservedEvent.getQuantity())
                .reason(reason).build();
        commandGateway.send(cancelProductReservationCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void  handle(PaymentProcessedEvent paymentProcessedEvent)
    {
//cancelDeadline();
ApprovedOrderCommand approvedOrderCommand = new ApprovedOrderCommand(paymentProcessedEvent.getOrderId());
        commandGateway.send(approvedOrderCommand);
        LOGGER.info("successfully approved order details");

    }

   /* private void cancelDeadline() {
        if (deadId != null) {
            deadlineManager.cancelSchedule("payment-processing-deadline", deadId);
            deadId = null;
        }
    }*/

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void  handle(OrderApprovedEvent orderApprovedEvent)
    {
        LOGGER.info("successfully got approval of order details saga completed" + orderApprovedEvent.getOrderId());
        queryUpdateEmitter.emit(FindOrderQuery.class, query->true, new OrderSummary(orderApprovedEvent.getOrderId(), orderApprovedEvent.getOrderStatus()));

    }

    @SagaEventHandler(associationProperty = "orderId")
    public void  handle(ProductReservationCancelEvent productReservationCancelEvent)
    {
        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(productReservationCancelEvent.getOrderId(), productReservationCancelEvent.getReason());
        commandGateway.send(rejectOrderCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void  handle(OrderRejectedEvent orderRejectedEvent)
    {
        LOGGER.info("rejected  order" + orderRejectedEvent.getOrderId());
        queryUpdateEmitter.emit(FindOrderQuery.class, query->true, new OrderSummary(orderRejectedEvent.getOrderId(), orderRejectedEvent.getOrderStatus()));
        SagaLifecycle.end();

    }

    @DeadlineHandler(deadlineName = "payment-processing-deadline")
    public void handleDeadline(ProductReservedEvent productReservedEvent){
        LOGGER.info("payment deadline took place");
        cancelProductReservation(productReservedEvent,"payment timeout");
    }


}
