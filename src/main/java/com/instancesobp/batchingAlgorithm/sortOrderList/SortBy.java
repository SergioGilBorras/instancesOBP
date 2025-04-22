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
package com.instancesobp.batchingAlgorithm.sortOrderList;

import com.instancesobp.models.Order;

import java.util.Comparator;
import java.util.List;

/**
 * Abstract base class for sorting a list of orders based on specific criteria.
 * This class provides a framework for implementing custom sorting algorithms
 * for orders in a warehouse. Subclasses must define the sorting logic and
 * the value used for comparison.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public abstract class SortBy implements Comparator<Order> {

    /**
     * Default constructor for the SortBy class.
     * This constructor initializes the sorting algorithm.
     */
    public SortBy() {
        // Default constructor
    }

    /**
     * Sorts a list of orders based on the implemented sorting criteria.
     * Subclasses must implement this method to define the sorting logic.
     *
     * @param orderList The list of orders to be sorted.
     * @return A sorted list of orders based on the implemented criteria.
     */
    public abstract List<Order> run(List<Order> orderList);

    /**
     * Retrieves the value used for comparison for a given order.
     * Subclasses must implement this method to define the value used
     * for sorting the orders.
     *
     * @param order The order for which the comparison value is retrieved.
     * @return The value used for comparison.
     */
    public abstract Double getValue(Order order);
}
