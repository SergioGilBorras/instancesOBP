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
package com.instancesobp.batchingAlgorithm.constructiveHeuristic;

import com.instancesobp.batchingAlgorithm.sortOrderList.SortBy;
import com.instancesobp.models.Batch;
import com.instancesobp.models.Order;
import com.instancesobp.models.Warehouse;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements a basic constructive batching algorithm for grouping orders into batches.
 * This algorithm processes a list of orders and creates batches based on its orden position.
 * It supports both compact and non-compact batching strategies.
 * Compact batching attempts to fill existing batches before creating new ones, while
 * non-compact batching creates a new batch for each unassigned order.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class BasicConstructive extends ConstructiveAlgorithm {

    /**
     * Indicates whether the algorithm uses compact batching.
     */
    protected boolean compact = true;

    /**
     * Sorting strategy for the order list.
     */
    protected SortBy sortBy;

    /**
     * Constructor for the BasicConstructive class.
     *
     * @param warehouse The warehouse object containing layout and configuration details.
     * @param sortBy    The sorting strategy to apply to the order list.
     */
    public BasicConstructive(Warehouse warehouse, SortBy sortBy) {
        super(warehouse);
        this.sortBy = sortBy;
    }

    /**
     * Constructor for the BasicConstructive class with an option to set compact batching.
     *
     * @param warehouse The warehouse object containing layout and configuration details.
     * @param sortBy    The sorting strategy to apply to the order list.
     * @param compact   Indicates whether to use compact batching.
     */
    public BasicConstructive(Warehouse warehouse, SortBy sortBy, boolean compact) {
        super(warehouse);
        this.compact = compact;
        this.sortBy = sortBy;
    }

    /**
     * Executes the basic constructive batching algorithm.
     * This method processes the given list of orders, optionally sorts them using the
     * provided sorting strategy, and creates batches based on the worker's capacity.
     *
     * @param orderList The list of orders to be batched.
     * @return A list of batches created from the given orders.
     * @throws Exception If an error occurs during the batching process.
     */
    @Override
    public List<Batch> run(List<Order> orderList) throws Exception {
        List<Order> initialOrders = new ArrayList<>(orderList);
        if (sortBy != null) {
            initialOrders = sortBy.run(initialOrders);
        }
        return buildBatches(initialOrders);
    }

    /**
     * Builds batches from the given list of orders.
     * This method iterates through the list of orders and assigns them to batches based
     * on the worker's capacity. If compact batching is enabled, it attempts to fill
     * existing batches before creating new ones.
     *
     * @param initialOrders The list of orders to be batched.
     * @return A list of batches created from the given orders.
     * @throws Exception If an order exceeds the maximum batch weight.
     */
    private List<Batch> buildBatches(List<Order> initialOrders) throws Exception {
        List<Batch> createdBatches = new ArrayList<>();
        if (!initialOrders.isEmpty()) {
            Batch currentBatch = new Batch(warehouse.getWorkerCapacity());
            createdBatches.add(currentBatch);

            for (Order order : initialOrders) {
                boolean assigned = false;

                if (this.compact) {
                    for (Batch batch : createdBatches) {
                        if (batch.getAvailableWeight() >= order.getWeight()) {
                            batch.addOrder(order);
                            assigned = true;
                            break;
                        }
                    }
                } else {
                    currentBatch = createdBatches.get(createdBatches.size() - 1);
                    if (currentBatch.getAvailableWeight() >= order.getWeight()) {
                        currentBatch.addOrder(order);
                        assigned = true;
                    }
                }

                if (!assigned) {
                    Batch newBatch = new Batch(warehouse.getWorkerCapacity());
                    try {
                        newBatch.addOrder(order);
                    } catch (Exception e) {
                        throw new Exception("Data error: A single order exceeds the maximum batch weight.");
                    }
                    createdBatches.add(newBatch);
                }
            }
        }
        return createdBatches;
    }
}