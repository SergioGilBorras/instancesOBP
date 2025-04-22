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
import java.util.Collections;
import java.util.HashMap;

import com.instancesobp.models.Batch;
import com.instancesobp.models.Order;
import com.instancesobp.models.Product;
import com.instancesobp.models.Warehouse;

import static com.instancesobp.utils.Constants.DEPOT_CENTER;
import static com.instancesobp.utils.Constants.DEPOT_CORNER;

/**
 * Implements the Ratliff and Rosenthal routing algorithm for order picking in a warehouse.
 * This algorithm calculates the total distance traveled by a picker to collect all items
 * in a batch, considering the warehouse layout and item locations.
 * The algorithm uses a dynamic programming approach to determine the optimal path
 * through the aisles, minimizing the total travel distance.
 * TODO: Add timeTurn to the algorithm.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class RatliffRosenthal extends RoutingAlgorithm {

    /**
     * Constructor for the RatliffRosenthal class.
     *
     * @param warehouse The warehouse object containing the layout and configuration details.
     */
    public RatliffRosenthal(Warehouse warehouse) {
        super(warehouse);
    }


    /**
     * Executes the Ratliff and Rosenthal routing algorithm for a given batch of orders.
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
            return 0;
        }

        int num_items = 0;

        HashMap<Integer, ArrayList<Double>> listHeightsByAisle = new HashMap<>();

        if (wh.getDepotPlacement() == DEPOT_CORNER) {
            ArrayList<Double> listHeight = new ArrayList<>();
            listHeight.add(0.0);
            listHeightsByAisle.put(0, listHeight);
        }

        for (Order order : batch.getOrders()) {
            for (Product product : order.getProducts()) {
                num_items++;

                if (listHeightsByAisle.containsKey(product.getAisle())) {
                    ArrayList<Double> listHeight = listHeightsByAisle.get(product.getAisle());
                    if (!listHeight.contains(product.getHeightPosition())) {
                        listHeight.add(product.getHeightPosition());
                    }
                } else {
                    ArrayList<Double> listHeight = new ArrayList<>();
                    listHeight.add(product.getHeightPosition());
                    listHeightsByAisle.put(product.getAisle(), listHeight);
                }
            }
        }

        double distancesBetweenAisles = wh.getShelfWidth() + wh.getAisleWidth();
        int num_pasos = (wh.getNumberOfAisles() * 2) - 1;

        int pasillo_extra_depot = 0;
        boolean extra_pasillo = false;
        if (wh.getDepotPlacement() == DEPOT_CENTER) {
            if (wh.getNumberOfAisles() % 2 == 0) {
                num_pasos += 2;
                pasillo_extra_depot = (wh.getNumberOfAisles() / 2) - 1;
                extra_pasillo = true;
            } else {
                pasillo_extra_depot = (wh.getNumberOfAisles() + 1) / 2;
            }
        }

        Double[][] tabla_result = new Double[num_pasos][7];


        boolean medio_pasillo = false;

        int step = 0;
        for (int i = 0; i < wh.getNumberOfAisles(); i++) {
            //Calculo las distancias de cada pasillo paralelo
            double max_alt = -1;
            double min_alt = 0;
            double max_gap = 0;
            double min_alt_gap = 0;
            boolean has_pasillos = false;
            if (listHeightsByAisle.containsKey(i)) {
                has_pasillos = true;
                ArrayList<Double> listHeights = listHeightsByAisle.get(i);
                if (!extra_pasillo && i + 1 == pasillo_extra_depot && wh.getDepotPlacement() == DEPOT_CENTER) {
                    listHeights.add(0.0);
                }
                Collections.sort(listHeights);
                max_alt = listHeights.get(listHeights.size() - 1);
                min_alt = listHeights.get(0);
                max_gap = 0;
                double alt_p = -1;
                for (double height : listHeights) {
                    double gap = height - alt_p;
                    if (alt_p > -1 && gap > max_gap) {
                        max_gap = gap;
                        min_alt_gap = alt_p;
                    }
                    alt_p = height;
                }
            } else if (!extra_pasillo && i + 1 == pasillo_extra_depot && wh.getDepotPlacement() == DEPOT_CENTER) {
                ArrayList<Double> lista_alturas = new ArrayList<>();
                lista_alturas.add(0.0);
                listHeightsByAisle.put(i, lista_alturas);
                has_pasillos = true;
            }

            Double[] path = new Double[6];
            path[0] = wh.getShelfLength() + wh.getAisleWidth();
            path[1] = ((wh.getShelfLength() - min_alt) * 2) + wh.getAisleWidth();
            if (max_alt == 0) {
                path[2] = 0.0;
            } else {
                path[2] = (max_alt * 2) + wh.getAisleWidth();
            }
            if (min_alt_gap == 0) {
                path[3] = ((wh.getShelfLength() - max_gap) * 2) + wh.getAisleWidth();
            } else {
                path[3] = ((wh.getShelfLength() - max_gap + wh.getAisleWidth()) * 2);
            }
            path[4] = (wh.getShelfLength() + wh.getAisleWidth()) * 2;
            path[5] = 0.0;

            Double[] path_V = new Double[6];
            if (!medio_pasillo) {
                path_V[0] = (distancesBetweenAisles * 2);
                path_V[1] = (distancesBetweenAisles * 2);
                path_V[2] = (distancesBetweenAisles * 2);
                path_V[3] = (distancesBetweenAisles * 4);
                path_V[4] = 0.0;
                path_V[5] = 0.0;
            } else {
                path_V[0] = distancesBetweenAisles;
                path_V[1] = distancesBetweenAisles;
                path_V[2] = distancesBetweenAisles;
                path_V[3] = (distancesBetweenAisles * 2);
                path_V[4] = 0.0;
                path_V[5] = 0.0;
                medio_pasillo = false;
            }

            if (step == 0) {
                //distancias pasillos paralelos
                tabla_result[step][0] = path[0];


                tabla_result[step][1] = path[1];

                tabla_result[step][2] = path[2];

                tabla_result[step][3] = path[4];

                tabla_result[step][4] = path[3];

                if (has_pasillos) {
                    tabla_result[step][5] = -1.0;
                } else {
                    tabla_result[step][5] = path[5];
                }

                tabla_result[step][6] = -1.0;

            } else {
                //distancias pasillos transversales
                tabla_result[step][0] = tabla_result[step - 1][0] + path_V[0];

                tabla_result[step][1] = tabla_result[step - 1][1] + path_V[1];

                tabla_result[step][4] = tabla_result[step - 1][1] + path_V[3];

                if (tabla_result[step - 1][1] != null && tabla_result[step - 1][1] != -1) {
                    tabla_result[step][6] = tabla_result[step - 1][1] + path_V[4];
                }

                tabla_result[step][2] = tabla_result[step - 1][2] + path_V[2];

                if (tabla_result[step][4] == null || tabla_result[step][4] > tabla_result[step - 1][2] + path_V[3]) {
                    tabla_result[step][4] = tabla_result[step - 1][2] + path_V[3];
                }

                if (tabla_result[step - 1][2] != null && tabla_result[step - 1][2] != -1) {
                    if (tabla_result[step][6] == null || tabla_result[step][6] > tabla_result[step - 1][2] + path_V[4]) {
                        tabla_result[step][6] = tabla_result[step - 1][2] + path_V[4];
                    }
                }

                if (tabla_result[step][1] == null || tabla_result[step][1] > tabla_result[step - 1][3] + path_V[1]) {
                    tabla_result[step][1] = tabla_result[step - 1][3] + path_V[1];
                }

                if (tabla_result[step][2] == null || tabla_result[step][2] > tabla_result[step - 1][3] + path_V[2]) {
                    tabla_result[step][2] = tabla_result[step - 1][3] + path_V[2];
                }
                tabla_result[step][3] = tabla_result[step - 1][3] + path_V[3];

                if (tabla_result[step - 1][3] != null && tabla_result[step - 1][3] != -1) {
                    if (tabla_result[step][6] == null || tabla_result[step][6] > tabla_result[step - 1][3] + path_V[4]) {
                        tabla_result[step][6] = tabla_result[step - 1][3] + path_V[4];
                    }
                }

                if (tabla_result[step][4] == null || tabla_result[step][4] > tabla_result[step - 1][4] + path_V[3]) {
                    tabla_result[step][4] = tabla_result[step - 1][4] + path_V[3];
                }

                if (tabla_result[step - 1][5] != null && tabla_result[step - 1][5] != -1) {
                    tabla_result[step][5] = tabla_result[step - 1][5] + path_V[4];
                }

                if (tabla_result[step - 1][6] != null && tabla_result[step - 1][6] != -1 && tabla_result[step][6] > tabla_result[step - 1][6] + path_V[4]) {
                    tabla_result[step][6] = tabla_result[step - 1][6] + path_V[4];
                }

                double select_value = Double.MAX_VALUE;

                int max_path = path.length;
                if (has_pasillos) {
                    max_path--;
                }

                for (int j = 1; j < max_path; j++) {
                    if (path[j] < select_value) {
                        select_value = path[j];
                    }
                }

                step++;
                //distancias pasillos paralelos
                tabla_result[step][3] = tabla_result[step - 1][0] + path[0];

                tabla_result[step][0] = tabla_result[step - 1][0] + select_value;

                if (!has_pasillos) {
                    if (tabla_result[step][0] > tabla_result[step - 1][0] + path[5]) {
                        tabla_result[step][0] = tabla_result[step - 1][0] + path[5];
                    }
                }

                if (tabla_result[step][0] == null || tabla_result[step][0] > tabla_result[step - 1][1] + path[0]) {
                    tabla_result[step][0] = tabla_result[step - 1][1] + path[0];
                }

                tabla_result[step][1] = tabla_result[step - 1][1] + path[1];

                tabla_result[step][4] = tabla_result[step - 1][1] + path[2];

                if (tabla_result[step][4] == null || tabla_result[step][4] > tabla_result[step - 1][1] + path[3]) {
                    tabla_result[step][4] = tabla_result[step - 1][1] + path[3];
                }

                if (tabla_result[step][3] == null || tabla_result[step][3] > tabla_result[step - 1][1] + path[4]) {
                    tabla_result[step][3] = tabla_result[step - 1][1] + path[4];
                }

                if (!has_pasillos) {
                    if (tabla_result[step][1] == null || tabla_result[step][1] > tabla_result[step - 1][1] + path[5]) {
                        tabla_result[step][1] = tabla_result[step - 1][1] + path[5];
                    }
                }

                if (tabla_result[step][0] == null || tabla_result[step][0] > tabla_result[step - 1][2] + path[0]) {
                    tabla_result[step][0] = tabla_result[step - 1][2] + path[0];
                }

                if (tabla_result[step][4] == null || tabla_result[step][4] > tabla_result[step - 1][2] + path[1]) {
                    tabla_result[step][4] = tabla_result[step - 1][2] + path[1];
                }

                tabla_result[step][2] = tabla_result[step - 1][2] + path[2];

                if (tabla_result[step][4] == null || tabla_result[step][4] > tabla_result[step - 1][2] + path[3]) {
                    tabla_result[step][4] = tabla_result[step - 1][2] + path[3];
                }

                if (tabla_result[step][3] == null || tabla_result[step][3] > tabla_result[step - 1][2] + path[4]) {
                    tabla_result[step][3] = tabla_result[step - 1][2] + path[4];
                }

                if (!has_pasillos) {
                    if (tabla_result[step][2] == null || tabla_result[step][2] > tabla_result[step - 1][2] + path[5]) {
                        tabla_result[step][2] = tabla_result[step - 1][2] + path[5];
                    }
                }

                if (tabla_result[step][0] == null || tabla_result[step][0] > tabla_result[step - 1][3] + path[0]) {
                    tabla_result[step][0] = tabla_result[step - 1][3] + path[0];
                }

                if (tabla_result[step][3] == null || tabla_result[step][3] > tabla_result[step - 1][3] + select_value) {
                    tabla_result[step][3] = tabla_result[step - 1][3] + select_value;
                }

                if (!has_pasillos) {
                    if (tabla_result[step][3] == null || tabla_result[step][3] > tabla_result[step - 1][3] + path[5]) {
                        tabla_result[step][3] = tabla_result[step - 1][3] + path[5];
                    }
                }

                if (tabla_result[step][0] == null || tabla_result[step][0] > tabla_result[step - 1][4] + path[0]) {
                    tabla_result[step][0] = tabla_result[step - 1][4] + path[0];
                }

                if (tabla_result[step][4] == null || tabla_result[step][4] > tabla_result[step - 1][4] + path[1]) {
                    tabla_result[step][4] = tabla_result[step - 1][4] + path[1];
                }

                if (tabla_result[step][4] == null || tabla_result[step][4] > tabla_result[step - 1][4] + path[2]) {
                    tabla_result[step][4] = tabla_result[step - 1][4] + path[2];
                }

                if (tabla_result[step][4] == null || tabla_result[step][4] > tabla_result[step - 1][4] + path[3]) {
                    tabla_result[step][4] = tabla_result[step - 1][4] + path[3];
                }

                if (tabla_result[step][3] == null || tabla_result[step][3] > tabla_result[step - 1][4] + path[4]) {
                    tabla_result[step][3] = tabla_result[step - 1][4] + path[4];
                }

                if (!has_pasillos) {
                    if (tabla_result[step][4] == null || tabla_result[step][4] > tabla_result[step - 1][4]) {
                        tabla_result[step][4] = tabla_result[step - 1][4];
                    }
                }

                if (tabla_result[step - 1][5] != null && tabla_result[step - 1][5] != -1 && tabla_result[step][0] > tabla_result[step - 1][5] + path[0]) {
                    tabla_result[step][0] = tabla_result[step - 1][5] + path[0];
                }

                if (tabla_result[step - 1][5] != null && tabla_result[step - 1][5] != -1 && tabla_result[step][1] > tabla_result[step - 1][5] + path[1]) {
                    tabla_result[step][1] = tabla_result[step - 1][5] + path[1];
                }

                if (tabla_result[step - 1][5] != null && tabla_result[step - 1][5] != -1 && tabla_result[step][2] > tabla_result[step - 1][5] + path[2]) {
                    tabla_result[step][2] = tabla_result[step - 1][5] + path[2];
                }

                if (tabla_result[step - 1][5] != null && tabla_result[step - 1][5] != -1 && tabla_result[step][4] > tabla_result[step - 1][5] + path[3]) {
                    tabla_result[step][4] = tabla_result[step - 1][5] + path[3];
                }

                if (tabla_result[step - 1][5] != null && tabla_result[step - 1][5] != -1 && tabla_result[step][3] > tabla_result[step - 1][5] + path[4]) {
                    tabla_result[step][3] = tabla_result[step - 1][5] + path[4];
                }

                if (!has_pasillos) {
                    if (tabla_result[step - 1][5] != null && tabla_result[step - 1][5] != -1 && tabla_result[step][5] == null) {
                        tabla_result[step][5] = tabla_result[step - 1][5] + path[5];
                    } else if (tabla_result[step - 1][5] != null && tabla_result[step - 1][5] != -1 && tabla_result[step][5] > tabla_result[step - 1][5] + path[5]) {
                        tabla_result[step][5] = tabla_result[step - 1][5] + path[5];
                    }
                }

                if (!has_pasillos) {
                    if (tabla_result[step - 1][6] != null && tabla_result[step - 1][6] != -1 && tabla_result[step][6] == null) {
                        tabla_result[step][6] = tabla_result[step - 1][6] + path[5];
                    } else if (tabla_result[step - 1][6] != null && tabla_result[step - 1][6] != -1 && tabla_result[step][6] > tabla_result[step - 1][6] + path[5]) {
                        tabla_result[step][6] = tabla_result[step - 1][6] + path[5];
                    }
                }

            }
            step++;

            if (extra_pasillo && pasillo_extra_depot == i) {

                medio_pasillo = true;

                path = new Double[6];
                path[0] = wh.getShelfLength() + wh.getAisleWidth();
                path[1] = (wh.getShelfLength() * 2) + wh.getAisleWidth();
                path[2] = 0.0;
                path[3] = (wh.getShelfLength() + wh.getAisleWidth()) * 2;
                path[4] = (wh.getShelfLength() + wh.getAisleWidth()) * 2;
                path[5] = 0.0;

                path_V = new Double[6];
                path_V[0] = distancesBetweenAisles;
                path_V[1] = distancesBetweenAisles;
                path_V[2] = distancesBetweenAisles;
                path_V[3] = (distancesBetweenAisles * 2);
                path_V[4] = 0.0;
                path_V[5] = 0.0;

                //distancias pasillos transversales
                tabla_result[step][0] = tabla_result[step - 1][0] + path_V[0];

                tabla_result[step][1] = tabla_result[step - 1][1] + path_V[1];

                tabla_result[step][4] = tabla_result[step - 1][1] + path_V[3];

                if (tabla_result[step - 1][1] != null && tabla_result[step - 1][1] != -1) {
                    tabla_result[step][6] = tabla_result[step - 1][1] + path_V[4];
                }

                tabla_result[step][2] = tabla_result[step - 1][2] + path_V[2];

                if (tabla_result[step][4] == null || tabla_result[step][4] > tabla_result[step - 1][2] + path_V[3]) {
                    tabla_result[step][4] = tabla_result[step - 1][2] + path_V[3];
                }

                if (tabla_result[step - 1][2] != null && tabla_result[step - 1][2] != -1) {
                    if (tabla_result[step][6] == null || tabla_result[step][6] > tabla_result[step - 1][2] + path_V[4]) {
                        tabla_result[step][6] = tabla_result[step - 1][2] + path_V[4];
                    }
                }

                if (tabla_result[step][1] == null || tabla_result[step][1] > tabla_result[step - 1][3] + path_V[1]) {
                    tabla_result[step][1] = tabla_result[step - 1][3] + path_V[1];
                }

                if (tabla_result[step][2] == null || tabla_result[step][2] > tabla_result[step - 1][3] + path_V[2]) {
                    tabla_result[step][2] = tabla_result[step - 1][3] + path_V[2];
                }
                tabla_result[step][3] = tabla_result[step - 1][3] + path_V[3];

                if (tabla_result[step - 1][3] != null && tabla_result[step - 1][3] != -1) {
                    if (tabla_result[step][6] == null || tabla_result[step][6] > tabla_result[step - 1][3] + path_V[4]) {
                        tabla_result[step][6] = tabla_result[step - 1][3] + path_V[4];
                    }
                }

                if (tabla_result[step][4] == null || tabla_result[step][4] > tabla_result[step - 1][4] + path_V[3]) {
                    tabla_result[step][4] = tabla_result[step - 1][4] + path_V[3];
                }

                if (tabla_result[step - 1][5] != null && tabla_result[step - 1][5] != -1) {
                    tabla_result[step][5] = tabla_result[step - 1][5] + path_V[4];
                }

                if (tabla_result[step - 1][6] != null && tabla_result[step - 1][6] != -1 && tabla_result[step][6] > tabla_result[step - 1][6] + path_V[4]) {
                    tabla_result[step][6] = tabla_result[step - 1][6] + path_V[4];
                }

                double select_value = Double.MAX_VALUE;

                int max_path = path.length;
                max_path--;

                for (int j = 1; j < max_path; j++) {
                    if (path[j] < select_value) {
                        select_value = path[j];
                    }
                }

                step++;

                tabla_result[step][3] = tabla_result[step - 1][0] + path[0];

                tabla_result[step][0] = tabla_result[step - 1][0] + select_value;

                if (tabla_result[step][0] == null || tabla_result[step][0] > tabla_result[step - 1][1] + path[0]) {
                    tabla_result[step][0] = tabla_result[step - 1][1] + path[0];
                }

                tabla_result[step][1] = tabla_result[step - 1][1] + path[1];

                tabla_result[step][4] = tabla_result[step - 1][1] + path[2];

                if (tabla_result[step][4] == null || tabla_result[step][4] > tabla_result[step - 1][1] + path[3]) {
                    tabla_result[step][4] = tabla_result[step - 1][1] + path[3];
                }

                if (tabla_result[step][3] == null || tabla_result[step][3] > tabla_result[step - 1][1] + path[4]) {
                    tabla_result[step][3] = tabla_result[step - 1][1] + path[4];
                }

                if (tabla_result[step][0] == null || tabla_result[step][0] > tabla_result[step - 1][2] + path[0]) {
                    tabla_result[step][0] = tabla_result[step - 1][2] + path[0];
                }

                if (tabla_result[step][4] == null || tabla_result[step][4] > tabla_result[step - 1][2] + path[1]) {
                    tabla_result[step][4] = tabla_result[step - 1][2] + path[1];
                }

                tabla_result[step][2] = tabla_result[step - 1][2] + path[2];

                if (tabla_result[step][4] == null || tabla_result[step][4] > tabla_result[step - 1][2] + path[3]) {
                    tabla_result[step][4] = tabla_result[step - 1][2] + path[3];
                }

                if (tabla_result[step][3] == null || tabla_result[step][3] > tabla_result[step - 1][2] + path[4]) {
                    tabla_result[step][3] = tabla_result[step - 1][2] + path[4];
                }

                if (tabla_result[step][0] == null || tabla_result[step][0] > tabla_result[step - 1][3] + path[0]) {
                    tabla_result[step][0] = tabla_result[step - 1][3] + path[0];
                }

                if (tabla_result[step][3] == null || tabla_result[step][3] > tabla_result[step - 1][3] + select_value) {
                    tabla_result[step][3] = tabla_result[step - 1][3] + select_value;
                }

                if (tabla_result[step][0] == null || tabla_result[step][0] > tabla_result[step - 1][4] + path[0]) {
                    tabla_result[step][0] = tabla_result[step - 1][4] + path[0];
                }

                if (tabla_result[step][4] == null || tabla_result[step][4] > tabla_result[step - 1][4] + path[1]) {
                    tabla_result[step][4] = tabla_result[step - 1][4] + path[1];
                }

                if (tabla_result[step][4] == null || tabla_result[step][4] > tabla_result[step - 1][4] + path[2]) {
                    tabla_result[step][4] = tabla_result[step - 1][4] + path[2];
                }

                if (tabla_result[step][4] == null || tabla_result[step][4] > tabla_result[step - 1][4] + path[3]) {
                    tabla_result[step][4] = tabla_result[step - 1][4] + path[3];
                }

                if (tabla_result[step][3] == null || tabla_result[step][3] > tabla_result[step - 1][4] + path[4]) {
                    tabla_result[step][3] = tabla_result[step - 1][4] + path[4];
                }

                if (tabla_result[step - 1][5] != null && tabla_result[step - 1][5] != -1 && tabla_result[step][0] > tabla_result[step - 1][5] + path[0]) {
                    tabla_result[step][0] = tabla_result[step - 1][5] + path[0];
                }

                if (tabla_result[step - 1][5] != null && tabla_result[step - 1][5] != -1 && tabla_result[step][1] > tabla_result[step - 1][5] + path[1]) {
                    tabla_result[step][1] = tabla_result[step - 1][5] + path[1];
                }

                if (tabla_result[step - 1][5] != null && tabla_result[step - 1][5] != -1 && tabla_result[step][2] > tabla_result[step - 1][5] + path[2]) {
                    tabla_result[step][2] = tabla_result[step - 1][5] + path[2];
                }

                if (tabla_result[step - 1][5] != null && tabla_result[step - 1][5] != -1 && tabla_result[step][4] > tabla_result[step - 1][5] + path[3]) {
                    tabla_result[step][4] = tabla_result[step - 1][5] + path[3];
                }

                if (tabla_result[step - 1][5] != null && tabla_result[step - 1][5] != -1 && tabla_result[step][3] > tabla_result[step - 1][5] + path[4]) {
                    tabla_result[step][3] = tabla_result[step - 1][5] + path[4];
                }

                step++;
            }

        }

        step--;

        double min = Double.MAX_VALUE;
        for (int j = 0; j < tabla_result[step].length; j++) {
            Double pa = tabla_result[step][j];
            if (pa != null) {
                if (pa > 0 && min > pa && j != 0 && j != 4 && j != 5) {
                    min = pa;
                }
            }
        }
        double distancia = min;


        return (distancia * wh.getTravelSpeed()) + wh.getDepotTime() + (wh.getPickingTime() * num_items);
    }

}
