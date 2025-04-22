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
package com.instancesobp.evaluationResults.graphics.algorithms;

import com.instancesobp.models.Batch;
import com.instancesobp.models.Order;
import com.instancesobp.models.Product;
import com.instancesobp.models.Warehouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.instancesobp.evaluationResults.graphics.Utils.*;
import static com.instancesobp.utils.Constants.DEPOT_CORNER;

/**
 * Implements the CombinedPlus routing algorithm for visualizing and calculating
 * optimized paths in a warehouse. This algorithm determines the most efficient
 * routes for picking items based on the layout and configuration of the warehouse.
 * The class provides methods to calculate routes, draw paths, and organize items
 * within aisles, ensuring that the visualization is accurate and adheres to the
 * warehouse's constraints.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class CombinedPlus {

    /** The warehouse object containing layout and configuration details. */
    private final Warehouse warehouse;

    /** List of batches to be processed and visualized. */
    private final List<Batch> batchList;

    /** The index of the current batch being processed. */
    private final int currentBatchIndex;

    /** Multiplier for scaling graphical dimensions. */
    private final double scaleMultiplier;

    /** Half of the scale multiplier for intermediate calculations. */
    private final double scaleMultiplierHalf;

    /** The orientation of the graphical representation (horizontal or vertical). */
    private final int graphicPosition;

    /**
     * Constructor for the CombinedPlus class.
     *
     * @param warehouse The warehouse object containing layout and configuration details.
     * @param batchList The list of batches to be processed and visualized.
     * @param currentBatchIndex The index of the current batch being processed.
     * @param scaleMultiplier The multiplier for scaling graphical dimensions.
     * @param graphicPosition The orientation of the graphical representation.
     */
    public CombinedPlus(Warehouse warehouse, List<Batch> batchList, int currentBatchIndex, int scaleMultiplier, int graphicPosition) {
        this.warehouse = warehouse;
        this.batchList = batchList;
        this.currentBatchIndex = currentBatchIndex;
        this.scaleMultiplier = scaleMultiplier;
        this.scaleMultiplierHalf = scaleMultiplier * 0.5;
        this.graphicPosition = graphicPosition;
    }

    /**
     * Enum representing the different routing strategies used in the algorithm.
     */
    enum Route {
        NONE,   // No route assigned
        LGA,    // Largest Gap Above
        LGB,    // Largest Gap Below
        LGAB,   // Largest Gap on Both Sides
        LGABN,  // New Largest Gap on Both Sides
        SS      // S-Shape route
    }

    /** The selected routing strategy for the current aisle. */
    private Route election;

    /**
     * Draws the optimized path for the CombinedPlus algorithm on the given graphics context.
     *
     * @param g The graphics context used for drawing.
     */
    public void drawPathCombinedPlus(Graphics g) {
        ArrayList<Product> allItems = new ArrayList<>();

        for (Order pedido : batchList.get(currentBatchIndex).getOrders()) {
            allItems.addAll(pedido.getProducts());
        }

        double extraToShelf = warehouse.getAisleWidth(); //0,2

        boolean[] arrayOfAisles = new boolean[warehouse.getNumberOfAisles()];

        int firstAisle = Integer.MAX_VALUE;
        int lastAisle = Integer.MIN_VALUE;
        int numberOfAislesOccupied = 0;
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

        double totalShelvesLong = warehouse.getShelfLength() + extraToShelf;
        Route[] routePerAisle = new Route[numberOfAislesOccupied];
        int[] aislesOccupied = new int[numberOfAislesOccupied];

        if (numberOfAislesOccupied == 1) {
            double farestDistance = Double.MIN_VALUE;
            for (Product it : allItems) {
                if (it.getHeightPosition() > farestDistance) {
                    farestDistance = it.getHeightPosition();
                }
            }
        } else {
            int index = 0;
            for (int aoa = 0; aoa < arrayOfAisles.length; aoa++) {
                if (arrayOfAisles[aoa]) {
                    aislesOccupied[index] = aoa;
                    index++;
                }
            }

            double[] lgPerAisleOld = new double[numberOfAislesOccupied];
            lgPerAisleOld[0] = totalShelvesLong;
            lgPerAisleOld[numberOfAislesOccupied - 1] = totalShelvesLong;

            boolean[] moreThanSS = new boolean[numberOfAislesOccupied];
            moreThanSS[0] = false;
            moreThanSS[numberOfAislesOccupied - 1] = false;

            Arrays.fill(routePerAisle, Route.NONE);
            routePerAisle[0] = Route.SS;
            routePerAisle[numberOfAislesOccupied - 1] = Route.SS;

            for (int aisle = 1; aisle < numberOfAislesOccupied - 1; aisle++) {
                election = Route.NONE;
                ArrayList<Product> itemsAtI = (ArrayList<Product>) organizeItemsAt(aislesOccupied[aisle], allItems);

                double theLargestGap = getLargestGap(itemsAtI);

                double totalTraversed = (2 * (warehouse.getShelfLength() - theLargestGap)) + extraToShelf;
                if (election == Route.LGAB) {
                    totalTraversed += extraToShelf;
                }

                //ERROR
                if (totalTraversed > warehouse.getShelfLength() + extraToShelf) {
                    election = Route.SS;
                }

                routePerAisle[aisle] = election;

                //lgPerAisle[aisle] = totalTraversed;
                lgPerAisleOld[aisle] = totalTraversed;

                //lgPerAisle[aisle] = totalShelvesLong;
                moreThanSS[aisle] = totalTraversed > totalShelvesLong;
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
                        //lgPerAisle[worstLGaisle] = totalShelvesLong;
                        routePerAisle[worstLGaisle] = Route.SS;
                        if (worstLGaisle != i) {
                            lastLG = i;
                        }
                    } else {
                        // convertir worstSSaisle an LG
                        //lgPerAisle[worstSSaisle] = lgPerAisleOld[worstSSaisle];
                        routePerAisle[worstSSaisle] = Route.LGABN;
                        lastLG = i;
                    }
                } else {
                    lastLG = i;
                }

                i++;
            }
        }

        double unidad_ancho_y = warehouse.getShelfWidth() + warehouse.getAisleWidth();
        double largo_pasillo = (warehouse.getAisleWidth() + warehouse.getShelfLength());

        int x_pos_baja = (int) Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf);
        int x_pos_alta = (int) Math.round((warehouse.getAisleWidth() + warehouse.getShelfLength()) * scaleMultiplier) + x_pos_baja;

        int n_pasillos = warehouse.getNumberOfAisles();
        double ancho_warehouse = (unidad_ancho_y * n_pasillos) * scaleMultiplier;

        int[] y_pos_pasillos = new int[n_pasillos];
        Double[] val_pos_pasillos = new Double[n_pasillos];

        y_pos_pasillos[0] = (int) Math.round(unidad_ancho_y * scaleMultiplierHalf);
        val_pos_pasillos[0] = 0.0;
        for (int i = 1; i < n_pasillos; i++) {
            y_pos_pasillos[i] = (int) Math.round(unidad_ancho_y * scaleMultiplier) + y_pos_pasillos[i - 1];
            val_pos_pasillos[i] = unidad_ancho_y + val_pos_pasillos[i - 1];
        }
        Point depot;
        Double val_y_depot = 0.0;
        g.setColor(routesColor);
        if (warehouse.getDepotPlacement() == DEPOT_CORNER) {
            depot = new Point(x_pos_baja, y_pos_pasillos[0]);
        } else {
            depot = new Point(x_pos_baja, (int) ancho_warehouse / 2);
            val_y_depot = (unidad_ancho_y * (n_pasillos - 1) * scaleMultiplier);
        }
        double dis = Math.abs(val_pos_pasillos[firstAisle] - val_y_depot);
        if (dis > 0) {
            drawLine(g, depot.x, depot.y, x_pos_baja, y_pos_pasillos[firstAisle], graphicPosition);
        }
        dis = Math.abs(val_pos_pasillos[lastAisle] - val_y_depot);
        if (dis > 0) {
            drawLine(g, depot.x, depot.y, x_pos_baja, y_pos_pasillos[lastAisle], graphicPosition);
        }
        if (numberOfAislesOccupied == 1) {
            double farestDistance = Double.MIN_VALUE;
            for (Product it : allItems) {
                if (it.getHeightPosition() > farestDistance) {
                    farestDistance = it.getHeightPosition();
                }
            }
            int pos_x = (int) ((farestDistance + warehouse.getAisleWidth()) * scaleMultiplier);
            double val = farestDistance + warehouse.getAisleWidth();
            drawLine(g, x_pos_baja, y_pos_pasillos[firstAisle], pos_x, y_pos_pasillos[firstAisle], graphicPosition);
            drawLine(g, x_pos_baja, y_pos_pasillos[firstAisle] + 5, pos_x, y_pos_pasillos[firstAisle] + 5, graphicPosition);
            g.setColor(measuresColor);
            drawLineMeasureH(g, formatString(val), pos_x, x_pos_baja, y_pos_pasillos[firstAisle] - 10, graphicPosition);
            g.setColor(routesColor);
        } else if (numberOfAislesOccupied == 2) {

            //PRIMER PASILLO
            drawLine(g, x_pos_baja, y_pos_pasillos[firstAisle], x_pos_alta, y_pos_pasillos[firstAisle], graphicPosition);
            g.setColor(measuresColor);
            drawLineMeasureH(g, formatString(largo_pasillo), x_pos_alta, x_pos_baja, y_pos_pasillos[firstAisle] - 10, graphicPosition);
            g.setColor(routesColor);
            //ULTIMO PASILLO
            drawLine(g, x_pos_baja, y_pos_pasillos[lastAisle], x_pos_alta, y_pos_pasillos[lastAisle], graphicPosition);
           g.setColor(routesColor);

            //UNION ALTA PRIMER Y ULTIMO PASILLO
            drawLine(g, x_pos_alta, y_pos_pasillos[firstAisle], x_pos_alta, y_pos_pasillos[lastAisle], graphicPosition);
            g.setColor(measuresColor);
            drawLineMeasureV(g, formatString(val_pos_pasillos[lastAisle] - val_pos_pasillos[firstAisle]), y_pos_pasillos[firstAisle], y_pos_pasillos[lastAisle], x_pos_alta - 10, graphicPosition);
            g.setColor(routesColor);
        } else {

            //PRIMER PASILLO
            drawLine(g, x_pos_baja, y_pos_pasillos[firstAisle], x_pos_alta, y_pos_pasillos[firstAisle], graphicPosition);
            g.setColor(measuresColor);
            drawLineMeasureH(g, formatString(largo_pasillo), x_pos_alta, x_pos_baja, y_pos_pasillos[firstAisle] - 10, graphicPosition);
            g.setColor(routesColor);
            //ULTIMO PASILLO
            drawLine(g, x_pos_baja, y_pos_pasillos[lastAisle], x_pos_alta, y_pos_pasillos[lastAisle], graphicPosition);
            g.setColor(routesColor);

            int lado_pos = 1;
            int pasillo_tiene_item = 0;
            boolean drawMeasureV = false;
            //boolean drawMeasureH = false;
            for (int i = 0; i < n_pasillos; i++) {
                if (i > firstAisle && i <= lastAisle) {
                    double distanceV = val_pos_pasillos[i] - val_pos_pasillos[i - 1];
                    if (lado_pos % 2 == 1) {
                        //UNION ALTA - PASILLO
                        drawLine(g, x_pos_alta, y_pos_pasillos[i], x_pos_alta, y_pos_pasillos[i - 1], graphicPosition);
                        if(!drawMeasureV) {
                            g.setColor(measuresColor);
                            drawLineMeasureV(g, formatString(distanceV), y_pos_pasillos[i], y_pos_pasillos[i - 1], x_pos_alta + 15, graphicPosition);
                            g.setColor(routesColor);
                            drawMeasureV = true;
                        }
                    } else {
                        //UNION BAJA - PASILLO
                        drawLine(g, x_pos_baja + 5, y_pos_pasillos[i], x_pos_baja + 5, y_pos_pasillos[i - 1], graphicPosition);
                        if(!drawMeasureV) {
                            g.setColor(measuresColor);
                            drawLineMeasureV(g, formatString(distanceV), y_pos_pasillos[i], y_pos_pasillos[i - 1], x_pos_baja - 15, graphicPosition);
                            g.setColor(routesColor);
                            drawMeasureV = true;
                        }
                    }
                }

                if (arrayOfAisles[i]) {
                    if (pasillo_tiene_item != 0 && pasillo_tiene_item != routePerAisle.length - 1) {
                        if (routePerAisle[pasillo_tiene_item] == Route.SS) {
                            drawLine(g, x_pos_baja, y_pos_pasillos[i], x_pos_alta, y_pos_pasillos[i], graphicPosition);
                            lado_pos++;
                        } else if (routePerAisle[pasillo_tiene_item] == Route.LGB) {
                            ArrayList<Product> itemsAtI = (ArrayList<Product>) organizeItemsAt(aislesOccupied[pasillo_tiene_item], allItems);
                            double val = itemsAtI.get(0).getHeightPosition() + (warehouse.getAisleWidth() / 2);
                            int pos_x = (int) (x_pos_baja + val * scaleMultiplier);

                            drawLine(g, x_pos_baja, y_pos_pasillos[i], pos_x, y_pos_pasillos[i], graphicPosition);
                            drawLine(g, x_pos_baja, y_pos_pasillos[i] + 5, pos_x, y_pos_pasillos[i] + 5, graphicPosition);
                            g.setColor(measuresColor);
                            drawLineMeasureH(g, formatString(val), pos_x, x_pos_baja, y_pos_pasillos[i] - 10, graphicPosition);
                            g.setColor(routesColor);
                        } else if (routePerAisle[pasillo_tiene_item] == Route.LGA) {
                            ArrayList<Product> itemsAtI = (ArrayList<Product>) organizeItemsAt(aislesOccupied[pasillo_tiene_item], allItems);

                            double val = largo_pasillo - (itemsAtI.get(itemsAtI.size() - 1).getHeightPosition() + (warehouse.getAisleWidth() / 2));
                            int pos_x = x_pos_alta - (int) (val * scaleMultiplier);

                            drawLine(g, x_pos_alta, y_pos_pasillos[i], pos_x, y_pos_pasillos[i], graphicPosition);
                            drawLine(g, x_pos_alta, y_pos_pasillos[i] + 5, pos_x, y_pos_pasillos[i] + 5, graphicPosition);
                            g.setColor(measuresColor);
                            drawLineMeasureH(g, formatString(val), pos_x, x_pos_alta, y_pos_pasillos[i] - 10, graphicPosition);
                            g.setColor(routesColor);

                        } else if (routePerAisle[pasillo_tiene_item] == Route.LGAB || routePerAisle[pasillo_tiene_item] == Route.LGABN) {
                            ArrayList<Product> itemsAtI = (ArrayList<Product>) organizeItemsAt(aislesOccupied[pasillo_tiene_item], allItems);
                            double distance = 0.0;
                            double max_item_gap = 0.0;
                            double min_item_gap = 0.0;
                            for (int j = 1; j < itemsAtI.size(); j++) {
                                double aux = itemsAtI.get(j - 1).getHeightPosition() - itemsAtI.get(j).getHeightPosition();
                                if (aux > distance) {
                                    distance = aux;
                                    min_item_gap = itemsAtI.get(j).getHeightPosition();
                                    max_item_gap = itemsAtI.get(j - 1).getHeightPosition();
                                }
                            }

                            double val = min_item_gap + (warehouse.getAisleWidth() / 2);
                            int pos_x = x_pos_baja + (int) (val * scaleMultiplier);
                            drawLine(g, x_pos_baja, y_pos_pasillos[i], pos_x, y_pos_pasillos[i], graphicPosition);
                            drawLine(g, x_pos_baja, y_pos_pasillos[i] + 5, pos_x, y_pos_pasillos[i] + 5, graphicPosition);
                            g.setColor(measuresColor);
                            drawLineMeasureH(g, formatString(val), pos_x, x_pos_baja, y_pos_pasillos[i] - 10, graphicPosition);
                            g.setColor(routesColor);

                            val = largo_pasillo - (max_item_gap + (warehouse.getAisleWidth() / 2));
                            pos_x = x_pos_alta - (int) (val * scaleMultiplier);
                            drawLine(g, x_pos_alta, y_pos_pasillos[i], pos_x, y_pos_pasillos[i], graphicPosition);
                            drawLine(g, x_pos_alta, y_pos_pasillos[i] + 5, pos_x, y_pos_pasillos[i] + 5, graphicPosition);
                            g.setColor(measuresColor);
                            drawLineMeasureH(g, formatString(val), pos_x, x_pos_alta, y_pos_pasillos[i] - 10, graphicPosition);
                            g.setColor(routesColor);
                        }
                    }
                    pasillo_tiene_item++;
                }
            }
        }
    }

    /**
     * Organizes the items in a specific aisle by their height position in descending order.
     *
     * @param aisle The aisle number to organize items for.
     * @param allItems The list of all items in the batch.
     * @return A list of items organized by height position.
     */
    private java.util.List<Product> organizeItemsAt(int aisle, java.util.List<Product> allItems) {
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
     * Calculates the largest gap between items in a given aisle.
     *
     * @param organizedItems The list of items organized by height position.
     * @return The largest gap between items in the aisle.
     */
    private double getLargestGap(List<Product> organizedItems) {
        double theLargestGap = -1;
        Product lastReference = null;
        double lastPoint = warehouse.getShelfLength();

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
}
