package com.instancesobp.objectiveFunction;

import java.util.List;

import com.instancesobp.models.Batch;
import com.instancesobp.models.Warehouse;
import com.instancesobp.routingAlgorithm.RoutingAlgorithm;

/**
 * Represents the objective function for calculating the sum of the absolute differences
 * between the average batch picking time and the picking time of each batch.
 * This class evaluates the uniformity of batch picking times in a warehouse,
 * aiming to minimize the deviation from the average picking time.
 * It uses a routing algorithm to calculate the service time for batches
 * that do not already have it calculated.
 * This objective function is useful for analyzing the efficiency and balance
 * of batch processing in warehouse operations.
 *
 * @author rasta
 * @version 1.0
 */
public class SumAbsoluteDiffBatchTimes extends ObjectiveFunction {

    /**
     * Constructs a SumDiffAvgBatchPickingTime objective function with the specified warehouse
     * and routing algorithm.
     *
     * @param warehouse        The warehouse object containing layout and configuration details.
     * @param routingAlgorithm The routing algorithm used to calculate the service time for batches.
     */
    public SumAbsoluteDiffBatchTimes(Warehouse warehouse, RoutingAlgorithm routingAlgorithm) {
        super(warehouse, routingAlgorithm);
    }

    /**
     * Calculates the sum of the absolute differences between the average batch picking time
     * and the picking time of each batch in a list of batches.
     * This method first calculates the total picking time for all batches and determines
     * the average picking time. Then, it computes the sum of the absolute differences
     * between the average picking time and the picking time of each batch.
     *
     * @param batchList The list of batches to be processed.
     * @return The sum of the absolute differences between the average picking time
     * and the picking time of each batch.
     * @throws Exception If an error occurs during the calculation of the service time.
     */
    @Override
    public double run(List<Batch> batchList) throws Exception {
        double totalPickingTime = 0; // Accumulator for the total picking time.

        // Calculate the total picking time for all batches.
        for (Batch batch : batchList) {
            totalPickingTime += this.run(batch);
        }

        // Calculate the average picking time.
        double averagePickingTime = totalPickingTime / batchList.size();

        double sumDiffAvgBatch = 0; // Accumulator for the sum of absolute differences.

        // Calculate the sum of the absolute differences from the average picking time.
        for (Batch batch : batchList) {
            sumDiffAvgBatch += Math.abs(averagePickingTime - this.run(batch));
        }

        return sumDiffAvgBatch; // Return the calculated sum of absolute differences.
    }

    /**
     * Calculates the picking time for a single batch.
     * This method calculates the service time for the batch if it has not been set,
     * and then returns the service time.
     *
     * @param batch The batch to be processed.
     * @return The picking time for the specified batch.
     * @throws Exception If an error occurs during the calculation of the service time.
     */
    @Override
    public double run(Batch batch) throws Exception {
        // Calculate the service time if it has not been set.
        if (batch.getServiceTime() == 0) {
            batch.setServiceTime(routingAlgorithm.run(batch));
        }

        // Return the service time for the batch.
        return batch.getServiceTime();
    }
}