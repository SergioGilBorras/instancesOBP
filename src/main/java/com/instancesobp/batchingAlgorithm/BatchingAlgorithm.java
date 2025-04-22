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
package com.instancesobp.batchingAlgorithm;

import com.instancesobp.models.Batch;
import com.instancesobp.models.Order;
import com.instancesobp.models.Warehouse;

import java.io.Serializable;
import java.util.List;

/**
 * Abstract base class for batching algorithms.
 * This class provides a framework for implementing batching algorithms
 * that group orders into batches for processing in a warehouse.
 * Subclasses must implement the {@code run} method to define the specific batching logic.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public abstract class BatchingAlgorithm implements Serializable {

    /**
     * The warehouse object containing layout and configuration details.
     */
    protected Warehouse warehouse;

    /**
     * Constructor for the BatchingAlgorithm class.
     *
     * @param warehouse The warehouse object containing layout and configuration details.
     */
    public BatchingAlgorithm(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    /**
     * Executes the batching algorithm for a given list of orders.
     * This method must be implemented by subclasses to define the specific batching logic.
     * It processes the list of orders and returns a list of batches.
     *
     * @param orderList The list of orders to be batched.
     * @return A list of batches created from the given orders.
     * @throws Exception If an error occurs during the batching process.
     */
    public abstract List<Batch> run(List<Order> orderList) throws Exception;
}