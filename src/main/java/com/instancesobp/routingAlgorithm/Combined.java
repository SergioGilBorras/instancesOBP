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
import java.util.Iterator;
import java.util.List;

import com.instancesobp.models.Batch;
import com.instancesobp.models.Order;
import com.instancesobp.models.Product;
import com.instancesobp.models.Warehouse;
import com.instancesobp.utils.Constants;

import static com.instancesobp.utils.Constants.DEPOT_CORNER;

/**
 * Implements the Combined routing algorithm for order picking in a warehouse.
 * This algorithm calculates the total distance traveled by a picker to collect all items
 * in a batch, considering the warehouse layout, aisle gaps, and item locations.
 * The algorithm combines different routing strategies to minimize the total travel distance.
 * It also accounts for turn times and depot placement.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class Combined extends RoutingAlgorithm {

    // Constants representing gap locations in the aisle
    private static final int GAP_BEGINNING = 0;
    private static final int GAP_MIDDLE = 1;
    private static final int GAP_END = 2;
    private static final int WITHOUT_GAP = 3;

    /**
     * Constructor for the Combined class.
     *
     * @param warehouse The warehouse object containing the layout and configuration details.
     */
    public Combined(Warehouse warehouse) {
        super(warehouse);
    }

    /**
     * Executes the Combined routing algorithm for a given batch of orders.
     * This method calculates the total distance traveled by the picker to collect
     * all items in the batch. It considers the warehouse layout, aisle gaps, and
     * item locations, as well as turn times and depot placement.
     *
     * @param batch The batch of orders to be processed.
     * @return The total distance traveled by the picker to collect all items in the batch.
     */
    @Override
    public double run(Batch batch) {
        if (batch.getOrders().isEmpty()) {
            return 0;
        }
        double distancia = 0;
        int max_pasillo = 0;
        int min_pasillo = Integer.MAX_VALUE;
        double tiempo_giros = 0.0;
        int num_items = 0;

        // HashMaps to store aisle information and maximum distances
        HashMap<Integer, List<Double>> Pasillos_max_dis = new HashMap<>();
        HashMap<Integer, Double> Pasillos_max_dis_com = new HashMap<>();

        //Precálculo de distancias en hashtable
        for (Order order : batch.getOrders()) {
            for (Product p : order.getProducts()) {
                num_items++;
                if (Pasillos_max_dis.containsKey(p.getAisle())) {
                    List<Double> lista_alturas = Pasillos_max_dis.get(p.getAisle());
                    if (!lista_alturas.contains(p.getHeightPosition())) {
                        lista_alturas.add(p.getHeightPosition());
                        Pasillos_max_dis.replace(p.getAisle(), lista_alturas);
                    }
                    if (Pasillos_max_dis_com.get(p.getAisle()) < p.getHeightPosition()) {
                        Pasillos_max_dis_com.replace(p.getAisle(), p.getHeightPosition());
                    }
                } else {
                    List<Double> lista_alturas;
                    lista_alturas = new ArrayList<>();
                    lista_alturas.add(p.getHeightPosition());
                    lista_alturas.add(0.0);
                    lista_alturas.add(wh.getShelfLength());
                    Pasillos_max_dis.put(p.getAisle(), lista_alturas);
                    Pasillos_max_dis_com.put(p.getAisle(), p.getHeightPosition());
                    if (max_pasillo < p.getAisle()) {
                        max_pasillo = p.getAisle();
                    }
                    if (min_pasillo > p.getAisle()) {
                        min_pasillo = p.getAisle();
                    }

                }
            }
        }

        HashMap<Integer, Integer> Pasillos_gap_locate = new HashMap<>();

        //Cálculo de distancia en los pasillos principales
        if (Pasillos_max_dis.size() > 2) {
            int aislePosition = 1;
            distancia = (wh.getAisleWidth() + wh.getShelfLength());
            ArrayList<Integer> aisleList = new ArrayList<>(Pasillos_max_dis.keySet());
            Collections.sort(aisleList);
            Iterator<Integer> aisleIterator = aisleList.iterator();
            while (aisleIterator.hasNext()) {
                int pasillo = aisleIterator.next();
                if (pasillo != min_pasillo) {
                    List<Double> lista_alturas = Pasillos_max_dis.get(pasillo);
                    Collections.sort(lista_alturas);

                    double max_gap = 0;
                    for (int i = 1; i < lista_alturas.size(); i++) {
                        double dd = lista_alturas.get(i) - lista_alturas.get(i - 1);
                        if (max_gap < dd) {
                            if (i == 1) {
                                Pasillos_gap_locate.put(pasillo, GAP_BEGINNING);
                            } else if (i == lista_alturas.size() - 1) {
                                Pasillos_gap_locate.put(pasillo, GAP_END);
                            } else {
                                Pasillos_gap_locate.put(pasillo, GAP_MIDDLE);
                            }
                            max_gap = dd;
                        }
                    }
                    double pasillo_extra_gag_medio = 0;
                    if (Pasillos_gap_locate.get(pasillo) == GAP_MIDDLE) {
                        pasillo_extra_gag_medio = wh.getAisleWidth();
                    }

                    if (!aisleIterator.hasNext() && aislePosition % 2 == 0) {
                        distancia += wh.getAisleWidth() + (Pasillos_max_dis_com.get(max_pasillo) * 2);
                    } else if (!aisleIterator.hasNext()) {
                        distancia += (wh.getAisleWidth() + wh.getShelfLength());
                        Pasillos_gap_locate.put(pasillo, WITHOUT_GAP);

                    } else if ((aislePosition % 2 == 0 && Pasillos_gap_locate.get(pasillo) == GAP_END && (wh.getShelfLength() > (wh.getShelfLength() - max_gap) * 2))
                            || (aislePosition % 2 == 1 && (wh.getShelfLength() > ((wh.getShelfLength() - max_gap) * 2) + pasillo_extra_gag_medio))) {

                        distancia += wh.getAisleWidth() + ((wh.getShelfLength() - max_gap) * 2);
                        if (Pasillos_gap_locate.get(pasillo) == GAP_MIDDLE) {
                            distancia += wh.getAisleWidth();
                        }
                    } else {
                        distancia += (wh.getAisleWidth() + wh.getShelfLength());
                        Pasillos_gap_locate.put(pasillo, WITHOUT_GAP);

                        aislePosition++;
                    }
                }

            }
        } else if (Pasillos_max_dis.size() > 1) {
            distancia = ((wh.getAisleWidth() + wh.getShelfLength()) * 2);
        } else {
            for (List<Double> lista_alturas : Pasillos_max_dis.values()) {
                Collections.sort(lista_alturas);
                distancia = wh.getAisleWidth() + (Pasillos_max_dis_com.get(max_pasillo) * 2);
            }
        }

        //cálculo de distancia en pasillos transversales
        if (wh.getDepotPlacement() == DEPOT_CORNER) {
            distancia += ((wh.getAisleWidth() + wh.getShelfWidth()) * (max_pasillo) * 2);
        } else if (wh.getNumberOfAisles() % 2 == 0) {
            int a = wh.getNumberOfAisles() / 2;
            distancia += ((wh.getAisleWidth() + wh.getShelfWidth()) * (max_pasillo - min_pasillo) * 2);
            if (a <= min_pasillo) {
                distancia += (((wh.getAisleWidth() + wh.getShelfWidth()) / 2) + ((wh.getAisleWidth() + wh.getShelfWidth()) * (min_pasillo - a))) * 2;
            } else if (a > max_pasillo) {
                distancia += (((wh.getAisleWidth() + wh.getShelfWidth()) / 2) + ((wh.getAisleWidth() + wh.getShelfWidth()) * (a - max_pasillo))) * 2;
            }
        } else {
            int a = wh.getNumberOfAisles() / 2;
            distancia += ((wh.getAisleWidth() + wh.getShelfWidth()) * (max_pasillo - min_pasillo) * 2);
            if (a <= min_pasillo) {
                distancia += ((wh.getAisleWidth() + wh.getShelfWidth()) * (min_pasillo - a)) * 2;
            } else if (a > max_pasillo) {
                distancia += ((wh.getAisleWidth() + wh.getShelfWidth()) * (a - max_pasillo)) * 2;
            }
        }

        //tiempo_giros = calculateTurnTime(Pasillos_max_dis, min_pasillo, Pasillos_gap_locate, max_pasillo);

        return (distancia * wh.getTravelSpeed()) + wh.getDepotTime() + (wh.getPickingTime() * num_items) + tiempo_giros;

    }

    /**
     * Calculates the total turn time based on the aisle configuration and gap locations.
     *
     * @param Pasillos_max_dis    A map of aisle numbers to their respective heights.
     * @param min_pasillo         The minimum aisle number.
     * @param Pasillos_gap_locate A map of aisle numbers to their gap locations.
     * @param max_pasillo         The maximum aisle number.
     * @return The total turn time.
     */
    private double calculateTurnTime(HashMap<Integer, List<Double>> Pasillos_max_dis, int min_pasillo, HashMap<Integer, Integer> Pasillos_gap_locate, int max_pasillo) {
        double tiempo_giros;
        int num_shape_path = 0;
        for (int aisleType : Pasillos_gap_locate.values()) {
            if (aisleType == WITHOUT_GAP) {
                num_shape_path++;
            }
        }

        //cálculo el número de giros
        if (wh.getDepotPlacement() == Constants.DEPOT_CORNER) {
            // Depot en una esquina

            if (Pasillos_max_dis.size() == 1) {
                tiempo_giros = wh.getInsideTurnTime();
                if (min_pasillo != 0) {
                    tiempo_giros += (2 * wh.getOutsideTurnTime());
                }
            } else {
                tiempo_giros = (Pasillos_max_dis.size() * 2 * wh.getOutsideTurnTime());
                tiempo_giros += ((Pasillos_gap_locate.size() - num_shape_path) * 2 * wh.getInsideTurnTime());
                if (min_pasillo == 0) {
                    tiempo_giros -= wh.getOutsideTurnTime();
                }
                int num_gap_medio = 0;
                for (Integer gap_location : Pasillos_gap_locate.values()) {
                    if (gap_location == 1) {
                        num_gap_medio++;
                    }
                }
                tiempo_giros += (num_gap_medio * 2 * wh.getOutsideTurnTime());
                tiempo_giros += (num_gap_medio * wh.getInsideTurnTime());
            }
        } else {
            //Depot central
            if ((min_pasillo == (Pasillos_max_dis.size() / 2) + 1) && (max_pasillo == (Pasillos_max_dis.size() / 2) + 1)) {
                tiempo_giros = wh.getInsideTurnTime();
            } else {
                tiempo_giros = (Pasillos_max_dis.size() * 2 * wh.getOutsideTurnTime());
                tiempo_giros += ((Pasillos_gap_locate.size() - num_shape_path) * 2 * wh.getInsideTurnTime());
                if ((min_pasillo == (Pasillos_max_dis.size() / 2) + 1) || (max_pasillo == (Pasillos_max_dis.size() / 2) + 1)) {
                    tiempo_giros -= wh.getOutsideTurnTime();
                }
                int num_gap_medio = 0;
                for (Integer gap_location : Pasillos_gap_locate.values()) {
                    if (gap_location == 1) {
                        num_gap_medio++;
                    }
                }
                tiempo_giros += (num_gap_medio * 2 * wh.getOutsideTurnTime());
                tiempo_giros += (num_gap_medio * wh.getInsideTurnTime());

            }
        }
        return tiempo_giros;
    }

}
