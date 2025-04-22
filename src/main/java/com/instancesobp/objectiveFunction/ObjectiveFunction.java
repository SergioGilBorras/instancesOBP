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

import java.io.Serializable;
import java.util.List;

import com.instancesobp.models.Batch;
import com.instancesobp.models.Warehouse;
import com.instancesobp.routingAlgorithm.RoutingAlgorithm;

/**
 * Abstract class representing an objective function for warehouse operations.
 * This class provides a base structure for implementing specific objective functions
 * that calculate metrics such as picking time or throughput time. It includes
 * references to a warehouse and a routing algorithm, which are essential for
 * performing calculations.
 * Subclasses must implement the abstract methods to define the specific behavior
 * of the objective function.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public abstract class ObjectiveFunction implements Serializable {

    /**
     * The warehouse object containing layout and configuration details.
     */
    protected Warehouse warehouse;

    /**
     * The routing algorithm used to calculate metrics for the objective function.
     */
    protected RoutingAlgorithm routingAlgorithm = null;

    /**
     * Constructs an ObjectiveFunction with the specified warehouse and routing algorithm.
     *
     * @param warehouse        The warehouse object containing layout and configuration details.
     * @param routingAlgorithm The routing algorithm used for calculations.
     */
    public ObjectiveFunction(Warehouse warehouse, RoutingAlgorithm routingAlgorithm) {
        this.routingAlgorithm = routingAlgorithm;
        this.warehouse = warehouse;
    }

    /**
     * Constructs an ObjectiveFunction with the specified warehouse.
     * This constructor can be used when no routing algorithm is required.
     *
     * @param warehouse The warehouse object containing layout and configuration details.
     */
    public ObjectiveFunction(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    /**
     * Retrieves the routing algorithm associated with this objective function.
     *
     * @return The routing algorithm used for calculations, or null if not set.
     */
    public RoutingAlgorithm getRoutingAlgorithm() {
        return routingAlgorithm;
    }

    /**
     * Abstract method to calculate the objective function for a list of batches.
     * Subclasses must implement this method to define the specific calculation logic.
     *
     * @param batchList The list of batches to be processed.
     * @return The calculated value of the objective function for the given batches.
     * @throws Exception If an error occurs during the calculation.
     */
    public abstract double run(List<Batch> batchList) throws Exception;

    /**
     * Abstract method to calculate the objective function for a single batch.
     * Subclasses must implement this method to define the specific calculation logic.
     *
     * @param batch The batch to be processed.
     * @return The calculated value of the objective function for the given batch.
     * @throws Exception If an error occurs during the calculation.
     */
    public abstract double run(Batch batch) throws Exception;

}