package com.example.medicineordering.strategy;

import com.example.medicineordering.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Context class for the Order Status Strategy pattern
 * This demonstrates how to use different order status strategies based on current status
 */
@Component
public class OrderStatusContext {
    
    private final List<OrderStatusStrategy> orderStatusStrategies;
    
    @Autowired
    public OrderStatusContext(List<OrderStatusStrategy> orderStatusStrategies) {
        this.orderStatusStrategies = orderStatusStrategies;
    }
    
    /**
     * Processes an order status update using the appropriate strategy
     * @param order the order to update
     * @param newStatus the new status to set
     * @return true if the status update was successful
     */
    public boolean processOrderStatusUpdate(Order order, String newStatus) {
        OrderStatusStrategy strategy = getStrategyForStatus(order.getStatus());
        if (strategy != null) {
            System.out.println("Using " + strategy.getStrategyName() + " for status: " + order.getStatus());
            return strategy.processStatusUpdate(order, newStatus);
        }
        System.err.println("No order status strategy found for current status: " + order.getStatus());
        return false;
    }
    
    /**
     * Gets the appropriate order status strategy for the given current status
     * @param currentStatus the current order status
     * @return the order status strategy for the current status, or null if not found
     */
    private OrderStatusStrategy getStrategyForStatus(String currentStatus) {
        return orderStatusStrategies.stream()
                .filter(strategy -> strategy.supportsStatus(currentStatus))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Gets all available order status strategies
     * @return list of all order status strategies
     */
    public List<OrderStatusStrategy> getAllStrategies() {
        return orderStatusStrategies;
    }
    
    /**
     * Gets the next possible statuses for an order
     * @param currentStatus the current order status
     * @return array of possible next statuses
     */
    public String[] getNextPossibleStatuses(String currentStatus) {
        OrderStatusStrategy strategy = getStrategyForStatus(currentStatus);
        if (strategy != null) {
            return strategy.getNextPossibleStatuses(currentStatus);
        }
        return new String[0];
    }
}



