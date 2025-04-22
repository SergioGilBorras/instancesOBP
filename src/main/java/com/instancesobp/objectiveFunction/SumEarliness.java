/*
 * Copyright (c) 2025 Sergio Gil Borrás
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to use
 * the Software for non-commercial research purposes only, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.instancesobp.objectiveFunction;

import java.util.List;

import com.instancesobp.models.Batch;
import com.instancesobp.models.Order;
import com.instancesobp.models.Warehouse;
import com.instancesobp.routingAlgorithm.RoutingAlgorithm;

/**
 * Represents the objective function for calculating the total earliness of orders in a warehouse.
 * Earliness is defined as the difference between the due date of an order and the time it is completed,
 * if the order is completed before its due date. This class calculates the total earliness for a list
 * of batches or a single batch, considering the service time of each batch and the current system time.
 * This objective function is useful for evaluating the efficiency of order processing in terms of
 * meeting or exceeding due dates.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class SumEarliness extends ObjectiveFunction {

    /**
     * Stores the current system time in milliseconds.
     */
    private long now = 0;

    /**
     * Constructs a SumEarliness objective function with the specified warehouse and routing algorithm.
     *
     * @param warehouse        The warehouse object containing layout and configuration details.
     * @param routingAlgorithm The routing algorithm used to calculate the service time for batches.
     */
    public SumEarliness(Warehouse warehouse, RoutingAlgorithm routingAlgorithm) {
        super(warehouse, routingAlgorithm);
    }

    /**
     * Calculates the total earliness for a list of batches.
     * This method iterates through all batches in the list, calculates their service time
     * if not already set, and determines the total earliness based on the due dates of
     * the orders in each batch.
     *
     * @param batchList The list of batches to be processed.
     * @return The total earliness for all orders in the list of batches.
     * @throws Exception If an error occurs during the calculation of the service time.
     */
    @Override
    public double run(List<Batch> batchList) throws Exception {
        now = System.currentTimeMillis(); // Capture the current system time.
        double serviceTimeAccumulate = 0; // Accumulator for the total service time.
        double totalEarliness = 0; // Variable to store the total earliness.

        for (Batch batch : batchList) {
            // Calculate the service time if it has not been set.
            if (batch.getServiceTime() == 0) {
                batch.setServiceTime(routingAlgorithm.run(batch));
            }

            // Accumulate the service time.
            serviceTimeAccumulate += batch.getServiceTime();

            // Add the batch earliness to the total earliness.
            totalEarliness += getBatchEarliness(batch, serviceTimeAccumulate);
        }

        return totalEarliness; // Return the total earliness.
    }

    /**
     * Calculates the earliness for a single batch.
     * This method calculates the service time for the batch if it has not been set,
     * and then computes the earliness for each order in the batch based on their due dates.
     *
     * @param batch The batch to be processed.
     * @return The total earliness for all orders in the batch.
     * @throws Exception If an error occurs during the calculation of the service time.
     */
    @Override
    public double run(Batch batch) throws Exception {
        now = System.currentTimeMillis(); // Capture the current system time.

        // Calculate the service time if it has not been set.
        if (batch.getServiceTime() == 0) {
            batch.setServiceTime(routingAlgorithm.run(batch));
        }

        return getBatchEarliness(batch, batch.getServiceTime()); // Return the total earliness for the batch.
    }

    /**
     * Calculates the earliness for a batch based on its orders.
     * This method computes the earliness for each order in the batch by comparing
     * the completion time of the batch with the due date of each order.
     *
     * @param batch                 The batch to be processed.
     * @param serviceTimeAccumulate The accumulated service time up to the current batch.
     * @return The total earliness for all orders in the batch.
     */
    private double getBatchEarliness(Batch batch, double serviceTimeAccumulate) {
        double batchEarliness = 0; // Variable to store the earliness for the current batch.

        // Calculate the completion time for the actual batch.
        double completionTimeBatch = now + serviceTimeAccumulate;

        // Calculate the earliness for each order in the batch.
        for (Order order : batch.getOrders()) {
            if (order.getDueDate() > completionTimeBatch) {
                batchEarliness += (order.getDueDate() - completionTimeBatch);
            }
        }
        return batchEarliness;
    }
}
