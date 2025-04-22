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
import com.instancesobp.models.Warehouse;
import com.instancesobp.routingAlgorithm.RoutingAlgorithm;

/**
 * Represents the objective function for calculating the maximum throughput time in a warehouse.
 * This class calculates the maximum time required to process a list of batches, considering
 * the service time of each batch and the time elapsed since their arrival. It uses a routing
 * algorithm to compute the service time for batches that do not already have it calculated.
 * The throughput time is defined as the sum of the accumulated service time and the time
 * since the batch's arrival, ensuring that the maximum value is returned.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class MaxThroughoutTime extends ObjectiveFunction {

    /** Stores the current system time in milliseconds. */
    private long now = 0;

    /**
     * Constructs a MaxThroughoutTime objective function with the specified warehouse and routing algorithm.
     *
     * @param warehouse The warehouse object containing layout and configuration details.
     * @param routingAlgorithm The routing algorithm used to calculate the service time for batches.
     */
    public MaxThroughoutTime(Warehouse warehouse, RoutingAlgorithm routingAlgorithm) {
        super(warehouse, routingAlgorithm);
    }

    /**
     * Calculates the maximum throughput time for a list of batches.
     * This method iterates through all batches in the list, calculates their service time
     * if not already set, and determines the maximum throughput time based on the accumulated
     * service time and the time since their arrival.
     *
     * @param batchList The list of batches to be processed.
     * @return The maximum throughput time for all batches in the list.
     * @throws Exception If an error occurs during the calculation of the service time.
     */
    @Override
    public double run(List<Batch> batchList) throws Exception {
        now = System.currentTimeMillis(); // Capture the current system time.
        double maxThroughputTime = 0; // Variable to store the maximum throughput time.
        double serviceTimeAccumulate = 0; // Accumulator for the total service time.

        for (Batch batch : batchList) {
            // Calculate the service time if it has not been set.
            if (batch.getServiceTime() == 0) {
                batch.setServiceTime(routingAlgorithm.run(batch));
            }

            // Accumulate the service time.
            serviceTimeAccumulate += batch.getServiceTime();

            // Calculate the throughput time for the current batch.
            double throughputTime = serviceTimeAccumulate + (now - batch.getEarliestArrivalTime());

            // Update the maximum throughput time if the current value is greater.
            if (maxThroughputTime < throughputTime) {
                maxThroughputTime = throughputTime;
            }
        }

        return maxThroughputTime; // Return the maximum throughput time.
    }

    /**
     * Calculates the throughput time for a single batch.
     * This method calculates the service time for the batch if it has not been set,
     * and then computes the throughput time as the sum of the service time and the
     * time elapsed since the batch's arrival.
     *
     * @param batch The batch to be processed.
     * @return The throughput time for the specified batch.
     * @throws Exception If an error occurs during the calculation of the service time.
     */
    @Override
    public double run(Batch batch) throws Exception {
        now = System.currentTimeMillis(); // Capture the current system time.

        // Calculate the service time if it has not been set.
        if (batch.getServiceTime() == 0) {
            batch.setServiceTime(routingAlgorithm.run(batch));
        }

        // Return the throughput time for the batch.
        return batch.getServiceTime() + (now - batch.getEarliestArrivalTime());
    }
}