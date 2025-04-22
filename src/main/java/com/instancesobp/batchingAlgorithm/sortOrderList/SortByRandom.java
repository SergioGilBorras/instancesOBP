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

import com.instancesobp.Configuration;
import com.instancesobp.models.Order;

import java.util.*;

/**
 * Implements a random sorting algorithm for a list of orders.
 * This class uses a random number generator to shuffle the order list
 * and provides a random value for comparison between orders.
 * The random behavior is controlled by a seed value from the configuration
 * to ensure reproducibility.
 * This class extends the {@link SortBy} abstract class and implements
 * the {@link Comparator} interface for comparing orders.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class SortByRandom extends SortBy implements Comparator<Order> {

    /**
     * Random number generator used for shuffling and comparisons.
     */
    private final Random randomGenerator;

    /**
     * Constructor for the SortByRandom class.
     * Initializes the random number generator with a seed value from the configuration.
     */
    public SortByRandom() {
        randomGenerator = new Random(Configuration.SEED);
    }

    /**
     * Compares two orders randomly.
     * Generates a random value and determines the comparison result based on it.
     *
     * @param order1 The first order to compare.
     * @param order2 The second order to compare.
     * @return 0 if the random value is exactly 0.5, 1 if it is less than 0.5, and -1 otherwise.
     */
    @Override
    public int compare(Order order1, Order order2) {
        double randomValue = randomGenerator.nextDouble();
        if (randomValue == 0.5) {
            return 0;
        }
        return (randomValue < 0.5) ? 1 : -1;
    }

    /**
     * Shuffles the given list of orders randomly.
     * Creates a copy of the input list, shuffles it using the random number generator,
     * and returns the shuffled list.
     *
     * @param orderList The list of orders to be shuffled.
     * @return A shuffled list of orders.
     */
    @Override
    public List<Order> run(List<Order> orderList) {
        ArrayList<Order> shuffledOrderList = new ArrayList<>(orderList);
        Collections.shuffle(shuffledOrderList, randomGenerator);
        return shuffledOrderList;
    }

    /**
     * Retrieves a random value for a given order.
     * This method generates and returns a random value using the random number generator.
     *
     * @param order The order for which the random value is generated.
     * @return A random value between 0.0 and 1.0.
     */
    @Override
    public Double getValue(Order order) {
        return randomGenerator.nextDouble();
    }
}