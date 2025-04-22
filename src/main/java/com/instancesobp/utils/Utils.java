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

import java.util.List;

/**
 * Utility class that provides static methods for common operations
 * related to orders and batches in the project.
 * This class includes methods for initializing arrival times,
 * counting orders and products, comparing batches, and printing solutions.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class Utils {

    /**
     * Private constructor to prevent instantiation.
     */
    private Utils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Initializes the arrival time for each order in the given list.
     * The arrival time is set to the current system time in milliseconds.
     *
     * @param orderList the list of orders to initialize
     */
    public static void initArrivalTime(List<Order> orderList) {
        for (Order order : orderList) {
            order.setArrivalTime(System.currentTimeMillis());
        }
    }

    /**
     * Counts the total number of orders across all batches in the given list.
     *
     * @param batches the list of batches to process
     * @return the total number of orders in all batches
     */
    public static int numberOrders(List<Batch> batches) {
        int num = 0;
        for (Batch b1 : batches) {
            num += b1.getOrders().size();
        }
        return num;
    }

    /**
     * Counts the total number of products across all orders in the given list.
     *
     * @param orderList the list of orders to process
     * @return the total number of products in all orders
     */
    public static int numberProducts(List<Order> orderList) {
        int num = 0;
        for (Order o1 : orderList) {
            num += o1.getProducts().size();
        }
        return num;
    }

    /**
     * Counts the total number of products across all batches in the given list.
     *
     * @param batchList the list of batches to process
     * @return the total number of products in all batches
     */
    public static int numberProductsInBatches(List<Batch> batchList) {
        int num = 0;
        for (Batch b1 : batchList) {
            num += numberProducts(b1.getOrders());
        }
        return num;
    }

    /**
     * Compares two lists of batches to determine if they are equal.
     * Two lists are considered equal if they have the same size and
     * each corresponding batch is equal.
     *
     * @param batchList  the first list of batches to compare
     * @param batchList1 the second list of batches to compare
     * @return true if the two lists are equal, false otherwise
     */
    public static boolean compare(List<Batch> batchList, List<Batch> batchList1) {
        if (batchList.size() != batchList1.size()) {
            return false;
        }
        for (int i = 0; i < batchList.size(); i++) {
            Batch b = batchList.get(i);
            Batch b1 = batchList1.get(i);
            if (!b.equals(b1)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Prints the solution represented by the given list of batches.
     * Each batch is printed as a list of order IDs, separated by commas.
     *
     * @param batchList the list of batches to print
     */
    public static void printSolution(List<Batch> batchList) {
        System.out.println("************");
        for (Batch batch : batchList) {
            System.out.print("[");
            for (Order order : batch.getOrders()) {
                System.out.print(order.getId() + ", ");
            }
            System.out.print("] - ");
        }
        System.out.println();
    }
}