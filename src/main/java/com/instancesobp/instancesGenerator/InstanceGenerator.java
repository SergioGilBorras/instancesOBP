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
package com.instancesobp.instancesGenerator;

import com.instancesobp.models.Aisles;
import com.instancesobp.models.Order;
import com.instancesobp.models.Product;
import com.instancesobp.models.Warehouse;
import com.instancesobp.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.instancesobp.utils.Constants.*;

/**
 * Generates instances of warehouses, orders, and products for simulation purposes.
 * This class provides methods to create warehouses with specific configurations,
 * generate orders with products, and assign products to aisles based on various criteria.
 * The generated instances are used for testing and evaluating warehouse algorithms.
 *
 * @author Sergio Gil Borrás
 */
public class InstanceGenerator {

    /**
     * Random number generator for generating random values.
     */
    private final Random random = new Random();
    /**
     * Unique identifier for products.
     */
    private int productId = 1;

    /**
     * Unique identifier for orders.
     */
    private int orderId = 1;

    /**
     * Default constructor for the InstanceGenerator class.
     * This constructor initializes the instance generator.
     */
    public InstanceGenerator() {
        // Default constructor
    }

    /**
     * Determines the side of the depot position for a given aisle.
     *
     * @param numberOfAisles The total number of aisles.
     * @param depotPosition  The position of the depot.
     * @param aisleIndex     The index of the current aisle.
     * @return The side of the depot position (-1 for left, 0 for front, 1 for right).
     */
    private static int getDepotSidePosition(int numberOfAisles, int depotPosition, int aisleIndex) {
        int side;
        double middleAisle = Math.floor(numberOfAisles / 2.0);

        if (depotPosition == DEPOT_CORNER && aisleIndex == 0) {
            side = FRONT_SIDE_DEPOT;
        } else if (depotPosition == DEPOT_CORNER) {
            side = LEFT_SIDE_DEPOT;
        } else if (aisleIndex < middleAisle) {
            side = RIGHT_SIDE_DEPOT;
        } else if (aisleIndex == middleAisle) {
            side = FRONT_SIDE_DEPOT;
        } else {
            side = LEFT_SIDE_DEPOT;
        }

        return side;
    }

    /**
     * Generates a warehouse instance with the specified configuration.
     *
     * @param warehouseId         The unique identifier for the warehouse.
     * @param numberOfOrders      The number of orders to generate.
     * @param numberOfAisles      The number of aisles in the warehouse.
     * @param isDepotCenter       Whether the depot is located at the center.
     * @param orderLocation       The location of the orders.
     * @param shelfLength         The length of the shelves.
     * @param shelfWidth          The width of the shelves.
     * @param aisleWidth          The width of the aisles.
     * @param workerCapacity      The capacity of the worker.
     * @param pickingTime         The time required for picking an item.
     * @param outsideTurnTime     The time required for turning outside the aisle.
     * @param insideTurnTime      The time required for turning inside the aisle.
     * @param numberOfSlots       The number of slots available in the warehouse.
     * @param isABC               Whether the warehouse uses ABC classification.
     * @param maxProductsPerOrder The maximum number of products per order.
     * @param minProductsPerOrder The minimum number of products per order.
     * @param isCapacityByUnits   Whether the capacity is measured by units.
     * @return A generated warehouse instance.
     * @throws Exception If the configuration is invalid.
     */
    public Warehouse generateWarehouse(int warehouseId, int numberOfOrders, int numberOfAisles, boolean isDepotCenter, int orderLocation, double shelfLength, double shelfWidth, double aisleWidth, double workerCapacity, double pickingTime, double outsideTurnTime, double insideTurnTime, int numberOfSlots, boolean isABC, int maxProductsPerOrder, int minProductsPerOrder, boolean isCapacityByUnits) throws Exception {
        List<Aisles> aislesList = new ArrayList<>();

        int depotPosition = (isDepotCenter ? DEPOT_CENTER : DEPOT_CORNER);

        for (int i = 0; i < numberOfAisles; i++) {
            int side = getDepotSidePosition(numberOfAisles, depotPosition, i);
            double aisleStart = ((shelfWidth + aisleWidth) / 2) + ((shelfWidth + aisleWidth) * i);
            double aisleEnd = ((shelfWidth + aisleWidth) * numberOfAisles);
            Aisles aisle = new Aisles(i, aisleEnd - aisleStart, aisleStart, side);
            aislesList.add(aisle);
        }

        List<Order> orderList = new ArrayList<>();
        int totalItems = 0;

        Warehouse warehouse = new Warehouse(numberOfOrders, numberOfAisles, totalItems, depotPosition, (isABC ? 1 : 0), shelfLength, shelfWidth, aisleWidth, workerCapacity, pickingTime, outsideTurnTime, insideTurnTime, numberOfSlots, aislesList, orderList);

        for (int i = 0; i < warehouse.getNumberOfOrders(); i++) {
            Order order = generateOrder(warehouse, isABC, maxProductsPerOrder, minProductsPerOrder, isCapacityByUnits);
            orderList.add(order);
        }

        totalItems = Utils.numberProducts(orderList);
        warehouse.setNumberOfItems(totalItems);

        if (isABC) {
            warehouse.setInstanceName("N_W" + warehouseId + "_" + numberOfOrders + "_ABC_" + workerCapacity);
        } else {
            warehouse.setInstanceName("N_W" + warehouseId + "_" + numberOfOrders + "_RND_" + workerCapacity);
        }

        return warehouse;
    }

