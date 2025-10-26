package com.example.medicineordering.strategy.impl;

import com.example.medicineordering.model.Order;
import com.example.medicineordering.strategy.OrderStatusStrategy;
import org.springframework.stereotype.Component;

/**
 * Concrete strategy for handling Assigned order status
 * This demonstrates the Strategy pattern for assigned order status processing
 */
@Component
public class AssignedOrderStrategy implements OrderStatusStrategy {
    
    @Override
    public boolean processStatusUpdate(Order order, String newStatus) {
        if (supportsStatus(newStatus)) {
            order.setStatus(newStatus);
            System.out.println("Order " + order.getId() + " status updated from Assigned to " + newStatus);
            return true;
        }
        return false;
    }
    
    @Override
    public String getStrategyName() {
        return "Assigned Order Status Strategy";
    }
    
    @Override
    public boolean supportsStatus(String status) {
        return "In Transit".equals(status) || "Delivered".equals(status) || "Failed".equals(status);
    }
    
    @Override
    public String[] getNextPossibleStatuses(String currentStatus) {
        if ("Assigned".equals(currentStatus)) {
            return new String[]{"In Transit", "Delivered", "Failed"};
        }
        return new String[0];
    }
}



