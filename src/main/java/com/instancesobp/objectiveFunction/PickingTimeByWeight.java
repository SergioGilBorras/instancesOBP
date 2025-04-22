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
 * Represents an objective function that calculates the picking time
 * in a warehouse, adjusted by the weight of the batches.
 * This class uses a routing algorithm to compute the time required to pick all items
 * in a batch or a list of batches, dividing the picking time by the weight of the batch.
 * It ensures that the service time for each batch is calculated only once and reused
 * for efficiency.
 *
 * @author rasta
 * @version 1.0
 */
public class PickingTimeByWeight extends ObjectiveFunction {

    /**
     * Constructs a PickingTimeByWeight objective function with the specified warehouse
     * and routing algorithm.
     *
     * @param warehouse        The warehouse object containing layout and configuration details.
     * @param routingAlgorithm The routing algorithm used to calculate the picking time.
     */
    public PickingTimeByWeight(Warehouse warehouse, RoutingAlgorithm routingAlgorithm) {
        super(warehouse, routingAlgorithm);
    }

    /**
     * Calculates the total picking time for a list of batches, adjusted by their weights.
     * This method iterates through all batches in the list and calculates the picking time
     * for each batch using the routing algorithm. The results are accumulated to return
     * the total picking time.
     *
     * @param batchList The list of batches to be processed.
     * @return The total picking time by weight for all batches in the list.
     * @throws Exception If an error occurs during the calculation of the picking time.
     */
    @Override
    public double run(List<Batch> batchList) throws Exception {
        // Accumulator for the total picking time by the weight of the batch.
        double totalPickingTime = 0;
        double totalWeight = 0;
        for (Batch batch : batchList) {
            if (batch.getServiceTime() == 0) { // Check if the service time has already been calculated.
                batch.setServiceTime(routingAlgorithm.run(batch)); // Calculate and set the service time.
            }
            totalWeight += batch.getWeight(); // Accumulate the total weight of the batches.
            totalPickingTime += batch.getServiceTime();
        }
        return totalPickingTime / totalWeight;
    }

    /**
     * Calculates the picking time for a single batch, adjusted by its weight.
     * If the service time for the batch has not been previously calculated, this method
     * uses the routing algorithm to compute it and stores the result in the batch.
     * The picking time is then divided by the weight of the batch.
     *
     * @param batch The batch to be processed.
     * @return The picking time for the specified batch, adjusted by its weight.
     * @throws Exception If an error occurs during the calculation of the picking time.
     */
    @Override
    public double run(Batch batch) throws Exception {
        if (batch.getServiceTime() == 0) { // Check if the service time has already been calculated.
            batch.setServiceTime(routingAlgorithm.run(batch)); // Calculate and set the service time.
        }
        return batch.getServiceTime() / batch.getWeight(); // Return the adjusted picking time by weight.
    }
}