    /**
     * Generates a new warehouse instance based on an existing warehouse configuration.
     *
     * @param warehouse           The existing warehouse configuration.
     * @param isABC               Whether the warehouse uses ABC classification.
     * @param maxProductsPerOrder The maximum number of products per order.
     * @param minProductsPerOrder The minimum number of products per order.
     * @param isCapacityByUnits   Whether the capacity is measured by units.
     * @return A new warehouse instance with updated orders.
     * @throws Exception If the configuration is invalid.
     */
    public Warehouse generateWarehouse(Warehouse warehouse, boolean isABC, int maxProductsPerOrder, int minProductsPerOrder, boolean isCapacityByUnits) throws Exception {
        List<Order> orderList = warehouse.getOrders();
        int numberOfOrders = warehouse.getNumberOfOrders();
        orderList.clear();

        for (int i = 0; i < numberOfOrders; i++) {
            orderList.add(generateOrder(warehouse, isABC, maxProductsPerOrder, minProductsPerOrder, isCapacityByUnits));
        }

        int totalItems = Utils.numberProducts(orderList);
        warehouse.setNumberOfItems(totalItems);

        return warehouse;
    }

    /**
     * Generates an order with the specified configuration.
     *
     * @param warehouse           The warehouse configuration.
     * @param isABC               Whether the warehouse uses ABC classification.
     * @param maxProductsPerOrder The maximum number of products per order.
     * @param minProductsPerOrder The minimum number of products per order.
     * @param isCapacityByUnits   Whether the capacity is measured by units.
     * @return A generated order instance.
     * @throws Exception If the configuration is invalid.
     */
    private Order generateOrder(Warehouse warehouse, boolean isABC, int maxProductsPerOrder, int minProductsPerOrder, boolean isCapacityByUnits) throws Exception {
        if (isCapacityByUnits && warehouse.getWorkerCapacity() < maxProductsPerOrder) {
            throw new Exception("Error[generateOrder] - When capacity is by units, the maximum number of products cannot exceed the worker's capacity.");
        }

        long dueDate = random.nextLong(60000, 3600000);
        int numberOfReferences = random.nextInt(minProductsPerOrder, maxProductsPerOrder + 1);

        Order order = new Order(orderId, dueDate, numberOfReferences);

        if (isCapacityByUnits) {
            for (int i = 0; i < numberOfReferences; i++) {
                order.addProduct(generateProduct(warehouse, isABC, 1.0));
            }
        } else {
            for (int i = 0; i < numberOfReferences; i++) {
                order.addProduct(generateProduct(warehouse, isABC, warehouse.getWorkerCapacity() / numberOfReferences));
            }
        }

        orderId++;
        return order;
    }

    /**
     * Generates a product with the specified configuration.
     *
     * @param warehouse The warehouse configuration.
     * @param isABC     Whether the warehouse uses ABC classification.
     * @param maxWeight The maximum weight of the product.
     * @return A generated product instance.
     * @throws Exception If the configuration is invalid.
     */
    private Product generateProduct(Warehouse warehouse, boolean isABC, Double maxWeight) throws Exception {
        int productsPerShelf = warehouse.getNumberOfSlots() / warehouse.getNumberOfAisles() / 2;
        double distanceBetweenProducts = warehouse.getShelfLength() / productsPerShelf;
        double firstProductPosition = distanceBetweenProducts / 2;

        int aisle;
        double height;
        int side = (random.nextBoolean() ? 0 : 1);
        double weight;

        if (maxWeight != 1.0) {
            weight = random.nextDouble(0.5, maxWeight);
        } else {
            weight = 1.0;
        }

        if (isABC) {
            double maxDistance = warehouse.getShelfLength() - firstProductPosition;

            if (warehouse.getDepotPlacement() == DEPOT_CORNER) {
                aisle = geometricDistributionGenerator(calculateProbability(warehouse.getNumberOfAisles()));
                aisle = ((aisle >= warehouse.getNumberOfAisles()) ? warehouse.getNumberOfAisles() - 1 : aisle);
            } else {
                int halfAisles = warehouse.getNumberOfAisles() / 2;
                if (warehouse.getNumberOfAisles() % 2 == 0) {
                    halfAisles--;
                }

                aisle = geometricDistributionGenerator(calculateProbability(halfAisles));
                boolean isRightSide = random.nextBoolean();

                if (isRightSide) {
                    aisle += halfAisles;
                } else {
                    aisle = halfAisles - aisle;
                }

                aisle = Math.abs(aisle % warehouse.getNumberOfAisles());
            }

            height = (((geometricDistributionGenerator(calculateProbability(productsPerShelf))) * distanceBetweenProducts) + firstProductPosition);
            height = (Math.min(height, maxDistance));
        } else {
            aisle = random.nextInt(0, warehouse.getNumberOfAisles());
            height = (random.nextInt(0, productsPerShelf) * distanceBetweenProducts) + firstProductPosition;
        }

        Product product = new Product(productId, aisle, side, height, weight);
        productId++;
        return product;
    }

    /**
     * Generates a value using a geometric distribution.
     *
     * @param probability The probability parameter for the distribution.
     * @return A generated value based on the geometric distribution.
     * @throws Exception If the probability is invalid.
     */
    private int geometricDistributionGenerator(double probability) throws Exception {
        int value = 0;

        if (probability > 1) {
            throw new Exception("Error[geometricDistributionGenerator] - The parameter p must be between [0-1].");
        }

        while (random.nextDouble(0, 1) < probability) {
            value++;
        }

        return value;
    }

    /**
     * Calculates the probability parameter for the geometric distribution.
     *
     * @param n The number of trials.
     * @return The calculated probability.
     */
    private Double calculateProbability(int n) {
        double probability = 0.01;

        while (Math.pow(probability, n) < 0.01) {
            probability += 0.01;
        }

        return probability;
    }
}