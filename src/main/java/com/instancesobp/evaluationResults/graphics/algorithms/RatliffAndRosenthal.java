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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.instancesobp.evaluationResults.graphics.Utils.*;
import static com.instancesobp.utils.Constants.DEPOT_CENTER;
import static com.instancesobp.utils.Constants.DEPOT_CORNER;

/**
 * This class implements the Ratliff and Rosenthal routing algorithm for optimizing
 * the picking paths in a warehouse. It calculates the shortest path for retrieving
 * items from the warehouse based on the depot placement and aisle configuration.
 * The algorithm uses dynamic programming to compute the optimal path and visualizes
 * the result using graphical components.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class RatliffAndRosenthal {

    /** The warehouse object containing layout and configuration details. */
    private final Warehouse warehouse;

    /** List of batches to be processed. */
    private final List<Batch> batchList;

    /** The index of the current batch being processed. */
    private final int currentBatchIndex;

    /** Scaling factor for graphical representation. */
    private final double scaleMultiplier;

    /** Half of the scaling factor for graphical representation. */
    private final double scaleMultiplierHalf;

    /** The orientation of the graphical representation (horizontal or vertical). */
    private final int graphicPosition;

    /**
     * Constructor for the RatliffAndRosenthal class.
     *
     * @param warehouse The warehouse object containing layout and configuration details.
     * @param batchList The list of batches to be processed.
     * @param currentBatchIndex The index of the current batch being processed.
     * @param scaleMultiplier The scaling factor for graphical representation.
     * @param graphicPosition The orientation of the graphical representation.
     */
    public RatliffAndRosenthal(Warehouse warehouse, List<Batch> batchList, int currentBatchIndex, int scaleMultiplier, int graphicPosition) {
        this.warehouse = warehouse;
        this.batchList = batchList;
        this.currentBatchIndex = currentBatchIndex;
        this.scaleMultiplier = scaleMultiplier;
        this.scaleMultiplierHalf = scaleMultiplier * 0.5;
        this.graphicPosition = graphicPosition;
    }

    /**
     * Draws the exact path for the Ratliff and Rosenthal algorithm.
     * This method calculates the optimal path for retrieving items from the warehouse
     * based on the depot placement and aisle configuration. It uses dynamic programming
     * to compute the shortest path and visualizes the result on the provided graphics context.
     *
     * @param g The graphics context used for drawing the path.
     */
    public void drawExactPathRatliff(Graphics g) {

        HashMap<Integer, ArrayList<Double>> lista_alturas_por_pasillo = new HashMap<>();

        if (warehouse.getDepotPlacement() == DEPOT_CORNER) {
            ArrayList<Double> lista_alturas = new ArrayList<>();
            lista_alturas.add(0.0);
            lista_alturas_por_pasillo.put(0, lista_alturas);
        }

        for (Order pedido : batchList.get(currentBatchIndex).getOrders()) {
            for (Product producto : pedido.getProducts()) {
                if (lista_alturas_por_pasillo.containsKey(producto.getAisle())) {
                    ArrayList<Double> lista_alturas = lista_alturas_por_pasillo.get(producto.getAisle());
                    if (!lista_alturas.contains(producto.getHeightPosition())) {
                        lista_alturas.add(producto.getHeightPosition());
                    }
                } else {
                    ArrayList<Double> lista_alturas = new ArrayList<>();
                    lista_alturas.add(producto.getHeightPosition());
                    lista_alturas_por_pasillo.put(producto.getAisle(), lista_alturas);
                }
            }
        }

        double distancia_entre_pasillos = warehouse.getShelfWidth() + warehouse.getAisleWidth();
        int num_pasos = (warehouse.getNumberOfAisles() * 2) - 1;

        int pasillo_extra_depot = 0;
        boolean extra_pasillo = false;
        if (warehouse.getDepotPlacement() == DEPOT_CENTER) {
            if (warehouse.getNumberOfAisles() % 2 == 0) {
                num_pasos += 2;
                pasillo_extra_depot = (int) (warehouse.getNumberOfAisles() / 2.0) - 1;
                extra_pasillo = true;
            } else {
                pasillo_extra_depot = (warehouse.getNumberOfAisles() + 1) / 2;
            }
        }

        Double[][] tabla_result = new Double[num_pasos][7];
        int[][] tabla_path = new int[num_pasos][7];
        int[][] tabla_back = new int[num_pasos][7];
        double[][] tabla_values = new double[num_pasos][7];
        Double[][] tabla_valor_pasillo = new Double[num_pasos][6];
        double[] tabla_ini_gap = new double[warehouse.getNumberOfAisles()];
        double[] tabla_fin_gap = new double[warehouse.getNumberOfAisles()];

        boolean medio_pasillo = false;

        int paso = 0;
        for (int i = 0; i < warehouse.getNumberOfAisles(); i++) {
            //Calculo las distancias de cada pasillo paralelo
            double max_alt = -1;
            double min_alt = 0;
            double max_gap = 0;
            boolean has_pasillos = false;
            if (lista_alturas_por_pasillo.containsKey(i)) {
                has_pasillos = true;
                ArrayList<Double> lista_alturas = lista_alturas_por_pasillo.get(i);
                if (!extra_pasillo && i + 1 == pasillo_extra_depot && warehouse.getDepotPlacement() == DEPOT_CENTER) {
                    lista_alturas.add(0.0);
                }
                Collections.sort(lista_alturas);
                max_alt = lista_alturas.get(lista_alturas.size() - 1);
                min_alt = lista_alturas.get(0);
                max_gap = 0;
                double alt_p = -1;
                for (double altura : lista_alturas) {
                    double gap = altura - alt_p;
                    if (alt_p > -1 && gap > max_gap) {
                        max_gap = gap;
                        tabla_ini_gap[i] = alt_p;
                        tabla_fin_gap[i] = altura;
                    }
                    alt_p = altura;
                }
            } else if (!extra_pasillo && i + 1 == pasillo_extra_depot && warehouse.getDepotPlacement() == DEPOT_CENTER) {
                ArrayList<Double> lista_alturas = new ArrayList<>();
                lista_alturas.add(0.0);
                lista_alturas_por_pasillo.put(i, lista_alturas);
                has_pasillos = true;
            }

            Double[] path = new Double[6];
            path[0] = warehouse.getShelfLength() + warehouse.getAisleWidth();
            path[1] = ((warehouse.getShelfLength() - min_alt) * 2) + warehouse.getAisleWidth();
            if (max_alt == 0) {
                path[2] = 0.0;
            } else {
                path[2] = (max_alt * 2) + warehouse.getAisleWidth();
            }
            if (min_alt == 0) {
                path[3] = ((warehouse.getShelfLength() - max_gap) * 2) + warehouse.getAisleWidth();
            } else {
                path[3] = ((warehouse.getShelfLength() - max_gap + warehouse.getAisleWidth()) * 2);
            }
            path[4] = (warehouse.getShelfLength() + warehouse.getAisleWidth()) * 2;
            path[5] = 0.0;

            Double[] path_V = new Double[6];
            if (!medio_pasillo) {
                path_V[0] = (distancia_entre_pasillos * 2);
                path_V[1] = (distancia_entre_pasillos * 2);
                path_V[2] = (distancia_entre_pasillos * 2);
                path_V[3] = (distancia_entre_pasillos * 4);
                path_V[4] = 0.0;
                path_V[5] = 0.0;
            } else {
                path_V[0] = distancia_entre_pasillos;
                path_V[1] = distancia_entre_pasillos;
                path_V[2] = distancia_entre_pasillos;
                path_V[3] = (distancia_entre_pasillos * 2);
                path_V[4] = 0.0;
                path_V[5] = 0.0;
                medio_pasillo = false;
            }

            if (paso == 0) {
                tabla_valor_pasillo[paso] = path;
            } else {
                tabla_valor_pasillo[paso + 1] = path;
                tabla_valor_pasillo[paso] = path_V;
            }

            if (paso == 0) {
                //distancias pasillos paralelos
                tabla_result[paso][0] = path[0];
                tabla_values[paso][0] = path[0];
                tabla_path[paso][0] = 1;

                tabla_result[paso][1] = path[1];
                tabla_values[paso][1] = path[1];
                tabla_path[paso][1] = 2;

                tabla_result[paso][2] = path[2];
                tabla_values[paso][2] = path[2];
                tabla_path[paso][2] = 3;

                tabla_result[paso][3] = path[4];
                tabla_values[paso][3] = path[4];
                tabla_path[paso][3] = 5;

                tabla_result[paso][4] = path[3];
                tabla_values[paso][4] = path[3];
                tabla_path[paso][4] = 4;

                if (has_pasillos) {
                    tabla_result[paso][5] = -1.0;
                    tabla_values[paso][5] = path[5];
                    tabla_path[paso][5] = -1;
                } else {
                    tabla_result[paso][5] = path[5];
                    tabla_values[paso][5] = path[5];
                    tabla_path[paso][5] = 6;
                }

                tabla_result[paso][6] = -1.0;
                tabla_values[paso][6] = path[5];
                tabla_path[paso][6] = -1;

            } else {
                //distancias pasillos transversales
                tabla_result[paso][0] = tabla_result[paso - 1][0] + path_V[0];
                tabla_path[paso][0] = 1;
                tabla_back[paso][0] = 0;
                tabla_values[paso][0] = path_V[0];

                tabla_result[paso][1] = tabla_result[paso - 1][1] + path_V[1];
                tabla_path[paso][1] = 2;
                tabla_back[paso][1] = 1;
                tabla_values[paso][1] = path_V[1];

                tabla_result[paso][4] = tabla_result[paso - 1][1] + path_V[3];
                tabla_path[paso][4] = 4;
                tabla_back[paso][4] = 1;
                tabla_values[paso][4] = path_V[3];

                if (tabla_result[paso - 1][1] != null && tabla_result[paso - 1][1] != -1) {
                    tabla_result[paso][6] = tabla_result[paso - 1][1] + path_V[4];
                    tabla_path[paso][6] = 5;
                    tabla_back[paso][6] = 1;
                    tabla_values[paso][6] = path_V[4];
                }

                tabla_result[paso][2] = tabla_result[paso - 1][2] + path_V[2];
                tabla_path[paso][2] = 3;
                tabla_back[paso][2] = 2;
                tabla_values[paso][2] = path_V[2];

                if (tabla_result[paso][4] == null || tabla_result[paso][4] > tabla_result[paso - 1][2] + path_V[3]) {
                    tabla_result[paso][4] = tabla_result[paso - 1][2] + path_V[3];
                    tabla_path[paso][4] = 4;
                    tabla_back[paso][4] = 2;
                    tabla_values[paso][4] = path_V[3];
                }

                if (tabla_result[paso - 1][2] != null && tabla_result[paso - 1][2] != -1) {
                    if (tabla_result[paso][6] == null || tabla_result[paso][6] > tabla_result[paso - 1][2] + path_V[4]) {
                        tabla_result[paso][6] = tabla_result[paso - 1][2] + path_V[4];
                        tabla_path[paso][6] = 5;
                        tabla_back[paso][6] = 2;
                        tabla_values[paso][6] = path_V[4];
                    }
                }

                if (tabla_result[paso][1] == null || tabla_result[paso][1] > tabla_result[paso - 1][3] + path_V[1]) {
                    tabla_result[paso][1] = tabla_result[paso - 1][3] + path_V[1];
                    tabla_path[paso][1] = 2;
                    tabla_back[paso][1] = 3;
                    tabla_values[paso][1] = path_V[1];
                }

                if (tabla_result[paso][2] == null || tabla_result[paso][2] > tabla_result[paso - 1][3] + path_V[2]) {
                    tabla_result[paso][2] = tabla_result[paso - 1][3] + path_V[2];
                    tabla_path[paso][2] = 3;
                    tabla_back[paso][2] = 3;
                    tabla_values[paso][2] = path_V[2];
                }
                tabla_result[paso][3] = tabla_result[paso - 1][3] + path_V[3];
                tabla_path[paso][3] = 4;
                tabla_back[paso][3] = 3;
                tabla_values[paso][3] = path_V[3];

                if (tabla_result[paso - 1][3] != null && tabla_result[paso - 1][3] != -1) {
                    if (tabla_result[paso][6] == null || tabla_result[paso][6] > tabla_result[paso - 1][3] + path_V[4]) {
                        tabla_result[paso][6] = tabla_result[paso - 1][3] + path_V[4];
                        tabla_path[paso][6] = 5;
                        tabla_back[paso][6] = 3;
                        tabla_values[paso][6] = path_V[4];
                    }
                }

                if (tabla_result[paso][4] == null || tabla_result[paso][4] > tabla_result[paso - 1][4] + path_V[3]) {
                    tabla_result[paso][4] = tabla_result[paso - 1][4] + path_V[3];
                    tabla_path[paso][4] = 4;
                    tabla_back[paso][4] = 4;
                    tabla_values[paso][4] = path_V[3];
                }

                if (tabla_result[paso - 1][5] != null && tabla_result[paso - 1][5] != -1) {
                    tabla_result[paso][5] = tabla_result[paso - 1][5] + path_V[4];
                    tabla_path[paso][5] = 5;
                    tabla_back[paso][5] = 5;
                    tabla_values[paso][5] = path_V[4];
                }

                if (tabla_result[paso - 1][6] != null && tabla_result[paso - 1][6] != -1 && tabla_result[paso][6] > tabla_result[paso - 1][6] + path_V[4]) {
                    tabla_result[paso][6] = tabla_result[paso - 1][6] + path_V[4];
                    tabla_path[paso][6] = 5;
                    tabla_back[paso][6] = 6;
                    tabla_values[paso][6] = path_V[4];
                }

                int select_index = 0;
                double select_value = Double.MAX_VALUE;

                int max_path = path.length;
                if (has_pasillos) {
                    max_path--;
                }

                for (int j = 1; j < max_path; j++) {
                    if (path[j] < select_value) {
                        select_value = path[j];
                        select_index = j;
                    }
                }
                select_index++;

                paso++;
                //System.out.println("paso:" + paso);
                //distancias pasillos paralelos
                tabla_result[paso][3] = tabla_result[paso - 1][0] + path[0];
                tabla_path[paso][3] = 1;
                tabla_back[paso][3] = 0;
                tabla_values[paso][3] = path[0];

                tabla_result[paso][0] = tabla_result[paso - 1][0] + select_value;
                tabla_path[paso][0] = select_index;
                tabla_back[paso][0] = 0;
                tabla_values[paso][0] = select_value;

                //System.out.println("--> Paso: " + paso + " - select_value:" + select_value + " - select_index:" + select_index);
                if (!has_pasillos) {
                    if (tabla_result[paso][0] > tabla_result[paso - 1][0] + path[5]) {
                        tabla_result[paso][0] = tabla_result[paso - 1][0] + path[5];
                        tabla_path[paso][0] = 6;
                        tabla_back[paso][0] = 0;
                        tabla_values[paso][3] = path[5];
                    }
                }

                if (tabla_result[paso][0] == null || tabla_result[paso][0] > tabla_result[paso - 1][1] + path[0]) {
                    tabla_result[paso][0] = tabla_result[paso - 1][1] + path[0];
                    tabla_path[paso][0] = 1;
                    tabla_back[paso][0] = 1;
                    tabla_values[paso][0] = path[0];
                }

                tabla_result[paso][1] = tabla_result[paso - 1][1] + path[1];
                tabla_path[paso][1] = 2;
                tabla_back[paso][1] = 1;
                tabla_values[paso][1] = path[1];

                tabla_result[paso][4] = tabla_result[paso - 1][1] + path[2];
                tabla_path[paso][4] = 3;
                tabla_back[paso][4] = 1;
                tabla_values[paso][4] = path[2];

                if (tabla_result[paso][4] == null || tabla_result[paso][4] > tabla_result[paso - 1][1] + path[3]) {
                    tabla_result[paso][4] = tabla_result[paso - 1][1] + path[3];
                    tabla_path[paso][4] = 4;
                    tabla_back[paso][4] = 1;
                    tabla_values[paso][4] = path[3];
                }

                if (tabla_result[paso][3] == null || tabla_result[paso][3] > tabla_result[paso - 1][1] + path[4]) {
                    tabla_result[paso][3] = tabla_result[paso - 1][1] + path[4];
                    tabla_path[paso][3] = 5;
                    tabla_back[paso][3] = 1;
                    tabla_values[paso][3] = path[4];
                }

                if (!has_pasillos) {
                    if (tabla_result[paso][1] == null || tabla_result[paso][1] > tabla_result[paso - 1][1] + path[5]) {
                        tabla_result[paso][1] = tabla_result[paso - 1][1] + path[5];
                        tabla_path[paso][1] = 6;
                        tabla_back[paso][1] = 1;
                        tabla_values[paso][1] = path[5];
                    }
                }

                if (tabla_result[paso][0] == null || tabla_result[paso][0] > tabla_result[paso - 1][2] + path[0]) {
                    tabla_result[paso][0] = tabla_result[paso - 1][2] + path[0];
                    tabla_path[paso][0] = 1;
                    tabla_back[paso][0] = 2;
                    tabla_values[paso][0] = path[0];
                }

                if (tabla_result[paso][4] == null || tabla_result[paso][4] > tabla_result[paso - 1][2] + path[1]) {
                    tabla_result[paso][4] = tabla_result[paso - 1][2] + path[1];
                    tabla_path[paso][4] = 2;
                    tabla_back[paso][4] = 2;
                    tabla_values[paso][4] = path[1];
                }

                tabla_result[paso][2] = tabla_result[paso - 1][2] + path[2];
                tabla_path[paso][2] = 3;
                tabla_back[paso][2] = 2;
                tabla_values[paso][2] = path[2];

                if (tabla_result[paso][4] == null || tabla_result[paso][4] > tabla_result[paso - 1][2] + path[3]) {
                    tabla_result[paso][4] = tabla_result[paso - 1][2] + path[3];
                    tabla_path[paso][4] = 4;
                    tabla_back[paso][4] = 2;
                    tabla_values[paso][4] = path[3];
                }

                if (tabla_result[paso][3] == null || tabla_result[paso][3] > tabla_result[paso - 1][2] + path[4]) {
                    tabla_result[paso][3] = tabla_result[paso - 1][2] + path[4];
                    tabla_path[paso][3] = 5;
                    tabla_back[paso][3] = 2;
                    tabla_values[paso][3] = path[4];
                }

                if (!has_pasillos) {
                    if (tabla_result[paso][2] == null || tabla_result[paso][2] > tabla_result[paso - 1][2] + path[5]) {
                        tabla_result[paso][2] = tabla_result[paso - 1][2] + path[5];
                        tabla_path[paso][2] = 6;
                        tabla_back[paso][2] = 2;
                        tabla_values[paso][2] = path[5];
                    }
                }

                if (tabla_result[paso][0] == null || tabla_result[paso][0] > tabla_result[paso - 1][3] + path[0]) {
                    tabla_result[paso][0] = tabla_result[paso - 1][3] + path[0];
                    tabla_path[paso][0] = 1;
                    tabla_back[paso][0] = 3;
                    tabla_values[paso][0] = path[0];
                }

                if (tabla_result[paso][3] == null || tabla_result[paso][3] > tabla_result[paso - 1][3] + select_value) {
                    tabla_result[paso][3] = tabla_result[paso - 1][3] + select_value;
                    tabla_path[paso][3] = select_index;
                    tabla_back[paso][3] = 3;
                    tabla_values[paso][3] = select_value;
                }

                if (!has_pasillos) {
                    if (tabla_result[paso][3] == null || tabla_result[paso][3] > tabla_result[paso - 1][3] + path[5]) {
                        tabla_result[paso][3] = tabla_result[paso - 1][3] + path[5];
                        tabla_path[paso][3] = 6;
                        tabla_back[paso][3] = 3;
                        tabla_values[paso][3] = path[5];
                    }
                }

                if (tabla_result[paso][0] == null || tabla_result[paso][0] > tabla_result[paso - 1][4] + path[0]) {
                    tabla_result[paso][0] = tabla_result[paso - 1][4] + path[0];
                    tabla_path[paso][0] = 1;
                    tabla_back[paso][0] = 4;
                    tabla_values[paso][0] = path[0];
                }

                if (tabla_result[paso][4] == null || tabla_result[paso][4] > tabla_result[paso - 1][4] + path[1]) {
                    tabla_result[paso][4] = tabla_result[paso - 1][4] + path[1];
                    tabla_path[paso][4] = 2;
                    tabla_back[paso][4] = 4;
                    tabla_values[paso][4] = path[1];
                }

                if (tabla_result[paso][4] == null || tabla_result[paso][4] > tabla_result[paso - 1][4] + path[2]) {
                    tabla_result[paso][4] = tabla_result[paso - 1][4] + path[2];
                    tabla_path[paso][4] = 3;
                    tabla_back[paso][4] = 4;
                    tabla_values[paso][4] = path[2];
                }

                if (tabla_result[paso][4] == null || tabla_result[paso][4] > tabla_result[paso - 1][4] + path[3]) {
                    tabla_result[paso][4] = tabla_result[paso - 1][4] + path[3];
                    tabla_path[paso][4] = 4;
                    tabla_back[paso][4] = 4;
                    tabla_values[paso][4] = path[3];
                }

                if (tabla_result[paso][3] == null || tabla_result[paso][3] > tabla_result[paso - 1][4] + path[4]) {
                    tabla_result[paso][3] = tabla_result[paso - 1][4] + path[4];
                    tabla_path[paso][3] = 5;
                    tabla_back[paso][3] = 4;
                    tabla_values[paso][3] = path[4];
                }

                if (!has_pasillos) {
                    if (tabla_result[paso][4] == null || tabla_result[paso][4] > tabla_result[paso - 1][4]) {
                        tabla_result[paso][4] = tabla_result[paso - 1][4];
                        tabla_path[paso][4] = 6;
                        tabla_back[paso][4] = 4;
                        tabla_values[paso][4] = path[4];
                    }
                }

                if (tabla_result[paso - 1][5] != null && tabla_result[paso - 1][5] != -1 && tabla_result[paso][0] > tabla_result[paso - 1][5] + path[0]) {
                    tabla_result[paso][0] = tabla_result[paso - 1][5] + path[0];
                    tabla_path[paso][0] = 1;
                    tabla_back[paso][0] = 5;
                    tabla_values[paso][0] = path[0];
                }

                if (tabla_result[paso - 1][5] != null && tabla_result[paso - 1][5] != -1 && tabla_result[paso][1] > tabla_result[paso - 1][5] + path[1]) {
                    tabla_result[paso][1] = tabla_result[paso - 1][5] + path[1];
                    tabla_path[paso][1] = 2;
                    tabla_back[paso][1] = 5;
                    tabla_values[paso][1] = path[1];
                }

                if (tabla_result[paso - 1][5] != null && tabla_result[paso - 1][5] != -1 && tabla_result[paso][2] > tabla_result[paso - 1][5] + path[2]) {
                    tabla_result[paso][2] = tabla_result[paso - 1][5] + path[2];
                    tabla_path[paso][2] = 3;
                    tabla_back[paso][2] = 5;
                    tabla_values[paso][2] = path[2];
                }

                if (tabla_result[paso - 1][5] != null && tabla_result[paso - 1][5] != -1 && tabla_result[paso][4] > tabla_result[paso - 1][5] + path[3]) {
                    tabla_result[paso][4] = tabla_result[paso - 1][5] + path[3];
                    tabla_path[paso][4] = 4;
                    tabla_back[paso][4] = 5;
                    tabla_values[paso][4] = path[3];
                }

                if (tabla_result[paso - 1][5] != null && tabla_result[paso - 1][5] != -1 && tabla_result[paso][3] > tabla_result[paso - 1][5] + path[4]) {
                    tabla_result[paso][3] = tabla_result[paso - 1][5] + path[4];
                    tabla_path[paso][3] = 5;
                    tabla_back[paso][3] = 5;
                    tabla_values[paso][3] = path[4];
                }

                if (!has_pasillos) {
                    if (tabla_result[paso - 1][5] != null && tabla_result[paso - 1][5] != -1 && tabla_result[paso][5] == null) {
                        tabla_result[paso][5] = tabla_result[paso - 1][5] + path[5];
                        tabla_path[paso][5] = 6;
                        tabla_back[paso][5] = 5;
                        tabla_values[paso][5] = path[5];
                    } else if (tabla_result[paso - 1][5] != null && tabla_result[paso - 1][5] != -1 && tabla_result[paso][5] > tabla_result[paso - 1][5] + path[5]) {
                        tabla_result[paso][5] = tabla_result[paso - 1][5] + path[5];
                        tabla_path[paso][5] = 6;
                        tabla_back[paso][5] = 5;
                        tabla_values[paso][5] = path[5];
                    }
                }

                if (!has_pasillos) {
                    if (tabla_result[paso - 1][6] != null && tabla_result[paso - 1][6] != -1 && tabla_result[paso][6] == null) {
                        tabla_result[paso][6] = tabla_result[paso - 1][6] + path[5];
                        tabla_path[paso][6] = 6;
                        tabla_back[paso][6] = 6;
                        tabla_values[paso][6] = path[5];
                    } else if (tabla_result[paso - 1][6] != null && tabla_result[paso - 1][6] != -1 && tabla_result[paso][6] > tabla_result[paso - 1][6] + path[5]) {
                        tabla_result[paso][6] = tabla_result[paso - 1][6] + path[5];
                        tabla_path[paso][6] = 6;
                        tabla_back[paso][6] = 6;
                        tabla_values[paso][6] = path[5];
                    }
                }

            }
            paso++;

            //System.out.println("pasillo_extra_depot:" + pasillo_extra_depot + " - Pasillo: " + i);
            if (extra_pasillo && pasillo_extra_depot == i) {

                has_pasillos = true;

                path = new Double[6];
                path[0] = warehouse.getShelfLength() + warehouse.getAisleWidth();
                path[1] = (warehouse.getShelfLength() * 2) + warehouse.getAisleWidth();
                path[2] = 0.0;
                path[3] = (warehouse.getShelfLength() + warehouse.getAisleWidth()) * 2;
                path[4] = (warehouse.getShelfLength() + warehouse.getAisleWidth()) * 2;
                path[5] = 0.0;

                path_V = new Double[6];
                path_V[0] = distancia_entre_pasillos;
                path_V[1] = distancia_entre_pasillos;
                path_V[2] = distancia_entre_pasillos;
                path_V[3] = (distancia_entre_pasillos * 2);
                path_V[4] = 0.0;
                path_V[5] = 0.0;

                tabla_valor_pasillo[paso + 1] = path;
                tabla_valor_pasillo[paso] = path_V;

                medio_pasillo = true;

                //distancias pasillos transversales
                tabla_result[paso][0] = tabla_result[paso - 1][0] + path_V[0];
                tabla_path[paso][0] = 1;
                tabla_back[paso][0] = 0;
                tabla_values[paso][0] = path_V[0];

                tabla_result[paso][1] = tabla_result[paso - 1][1] + path_V[1];
                tabla_path[paso][1] = 2;
                tabla_back[paso][1] = 1;
                tabla_values[paso][1] = path_V[1];

                tabla_result[paso][4] = tabla_result[paso - 1][1] + path_V[3];
                tabla_path[paso][4] = 4;
                tabla_back[paso][4] = 1;
                tabla_values[paso][4] = path_V[3];

                if (tabla_result[paso - 1][1] != null && tabla_result[paso - 1][1] != -1) {
                    tabla_result[paso][6] = tabla_result[paso - 1][1] + path_V[4];
                    tabla_path[paso][6] = 5;
                    tabla_back[paso][6] = 1;
                    tabla_values[paso][6] = path_V[4];
                }

                tabla_result[paso][2] = tabla_result[paso - 1][2] + path_V[2];
                tabla_path[paso][2] = 3;
                tabla_back[paso][2] = 2;
                tabla_values[paso][2] = path_V[2];

                if (tabla_result[paso][4] == null || tabla_result[paso][4] > tabla_result[paso - 1][2] + path_V[3]) {
                    tabla_result[paso][4] = tabla_result[paso - 1][2] + path_V[3];
                    tabla_path[paso][4] = 4;
                    tabla_back[paso][4] = 2;
                    tabla_values[paso][4] = path_V[3];
                }

                if (tabla_result[paso - 1][2] != null && tabla_result[paso - 1][2] != -1) {
                    if (tabla_result[paso][6] == null || tabla_result[paso][6] > tabla_result[paso - 1][2] + path_V[4]) {
                        tabla_result[paso][6] = tabla_result[paso - 1][2] + path_V[4];
                        tabla_path[paso][6] = 5;
                        tabla_back[paso][6] = 2;
                        tabla_values[paso][6] = path_V[4];
                    }
                }

                if (tabla_result[paso][1] == null || tabla_result[paso][1] > tabla_result[paso - 1][3] + path_V[1]) {
                    tabla_result[paso][1] = tabla_result[paso - 1][3] + path_V[1];
                    tabla_path[paso][1] = 2;
                    tabla_back[paso][1] = 3;
                    tabla_values[paso][1] = path_V[1];
                }

                if (tabla_result[paso][2] == null || tabla_result[paso][2] > tabla_result[paso - 1][3] + path_V[2]) {
                    tabla_result[paso][2] = tabla_result[paso - 1][3] + path_V[2];
                    tabla_path[paso][2] = 3;
                    tabla_back[paso][2] = 3;
                    tabla_values[paso][2] = path_V[2];
                }
                tabla_result[paso][3] = tabla_result[paso - 1][3] + path_V[3];
                tabla_path[paso][3] = 4;
                tabla_back[paso][3] = 3;
                tabla_values[paso][3] = path_V[3];

                if (tabla_result[paso - 1][3] != null && tabla_result[paso - 1][3] != -1) {
                    if (tabla_result[paso][6] == null || tabla_result[paso][6] > tabla_result[paso - 1][3] + path_V[4]) {
                        tabla_result[paso][6] = tabla_result[paso - 1][3] + path_V[4];
                        tabla_path[paso][6] = 5;
                        tabla_back[paso][6] = 3;
                        tabla_values[paso][6] = path_V[4];
                    }
                }

                if (tabla_result[paso][4] == null || tabla_result[paso][4] > tabla_result[paso - 1][4] + path_V[3]) {
                    tabla_result[paso][4] = tabla_result[paso - 1][4] + path_V[3];
                    tabla_path[paso][4] = 4;
                    tabla_back[paso][4] = 4;
                    tabla_values[paso][4] = path_V[3];
                }

                if (tabla_result[paso - 1][5] != null && tabla_result[paso - 1][5] != -1) {
                    tabla_result[paso][5] = tabla_result[paso - 1][5] + path_V[4];
                    tabla_path[paso][5] = 5;
                    tabla_back[paso][5] = 5;
                    tabla_values[paso][5] = path_V[4];
                }

                if (tabla_result[paso - 1][6] != null && tabla_result[paso - 1][6] != -1 && tabla_result[paso][6] > tabla_result[paso - 1][6] + path_V[4]) {
                    tabla_result[paso][6] = tabla_result[paso - 1][6] + path_V[4];
                    tabla_path[paso][6] = 5;
                    tabla_back[paso][6] = 6;
                    tabla_values[paso][6] = path_V[4];
                }

                int select_index = 0;
                double select_value = Double.MAX_VALUE;

                int max_path = path.length;
                if (has_pasillos) {
                    max_path--;
                }

                for (int j = 1; j < max_path; j++) {
                    if (path[j] < select_value) {
                        select_value = path[j];
                        select_index = j;
                    }
                }
                select_index++;

                paso++;
                //System.out.println("paso:" + paso);
                //distancias pasillos paralelos
                tabla_result[paso][3] = tabla_result[paso - 1][0] + path[0];
                tabla_path[paso][3] = 1;
                tabla_back[paso][3] = 0;
                tabla_values[paso][3] = path[0];

                tabla_result[paso][0] = tabla_result[paso - 1][0] + select_value;
                tabla_path[paso][0] = select_index;
                tabla_back[paso][0] = 0;
                tabla_values[paso][0] = select_value;

                if (!has_pasillos) {
                    if (tabla_result[paso][0] > tabla_result[paso - 1][0] + path[5]) {
                        tabla_result[paso][0] = tabla_result[paso - 1][0] + path[5];
                        tabla_path[paso][0] = 6;
                        tabla_back[paso][0] = 0;
                        tabla_values[paso][3] = path[5];
                    }
                }

                if (tabla_result[paso][0] == null || tabla_result[paso][0] > tabla_result[paso - 1][1] + path[0]) {
                    tabla_result[paso][0] = tabla_result[paso - 1][1] + path[0];
                    tabla_path[paso][0] = 1;
                    tabla_back[paso][0] = 1;
                    tabla_values[paso][0] = path[0];
                }

                tabla_result[paso][1] = tabla_result[paso - 1][1] + path[1];
                tabla_path[paso][1] = 2;
                tabla_back[paso][1] = 1;
                tabla_values[paso][1] = path[1];

                tabla_result[paso][4] = tabla_result[paso - 1][1] + path[2];
                tabla_path[paso][4] = 3;
                tabla_back[paso][4] = 1;
                tabla_values[paso][4] = path[2];

                if (tabla_result[paso][4] == null || tabla_result[paso][4] > tabla_result[paso - 1][1] + path[3]) {
                    tabla_result[paso][4] = tabla_result[paso - 1][1] + path[3];
                    tabla_path[paso][4] = 4;
                    tabla_back[paso][4] = 1;
                    tabla_values[paso][4] = path[3];
                }

                if (tabla_result[paso][3] == null || tabla_result[paso][3] > tabla_result[paso - 1][1] + path[4]) {
                    tabla_result[paso][3] = tabla_result[paso - 1][1] + path[4];
                    tabla_path[paso][3] = 5;
                    tabla_back[paso][3] = 1;
                    tabla_values[paso][3] = path[4];
                }

                if (!has_pasillos) {
                    if (tabla_result[paso][1] == null || tabla_result[paso][1] > tabla_result[paso - 1][1] + path[5]) {
                        tabla_result[paso][1] = tabla_result[paso - 1][1] + path[5];
                        tabla_path[paso][1] = 6;
                        tabla_back[paso][1] = 1;
                        tabla_values[paso][1] = path[5];
                    }
                }

                if (tabla_result[paso][0] == null || tabla_result[paso][0] > tabla_result[paso - 1][2] + path[0]) {
                    tabla_result[paso][0] = tabla_result[paso - 1][2] + path[0];
                    tabla_path[paso][0] = 1;
                    tabla_back[paso][0] = 2;
                    tabla_values[paso][0] = path[0];
                }

                if (tabla_result[paso][4] == null || tabla_result[paso][4] > tabla_result[paso - 1][2] + path[1]) {
                    tabla_result[paso][4] = tabla_result[paso - 1][2] + path[1];
                    tabla_path[paso][4] = 2;
                    tabla_back[paso][4] = 2;
                    tabla_values[paso][4] = path[1];
                }

                tabla_result[paso][2] = tabla_result[paso - 1][2] + path[2];
                tabla_path[paso][2] = 3;
                tabla_back[paso][2] = 2;
                tabla_values[paso][2] = path[2];

                if (tabla_result[paso][4] == null || tabla_result[paso][4] > tabla_result[paso - 1][2] + path[3]) {
                    tabla_result[paso][4] = tabla_result[paso - 1][2] + path[3];
                    tabla_path[paso][4] = 4;
                    tabla_back[paso][4] = 2;
                    tabla_values[paso][4] = path[3];
                }

                if (tabla_result[paso][3] == null || tabla_result[paso][3] > tabla_result[paso - 1][2] + path[4]) {
                    tabla_result[paso][3] = tabla_result[paso - 1][2] + path[4];
                    tabla_path[paso][3] = 5;
                    tabla_back[paso][3] = 2;
                    tabla_values[paso][3] = path[4];
                }

                if (!has_pasillos) {
                    if (tabla_result[paso][2] == null || tabla_result[paso][2] > tabla_result[paso - 1][2] + path[5]) {
                        tabla_result[paso][2] = tabla_result[paso - 1][2] + path[5];
                        tabla_path[paso][2] = 6;
                        tabla_back[paso][2] = 2;
                        tabla_values[paso][2] = path[5];
                    }
                }

                if (tabla_result[paso][0] == null || tabla_result[paso][0] > tabla_result[paso - 1][3] + path[0]) {
                    tabla_result[paso][0] = tabla_result[paso - 1][3] + path[0];
                    tabla_path[paso][0] = 1;
                    tabla_back[paso][0] = 3;
                    tabla_values[paso][0] = path[0];
                }

                if (tabla_result[paso][3] == null || tabla_result[paso][3] > tabla_result[paso - 1][3] + select_value) {
                    tabla_result[paso][3] = tabla_result[paso - 1][3] + select_value;
                    tabla_path[paso][3] = select_index;
                    tabla_back[paso][3] = 3;
                    tabla_values[paso][3] = select_value;
                }

                if (!has_pasillos) {
                    if (tabla_result[paso][3] == null || tabla_result[paso][3] > tabla_result[paso - 1][3] + path[5]) {
                        tabla_result[paso][3] = tabla_result[paso - 1][3] + path[5];
                        tabla_path[paso][3] = 6;
                        tabla_back[paso][3] = 3;
                        tabla_values[paso][3] = path[5];
                    }
                }

                if (tabla_result[paso][0] == null || tabla_result[paso][0] > tabla_result[paso - 1][4] + path[0]) {
                    tabla_result[paso][0] = tabla_result[paso - 1][4] + path[0];
                    tabla_path[paso][0] = 1;
                    tabla_back[paso][0] = 4;
                    tabla_values[paso][0] = path[0];
                }

                if (tabla_result[paso][4] == null || tabla_result[paso][4] > tabla_result[paso - 1][4] + path[1]) {
                    tabla_result[paso][4] = tabla_result[paso - 1][4] + path[1];
                    tabla_path[paso][4] = 2;
                    tabla_back[paso][4] = 4;
                    tabla_values[paso][4] = path[1];
                }

                if (tabla_result[paso][4] == null || tabla_result[paso][4] > tabla_result[paso - 1][4] + path[2]) {
                    tabla_result[paso][4] = tabla_result[paso - 1][4] + path[2];
                    tabla_path[paso][4] = 3;
                    tabla_back[paso][4] = 4;
                    tabla_values[paso][4] = path[2];
                }

                if (tabla_result[paso][4] == null || tabla_result[paso][4] > tabla_result[paso - 1][4] + path[3]) {
                    tabla_result[paso][4] = tabla_result[paso - 1][4] + path[3];
                    tabla_path[paso][4] = 4;
                    tabla_back[paso][4] = 4;
                    tabla_values[paso][4] = path[3];
                }

                if (tabla_result[paso][3] == null || tabla_result[paso][3] > tabla_result[paso - 1][4] + path[4]) {
                    tabla_result[paso][3] = tabla_result[paso - 1][4] + path[4];
                    tabla_path[paso][3] = 5;
                    tabla_back[paso][3] = 4;
                    tabla_values[paso][3] = path[4];
                }

                if (!has_pasillos) {
                    if (tabla_result[paso][4] == null || tabla_result[paso][4] > tabla_result[paso - 1][4]) {
                        tabla_result[paso][4] = tabla_result[paso - 1][4];
                        tabla_path[paso][4] = 6;
                        tabla_back[paso][4] = 4;
                        tabla_values[paso][4] = path[4];
                    }
                }

                if (tabla_result[paso - 1][5] != null && tabla_result[paso - 1][5] != -1 && tabla_result[paso][0] > tabla_result[paso - 1][5] + path[0]) {
                    tabla_result[paso][0] = tabla_result[paso - 1][5] + path[0];
                    tabla_path[paso][0] = 1;
                    tabla_back[paso][0] = 5;
                    tabla_values[paso][0] = path[0];
                }

                if (tabla_result[paso - 1][5] != null && tabla_result[paso - 1][5] != -1 && tabla_result[paso][1] > tabla_result[paso - 1][5] + path[1]) {
                    tabla_result[paso][1] = tabla_result[paso - 1][5] + path[1];
                    tabla_path[paso][1] = 2;
                    tabla_back[paso][1] = 5;
                    tabla_values[paso][1] = path[1];
                }

                if (tabla_result[paso - 1][5] != null && tabla_result[paso - 1][5] != -1 && tabla_result[paso][2] > tabla_result[paso - 1][5] + path[2]) {
                    tabla_result[paso][2] = tabla_result[paso - 1][5] + path[2];
                    tabla_path[paso][2] = 3;
                    tabla_back[paso][2] = 5;
                    tabla_values[paso][2] = path[2];
                }

                if (tabla_result[paso - 1][5] != null && tabla_result[paso - 1][5] != -1 && tabla_result[paso][4] > tabla_result[paso - 1][5] + path[3]) {
                    tabla_result[paso][4] = tabla_result[paso - 1][5] + path[3];
                    tabla_path[paso][4] = 4;
                    tabla_back[paso][4] = 5;
                    tabla_values[paso][4] = path[3];
                }

                if (tabla_result[paso - 1][5] != null && tabla_result[paso - 1][5] != -1 && tabla_result[paso][3] > tabla_result[paso - 1][5] + path[4]) {
                    tabla_result[paso][3] = tabla_result[paso - 1][5] + path[4];
                    tabla_path[paso][3] = 5;
                    tabla_back[paso][3] = 5;
                    tabla_values[paso][3] = path[4];
                }

                if (!has_pasillos) {
                    if (tabla_result[paso - 1][5] != null && tabla_result[paso - 1][5] != -1 && tabla_result[paso][5] == null) {
                        tabla_result[paso][5] = tabla_result[paso - 1][5] + path[5];
                        tabla_path[paso][5] = 6;
                        tabla_back[paso][5] = 5;
                        tabla_values[paso][5] = path[5];
                    } else if (tabla_result[paso - 1][5] != null && tabla_result[paso - 1][5] != -1 && tabla_result[paso][5] > tabla_result[paso - 1][5] + path[5]) {
                        tabla_result[paso][5] = tabla_result[paso - 1][5] + path[5];
                        tabla_path[paso][5] = 6;
                        tabla_back[paso][5] = 5;
                        tabla_values[paso][5] = path[5];
                    }
                }

                if (!has_pasillos) {
                    if (tabla_result[paso - 1][6] != null && tabla_result[paso - 1][6] != -1 && tabla_result[paso][6] == null) {
                        tabla_result[paso][6] = tabla_result[paso - 1][6] + path[5];
                        tabla_path[paso][6] = 6;
                        tabla_back[paso][6] = 6;
                        tabla_values[paso][6] = path[5];
                    } else if (tabla_result[paso - 1][6] != null && tabla_result[paso - 1][6] != -1 && tabla_result[paso][6] > tabla_result[paso - 1][6] + path[5]) {
                        tabla_result[paso][6] = tabla_result[paso - 1][6] + path[5];
                        tabla_path[paso][6] = 6;
                        tabla_back[paso][6] = 6;
                        tabla_values[paso][6] = path[5];
                    }
                }

                paso++;
            }

        }
        paso--;
        double min = Double.MAX_VALUE;
        int index_back = -1;
        for (int j = 0; j < tabla_result[paso].length; j++) {
            Double pa = tabla_result[paso][j];
            if (pa != null) {
                if (pa > 0 && min > pa && j != 0 && j != 4 && j != 5) {
                    min = pa;
                    index_back = j;
                }
            }
        }

        double unidad_ancho_y = warehouse.getShelfWidth() + warehouse.getAisleWidth();

        int x_pos_alta = (int) Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf);
        int x_pos_baja = (int) (Math.round((warehouse.getAisleWidth() + warehouse.getShelfLength()) * scaleMultiplier) + x_pos_alta);

        int n_pasillos = warehouse.getNumberOfAisles();
        if (extra_pasillo) {
            n_pasillos++;
        }

        int[] y_pos_pasillos = new int[n_pasillos];

        y_pos_pasillos[0] = (int) Math.round(unidad_ancho_y * scaleMultiplierHalf);
        boolean entra = false;
        for (int i = 1; i < n_pasillos; i++) {
            if (extra_pasillo && pasillo_extra_depot + 1 == i) {
                y_pos_pasillos[i] = (int) (Math.round(unidad_ancho_y * scaleMultiplierHalf) + y_pos_pasillos[i - 1]);
                entra = true;
            } else if (entra) {
                y_pos_pasillos[i] = (int) (Math.round(unidad_ancho_y * scaleMultiplierHalf) + y_pos_pasillos[i - 1]);
                entra = false;
            } else {
                y_pos_pasillos[i] = (int) (Math.round(unidad_ancho_y * scaleMultiplier) + y_pos_pasillos[i - 1]);
            }
        }
        int pasillo = warehouse.getNumberOfAisles() - 1;
        g.setColor(routesColor);
        int para_pasillo = 0;
        boolean drawSSAisleH = false;
        boolean drawTraversal = false;
        for (int i = paso; i >= 0; i--) {
            if (i % 2 == 0) {
                switch (tabla_path[i][index_back]) {
                    case 1 -> {
                        //System.out.println("Paso: " + i + " - Paralelo: 1 - Pasillo: (" + pasillo + ") " + (i / 2) + " - Value: " + tabla_values[i][index_back] + " - BACK: " + tabla_back[i][index_back]);
                        drawLine(g, x_pos_alta, y_pos_pasillos[(i / 2)], x_pos_baja, y_pos_pasillos[(i / 2)], graphicPosition);
                        if (!drawSSAisleH) {
                            g.setColor(measuresColor);
                            drawLineMeasureH(g, formatString(tabla_valor_pasillo[i][0]), x_pos_alta, x_pos_baja, y_pos_pasillos[(i / 2)] + 10, graphicPosition);
                            g.setColor(routesColor);
                            drawSSAisleH = true;
                        }
                    }
                    case 2 -> {
                        ArrayList<Double> lista_alturas = lista_alturas_por_pasillo.get(pasillo);
                        Collections.sort(lista_alturas);
                        int min_alt = (int) (Math.round((warehouse.getAisleWidth() + lista_alturas.get(0)) * scaleMultiplier));
                        drawLine(g, x_pos_baja, y_pos_pasillos[(i / 2)], min_alt, y_pos_pasillos[(i / 2)], graphicPosition);
                        drawLine(g, x_pos_baja, y_pos_pasillos[(i / 2)] + 5, min_alt, y_pos_pasillos[(i / 2)] + 5, graphicPosition);
                        g.setColor(measuresColor);
                        drawLineMeasureH(g, formatString(tabla_valor_pasillo[i][1] / 2.0), x_pos_baja, min_alt, y_pos_pasillos[(i / 2)] - 10, graphicPosition);
                        g.setColor(routesColor);
                    }
                    case 3 -> {
                        if (tabla_values[i][index_back] > 0) {
                            ArrayList<Double> lista_alturas = lista_alturas_por_pasillo.get(pasillo);

                            Collections.sort(lista_alturas);
                            double p_elemento = lista_alturas.get(lista_alturas.size() - 1);
                            if (p_elemento > 0) {
                                int max_alt = (int) (Math.round((warehouse.getAisleWidth() + p_elemento) * scaleMultiplier));
                                drawLine(g, max_alt, y_pos_pasillos[(i / 2)], x_pos_alta, y_pos_pasillos[(i / 2)], graphicPosition);
                                drawLine(g, max_alt, y_pos_pasillos[(i / 2)] + 5, x_pos_alta, y_pos_pasillos[(i / 2)] + 5, graphicPosition);
                                g.setColor(measuresColor);
                                drawLineMeasureH(g, formatString(tabla_valor_pasillo[i][2] / 2.0), max_alt, x_pos_alta, y_pos_pasillos[(i / 2)] - 10, graphicPosition);
                                g.setColor(routesColor);

                            }
                        }
                    }
                    case 4 -> {
                        int min_alt = (int) (Math.round((warehouse.getAisleWidth() + tabla_ini_gap[pasillo]) * scaleMultiplier));
                        int max_alt = (int) (Math.round((warehouse.getAisleWidth() + tabla_fin_gap[pasillo]) * scaleMultiplier));
                        ArrayList<Double> lista_alturas = lista_alturas_por_pasillo.get(pasillo);
                        Collections.sort(lista_alturas);
                        double p_elemento = lista_alturas.get(0);
                        if (p_elemento > 0) {
                            drawLine(g, min_alt, y_pos_pasillos[(i / 2)], x_pos_alta, y_pos_pasillos[(i / 2)], graphicPosition);
                            drawLine(g, min_alt, y_pos_pasillos[(i / 2)] + 5, x_pos_alta, y_pos_pasillos[(i / 2)] + 5, graphicPosition);
                            g.setColor(measuresColor);
                            drawLineMeasureH(g, formatString((warehouse.getAisleWidth() / 2.0) + tabla_ini_gap[pasillo]), min_alt, x_pos_alta, y_pos_pasillos[(i / 2)] - 10, graphicPosition);
                            g.setColor(routesColor);
                        }
                        drawLine(g, max_alt, y_pos_pasillos[(i / 2)], x_pos_baja, y_pos_pasillos[(i / 2)], graphicPosition);
                        drawLine(g, max_alt, y_pos_pasillos[(i / 2)] + 5, x_pos_baja, y_pos_pasillos[(i / 2)] + 5, graphicPosition);
                        g.setColor(measuresColor);
                        drawLineMeasureH(g, formatString((warehouse.getAisleWidth() / 2.0) + warehouse.getShelfLength() - tabla_fin_gap[pasillo]), max_alt, x_pos_baja, y_pos_pasillos[(i / 2)] - 10, graphicPosition);
                        g.setColor(routesColor);
                    }
                    case 5 -> {
                        drawLine(g, x_pos_alta, y_pos_pasillos[(i / 2)], x_pos_baja, y_pos_pasillos[(i / 2)], graphicPosition);
                        drawLine(g, x_pos_alta, y_pos_pasillos[(i / 2)] + 5, x_pos_baja, y_pos_pasillos[(i / 2)] + 5, graphicPosition);
                        g.setColor(measuresColor);
                        drawLineMeasureH(g, formatString(tabla_valor_pasillo[i][0]), x_pos_alta, x_pos_baja, y_pos_pasillos[(i / 2)] - 10, graphicPosition);
                        g.setColor(routesColor);
                    }
                    default -> {
                    }
                }
            } else {
                if (tabla_path[i][index_back] == 1) {
                    drawLine(g, x_pos_alta, y_pos_pasillos[((i + 1) / 2)], x_pos_alta, y_pos_pasillos[((i + 1) / 2) - 1], graphicPosition);
                    drawLine(g, x_pos_baja, y_pos_pasillos[((i + 1) / 2)], x_pos_baja, y_pos_pasillos[((i + 1) / 2) - 1], graphicPosition);
                    if (!drawTraversal) {
                        g.setColor(measuresColor);
                        drawLineMeasureV(g, formatString(tabla_valor_pasillo[i][0] / 2.0), y_pos_pasillos[((i + 1) / 2)], y_pos_pasillos[((i + 1) / 2) - 1], x_pos_baja + 15, graphicPosition);
                        g.setColor(routesColor);
                        drawTraversal = true;
                    }
                } else if (tabla_path[i][index_back] == 2) {
                    drawLine(g, x_pos_baja, y_pos_pasillos[((i + 1) / 2)], x_pos_baja, y_pos_pasillos[((i + 1) / 2) - 1], graphicPosition);
                    drawLine(g, x_pos_baja - 5, y_pos_pasillos[((i + 1) / 2)], x_pos_baja - 5, y_pos_pasillos[((i + 1) / 2) - 1], graphicPosition);
                    if (!drawTraversal) {
                        g.setColor(measuresColor);
                        drawLineMeasureV(g, formatString(tabla_valor_pasillo[i][1] / 2.0), y_pos_pasillos[((i + 1) / 2)], y_pos_pasillos[((i + 1) / 2) - 1], x_pos_baja + 15, graphicPosition);
                        g.setColor(routesColor);
                        drawTraversal = true;
                    }
                } else if (tabla_path[i][index_back] == 3) {
                    drawLine(g, x_pos_alta, y_pos_pasillos[((i + 1) / 2)], x_pos_alta, y_pos_pasillos[((i + 1) / 2) - 1], graphicPosition);
                    drawLine(g, x_pos_alta + 5, y_pos_pasillos[((i + 1) / 2)], x_pos_alta + 5, y_pos_pasillos[((i + 1) / 2) - 1], graphicPosition);
                    if (!drawTraversal) {
                        g.setColor(measuresColor);
                        drawLineMeasureVL(g, formatString(tabla_valor_pasillo[i][2] / 2.0), y_pos_pasillos[((i + 1) / 2)], y_pos_pasillos[((i + 1) / 2) - 1], x_pos_alta - 8, graphicPosition);
                        g.setColor(routesColor);
                        drawTraversal = true;
                    }
                } else if (tabla_path[i][index_back] == 4) {
                    drawLine(g, x_pos_alta, y_pos_pasillos[((i + 1) / 2)], x_pos_alta, y_pos_pasillos[((i + 1) / 2) - 1], graphicPosition);
                    drawLine(g, x_pos_alta + 5, y_pos_pasillos[((i + 1) / 2)], x_pos_alta + 5, y_pos_pasillos[((i + 1) / 2) - 1], graphicPosition);
                    drawLine(g, x_pos_baja, y_pos_pasillos[((i + 1) / 2)], x_pos_baja, y_pos_pasillos[((i + 1) / 2) - 1], graphicPosition);
                    drawLine(g, x_pos_baja - 5, y_pos_pasillos[((i + 1) / 2)], x_pos_baja - 5, y_pos_pasillos[((i + 1) / 2) - 1], graphicPosition);
                    if (!drawTraversal) {
                        g.setColor(measuresColor);
                        drawLineMeasureV(g, formatString(tabla_valor_pasillo[i][3] / 4), y_pos_pasillos[((i + 1) / 2)], y_pos_pasillos[((i + 1) / 2) - 1], x_pos_baja + 15, graphicPosition);
                        g.setColor(routesColor);
                        drawTraversal = true;
                    }
                }

                if (para_pasillo != 1) {
                    pasillo--;
                } else {
                    para_pasillo++;
                }
                if (warehouse.getDepotPlacement() == DEPOT_CENTER && extra_pasillo) {
                    if (pasillo_extra_depot == pasillo) {
                        para_pasillo++;
                    }
                }
            }
            index_back = tabla_back[i][index_back];
        }
    }

}
