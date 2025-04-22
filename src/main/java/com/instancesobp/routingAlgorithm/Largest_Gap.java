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

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;

import com.instancesobp.models.Batch;
import com.instancesobp.models.Order;
import com.instancesobp.models.Product;
import com.instancesobp.models.Warehouse;

import static com.instancesobp.utils.Constants.DEPOT_CORNER;

/**
 * Implements the Largest Gap routing algorithm for order picking in a warehouse.
 * This algorithm calculates the total distance traveled by a picker to collect all items
 * in a batch, considering the largest gaps between items in each aisle.
 * The algorithm determines the optimal path through the aisles by analyzing the gaps
 * between items and adjusting the travel distance accordingly.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class Largest_Gap extends RoutingAlgorithm {

    // Constants representing the location of the largest gap in an aisle.
    private static final int GAP_BEGINNING = 0; // Gap at the beginning of the aisle.
    private static final int GAP_MIDDLE = 1;    // Gap in the middle of the aisle.
    private static final int GAP_END = 2;       // Gap at the end of the aisle.

    /**
     * Constructor for the Largest_Gap class.
     *
     * @param warehouse The warehouse object containing the layout and configuration details.
     */
    public Largest_Gap(Warehouse warehouse) {
        super(warehouse);
    }

    /**
     * Executes the Largest Gap routing algorithm for a given batch of orders.
     * This method calculates the total distance traveled by the picker to collect
     * all items in the batch, considering the largest gaps in each aisle and the
     * warehouse layout.
     *
     * @param batch The batch of orders to be processed.
     * @return The total distance traveled by the picker to collect all items in the batch.
     */
    @Override
    public double run(Batch batch) {
        if (batch.getOrders().isEmpty()) {
            return 0; // If the batch is empty, no distance is traveled.
        }

        double totalDistance = 0;
        int maxAisle = 0;
        int minAisle = Integer.MAX_VALUE;
        double turnTime = 0.0;
        HashMap<Integer, List<Double>> aisleHeightsMap = new HashMap<>();
        int totalItems = 0;

        // Pre-calculate distances and populate the aisle heights map.
        for (Order order : batch.getOrders()) {
            for (Product product : order.getProducts()) {
                totalItems++;
                if (aisleHeightsMap.containsKey(product.getAisle())) {
                    List<Double> heights = aisleHeightsMap.get(product.getAisle());
                    if (!heights.contains(product.getHeightPosition())) {
                        heights.add(product.getHeightPosition());
                        aisleHeightsMap.replace(product.getAisle(), heights);
                    }
                } else {
                    List<Double> heights = new ArrayList<>();
                    heights.add(product.getHeightPosition());
                    heights.add(0.0); // Add the floor level.
                    heights.add(wh.getShelfLength()); // Add the top shelf level.

                    aisleHeightsMap.put(product.getAisle(), heights);
                    if (maxAisle < product.getAisle()) {
                        maxAisle = product.getAisle();
                    }
                    if (minAisle > product.getAisle()) {
                        minAisle = product.getAisle();
                    }
                }
            }
        }

        HashMap<Integer, Integer> aisleGapLocations = new HashMap<>();

        // Calculate distances for main aisles.
        if (aisleHeightsMap.size() > 2) {
            totalDistance = ((wh.getAisleWidth() + wh.getShelfLength()) * 2);
            for (HashMap.Entry<Integer, List<Double>> entry : aisleHeightsMap.entrySet()) {
                if (!entry.getKey().equals(minAisle) && !entry.getKey().equals(maxAisle)) {
                    List<Double> heights = entry.getValue();
                    Collections.sort(heights);
                    double maxGap = 0.0;
                    for (int i = 1; i < heights.size(); i++) {
                        double gap = heights.get(i) - heights.get(i - 1);
                        if (maxGap < gap) {
                            if (i == 1) {
                                aisleGapLocations.put(entry.getKey(), GAP_BEGINNING);
                            } else if (i == heights.size() - 1) {
                                aisleGapLocations.put(entry.getKey(), GAP_END);
                            } else {
                                aisleGapLocations.put(entry.getKey(), GAP_MIDDLE);
                            }
                            maxGap = gap;
                        }
                    }
                    totalDistance += wh.getAisleWidth() + ((wh.getShelfLength() - maxGap) * 2);
                    if (aisleGapLocations.get(entry.getKey()) == GAP_MIDDLE) {
                        totalDistance += wh.getAisleWidth();
                    }
                }
            }
        } else if (aisleHeightsMap.size() > 1) {
            totalDistance = ((wh.getAisleWidth() + wh.getShelfLength()) * 2);
        } else {
            for (List<Double> heights : aisleHeightsMap.values()) {
                Collections.sort(heights);
                totalDistance = wh.getAisleWidth() + ((heights.get(heights.size() - 2)) * 2);
            }
        }

        // Calculate distances for transversal aisles.
        if (wh.getDepotPlacement() == DEPOT_CORNER) {
            totalDistance += ((wh.getAisleWidth() + wh.getShelfWidth()) * (maxAisle) * 2);
        } else if (wh.getNumberOfAisles() % 2 == 0) {
            int centralAisle = wh.getNumberOfAisles() / 2;
            totalDistance += ((wh.getAisleWidth() + wh.getShelfWidth()) * (maxAisle - minAisle) * 2);
            if (centralAisle <= minAisle) {
                totalDistance += (((wh.getAisleWidth() + wh.getShelfWidth()) / 2) + ((wh.getAisleWidth() + wh.getShelfWidth()) * (minAisle - centralAisle))) * 2;
            } else if (centralAisle > maxAisle) {
                totalDistance += (((wh.getAisleWidth() + wh.getShelfWidth()) / 2) + ((wh.getAisleWidth() + wh.getShelfWidth()) * (centralAisle - maxAisle))) * 2;
            }
        } else {
            int centralAisle = wh.getNumberOfAisles() / 2;
            totalDistance += ((wh.getAisleWidth() + wh.getShelfWidth()) * (maxAisle - minAisle) * 2);
            if (centralAisle <= minAisle) {
                totalDistance += ((wh.getAisleWidth() + wh.getShelfWidth()) * (minAisle - centralAisle)) * 2;
            } else if (centralAisle > maxAisle) {
                totalDistance += ((wh.getAisleWidth() + wh.getShelfWidth()) * (centralAisle - maxAisle)) * 2;
            }
        }

        //turnTime = calculateTurnTime(aisleHeightsMap, minAisle, aisleGapLocations, maxAisle);

        return (totalDistance * wh.getTravelSpeed()) + wh.getDepotTime() + (wh.getPickingTime() * totalItems) + turnTime;
    }

    /**
     * Calculates the time spent turning in the warehouse based on the depot location
     * and the largest gaps in the aisles.
     *
     * @param aisleHeightsMap   A map of aisle numbers to their respective item heights.
     * @param minAisle          The minimum aisle number.
     * @param aisleGapLocations A map of aisle numbers to the location of their largest gap.
     * @param maxAisle          The maximum aisle number.
     * @return The total time spent turning in the warehouse.
     */
    private double calculateTurnTime(HashMap<Integer, List<Double>> aisleHeightsMap, int minAisle, HashMap<Integer, Integer> aisleGapLocations, int maxAisle) {
        double turnTime;
        if (wh.getDepotPlacement() == DEPOT_CORNER) {
            if (aisleHeightsMap.size() == 1) {
                turnTime = wh.getInsideTurnTime();
                if (minAisle != 0) {
                    turnTime += (2 * wh.getOutsideTurnTime());
                }
            } else {
                turnTime = (aisleHeightsMap.size() * 2 * wh.getOutsideTurnTime());
                turnTime += (aisleGapLocations.size() * 2 * wh.getInsideTurnTime());
                if (minAisle == 0) {
                    turnTime -= wh.getOutsideTurnTime();
                }
                int middleGapCount = 0;
                for (Integer gapLocation : aisleGapLocations.values()) {
                    if (gapLocation == GAP_MIDDLE) {
                        middleGapCount++;
                    }
                }
                turnTime += (middleGapCount * 2 * wh.getOutsideTurnTime());
                turnTime += (middleGapCount * wh.getInsideTurnTime());
            }
        } else {
            if ((minAisle == (aisleHeightsMap.size() / 2) + 1) && (maxAisle == (aisleHeightsMap.size() / 2) + 1)) {
                turnTime = wh.getInsideTurnTime();
            } else {
                turnTime = (aisleHeightsMap.size() * 2 * wh.getOutsideTurnTime());
                turnTime += (aisleGapLocations.size() * 2 * wh.getInsideTurnTime());
                if ((minAisle == (aisleHeightsMap.size() / 2) + 1) || (maxAisle == (aisleHeightsMap.size() / 2) + 1)) {
                    turnTime -= wh.getOutsideTurnTime();
                }
                int middleGapCount = 0;
                for (Integer gapLocation : aisleGapLocations.values()) {
                    if (gapLocation == GAP_MIDDLE) {
                        middleGapCount++;
                    }
                }
                turnTime += (middleGapCount * 2 * wh.getOutsideTurnTime());
                turnTime += (middleGapCount * wh.getInsideTurnTime());
            }
        }
        return turnTime;
    }
}
