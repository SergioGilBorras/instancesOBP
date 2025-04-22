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
package com.instancesobp.routingAlgorithm;

import java.io.Serializable;
import com.instancesobp.models.Batch;
import com.instancesobp.models.Warehouse;

/**
 * Abstract base class for implementing routing algorithms in a warehouse.
 * This class provides a common structure for all routing algorithms, which
 * calculate the total distance traveled by a picker to collect items in a batch.
 * Subclasses must implement the {@code run} method to define the specific routing logic.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public abstract class RoutingAlgorithm implements Serializable {

    /** The warehouse object containing layout and configuration details. */
    protected Warehouse wh;

    /**
     * Constructor for the RoutingAlgorithm class.
     *
     * @param warehouse The warehouse object containing the layout and configuration details.
     */
    public RoutingAlgorithm(Warehouse warehouse) {
        this.wh = warehouse;
    }

    /**
     * Executes the routing algorithm for a given batch of orders.
     * This method must be implemented by subclasses to define the specific routing logic.
     * It calculates the total distance traveled by the picker to collect all items in the batch.
     *
     * @param batch The batch of orders to be processed.
     * @return The total distance traveled by the picker to collect all items in the batch.
     */
    public abstract double run(Batch batch);

}
