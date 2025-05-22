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

import java.io.Serializable;

import static com.instancesobp.utils.Constants.LEFT_SIDE_AISLE;
import static com.instancesobp.utils.Constants.RIGHT_SIDE_AISLE;

/**
 * Represents a product in the warehouse, including its location, weight, and other attributes.
 * This class is used to model the properties of a product and its placement in the warehouse.
 * It implements {@code Cloneable} and {@code Serializable} for cloning and serialization purposes.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class Product implements Cloneable, Serializable {

    /**
     * Unique identifier for the product.
     */
    private int id;

    /**
     * Aisle where the product is located.
     */
    private int aisle;

    /**
     * Side of the aisle where the product is located (0 for LEFT_SIDE_AISLE, 1 for RIGHT_SIDE_AISLE).
     */
    private int side;

    /**
     * Height of the product's location in the aisle.
     */
    private double heightPosition;

    /**
     * Weight of the product.
     */
    private double weight;

    /**
     * Constructs a new {@code Product} with the specified attributes.
     *
     * @param id             The unique identifier for the product.
     * @param aisle          The aisle where the product is located.
     * @param side           The side of the aisle where the product is located (0 for LEFT_SIDE_AISLE, 1 for RIGHT_SIDE_AISLE).
     * @param heightPosition The height of the product's location in the aisle.
     * @param weight         The weight of the product.
     */
    public Product(int id, int aisle, int side, double heightPosition, double weight) {
        this.id = id;
        this.aisle = aisle;
        this.side = side;
        this.heightPosition = heightPosition;
        this.weight = weight;
    }

    /**
     * Constructs a new {@code Product} by copying the attributes of another product.
     *
     * @param product The product to copy.
     */
    public Product(Product product) {
        this.id = product.id;
        this.aisle = product.aisle;
        this.side = product.side;
        this.heightPosition = product.heightPosition;
        this.weight = product.weight;
    }

    public Product() {

    }

    /**
     * Converts the item side aisle location integer value to a string representation.
     *
     * @param itemSideAisle The item side aisle location value.
     * @return A string representation of the order location.
     */
    private static String getItemSideAisleString(int itemSideAisle) {
        return switch (itemSideAisle) {
            case LEFT_SIDE_AISLE -> "LEFT_SIDE_AISLE";
            case RIGHT_SIDE_AISLE -> "RIGHT_SIDE_AISLE";
            default -> "Unknown";
        };
    }

    /**
     * Compares this product with another product for equality.
     *
     * @param other The product to compare with.
     * @return {@code true} if the products are equal, {@code false} otherwise.
     */
    public boolean equals(Product other) {
        if (this.id != other.id) {
            return false;
        }
        if (this.aisle != other.getAisle()) {
            return false;
        }
        if (this.side != other.getSide()) {
            return false;
        }
        if (this.heightPosition != other.getHeightPosition()) {
            return false;
        }
        return this.weight == other.getWeight();
    }

    /**
     * Creates and returns a copy of this product.
     *
     * @return A clone of this product.
     * @throws CloneNotSupportedException If the cloning operation is not supported.
     */
    @Override
    public Product clone() throws CloneNotSupportedException {
        Product clone = (Product) super.clone();
        clone.id = this.id;
        clone.aisle = this.aisle;
        clone.side = this.side;
        clone.heightPosition = this.heightPosition;
        clone.weight = this.weight;
        return clone;
    }

    /**
     * Returns a string representation of the product, including all its attributes.
     *
     * @return A string representation of the product.
     */
    @Override
    public String toString() {
        return "Product ID: " + this.id + "\n" +
                "Aisle: " + this.aisle + "\n" +
                "Side: " + getItemSideAisleString(this.side) + "\n" +
                "Height: " + this.heightPosition + "\n" +
                "Weight: " + this.weight + "\n";
    }

    /**
     * Prints the string representation of the product to the console.
     */
    public void toPrint() {
        System.out.println(this);
    }

    /**
     * Returns the unique identifier of the product.
     *
     * @return The product ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the aisle where the product is located.
     *
     * @return The aisle number.
     */
    public int getAisle() {
        return aisle;
    }

    /**
     * Returns the side of the aisle where the product is located.
     *
     * @return The side of the aisle (0 for left, 1 for right).
     */
    public int getSide() {
        return side;
    }

    /**
     * Returns the height of the product's location in the aisle.
     *
     * @return The height of the product.
     */
    public double getHeightPosition() {
        return heightPosition;
    }

    /**
     * Returns the weight of the product.
     *
     * @return The weight of the product.
     */
    public double getWeight() {
        return weight;
    }

    /**
     *
     * @param weight
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }
}