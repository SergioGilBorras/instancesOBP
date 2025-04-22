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

import com.instancesobp.models.Batch;
import com.instancesobp.models.Order;
import com.instancesobp.models.Warehouse;
import com.instancesobp.objectiveFunction.ObjectiveFunction;

import java.util.ArrayList;
import java.util.List;

import static com.instancesobp.utils.BatchOperations.canAddOrdersInABatch;
import static com.instancesobp.utils.BatchOperations.unionBatch;

/**
 * Implements the Clarke and Wright Savings constructive heuristic algorithm for batching orders.
 * This algorithm groups orders into batches while minimizing the total cost or distance.
 * It uses an objective function to evaluate the quality of the batches.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class CWSavingConstructive extends ConstructiveAlgorithm {

    /**
     * The objective function used to evaluate the quality of batches.
     */
    private final ObjectiveFunction objectiveFunction;

    /**
     * Constructor for the CWSavingConstructive class.
     *
     * @param warehouse         The warehouse containing the orders and configuration details.
     * @param objectiveFunction The objective function used to evaluate batches.
     */
    public CWSavingConstructive(Warehouse warehouse, ObjectiveFunction objectiveFunction) {
        super(warehouse);
        this.objectiveFunction = objectiveFunction;
    }

    /**
     * Executes the Clarke and Wright Savings algorithm to create batches from a list of orders.
     *
     * @param orderList The list of initial orders to be batched.
     * @return A list of batches created by the algorithm.
     * @throws Exception If an error occurs during the batching process.
     */
    @Override
    public List<Batch> run(List<Order> orderList) throws Exception {
        return applyClarkeAndWright(orderList);
    }

    /**
     * Applies the Clarke and Wright Savings algorithm to group orders into batches.
     *
     * @param initialOrders The list of initial orders to be batched.
     * @return A list of batches created by the algorithm.
     * @throws Exception If an error occurs during the batching process.
     */
    private List<Batch> applyClarkeAndWright(List<Order> initialOrders) throws Exception {
        ArrayList<Batch> batchList = new ArrayList<>();
        if (initialOrders.size() == 1) {
            Batch batch = new Batch(warehouse.getWorkerCapacity());
            batch.addOrder(initialOrders.get(0));
            batchList.add(batch);
        } else if (initialOrders.size() == 2 && canAddOrdersInABatch(initialOrders.get(0), initialOrders.get(1), warehouse.getWorkerCapacity())) {
            Batch batch = new Batch(warehouse.getWorkerCapacity());
            batch.addOrder(initialOrders.get(0));
            batch.addOrder(initialOrders.get(1));
            batchList.add(batch);
        } else if (initialOrders.size() == 2) {
            Batch batch1 = new Batch(warehouse.getWorkerCapacity());
            Batch batch2 = new Batch(warehouse.getWorkerCapacity());
            batch1.addOrder(initialOrders.get(0));
            batch2.addOrder(initialOrders.get(1));
            batchList.add(batch1);
            batchList.add(batch2);
        } else {
            batchList = initializeBatchList(initialOrders);

            do {
                ArrayList<Batch> newBatchList = combineBestBatches(batchList);
                if (newBatchList != null) {
                    batchList = newBatchList;
                } else {
                    break;
                }
            } while (true);
        }
        return batchList;
    }

    /**
     * Initializes a list of batches, each containing a single order.
     *
     * @param initialOrders The list of initial orders to be batched.
     * @return A list of batches, each containing one order.
     * @throws Exception If an error occurs during the initialization process.
     */
    public ArrayList<Batch> initializeBatchList(List<Order> initialOrders) throws Exception {
        ArrayList<Batch> batchList = new ArrayList<>();

        for (Order order : initialOrders) {
            Batch batch = new Batch(warehouse.getWorkerCapacity());
            batch.addOrder(order);
            batchList.add(batch);
            objectiveFunction.run(batch);
        }
        return batchList;
    }

    /**
     * Combines the two best batches based on the savings calculated by the objective function.
     *
     * @param batchList The current list of batches.
     * @return A new list of batches after combining the best two batches, or null if no combination is possible.
     * @throws Exception If an error occurs during the combination process.
     */
    public ArrayList<Batch> combineBestBatches(ArrayList<Batch> batchList) throws Exception {
        Batch bestBatch1 = null, bestBatch2 = null;
        double bestSaving = Double.MAX_VALUE;
        boolean hasChange = false;

        for (int i = 0; i < batchList.size(); i++) {
            for (int j = i + 1; j < batchList.size(); j++) {
                if (batchList.get(i).getWeight() + batchList.get(j).getWeight() <= warehouse.getWorkerCapacity()) {
                    Batch combinedBatch = unionBatch(batchList.get(i), batchList.get(j));
                    double combinedValue = objectiveFunction.run(combinedBatch);
                    double saving = objectiveFunction.run(batchList.get(i)) + objectiveFunction.run(batchList.get(j)) - combinedValue;

                    if (saving < bestSaving) {
                        bestSaving = combinedValue;
                        bestBatch1 = batchList.get(i);
                        bestBatch2 = batchList.get(j);
                        hasChange = true;
                    }
                }
            }
        }

        if (!hasChange) {
            return null;
        }

        batchList.remove(bestBatch1);
        batchList.remove(bestBatch2);
        batchList.add(unionBatch(bestBatch1, bestBatch2));
        return batchList;
    }
}