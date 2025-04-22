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

import com.instancesobp.models.Batch;
import com.instancesobp.models.Warehouse;
import com.instancesobp.routingAlgorithm.RoutingAlgorithm;

import java.util.List;

/**
 * Represents the objective function for calculating the sum of earliness and tardiness
 * of orders in a warehouse. Earliness is the time difference when an order is completed
 * before its due date, while tardiness is the time difference when an order is completed
 * after its due date. This class combines both metrics to evaluate the efficiency of
 * order processing.
 * <p>
 * This objective function is useful for balancing early and late order completions
 * in warehouse operations.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class SumEarlinessTardiness extends ObjectiveFunction {

    /**
     * Constructs a SumEarlinessTardiness objective function with the specified warehouse
     * and routing algorithm.
     *
     * @param warehouse        The warehouse object containing layout and configuration details.
     * @param routingAlgorithm The routing algorithm used to calculate the service time for batches.
     */
    public SumEarlinessTardiness(Warehouse warehouse, RoutingAlgorithm routingAlgorithm) {
        super(warehouse, routingAlgorithm);
    }

    /**
     * Calculates the sum of earliness and tardiness for a list of batches.
     * This method uses the SumEarliness and SumTardiness objective functions to compute
     * the respective metrics and returns their sum.
     *
     * @param batchList The list of batches to be processed.
     * @return The sum of earliness and tardiness for all orders in the list of batches.
     * @throws Exception If an error occurs during the calculation of the service time.
     */
    @Override
    public double run(List<Batch> batchList) throws Exception {
        SumEarliness sumEarliness = new SumEarliness(warehouse, routingAlgorithm);
        SumTardiness sumTardiness = new SumTardiness(warehouse, routingAlgorithm);

        return sumTardiness.run(batchList) + sumEarliness.run(batchList);
    }

    /**
     * Calculates the sum of earliness and tardiness for a single batch.
     * This method uses the SumEarliness and SumTardiness objective functions to compute
     * the respective metrics and returns their sum.
     *
     * @param batch The batch to be processed.
     * @return The sum of earliness and tardiness for all orders in the batch.
     * @throws Exception If an error occurs during the calculation of the service time.
     */
    @Override
    public double run(Batch batch) throws Exception {
        SumEarliness sumEarliness = new SumEarliness(warehouse, routingAlgorithm);
        SumTardiness sumTardiness = new SumTardiness(warehouse, routingAlgorithm);

        return sumTardiness.run(batch) + sumEarliness.run(batch);
    }
}