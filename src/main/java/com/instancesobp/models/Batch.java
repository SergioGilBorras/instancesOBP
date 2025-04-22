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
package com.instancesobp.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a batch of orders in a warehouse system.
 * A batch is a collection of orders that are processed together. This class
 * provides methods to manage orders within the batch, calculate weights, and
 * handle constraints such as maximum weight limits.
 * Implements the {@code Cloneable} interface to allow deep cloning of batches.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class Batch implements Cloneable {

    /**
     * The current weight of the batch.
     */
    private double weight = 0;

    /**
     * The maximum allowable weight for the batch.
     */
    private double maxWeight = 0;

    /**
     * The list of orders in the batch.
     */
    private List<Order> orders;

    /**
     * The service time required to process the batch.
     */
    private double serviceTime = 0;

    /**
     * The earliest arrival time among all orders in the batch.
     */
    private long earliestArrivalTime = Long.MAX_VALUE;

    /**
     * The completion time of the batch.
     */
    private double completionTime = 0;

    /**
     * Constructs a new {@code Batch} with the specified maximum weight.
     *
     * @param maxWeight The maximum allowable weight for the batch.
     */
    public Batch(double maxWeight) {
        this.maxWeight = maxWeight;
        this.orders = new ArrayList<>();
        this.earliestArrivalTime = System.currentTimeMillis();
    }

    /**
     * Constructs a new {@code Batch} by copying the attributes of another batch.
     *
     * @param batch The batch to copy.
     */
    public Batch(Batch batch) {
        this.maxWeight = batch.getMaxWeight();
        this.weight = batch.getWeight();
        this.serviceTime = batch.serviceTime;
        this.earliestArrivalTime = batch.earliestArrivalTime;
        this.orders = new ArrayList<>(batch.orders);
        this.completionTime = batch.completionTime;
    }

    /**
     * Creates and returns a deep copy of this batch.
     *
     * @return A clone of this batch.
     * @throws CloneNotSupportedException If the cloning operation is not supported.
     */
    @Override
    public Batch clone() throws CloneNotSupportedException {
        Batch clone = (Batch) super.clone();
        clone.maxWeight = this.maxWeight;
        clone.weight = this.getWeight();
        clone.serviceTime = this.serviceTime;
        clone.earliestArrivalTime = this.earliestArrivalTime;
        clone.orders = new ArrayList<>();
        for (Order order : this.orders) {
            clone.orders.add(order.clone());
        }
        return clone;
    }

    /**
     * Compares this batch with another batch for equality.
     *
     * @param batch The batch to compare with.
     * @return {@code true} if the batches are equal, {@code false} otherwise.
     */
    public boolean equals(Batch batch) {

        if (weight != batch.getWeight()) {
            return false;
        }
        if (maxWeight != batch.maxWeight) {
            return false;
        }
        return orders.equals(batch.orders);

    }

    /**
     * Adds an order to the batch if the weight does not exceed the maximum weight.
     *
     * @param order The order to add.
     * @throws Exception If the weight exceeds the maximum weight.
     */
    public void addOrder(Order order) throws Exception {
        if ((this.getWeight() + order.getWeight()) <= this.maxWeight) {
            this.weight += order.getWeight();
            orders.add(order);
            if (this.earliestArrivalTime > order.getArrivalTime()) {
                this.earliestArrivalTime = order.getArrivalTime();
            }
        } else {
            throw new Exception("Maximum weight limit exceeded for the batch. [Batch.addOrder].");
        }

        this.serviceTime = 0;
    }

    /**
     * Removes an order from the batch by its index.
     *
     * @param index The index of the order to remove.
     * @throws Exception If the order cannot be removed.
     */
    public void removeOrder(int index) throws Exception {
        Order order = this.orders.get(index);
        this.weight -= order.getWeight();
        if (orders.remove(index) == null) {
            throw new Exception("The order (index: " + index + ") can't be removed. [Batch.removeOrder].");
        }
        if (this.earliestArrivalTime == order.getArrivalTime()) {
            recalculateMinimumArrivalTime();
        }
        this.serviceTime = 0.0;
    }

    /**
     * Removes an order from the batch.
     *
     * @param order The order to remove of the batch.
     * @throws Exception If the order cannot be removed.
     */
    public void removeOrder(Order order) throws Exception {

        for (Order order1 : orders) {
            if (order1.equals(order)) {
                this.weight -= order.getWeight();
                if (!orders.remove(order)) {
                    throw new Exception("The order (id: " + order.getId() + ") can't be removed. [Batch.removeOrder]");
                }
                if (this.earliestArrivalTime == order.getArrivalTime()) {
                    recalculateMinimumArrivalTime();
                }
                break;
            }
        }
        this.serviceTime = 0.0;
    }

    /**
     * Recalculate the minimum arrival time among all orders in the batch.
     *
     */
    public void recalculateMinimumArrivalTime() {
        long minimumArrivalTime = Long.MAX_VALUE;
        for (Order order : orders) {
            if (minimumArrivalTime > order.getArrivalTime()) {
                minimumArrivalTime = order.getArrivalTime();
            }
        }
        earliestArrivalTime = minimumArrivalTime;
    }

    /**
     * Returns the maximum arrival time among all orders in the batch.
     *
     * @return The maximum arrival time.
     */
    public long getMaximumArrivalTime() {
        long maximumArrivalTime = 0;
        for (Order order : orders) {
            if (maximumArrivalTime < order.getArrivalTime()) {
                maximumArrivalTime = order.getArrivalTime();
            }
        }
        return maximumArrivalTime;
    }

    /**
     * Checks if the batch contains a specific order.
     *
     * @param order The order to check.
     * @return {@code true} if the batch contains the order, {@code false} otherwise.
     */
    public boolean containsOrder(Order order) {
        for (Order currentOrder : orders) {
            if (currentOrder.equals(order)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of unique aisle numbers that contains products in the batch.
     *
     * @return A list of aisle numbers.
     */
    public List<Integer> getAisleList() {
        List<Integer> aisleList = new ArrayList<>();
        for (Order order : this.orders) {
            for (Product product : order.getProducts()) {
                if (!aisleList.contains(product.getAisle())) {
                    aisleList.add(product.getAisle());
                }
            }
        }
        return aisleList;
    }


    /**
     * Returns a string representation of the batch, including all its attributes.
     *
     * @return A string representation of the batch.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Weight: ");
        sb.append(this.getWeight());
        sb.append("\n\r");
        sb.append("Max weight: ");
        sb.append(this.maxWeight);
        sb.append("\n\r");
        sb.append("Number of orders: ");
        sb.append(this.orders.size());
        sb.append("\n\r");
        for (Order pedido : orders) {
            sb.append(pedido.toString_short());
        }
        return sb.toString();
    }

    /**
     * Returns a string short representation of the batch, only including the ID of its Orders.
     *
     * @return A string short representation of the batch.
     */
    public String toString_short() {
        StringBuilder sb = new StringBuilder();
        sb.append("Number of orders: ");
        sb.append(this.orders.size());
        sb.append(" [");
        for (Order order : orders) {
            sb.append(order.getId()).append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Prints the string representation of the batch to the console.
     */
    public void toPrint() {
        System.out.println(this);
    }

    /**
     * Prints the string short representation of the batch to the console.
     */
    public void toPrintShort() {
        System.out.println(this.toString_short());
    }

    /**
     * Returns the maximum allowable weight for the batch.
     *
     * @return The maximum weight.
     */
    public double getMaxWeight() {
        return maxWeight;
    }

    /**
     * Returns the list of orders in the batch.
     *
     * @return The list of orders.
     */
    public List<Order> getOrders() {
        return orders;
    }

    /**
     * Returns the service time required to process the batch.
     *
     * @return The service time.
     */
    public double getServiceTime() {
        return serviceTime;
    }

    /**
     * Sets the service time required to process the batch.
     *
     * @param serviceTime The service time.
     */
    public void setServiceTime(double serviceTime) {
        this.serviceTime = serviceTime;
    }

    /**
     * Returns the earliest arrival time among all orders in the batch.
     *
     * @return The earliest arrival time.
     */
    public long getEarliestArrivalTime() {
        return earliestArrivalTime;
    }

    /**
     * Returns the available weight of the batch.
     *
     * @return The available weight.
     */
    public double getAvailableWeight() {
        return this.maxWeight - this.getWeight();
    }

    /**
     * Returns the current weight of the batch.
     *
     * @return The current weight.
     */
    public double getWeight() {
        return this.weight;
    }

    /**
     * Returns the completion time of the batch.
     *
     * @return The completion time.
     */
    public double getCompletionTime() {
        return completionTime;
    }

    /**
     * Sets the completion time of the batch.
     *
     * @param completionTime The completion time.
     */
    public void setCompletionTime(double completionTime) {
        this.completionTime = completionTime;
    }
}
