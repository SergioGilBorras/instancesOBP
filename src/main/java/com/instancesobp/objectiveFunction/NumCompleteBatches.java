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

/**
 * Represents the objective function for calculating the number of full batches in a warehouse.
 * A batch is considered full if its available weight is zero.
 * This class provides methods to calculate the total number of full batches
 * from a list of batches or a single batch.
 * This objective function is useful for evaluating the efficiency of batch processing
 * in warehouse operations.
 *
 * @author rasta
 * @version 1.0
 */
public class NumCompleteBatches extends ObjectiveFunction {

    /**
     * Constructs a NumFullBatches objective function with the specified warehouse.
     *
     * @param warehouse The warehouse object containing layout and configuration details.
     */
    public NumCompleteBatches(Warehouse warehouse) {
        super(warehouse);
    }

    /**
     * Calculates the total number of full batches in a list of batches.
     * A batch is considered full if its available weight is zero.
     *
     * @param batchList The list of batches to be processed.
     * @return The total number of full batches in the list.
     * @throws Exception If an error occurs during the calculation.
     */
    @Override
    public double run(List<Batch> batchList) throws Exception {
        double numFullBatches = 0; // Counter for the number of full batches.

        // Iterate through each batch in the list and calculate the number of full batches.
        for (Batch batch : batchList) {
            numFullBatches += run(batch); // Add 1 if the batch is full, otherwise add 0.
        }

        return numFullBatches; // Return the total number of full batches.
    }

    /**
     * Determines if a single batch is full.
     * A batch is considered full if its available weight is zero.
     *
     * @param batch The batch to be evaluated.
     * @return 1 if the batch is full, 0 otherwise.
     * @throws Exception If an error occurs during the calculation.
     */
    @Override
    public double run(Batch batch) throws Exception {
        // Return 1 if the batch's available weight is zero, otherwise return 0.
        return (batch.getAvailableWeight() == 0) ? 1 : 0;
    }
}