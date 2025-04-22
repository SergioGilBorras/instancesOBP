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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implements a sorting algorithm for orders based on their weight.
 * This class extends the {@link SortBy} abstract class and provides
 * a concrete implementation for sorting orders by their weight in ascending order.
 * It also implements the {@link Comparator} interface to compare two orders.
 * The sorting logic is defined in the {@code compare} method, and the
 * {@code run} method sorts a list of orders using this logic.
 * Additionally, the {@code getValue} method retrieves the weight of an order
 * for comparison purposes.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class SortByWeight extends SortBy implements Comparator<Order> {

    /**
     * Default constructor for the SortByWeight class.
     * This constructor initializes the sorting algorithm.
     */
    public SortByWeight() {
        super();
    }

    /**
     * Compares two orders based on their weight.
     * This method retrieves the weight of both orders and compares them
     * in ascending order.
     *
     * @param order1 The first order to compare.
     * @param order2 The second order to compare.
     * @return A negative integer, zero, or a positive integer as the first order
     * is less than, equal to, or greater than the second order based on weight.
     */
    @Override
    public int compare(Order order1, Order order2) {
        return order1.getWeight().compareTo(order2.getWeight());
    }

    /**
     * Sorts a list of orders by their weight in ascending order.
     * This method creates a copy of the input list, sorts it using the
     * {@code compare} method, and returns the sorted list.
     *
     * @param orderList The list of orders to be sorted.
     * @return A sorted list of orders by weight in ascending order.
     */
    @Override
    public List<Order> run(List<Order> orderList) {
        ArrayList<Order> sortedOrderList = new ArrayList<>(orderList);
        sortedOrderList.sort(new SortByWeight());
        return sortedOrderList;
    }

    /**
     * Retrieves the weight of an order for comparison purposes.
     * This method is used to extract the value used for sorting orders.
     *
     * @param order The order for which the weight is retrieved.
     * @return The weight of the order as a {@code Double}.
     */
    @Override
    public Double getValue(Order order) {
        return order.getWeight();
    }
}