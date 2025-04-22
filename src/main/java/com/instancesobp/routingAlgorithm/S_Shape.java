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
package com.instancesobp.routingAlgorithm;

import java.util.HashMap;

import com.instancesobp.models.Batch;
import com.instancesobp.models.Order;
import com.instancesobp.models.Product;
import com.instancesobp.models.Warehouse;

import static com.instancesobp.utils.Constants.DEPOT_CORNER;

/**
 * Implements the S-Shape routing algorithm for order picking in a warehouse.
 * This algorithm calculates the total distance traveled by a picker to collect
 * all items in a batch, considering the warehouse layout and item locations.
 * The S-Shape algorithm traverses each aisle completely, turning at the end of
 * the aisle to move to the next one, and returns to the depot after completing
 * all aisles.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class S_Shape extends RoutingAlgorithm {


    /**
     * Constructor for the S_Shape class.
     *
     * @param warehouse The warehouse object containing the layout and configuration details.
     */
    public S_Shape(Warehouse warehouse) {
        super(warehouse);
    }

    /**
     * Executes the S-Shape routing algorithm for a given batch of orders.
     * This method calculates the total distance traveled by the picker to collect
     * all items in the batch. It considers the warehouse layout, including aisle
     * dimensions, shelf dimensions, and the location of the depot.
     *
     * @param batch The batch of orders to be processed.
     * @return The total distance traveled by the picker to collect all items in the batch.
     */
    @Override
    public double run(Batch batch) {
        if (batch.getOrders().isEmpty()) {
            return 0; // If the batch is empty, no distance is traveled.
        }

        double turnTime = 0.0; // Time taken for turns (not used in this implementation).
        double totalDistance; // Total distance traveled.
        int maxAisle = 0; // Maximum aisle number with items.
        int minAisle = Integer.MAX_VALUE; // Minimum aisle number with items.
        HashMap<Integer, Double> aisleMaxHeights = new HashMap<>(); // Stores the maximum height of items in each aisle.
        int totalItems = 0; // Total number of items in the batch.

        // Precompute distances and item locations in a hash table.
        for (Order order : batch.getOrders()) {
            for (Product product : order.getProducts()) {
                totalItems++;
                aisleMaxHeights.merge(product.getAisle(), product.getHeightPosition(), Math::max);
                maxAisle = Math.max(maxAisle, product.getAisle());
                minAisle = Math.min(minAisle, product.getAisle());
            }
        }

        // Calculate the distance in the main aisles.
        int aisleCount = aisleMaxHeights.size();
        if (aisleCount % 2 == 0) {
            totalDistance = (wh.getAisleWidth() + wh.getShelfLength()) * aisleCount;
        } else {
            totalDistance = (wh.getAisleWidth() + wh.getShelfLength()) * (aisleCount - 1)
                    + wh.getAisleWidth()
                    + (aisleMaxHeights.get(maxAisle) * 2);
        }

        // Calculate the distance in the transversal aisles.
        if (wh.getDepotPlacement() == DEPOT_CORNER) {
            totalDistance += (wh.getAisleWidth() + wh.getShelfWidth()) * maxAisle * 2;
        } else {
            double mid = (wh.getNumberOfAisles() - 1) / 2.0;
            if (mid < minAisle) {
                totalDistance += (2 * (wh.getAisleWidth() + wh.getShelfWidth())) * (maxAisle - mid);
            } else if (mid > maxAisle) {
                totalDistance += (2 * (wh.getAisleWidth() + wh.getShelfWidth())) * (mid - minAisle);
            } else {
                totalDistance += (2 * (wh.getAisleWidth() + wh.getShelfWidth())) * (maxAisle - minAisle);
            }
        }

        // Calculate turn time.
        //turnTime = calculateTurnTime(aisleMaxHeights, minAisle, maxAisle);

        // Return the total distance, including travel speed, depot time, and picking time.
        return (totalDistance * wh.getTravelSpeed())
                + wh.getDepotTime()
                + (wh.getPickingTime() * totalItems)
                + turnTime;
    }

    /**
     * Calculates the total turn time required for navigating the aisles in the warehouse.
     * The calculation depends on the depot location (corner or central) and the number
     * of aisles with products.
     *
     * @param aisleMaxHeights A HashMap containing the aisles and the maximum height of products in each aisle.
     * @param minAisle        The closest aisle to the depot with products.
     * @param maxAisle        The farthest aisle from the depot with products.
     * @return The total turn time required for navigating the aisles.
     */
    private double calculateTurnTime(HashMap<Integer, Double> aisleMaxHeights, int minAisle, int maxAisle) {
        double turnTime;
        int aisleCount = aisleMaxHeights.size();

        if (wh.getDepotPlacement() == DEPOT_CORNER) {
            // Depot in a corner
            if (aisleCount == 1) {
                turnTime = wh.getInsideTurnTime();
                if (minAisle != 0) {
                    turnTime += 2 * wh.getOutsideTurnTime();
                }
            } else {
                turnTime = (aisleCount * 2) * wh.getOutsideTurnTime();
                if (minAisle == 0) {
                    turnTime -= wh.getOutsideTurnTime();
                }
                if (aisleCount % 2 != 0) {
                    turnTime += wh.getInsideTurnTime();
                }
            }
        } else {
            // Depot in the center
            if (minAisle == (aisleCount / 2) + 1 && maxAisle == (aisleCount / 2) + 1) {
                turnTime = wh.getInsideTurnTime();
            } else {
                turnTime = (aisleCount * 2) * wh.getOutsideTurnTime();
                if (aisleCount % 2 != 0) {
                    turnTime += wh.getInsideTurnTime();
                    if (minAisle == (aisleCount / 2) + 1 || maxAisle == (aisleCount / 2) + 1) {
                        turnTime -= wh.getOutsideTurnTime();
                    }
                }
            }
        }
        return turnTime;
    }
}
