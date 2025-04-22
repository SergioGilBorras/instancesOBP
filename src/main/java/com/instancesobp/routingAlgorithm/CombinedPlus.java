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
import java.util.Arrays;
import java.util.List;

import com.instancesobp.models.Batch;
import com.instancesobp.models.Order;
import com.instancesobp.models.Product;
import com.instancesobp.models.Warehouse;
import com.instancesobp.utils.Constants;

/**
 * Implements the CombinedPlus routing algorithm for order picking in a warehouse.
 * This algorithm is an improving of Combined. Calculates the total time required
 * to pick all items in a batch, considering the warehouse layout, item locations,
 * and routing strategies.
 * The algorithm uses a combination of strategies to minimize the total travel time
 * and optimize the picking process.
 * TODO: Add timeTurn to the algorithm.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class CombinedPlus extends RoutingAlgorithm {

    /**
     * Constructor for the CombinedPlus class.
     *
     * @param warehouse The warehouse object containing the layout and configuration details.
     */
    public CombinedPlus(Warehouse warehouse) {
        super(warehouse);
    }

    /**
     * Executes the CombinedPlus routing algorithm for a given batch of orders.
     * This method calculates the total time required to pick all items in the batch.
     *
     * @param batch The batch of orders to be processed.
     * @return The total time required to pick all items in the batch.
     */
    @Override
    public double run(Batch batch) {
        if (batch.getOrders().isEmpty()) {
            return 0;// If the batch is empty, no time is consumed.
        }
        ArrayList<Product> LP = new ArrayList<>();
        for (Order pedido : batch.getOrders()) {
            LP.addAll(pedido.getProducts());
        }

        return timeForTheseItems(LP);
    }

    /**
     * Additional distance to the shelf for picking items.
     */
    private final double extraToShelf = wh.getAisleWidth();
    /**
     * Total length of shelves in the warehouse.
     */
    private final double totalShelvesLong = wh.getShelfLength() + extraToShelf;

    /**
     * Calculates the total time required to pick a list of items.
     *
     * @param allItems The list of items to be picked.
     * @return The total time required to pick the items.
     */
    protected double timeForTheseItems(List<Product> allItems) {
        double timeConsumed = 0.0;

        boolean[] arrayOfAisles = new boolean[wh.getNumberOfAisles()];

        int firstAisle = Integer.MAX_VALUE;
        int lastAisle = Integer.MIN_VALUE;
        int numberOfAislesOccupied = 0;

        // Determine the range of aisles occupied by the items.
        for (Product it : allItems) {
            int aisle = it.getAisle();
            if (aisle < firstAisle) {
                firstAisle = aisle;
            }
            if (aisle > lastAisle) {
                lastAisle = aisle;
            }

            if (!arrayOfAisles[aisle]) {
                numberOfAislesOccupied++;
                arrayOfAisles[aisle] = true;
            }
        }

        // Calculate the distance in transversal aisles based on depot placement.
        if (wh.getDepotPlacement() == Constants.DEPOT_CORNER) {
            timeConsumed += ((wh.getAisleWidth() + wh.getShelfWidth()) * (lastAisle) * 2);
        } else if (wh.getNumberOfAisles() % 2 == 0) {
            int a = wh.getNumberOfAisles() / 2;
            timeConsumed += ((wh.getAisleWidth() + wh.getShelfWidth()) * (lastAisle - firstAisle) * 2);
            if (a <= firstAisle) {
                timeConsumed += (((wh.getAisleWidth() + wh.getShelfWidth()) / 2) + ((wh.getAisleWidth() + wh.getShelfWidth()) * (firstAisle - a))) * 2;
            } else if (a > lastAisle) {
                timeConsumed += (((wh.getAisleWidth() + wh.getShelfWidth()) / 2) + ((wh.getAisleWidth() + wh.getShelfWidth()) * (a - lastAisle))) * 2;
            }
        } else {
            int a = wh.getNumberOfAisles() / 2;
            timeConsumed += ((wh.getAisleWidth() + wh.getShelfWidth()) * (lastAisle - firstAisle) * 2);
            if (a <= firstAisle) {
                timeConsumed += ((wh.getAisleWidth() + wh.getShelfWidth()) * (lastAisle - a)) * 2;
            } else if (a > lastAisle) {
                timeConsumed += ((wh.getAisleWidth() + wh.getShelfWidth()) * (a - lastAisle)) * 2;
            }
        }

        // Calculate the time based on the number of aisles occupied.
        switch (numberOfAislesOccupied) {
            case 1:
                double farestDistance = Float.MIN_VALUE;
                for (Product it : allItems) {
                    if (it.getHeightPosition() > farestDistance) {
                        farestDistance = it.getHeightPosition();
                    }
                }
                timeConsumed += (2 * farestDistance) + extraToShelf;
                break;
            case 2:
                timeConsumed += numberOfAislesOccupied * totalShelvesLong;
                break;
            default:
                // Logic for multiple aisles occupied.
                timeConsumed += calculateMultiAisleTime(allItems, numberOfAislesOccupied, arrayOfAisles);
                break;
        }

        timeConsumed *= wh.getTravelSpeed();
        timeConsumed += allItems.size() * wh.getPickingTime();//*2
        timeConsumed += wh.getDepotTime();

        return timeConsumed;
    }

    /**
     * Calculates the time required for multiple aisles occupied.
     *
     * @param allItems               The list of items to be picked.
     * @param arrayOfAisles          Array indicating which aisles are occupied.
     * @param numberOfAislesOccupied The total number of aisles occupied.
     * @return The time required for multiple aisles.
     */
    private double calculateMultiAisleTime(List<Product> allItems, int numberOfAislesOccupied, boolean[] arrayOfAisles) {
        double timeConsumed = 0.0;
        int[] aislesOccupied = new int[numberOfAislesOccupied];
        int index = 0;
        for (int aoa = 0; aoa < arrayOfAisles.length; aoa++) {
            if (arrayOfAisles[aoa]) {
                aislesOccupied[index] = aoa;
                index++;
            }
        }
        double[] lgPerAisle = new double[numberOfAislesOccupied];
        lgPerAisle[0] = totalShelvesLong;
        lgPerAisle[numberOfAislesOccupied - 1] = totalShelvesLong;
        double[] lgPerAisleOld = new double[numberOfAislesOccupied];
        lgPerAisleOld[0] = totalShelvesLong;
        lgPerAisleOld[numberOfAislesOccupied - 1] = totalShelvesLong;
        boolean[] moreThanSS = new boolean[numberOfAislesOccupied];
        moreThanSS[0] = false;
        moreThanSS[numberOfAislesOccupied - 1] = false;
        Route[] routePerAisle = new Route[numberOfAislesOccupied];
        Arrays.fill(routePerAisle, Route.NONE);
        routePerAisle[0] = Route.SS;
        routePerAisle[numberOfAislesOccupied - 1] = Route.SS;
        for (int aisle = 1; aisle < numberOfAislesOccupied - 1; aisle++) {
            election = Route.NONE;
            ArrayList<Product> itemsAtI = (ArrayList<Product>) organizeItemsAt(aislesOccupied[aisle], allItems);

            double theLargestGap = getLargestGap(itemsAtI);


            // election = LGB -> solo entramos por abajo
            // election = LGAB -> entramos por arriba y por abajo
            // election = LGA -> solo entramos por arriba
            double totalTraversed = (2 * (wh.getShelfLength() - theLargestGap)) + extraToShelf;  //ERROR
            if (election == Route.LGAB) {
                totalTraversed += extraToShelf;
            }

            //ERROR
            if (totalTraversed > wh.getShelfLength() + extraToShelf) {
                election = Route.SS;
            }

            routePerAisle[aisle] = election;

            lgPerAisle[aisle] = totalTraversed;
            lgPerAisleOld[aisle] = totalTraversed;

            if (totalTraversed > totalShelvesLong) {
                lgPerAisle[aisle] = totalShelvesLong;
                moreThanSS[aisle] = true;
            } else {
                moreThanSS[aisle] = false;
            }
        }
        int lastLG = -1;
        int i = 1;
        while (i < numberOfAislesOccupied) {
            int worstSSaisle = -1;
            int worstLGaisle = -1;
            double distanceWorstSSaisle = Double.POSITIVE_INFINITY;
            double distanceWorstLGaisle = Double.NEGATIVE_INFINITY;
            int countSS = 0;

            while (i < numberOfAislesOccupied - 1 && (moreThanSS[i] || routePerAisle[i] == Route.LGB)) {
                if (moreThanSS[i]) {
                    if ((routePerAisle[i] == Route.LGB || countSS % 2 == 0) && lgPerAisleOld[i] < distanceWorstSSaisle) {
                        distanceWorstSSaisle = lgPerAisleOld[i];
                        worstSSaisle = i;
                    }
                    countSS++;
                } else if (lgPerAisleOld[i] > distanceWorstLGaisle) {
                    distanceWorstLGaisle = lgPerAisleOld[i];
                    worstLGaisle = i;
                }
                i++;
            }

            if (countSS % 2 == 1) {
                if (i != numberOfAislesOccupied - 1 && lgPerAisleOld[i] > distanceWorstLGaisle) {
                    distanceWorstLGaisle = lgPerAisleOld[i];
                    worstLGaisle = i;
                }

                if (lastLG != -1 && lgPerAisleOld[lastLG] > distanceWorstLGaisle) {
                    distanceWorstLGaisle = lgPerAisleOld[lastLG];
                    worstLGaisle = lastLG;
                }

                if (totalShelvesLong - distanceWorstLGaisle < distanceWorstSSaisle - totalShelvesLong) {
                    // convertir worstLGaisle a SS
                    lgPerAisle[worstLGaisle] = totalShelvesLong;
                    routePerAisle[worstLGaisle] = Route.SS;
                    if (worstLGaisle != i) {
                        lastLG = i;
                    }
                } else {
                    // convertir worstSSaisle a LG
                    lgPerAisle[worstSSaisle] = lgPerAisleOld[worstSSaisle];
                    routePerAisle[worstSSaisle] = Route.LGABN;
                    lastLG = i;
                }
            } else {
                lastLG = i;
            }

            i++;
        }
        for (double distance : lgPerAisle) {
            timeConsumed += distance;
        }
        return timeConsumed;
    }

    /**
     * Organizes the items in a specific aisle by their height in descending order.
     *
     * @param aisle    The aisle to organize items for.
     * @param allItems The list of all items.
     * @return A list of items organized by height in descending order.
     */
    protected List<Product> organizeItemsAt(int aisle, List<Product> allItems) {
        ArrayList<Product> organized = new ArrayList<>();
        for (Product it : allItems) {
            if (it.getAisle() == aisle) {
                organized.add(it);
            }
        }

        organized.sort((Product a, Product b) -> Double.compare(b.getHeightPosition(), a.getHeightPosition()));

        return organized;
    }

    /**
     * Calculates the largest gap between items in a list.
     *
     * @param organizedItems The list of items organized by height.
     * @return The largest gap between items.
     */
    protected double getLargestGap(List<Product> organizedItems) {
        double theLargestGap = -1;
        Product lastReference = null;
        double lastPoint = wh.getShelfLength();

        int numReferencesAisle = 0;

        for (Product it : organizedItems) {
            lastReference = it;

            numReferencesAisle++;

            double distance = lastPoint - it.getHeightPosition();
            lastPoint = it.getHeightPosition();

            if (distance > theLargestGap) {
                theLargestGap = distance;
                if (numReferencesAisle == 1) {
                    election = Route.LGB;
                } else {
                    election = Route.LGAB;
                }
            }

        }

        if (lastReference != null && lastReference.getHeightPosition() > theLargestGap) {
            theLargestGap = lastReference.getHeightPosition();
            election = Route.LGA;
        }

        return theLargestGap;
    }

    /**
     * Enum representing different routing strategies.
     */
    private enum Route {
        NONE, // No specific route.
        LGA, // Largest Gap Above.
        LGB, // Largest Gap Below.
        LGAB, // Largest Gap Above and Below.
        LGABN, // New strategy.
        SS, // S-Shape strategy.
    }

    /**
     * The selected routing strategy for the current aisle.
     */
    private Route election;

}
