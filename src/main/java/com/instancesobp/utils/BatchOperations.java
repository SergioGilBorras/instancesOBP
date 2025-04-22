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
package com.instancesobp.utils;

import com.instancesobp.models.Batch;
import com.instancesobp.models.Order;
import com.instancesobp.models.Product;
import com.instancesobp.models.Warehouse;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for performing operations on batches of orders.
 * This class provides methods for batch manipulation, validation, and analysis.
 */
public class BatchOperations {

    /**
     * Private constructor to prevent instantiation.
     */
    private BatchOperations() {
        throw new UnsupportedOperationException("This is a static class and cannot be instantiated");
    }

    /**
     * Compares two lists of orders to check if they are equal.
     *
     * @param orderList1 The first list of orders.
     * @param orderList2 The second list of orders.
     * @return True if both lists contain the same orders, false otherwise.
     */
    public static boolean equals(List<Order> orderList1, List<Order> orderList2) {
        if (orderList2.size() != orderList1.size()) {
            return false;
        }
        boolean contains = false;
        for (Order order2 : orderList2) {
            contains = false;
            for (Order order1 : orderList1) {
                if (order2.equals(order1)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                break;
            }
        }
        return contains;
    }

    /**
     * Checks if an order can be added to a batch without exceeding the maximum weight limit.
     *
     * @param order The order to be added.
     * @param batch The batch to which the order will be added.
     * @return True if the order can be added without exceeding the weight limit, false otherwise.
     */
    public static boolean canAddOrderToBatch(Order order, Batch batch) {
        return (batch.getWeight() + order.getWeight()) <= batch.getMaxWeight();
    }

    /**
     * Checks if two orders can be added together in a batch without exceeding the maximum weight limit.
     *
     * @param order1    The first order to be added.
     * @param order2    The second order to be added.
     * @param maxWeight The maximum weight allowed for the batch.
     * @return True if the combined weight of the two orders does not exceed the maximum weight, false otherwise.
     */
    public static boolean canAddOrdersInABatch(Order order1, Order order2, double maxWeight) {
        return (order1.getWeight() + order2.getWeight()) <= maxWeight;
    }


    /**
     * Combines two batches into one, ensuring the combined weight does not exceed the maximum allowed weight.
     *
     * @param batch1 The first batch.
     * @param batch2 The second batch.
     * @return A new batch containing all orders from both batches.
     * @throws Exception If the combined weight exceeds the maximum allowed weight.
     */
    public static Batch unionBatch(Batch batch1, Batch batch2) throws Exception {
        Batch combinedBatch = batch1.clone();
        if ((batch1.getWeight() + batch2.getWeight()) <= combinedBatch.getMaxWeight()) {
            for (Order order : batch2.getOrders()) {
                combinedBatch.addOrder(order);
            }
            if (batch2.getEarliestArrivalTime() < combinedBatch.getEarliestArrivalTime()) {
                combinedBatch.recalculateMinimumArrivalTime();
            }
        } else {
            throw new Exception("Maximum weight limit exceeded for the batch. [BatchOperations.unionBatch]");
        }
        combinedBatch.setServiceTime(0.0);
        return combinedBatch;
    }

    /**
     * Retrieves a list of unique product IDs from a batch.
     *
     * @param batch The batch to extract product IDs from.
     * @return A list of unique product IDs.
     */
    public static List<Integer> getListProductId(Batch batch) {
        List<Integer> productIds = new ArrayList<>();
        for (Order order : batch.getOrders()) {
            for (Product product : order.getProducts()) {
                if (!productIds.contains(product.getId())) {
                    productIds.add(product.getId());
                }
            }
        }
        return productIds;
    }

    /**
     * Counts the number of duplicate orders across a list of batches.
     *
     * @param createdBatches The list of batches to analyze.
     * @return The number of duplicate orders.
     * @throws Exception If an error occurs during processing.
     */
    public static int numberOfDuplicateOrders(List<Batch> createdBatches) throws Exception {
        List<Integer> orderIds = new ArrayList<>();
        int duplicateCount = 0;
        for (Batch batch : createdBatches) {
            for (Order order : batch.getOrders()) {
                if (orderIds.contains(order.getId())) {
                    duplicateCount++;
                } else {
                    orderIds.add(order.getId());
                }
            }
        }
        return duplicateCount;
    }

    /**
     * Counts the total number of orders across a list of batches.
     *
     * @param batchList The list of batches to analyze.
     * @return The total number of orders.
     */
    public static int numberOfOrders(List<Batch> batchList) {
        int totalOrders = 0;
        for (Batch batch : batchList) {
            totalOrders += batch.getOrders().size();
        }
        return totalOrders;
    }

    /**
     * Validates a solution by checking the number of orders and ensuring no duplicate orders exist.
     *
     * @param warehouse The warehouse containing the original list of orders.
     * @param batchList The list of batches to validate.
     * @throws Exception If the number of orders is incorrect or if duplicate orders are found.
     */
    public static void validateSolution(Warehouse warehouse, List<Batch> batchList) throws Exception {
        if (warehouse.getOrders().size() != (numberOfOrders(batchList))) {
            throw new Exception("Number of orders in the solution is incorrect. [BatchOperations.validationSolution]");
        }

        if (numberOfDuplicateOrders(batchList) != 0) {
            throw new Exception("There are duplicate orders in the solution. [BatchOperations.validationSolution]");
        }
    }
}