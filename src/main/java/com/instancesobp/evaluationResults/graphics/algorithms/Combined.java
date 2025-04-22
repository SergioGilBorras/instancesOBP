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
import java.util.*;
import java.util.List;

import static com.instancesobp.evaluationResults.graphics.Utils.*;
import static com.instancesobp.utils.Constants.DEPOT_CORNER;

/**
 * This class implements the Combined routing algorithm for visualizing
 * the paths taken in a warehouse. It calculates and draws the optimal
 * routes for picking items in a batch, considering the warehouse layout
 * and product positions.
 * The Combined algorithm combines different routing strategies to optimize
 * the picking process.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class Combined {

    /** The warehouse object containing layout and configuration details. */
    private final Warehouse warehouse;

    /** List of batches to be visualized. */
    private final List<Batch> batchList;

    /** The index of the current batch being processed. */
    private final int currentBatchIndex;

    /** Scaling factor for graphical representation. */
    private final double scaleMultiplier;

    /** Scaling factor for 1.5 times the base scale. */
    private final double scaleMultiplierOneHalf;

    /** Scaling factor for half the base scale. */
    private final double scaleMultiplierHalf;

    /** The orientation of the graphical representation (horizontal or vertical). */
    private final int graphicPosition;

    /**
     * Constructor for the Combined class.
     *
     * @param warehouse The warehouse object containing layout and configuration details.
     * @param batchList The list of batches to be visualized.
     * @param currentBatchIndex The index of the current batch being processed.
     * @param scaleMultiplier The scaling factor for graphical representation.
     * @param graphicPosition The orientation of the graphical representation.
     */
    public Combined(Warehouse warehouse, List<Batch> batchList, int currentBatchIndex, int scaleMultiplier, int graphicPosition) {
        this.warehouse = warehouse;
        this.batchList = batchList;
        this.currentBatchIndex = currentBatchIndex;
        this.scaleMultiplier = scaleMultiplier;
        this.scaleMultiplierOneHalf = scaleMultiplier * 1.5;
        this.scaleMultiplierHalf = scaleMultiplier * 0.5;
        this.graphicPosition = graphicPosition;
    }

    /**
     * Draws the path for the Combined routing algorithm.
     * This method calculates the optimal route for picking items in the current batch
     * and visualizes it on the provided Graphics object. It considers the positions
     * of products, aisles, and the depot to generate the route.
     *
     * @param g The Graphics object used for drawing the route.
     */
    public void drawPathCombine(Graphics g) {
        int max_pasillo = 0;
        int min_pasillo = Integer.MAX_VALUE;
        HashMap<Integer, List<Double>> Pasillos_max_dis = new HashMap<>();
        HashMap<Integer, Double> Pasillos_max_dis_com = new HashMap<>();

        //Precálculo de distancias en hashtable
        for (Order order : batchList.get(currentBatchIndex).getOrders()){
           for (Product product1 : order.getProducts()){
                if (Pasillos_max_dis.containsKey(product1.getAisle())) {
                    java.util.List<Double> lista_alturas = Pasillos_max_dis.get(product1.getAisle());
                    if (!lista_alturas.contains(product1.getHeightPosition())) {
                        lista_alturas.add(product1.getHeightPosition());
                        Pasillos_max_dis.replace(product1.getAisle(), lista_alturas);
                    }
                    if (Pasillos_max_dis_com.get(product1.getAisle()) < product1.getHeightPosition()) {
                        Pasillos_max_dis_com.replace(product1.getAisle(), product1.getHeightPosition());
                    }
                } else {
                    List<Double> lista_alturas;
                    lista_alturas = new ArrayList<>();
                    lista_alturas.add(product1.getHeightPosition());
                    lista_alturas.add(0.0);
                    lista_alturas.add(warehouse.getShelfLength());
                    Pasillos_max_dis.put(product1.getAisle(), lista_alturas);
                    Pasillos_max_dis_com.put(product1.getAisle(), product1.getHeightPosition());
                    if (max_pasillo < product1.getAisle()) {
                        max_pasillo = product1.getAisle();
                    }
                    if (min_pasillo > product1.getAisle()) {
                        min_pasillo = product1.getAisle();
                    }

                }
            }
        }

        List<Integer> sortedKeys = new ArrayList<>(Pasillos_max_dis.keySet());
        Collections.sort(sortedKeys);
        int yy1, yy2, xx1, xx2;
        boolean primero = true;
        Point p_R_vuelta = new Point();
        Point p_R_ida = new Point();
        Point p_R_ini = new Point();
        Point p_R_fin = new Point();
        Point p_L = new Point();
        Point p_Depot;
        int lado_almacen = 0;
        double ancho_warehouse = ((warehouse.getShelfWidth() + warehouse.getAisleWidth()) * warehouse.getNumberOfAisles()) * scaleMultiplier;
        double DistanciaUltimoPasilloImpar = 0;

        g.setColor(routesColor);

        p_Depot = new Point();
        // Fijo punto de depot. (p_Depot)
        if (warehouse.getDepotPlacement() == DEPOT_CORNER) {
            p_Depot.y = (int) (5 + Math.round((warehouse.getShelfWidth() + warehouse.getAisleWidth()) * scaleMultiplierHalf));
        } else {
            p_Depot.y = (int) (5 + Math.round(ancho_warehouse / 2.0));
        }
        p_Depot.x = (int) (5 + Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf));

        Iterator<Integer> aisleIterator = sortedKeys.iterator();
        while (aisleIterator.hasNext()) {
            g.setColor(routesColor);

            int pasillo = aisleIterator.next();

            if (primero && !aisleIterator.hasNext()) {
                List<Double> lista_alturas = Pasillos_max_dis.get(pasillo);
                Collections.sort(lista_alturas);
                yy1 = (int) (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasillo)));
                xx1 = (int) (5 + Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf));
                xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplier) + (lista_alturas.get(lista_alturas.size() - 2) * scaleMultiplier)));

                drawLine(g, xx1, yy1, xx2, yy1, graphicPosition);
                drawLine(g, xx1, yy1 + 5, xx2, yy1 + 5, graphicPosition);
                drawLine(g, xx2, yy1, xx2, yy1 + 5, graphicPosition); //une la doble vuelta en mismo pasillo

                p_R_ini = new Point(xx1, yy1);
                p_R_fin = new Point(xx1, yy1);

                DistanciaUltimoPasilloImpar = Pasillos_max_dis_com.get(pasillo);
                primero = false;
            } else if (primero) {
                yy1 = (int) (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasillo)));
                xx1 = (int) (5 + Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf));
                xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplierOneHalf) + (warehouse.getShelfLength() * scaleMultiplier)));
                drawLine(g, xx1 - 5, yy1, xx2, yy1, graphicPosition);
                p_R_ida = new Point(xx1, yy1);
                p_R_vuelta = new Point(xx1 - 5, yy1);
                p_L = new Point(xx2, yy1);
                p_R_ini = new Point(xx1, yy1);

                lado_almacen = 1;
                primero = false;
            } else if (lado_almacen == 1 && !aisleIterator.hasNext()) {
                yy1 = (int) (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasillo)));
                xx1 = (int) (5 + Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf));
                xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplierOneHalf) + (warehouse.getShelfLength() * scaleMultiplier)));
                drawLine(g, xx1 - 5, yy1, xx2, yy1, graphicPosition);

                //conecto con el anterior pasillo lado Izquierdo
                drawLine(g, xx2, yy1, p_L.x, p_L.y, graphicPosition);

                //conecto con el anterior pasillo lado derecho de vuelta
                drawLine(g, xx1 - 5, yy1, p_R_vuelta.x, p_R_vuelta.y, graphicPosition);

                p_R_fin = new Point(xx1, yy1);

            } else if (lado_almacen == 0 && !aisleIterator.hasNext()) {
                List<Double> lista_alturas = Pasillos_max_dis.get(pasillo);
                Collections.sort(lista_alturas);
                yy1 = (int) (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasillo)));
                xx1 = (int) (5 + Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf));
                xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplier) + (lista_alturas.get(lista_alturas.size() - 2) * scaleMultiplier)));

                drawLine(g, xx1, yy1, xx2, yy1, graphicPosition);
                drawLine(g, xx1 - 5, yy1 + 5, xx2, yy1 + 5, graphicPosition);
                drawLine(g, xx2, yy1, xx2, yy1 + 5, graphicPosition); //une la doble vuelta en mismo pasillo

                DistanciaUltimoPasilloImpar = Pasillos_max_dis_com.get(pasillo);

                //conecto con el anterior pasillo lado derecho de vuelta
                drawLine(g, xx1 - 5, yy1 + 5, p_R_vuelta.x, p_R_vuelta.y, graphicPosition);

                //conecto con el anterior pasillo lado derecho de ida
                drawLine(g, xx1, yy1, p_R_ida.x, p_R_ida.y, graphicPosition);

                p_R_fin = new Point(xx1, yy1);

            } else {
                List<Double> lista_alturas = Pasillos_max_dis.get(pasillo);
                Collections.sort(lista_alturas);
                double max_gap = 0;
                double ini_gap = 0;
                double fin_gap = 0;
                for (int i = 1; i < lista_alturas.size(); i++) {
                    double dd = lista_alturas.get(i) - (double) lista_alturas.get(i - 1);
                    if (max_gap < dd) {
                        max_gap = dd;
                        ini_gap = lista_alturas.get(i - 1);
                        fin_gap = lista_alturas.get(i);
                    }
                }

                double pasillo_extra_gag_medio = 0;
                if ((fin_gap != warehouse.getShelfLength()) && ini_gap != 0) {
                    pasillo_extra_gag_medio = warehouse.getAisleWidth();
                }

                if ((lado_almacen == 0 && fin_gap != warehouse.getShelfLength())
                        || (warehouse.getShelfLength() < ((warehouse.getShelfLength() - max_gap) * 2) + pasillo_extra_gag_medio)) {
                    yy1 = (int) (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasillo)));
                    xx1 = (int) (5 + Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf));
                    xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplierOneHalf) + (warehouse.getShelfLength() * scaleMultiplier)));
                    drawLine(g, xx1, yy1, xx2, yy1, graphicPosition);

                    if (lado_almacen == 1) {
                        //conecto con el anterior pasillo lado Izquierdo
                        drawLine(g, xx2, yy1, p_L.x, p_L.y, graphicPosition);
                    } else {
                        //conecto con el anterior pasillo lado derecho de ida
                        drawLine(g, xx1, yy1, p_R_ida.x, p_R_ida.y, graphicPosition);
                    }
                    p_R_ida = new Point(xx1, yy1);
                    p_L = new Point(xx2, yy1);
                    lado_almacen = (lado_almacen + 1) % 2;
                } else if (ini_gap == 0) { // gap al principio
                    yy1 = (int) (2 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasillo)));
                    xx1 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplier) + (fin_gap * scaleMultiplier)));
                    xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplierOneHalf) + (warehouse.getShelfLength() * scaleMultiplier)));
                    drawLine(g, xx1, yy1, xx2, yy1, graphicPosition);
                    drawLine(g, xx1, yy1 + 5, xx2, yy1 + 5, graphicPosition);
                    drawLine(g, xx1, yy1, xx1, yy1 + 5, graphicPosition); //une la doble vuelta en mismo pasillo

                    g.setColor(measuresColor);
                    double valor = (warehouse.getAisleWidth() / 2.0) + (warehouse.getShelfLength() - fin_gap);
                    drawLineMeasureH(g, formatString(valor), xx1, xx2, yy1 - ((int) Math.round(warehouse.getAisleWidth() * 0.25 * scaleMultiplier)), graphicPosition);
                    g.setColor(routesColor);

                    //conecto con el anterior pasillo lado Izquierdo
                    drawLine(g, xx2, yy1, p_L.x, p_L.y, graphicPosition);

                    p_L = new Point(xx2, yy1 + 5);

                } else if (lado_almacen == 0 && fin_gap == warehouse.getShelfLength()) { // gap al final
                    yy1 = (int) (2 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasillo)));
                    xx1 = (int) (5 + Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf));
                    xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplier) + (ini_gap * scaleMultiplier)));
                    drawLine(g, xx1, yy1, xx2, yy1, graphicPosition);
                    drawLine(g, xx1, yy1 + 5, xx2, yy1 + 5, graphicPosition);
                    drawLine(g, xx2, yy1, xx2, yy1 + 5, graphicPosition); //une la doble vuelta en mismo pasillo

                    g.setColor(measuresColor);
                    double valor = (warehouse.getAisleWidth() / 2.0) + ini_gap;
                    drawLineMeasureH(g, formatString(valor), xx1, xx2, yy1 - ((int) Math.round(warehouse.getAisleWidth() * 0.25 * scaleMultiplier)), graphicPosition);
                    g.setColor(routesColor);

                    //conecto con el anterior pasillo lado derecho de ida
                    drawLine(g, xx1, yy1, p_R_ida.x, p_R_ida.y, graphicPosition);

                    p_R_ida = new Point(xx1, yy1 + 5);

                } else if (lado_almacen == 1 && fin_gap == warehouse.getShelfLength()) { // gap al final
                    yy1 = (int) (2 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasillo)));
                    xx1 = (int) (5 + Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf));
                    xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplier) + (ini_gap * scaleMultiplier)));
                    drawLine(g, xx1 - 5, yy1, xx2, yy1, graphicPosition);
                    drawLine(g, xx1 - 5, yy1 + 5, xx2, yy1 + 5, graphicPosition);
                    drawLine(g, xx2, yy1, xx2, yy1 + 5, graphicPosition); //une la doble vuelta en mismo pasillo

                    g.setColor(measuresColor);
                    double valor = (warehouse.getAisleWidth() / 2.0) + ini_gap;
                    drawLineMeasureH(g, formatString(valor), xx1, xx2, yy1 - ((int) Math.round(warehouse.getAisleWidth() * 0.25 * scaleMultiplier)), graphicPosition);
                    g.setColor(routesColor);

                    //conecto con el anterior pasillo lado derecho de vuelta
                    drawLine(g, xx1 - 5, yy1, p_R_vuelta.x, p_R_vuelta.y, graphicPosition);

                    p_R_vuelta = new Point(xx1 - 5, yy1 + 5);

                } else { // gap en medio
                    yy1 = (int) (2 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasillo)));
                    xx1 = (int) (5 + Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf));
                    xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplier) + (ini_gap * scaleMultiplier)));

                    drawLine(g, xx1 - 5, yy1, xx2, yy1, graphicPosition);
                    drawLine(g, xx1 - 5, yy1 + 5, xx2, yy1 + 5, graphicPosition);
                    drawLine(g, xx2, yy1, xx2, yy1 + 5, graphicPosition); //une la doble vuelta en mismo pasillo
                    //une la doble vuelta en mismo pasillo
                    if (lado_almacen == 1) {
                        //conecto con el anterior pasillo lado derecho de vuelta
                        drawLine(g, xx1 - 5, yy1, p_R_vuelta.x, p_R_vuelta.y, graphicPosition);
                        p_R_vuelta = new Point(xx1 - 5, yy1 + 5);
                    } else {
                        //conecto con el anterior pasillo lado derecho de ida
                        drawLine(g, xx1, yy1, p_R_ida.x, p_R_ida.y, graphicPosition);
                        p_R_ida = new Point(xx1, yy1 + 5);
                    }

                    g.setColor(measuresColor);
                    double valor = (warehouse.getAisleWidth() / 2.0) + ini_gap;
                    drawLineMeasureH(g, formatString(valor), xx1, xx2, yy1 - ((int) Math.round(warehouse.getAisleWidth() * 0.25 * scaleMultiplier)), graphicPosition);
                    g.setColor(routesColor);

                    xx1 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplier) + (fin_gap * scaleMultiplier)));
                    xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplierOneHalf) + (warehouse.getShelfLength() * scaleMultiplier)));
                    drawLine(g, xx1, yy1, xx2, yy1, graphicPosition);
                    drawLine(g, xx1, yy1 + 5, xx2, yy1 + 5, graphicPosition);
                    drawLine(g, xx1, yy1, xx1, yy1 + 5, graphicPosition); //une la doble vuelta en mismo pasillo

                    //conecto con el anterior pasillo lado Izquierdo
                    drawLine(g, xx2, yy1, p_L.x, p_L.y, graphicPosition);

                    p_L = new Point(xx2, yy1 + 5);

                    g.setColor(measuresColor);
                    valor = (warehouse.getAisleWidth() / 2.0) + (warehouse.getShelfLength() - fin_gap);
                    drawLineMeasureH(g, formatString(valor), xx1, xx2, yy1 - ((int) Math.round(warehouse.getAisleWidth() * 0.25 * scaleMultiplier)), graphicPosition);
                    g.setColor(routesColor);
                }
            }

        }

        //Camino desde el recorrido hasta el depot
        if (p_Depot.y < p_R_ini.y) {
            drawLine(g, p_Depot.x, p_Depot.y, p_R_ini.x, p_R_ini.y, graphicPosition);
            drawLine(g, p_Depot.x - 5, p_Depot.y, p_R_ini.x - 5, p_R_ini.y, graphicPosition);
            //g.setColor(Color.green);
            //drawLine(g, p_R_ini.x - 4, p_R_ini.y, p_R_ini.x - 1, p_R_ini.y, graphicPosition);
            g.setColor(routesColor);

            if (Pasillos_max_dis.size() == 1) { //cuando solo hay un pasillo
                drawLine(g, p_R_ini.x - 5, p_R_ini.y, p_R_ini.x - 5, p_R_ini.y + 5, graphicPosition);
                drawLine(g, p_R_ini.x - 5, p_R_ini.y + 5, p_R_ini.x, p_R_ini.y + 5, graphicPosition);
            }

        } else if (p_Depot.y > p_R_fin.y) {
            drawLine(g, p_Depot.x, p_Depot.y, p_R_fin.x, p_R_fin.y, graphicPosition);
            drawLine(g, p_Depot.x - 5, p_Depot.y, p_R_fin.x - 5, p_R_fin.y, graphicPosition);
            //g.setColor(Color.green);
            //drawLine(g, p_R_fin.x - 4, p_R_fin.y, p_R_fin.x - 1, p_R_fin.y, graphicPosition);
            g.setColor(routesColor);

            if (Pasillos_max_dis.size() == 1) { //cuando solo hay un pasillo
                drawLine(g, p_R_ini.x - 5, p_R_ini.y, p_R_ini.x - 5, p_R_ini.y - 5, graphicPosition);
                drawLine(g, p_R_ini.x - 5, p_R_ini.y - 5, p_R_ini.x, p_R_ini.y - 5, graphicPosition);
            }
        }

        //dibujo las distancias Verticales recorridas
        int pasilloIni;
        if (warehouse.getDepotPlacement() == DEPOT_CORNER) {
            pasilloIni = 0;
            int pasilloFin = sortedKeys.get(sortedKeys.size() - 1);
            if (pasilloIni != pasilloFin) {
                yy1 = (int) (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasilloIni)));
                yy2 = (int) (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasilloFin)));
                xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplier * 0.25) + (warehouse.getAisleWidth() * scaleMultiplierOneHalf) + (warehouse.getShelfLength() * scaleMultiplier)));
                double valor = (warehouse.getAisleWidth() + warehouse.getShelfWidth()) * (pasilloFin - pasilloIni);
                g.setColor(measuresColor);
                drawLineMeasureV(g, formatString(valor), yy1, yy2, xx2, graphicPosition);
            }
        } else if (sortedKeys.get(0) < (warehouse.getNumberOfAisles() / 2.0)) {
            pasilloIni = sortedKeys.get(0);
            int pasilloFin = sortedKeys.get(sortedKeys.size() - 1);
            if (pasilloIni != pasilloFin) {
                yy1 = (int) (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasilloIni)));
                yy2 = (int) (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasilloFin)));
                xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplier * 0.25) + (warehouse.getAisleWidth() * scaleMultiplierOneHalf) + (warehouse.getShelfLength() * scaleMultiplier)));
                double valor = (warehouse.getAisleWidth() + warehouse.getShelfWidth()) * (pasilloFin - pasilloIni);
                g.setColor(measuresColor);
                drawLineMeasureV(g, formatString(valor), yy1, yy2, xx2, graphicPosition);
            }
        } else {
            double valor;
            int pasilloFin = sortedKeys.get(sortedKeys.size() - 1);

            if (warehouse.getNumberOfAisles() % 2 == 0) {
                valor = ((warehouse.getAisleWidth() + warehouse.getShelfWidth()) / 2.0) + (warehouse.getAisleWidth() + warehouse.getShelfWidth()) * (pasilloFin - warehouse.getNumberOfAisles() / 2.0);

            } else {
                valor = (warehouse.getAisleWidth() + warehouse.getShelfWidth()) * (pasilloFin - (double) warehouse.getNumberOfAisles() / 2.0);
            }

            yy1 = (int) (5 + Math.round(ancho_warehouse / 2.0));
            yy2 = (int) (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasilloFin)));
            xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplier * 0.25) + (warehouse.getAisleWidth() * scaleMultiplierOneHalf) + (warehouse.getShelfLength() * scaleMultiplier)));
            g.setColor(measuresColor);
            drawLineMeasureV(g, formatString(valor), yy1, yy2, xx2, graphicPosition);

        }

        //dibujo las distancias Horizontales recorridas
        g.setColor(measuresColor);
        double valor = (warehouse.getAisleWidth() + warehouse.getShelfLength());

        xx1 = (int) (5 + Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf));
        xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplierOneHalf) + (warehouse.getShelfLength() * scaleMultiplier)));
        yy1 = (int) (-15 + Math.round((warehouse.getAisleWidth() * scaleMultiplier * 0.25) + (warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf))) ;//+ (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * (warehouse.getNumberOfAisles() - 1))));
        drawLineMeasureH(g, formatString(valor), xx1, xx2, yy1, graphicPosition);

        if (DistanciaUltimoPasilloImpar != 0) {
            valor = (warehouse.getAisleWidth() / 2.0) + DistanciaUltimoPasilloImpar;
            xx1 = (int) (5 + Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf));
            xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplier) + (DistanciaUltimoPasilloImpar * scaleMultiplier)));
            yy1 = (int) (-15 + Math.round((warehouse.getAisleWidth() * scaleMultiplier * 0.25) + (warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * (warehouse.getNumberOfAisles() - 1))));
            drawLineMeasureH(g, formatString(valor), xx1, xx2, yy1, graphicPosition);
        }
    }
}
