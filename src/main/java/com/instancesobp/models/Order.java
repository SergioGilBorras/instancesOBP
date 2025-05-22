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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an order in the warehouse, containing a list of products,
 * due date, weight, and other attributes. This class models the properties
 * and behavior of an order and implements {@code Cloneable} and {@code Serializable}.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
@JsonPropertyOrder({ "id", "dueDate", "arrivalTime", "serviceTime", "numReferences", "weight", "completionTime", "products" })
public class Order implements Cloneable, Serializable {

    /**
     * Unique identifier for the order.
     */
    private int id = -1;

    /**
     * List of products in the order.
     */
    private List<Product> products;

    /**
     * Number of product references in the order.
     */
    private int numReferences;

    /**
     * Total weight of the order.
     */
    private double weight = 0.0;

    /**
     * Due date of the order.
     */
    private long dueDate;

    /**
     * Arrival time of the order.
     */
    private long arrivalTime = 0;

    /**
     * Service time required to process the order.
     */
    private double serviceTime = 0;

    /**
     * Completion time of the order.
     */
    private double completionTime = 0;

    /**
     * Constructs a new {@code Order} with the specified attributes.
     *
     * @param id            The unique identifier for the order.
     * @param dueDate       The due date of the order.
     * @param numReferences The number of product references in the order.
     */
    public Order(int id, long dueDate, int numReferences) {
        this.id = id;
        this.dueDate = dueDate;
        this.numReferences = numReferences;
        this.products = new ArrayList<>();
        this.arrivalTime = System.currentTimeMillis();
    }

    /**
     * Constructs a new {@code Order} by copying the attributes of another order.
     *
     * @param order The order to copy.
     */
    public Order(Order order) {
        this.id = order.id;
        this.dueDate = order.dueDate;
        this.numReferences = order.numReferences;
        this.products = new ArrayList<>(order.products);
        this.weight = order.weight;
        this.arrivalTime = order.arrivalTime;
        this.serviceTime = 0.0;
        this.completionTime = 0.0;
    }

    /**
     *
     */
    public Order(int id, List<Product> products, int numReferences, double weight, long dueDate, long arrivalTime,
                 double serviceTime, double completionTime) {
        super();
        this.id = id;
        this.products = products;
        this.numReferences = numReferences;
        this.weight = weight;
        this.dueDate = dueDate;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.completionTime = completionTime;
    }


    public Order() {
    }

    /**
     * Adds a product to the order. Throws an exception if the maximum number
     * of product references is exceeded.
     *
     * @param product The product to add.
     * @throws Exception If the maximum number of product references is exceeded.
     */
    public void addProduct(Product product) throws Exception {
        weight += product.getWeight();
        if (products.size() < numReferences) {
            products.add(product);
        } else {
            throw new Exception("Limit exceeded. All products are already loaded.");
        }
    }

    /**
     * Creates and returns a copy of this order.
     *
     * @return A clone of this order.
     * @throws CloneNotSupportedException If the cloning operation is not supported.
     */
    @Override
    public Order clone() throws CloneNotSupportedException {
        Order clone = (Order) super.clone();
        clone.id = this.id;
        clone.dueDate = this.dueDate;
        clone.numReferences = this.numReferences;
        clone.weight = this.weight;
        clone.arrivalTime = this.arrivalTime;
        clone.products = new ArrayList<>();
        for (Product product : this.products) {
            clone.products.add(product.clone());
        }
        return clone;
    }

    /**
     * Generates a hash code for this order based on its attributes.
     *
     * @return The hash code of this order.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.id;
        hash = 97 * hash + Objects.hashCode(this.products);
        hash = 97 * hash + this.numReferences;
        hash = 97 * hash + Long.hashCode(Double.doubleToLongBits(this.weight));
        hash = 97 * hash + Long.hashCode(this.dueDate);
        hash = 97 * hash + Long.hashCode(this.arrivalTime);
        hash = 97 * hash + Long.hashCode(Double.doubleToLongBits(this.serviceTime));
        return hash;
    }

    /**
     * Compares this order with another object for equality.
     *
     * @param order The object to compare with.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    public boolean equals(Order order) {
        if (this == order) {
            return true;
        }
        if (order == null || getClass() != order.getClass()) {
            return false;
        }
        return this.id == order.id &&
                this.numReferences == order.numReferences &&
                Double.doubleToLongBits(this.weight) == Double.doubleToLongBits(order.weight) &&
                this.dueDate == order.dueDate &&
                this.arrivalTime == order.arrivalTime &&
                Double.doubleToLongBits(this.serviceTime) == Double.doubleToLongBits(order.serviceTime) &&
                this.products.equals(order.products);
    }

    /**
     * Returns a string representation of the order, including all its attributes.
     *
     * @return A string representation of the order.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(this.id).append("\n");
        sb.append("Due Date: ").append(this.dueDate).append("\n");
        sb.append("Number of References: ").append(this.numReferences).append("\n");
        sb.append("Total Weight: ").append(this.weight).append("\n");
        sb.append("** Product Details: **\n");
        for (Product product : products) {
            sb.append(product.toString()).append(" ******* \n");
        }
        return sb.toString();
    }


    /**
     * Returns a string short representation of the order, only including its ID
     * and the ID of its Products.
     *
     * @return A string short representation of the order.
     */
    public String toString_short() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(this.id).append("\n [");
        for (Product product : products) {
            sb.append(product.getId()).append(", ");
        }
        sb.append("]\n");
        return sb.toString();
    }

    /**
     * Prints the string representation of the order to the console.
     */
    public void print() {
        System.out.println(this);
    }

    /**
     * Returns the due date of the order.
     *
     * @return The due date.
     */
    public Long getDueDate() {
        return dueDate;
    }

    /**
     * Returns the number of product references in the order.
     *
     * @return The number of references.
     */
    public int getNumReferences() {
        return numReferences;
    }

    /**
     * Returns the arrival time of the order.
     *
     * @return The arrival time.
     */
    public Long getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Sets the arrival time of the order.
     *
     * @param arrivalTime The arrival time.
     */
    public void setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    /**
     * Returns the total weight of the order.
     *
     * @return The total weight.
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * Returns the completion time of the order.
     *
     * @return The completion time.
     */
    public Double getCompletionTime() {
        return completionTime;
    }

    /**
     * Sets the completion time of the order.
     *
     * @param completionTime The completion time.
     */
    public void setCompletionTime(double completionTime) {
        this.completionTime = completionTime;
    }

    /**
     * Returns the service time required to process the order.
     *
     * @return The service time.
     */
    public Double getServiceTime() {
        return serviceTime;
    }

    /**
     * Sets the service time required to process the order.
     *
     * @param serviceTime The service time.
     */
    public void setServiceTime(double serviceTime) {
        this.serviceTime = serviceTime;
    }

    /**
     * Returns the unique identifier of the order.
     *
     * @return The order ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the list of products in the order.
     *
     * @return The list of products.
     */
    public List<Product> getProducts() {
        return products;
    }
}