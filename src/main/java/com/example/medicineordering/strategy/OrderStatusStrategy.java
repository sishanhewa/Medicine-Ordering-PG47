package com.example.medicineordering.strategy;

import com.example.medicineordering.model.Order;

/**
 * Strategy interface for different order status processing strategies
 * This demonstrates the Strategy pattern for handling different order status updates
 */
public interface OrderStatusStrategy {
    
    /**
     * Processes the order status update
     * @param order the order to update
     * @param newStatus the new status to set
     * @return true if the status update was successful
     */
    boolean processStatusUpdate(Order order, String newStatus);
    
    /**
     * Gets the strategy name for logging purposes
     * @return the name of the order status strategy
     */
    String getStrategyName();
    
    /**
     * Checks if this strategy supports the given status
     * @param status the order status to check
     * @return true if this strategy supports the status
     */
    boolean supportsStatus(String status);
    
    /**
     * Gets the next possible statuses from the current status
     * @param currentStatus the current order status
     * @return array of possible next statuses
     */
    String[] getNextPossibleStatuses(String currentStatus);
}



