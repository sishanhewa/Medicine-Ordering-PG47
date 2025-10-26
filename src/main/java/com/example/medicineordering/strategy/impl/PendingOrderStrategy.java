package com.example.medicineordering.strategy.impl;

import com.example.medicineordering.model.Order;
import com.example.medicineordering.strategy.OrderStatusStrategy;
import org.springframework.stereotype.Component;

/**
 * Concrete strategy for handling Pending order status
 * This demonstrates the Strategy pattern for pending order status processing
 */
@Component
public class PendingOrderStrategy implements OrderStatusStrategy {
    
    @Override
    public boolean processStatusUpdate(Order order, String newStatus) {
        if (supportsStatus(newStatus)) {
            order.setStatus(newStatus);
            System.out.println("Order " + order.getId() + " status updated from Pending to " + newStatus);
            return true;
        }
        return false;
    }
    
    @Override
    public String getStrategyName() {
        return "Pending Order Status Strategy";
    }
    
    @Override
    public boolean supportsStatus(String status) {
        return "Ready".equals(status) || "Cancelled".equals(status);
    }
    
    @Override
    public String[] getNextPossibleStatuses(String currentStatus) {
        if ("Pending".equals(currentStatus)) {
            return new String[]{"Ready", "Cancelled"};
        }
        return new String[0];
    }
}



