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

import static com.instancesobp.utils.Constants.*;

/**
 * Represents an aisle in a warehouse, including its unique identifier,
 * distances from the origin on both sides, and the side it belongs to.
 * This class is used to model the layout of aisles in the warehouse.
 * Implements {@link Serializable} to allow serialization of aisle objects.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class Aisles implements Serializable {

    /**
     * The unique identifier of the aisle.
     */
    private final int aisleId;

    /**
     * The distance from the origin to the right side of the aisle.
     */
    private final double distanceFromOriginRight;

    /**
     * The distance from the origin to the left side of the aisle.
     */
    private final double distanceFromOriginLeft;

    /**
     * The side of the aisle from depot (e.g., left = -1 ; center = 0 ; right = 1).
     */
    private final int side;

    /**
     * Constructs an Aisles object with the specified parameters.
     *
     * @param aisleId                 The unique identifier of the aisle.
     * @param distanceFromOriginRight The distance from the origin to the right side of the aisle.
     * @param distanceFromOriginLeft  The distance from the origin to the left side of the aisle.
     * @param side                    The side of the aisle from depot (e.g., left = -1 ; center = 0 ; right = 1).
     */
    public Aisles(int aisleId, double distanceFromOriginRight, double distanceFromOriginLeft, int side) {
        this.aisleId = aisleId;
        this.distanceFromOriginRight = distanceFromOriginRight;
        this.distanceFromOriginLeft = distanceFromOriginLeft;
        this.side = side;
    }

    /**
     * Gets the unique identifier of the aisle.
     *
     * @return The unique identifier of the aisle.
     */
    public int getAisleId() {
        return aisleId;
    }

    /**
     * Gets the distance from the origin to the right side of the aisle.
     *
     * @return The distance from the origin to the right side of the aisle.
     */
    public double getDistanceFromOriginRight() {
        return distanceFromOriginRight;
    }

    /**
     * Gets the distance from the origin to the left side of the aisle.
     *
     * @return The distance from the origin to the left side of the aisle.
     */
    public double getDistanceFromOriginLeft() {
        return distanceFromOriginLeft;
    }

    /**
     * Gets the side of the aisle (e.g., left = -1 ; center = 0 ; right = 1).
     *
     * @return The side of the aisle from depot.
     */
    public int getSide() {
        return side;
    }

    /**
     * Returns a string representation of the aisle object, including its
     * identifier, distances from the origin, and side.
     *
     * @return A string representation of the aisle object.
     */
    @Override
    public String toString() {
        return "Aisle ID: " +
                this.aisleId +
                "\n" +
                "Distance from origin (right): " +
                this.distanceFromOriginRight +
                "\n" +
                "Distance from origin (left): " +
                this.distanceFromOriginLeft +
                "\n" +
                "Side: " +
                getDepotSideAisle(this.side) +
                "\n";
    }

    /**
     * Prints the string representation of the aisle object to the console.
     */
    public void toPrint() {
        System.out.println(this);
    }

    /**
     * Converts the integer value representing the depot's location on one side of the aisle
     * into a string representation.
     *
     * @param depotSideAisle The integer value representing the depot's location on one side of the aisle.
     * @return A string representation of the depot's location on one side of the aisle.
     */
    private static String getDepotSideAisle(int depotSideAisle) {
        return switch (depotSideAisle) {
            case LEFT_SIDE_DEPOT -> "LEFT_SIDE_OF_DEPOT";
            case FRONT_SIDE_DEPOT -> "FRONT_DEPOT";
            case RIGHT_SIDE_DEPOT -> "RIGHT_SIDE_OF_DEPOT";
            default -> "Unknown";
        };
    }
}