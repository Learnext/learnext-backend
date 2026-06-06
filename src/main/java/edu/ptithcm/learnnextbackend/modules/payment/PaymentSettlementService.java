package edu.ptithcm.learnnextbackend.modules.payment;

import edu.ptithcm.learnnextbackend.modules.order.entity.Order;

public interface PaymentSettlementService {
    Order settlePaidOrder(Order order);
}
