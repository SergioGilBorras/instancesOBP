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
import static com.instancesobp.utils.Constants.DEPOT_CENTER;
import static com.instancesobp.utils.Constants.DEPOT_CORNER;

/**
 * Implements the Largest Gap routing algorithm for visualizing the path
 * taken in a warehouse layout. This algorithm calculates the largest gaps
 * between items in aisles and determines the optimal path for picking.
 * The class provides methods to draw the path and calculate distances
 * for visualization purposes.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class LargestGap {

    /** The warehouse object containing layout and configuration details. */
    private final Warehouse warehouse;

    /** List of batches to be visualized. */
    private final List<Batch> batchList;

    /** The index of the current batch being processed. */
    private final int currentBatchIndex;

    /** Multiplier for scaling the warehouse dimensions in the graphical representation. */
    private final double scaleMultiplier;

    /** Multiplier for scaling dimensions by 1.5. */
    private final double scaleMultiplierOneHalf;

    /** Multiplier for scaling dimensions by 0.5. */
    private final double scaleMultiplierHalf;

    /** Current graphical position (rotated or default). */
    private final int graphicPosition;

    /**
     * Constructor for the LargestGap class.
     *
     * @param warehouse The warehouse object containing layout and configuration details.
     * @param batchList The list of batches to be visualized.
     * @param currentBatchIndex The index of the current batch being processed.
     * @param scaleMultiplier The multiplier for scaling the warehouse dimensions.
     * @param graphicPosition The orientation of the graphic (horizontal or vertical).
     */
    public LargestGap(Warehouse warehouse, List<Batch> batchList, int currentBatchIndex, int scaleMultiplier, int graphicPosition) {
        this.warehouse = warehouse;
        this.batchList = batchList;
        this.currentBatchIndex = currentBatchIndex;
        this.scaleMultiplier = scaleMultiplier;
        this.scaleMultiplierOneHalf = scaleMultiplier * 1.5;
        this.scaleMultiplierHalf = scaleMultiplier * 0.5;
        this.graphicPosition = graphicPosition;
    }

    /**
     * Draws the path for the Largest Gap routing algorithm on the graphics context.
     * This method calculates the largest gaps between items in aisles and visualizes
     * the path taken for picking items in the warehouse.
     *
     * @param g The graphics context to draw on.
     */
    public void drawPathLargestGap(Graphics g) {
        int max_pasillo = 0;
        int min_pasillo = Integer.MAX_VALUE;
        HashMap<Integer, java.util.List<Double>> Pasillos_max_dis = new HashMap<>();

        for (Order order : batchList.get(currentBatchIndex).getOrders()) {
            for (Product p : order.getProducts()) {
                if (Pasillos_max_dis.containsKey(p.getAisle())) {
                    java.util.List<Double> lista_alturas = Pasillos_max_dis.get(p.getAisle());
                    if (!lista_alturas.contains(p.getHeightPosition())) {
                        lista_alturas.add(p.getHeightPosition());
                        Pasillos_max_dis.replace(p.getAisle(), lista_alturas);
                    }
                } else {
                    java.util.List<Double> lista_alturas;
                    lista_alturas = new ArrayList<>();
                    lista_alturas.add(p.getHeightPosition());
                    lista_alturas.add(0.0);
                    lista_alturas.add(warehouse.getShelfLength());
                    Pasillos_max_dis.put(p.getAisle(), lista_alturas);
                    if (max_pasillo < p.getAisle()) {
                        max_pasillo = p.getAisle();
                    }
                    if (min_pasillo > p.getAisle()) {
                        min_pasillo = p.getAisle();
                    }
                }
            }
        }

        g.setColor(routesColor);

        ArrayList<Integer> aisleList = new ArrayList<>(Pasillos_max_dis.keySet());
        Collections.sort(aisleList);
        Iterator<Integer> aisleIterator = aisleList.iterator();
        int yy1, yy2, xx1, xx2;
        Point p_ini = new Point();
        double ancho_warehouse = ((warehouse.getShelfWidth() + warehouse.getAisleWidth()) * warehouse.getNumberOfAisles()) * scaleMultiplier;

        // Fijo punto de inicio. (pIni)
        if (warehouse.getDepotPlacement() == DEPOT_CORNER) {
            p_ini.y = (int) (5 + Math.round((warehouse.getShelfWidth() + warehouse.getAisleWidth()) * scaleMultiplierHalf));
        } else {
            p_ini.y = (int) (5 + Math.round(ancho_warehouse / 2.0));
        }
        p_ini.x = (int) (5 + Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf));

        if ((warehouse.getDepotPlacement() == DEPOT_CORNER) && (aisleList.get(0) > 0)) {
            xx1 = p_ini.x;// + 5;
            yy1 = p_ini.y - 5;
            yy2 = (int) (5 + Math.round(((warehouse.getShelfWidth() + warehouse.getAisleWidth()) * scaleMultiplierHalf) + ((((warehouse.getShelfWidth() + warehouse.getAisleWidth()) * scaleMultiplier)) * aisleList.get(0))));

            drawLine(g, xx1, yy1, xx1, yy2, graphicPosition);
        }

        boolean primero = true;
        double DistanciaUltimoPasilloImpar = 0;
        Point p_R = new Point();
        Point p_L = new Point();
        while (aisleIterator.hasNext()) {
            int pasillo = aisleIterator.next();
            if (primero && !aisleIterator.hasNext()) {
                java.util.List<Double> lista_alturas = Pasillos_max_dis.get(pasillo);
                Collections.sort(lista_alturas);
                yy1 = (int) (2 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasillo)));
                xx1 = (int) (5 + Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf));
                xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplier) + (lista_alturas.get(lista_alturas.size() - 2) * scaleMultiplier)));
                DistanciaUltimoPasilloImpar = lista_alturas.get(lista_alturas.size() - 2);
                if (warehouse.getDepotPlacement() == DEPOT_CENTER) {
                    if ((aisleList.get(0) % 2 == 0) && (aisleList.get(0) >= (warehouse.getNumberOfAisles() / 2.0))) {

                        drawLine(g, xx1 + 5, yy1, xx2, yy1, graphicPosition);
                        drawLine(g, xx1, yy1 + 5, xx2, yy1 + 5, graphicPosition);
                        drawLine(g, xx2, yy1, xx2, yy1 + 5, graphicPosition); //une la doble vuelta en mismo pasillo

                        drawLine(g, xx1 + 5, yy1, p_ini.x + 5, p_ini.y, graphicPosition);//ida desde origen 1
                        drawLine(g, xx1, yy1 + 5, p_ini.x, p_ini.y, graphicPosition);//vuelta al origen 2
                    } else if ((aisleList.get(0) % 2 == 0) && (aisleList.get(0) < (warehouse.getNumberOfAisles() / 2.0))) {

                        drawLine(g, xx1, yy1, xx2, yy1, graphicPosition);
                        drawLine(g, xx1 + 5, yy1 + 5, xx2, yy1 + 5, graphicPosition);
                        drawLine(g, xx2, yy1, xx2, yy1 + 5, graphicPosition); //une la doble vuelta en mismo pasillo

                        drawLine(g, xx1, yy1, p_ini.x, p_ini.y, graphicPosition);//ida desde origen 1
                        drawLine(g, xx1 + 5, yy1 + 5, p_ini.x, p_ini.y, graphicPosition);//vuelta al origen 2
                    } else if ((aisleList.get(0) % 2 == 1) && (aisleList.get(0) > (warehouse.getNumberOfAisles() / 2.0))) {

                        drawLine(g, xx1 + 5, yy1, xx2, yy1, graphicPosition);
                        drawLine(g, xx1, yy1 + 5, xx2, yy1 + 5, graphicPosition);
                        drawLine(g, xx2, yy1, xx2, yy1 + 5, graphicPosition); //une la doble vuelta en mismo pasillo

                        drawLine(g, xx1 + 5, yy1, p_ini.x + 5, p_ini.y, graphicPosition);//ida desde origen 1
                        drawLine(g, xx1, yy1 + 5, p_ini.x, p_ini.y, graphicPosition);//vuelta al origen 2
                    } else if ((aisleList.get(0) % 2 == 1) && (aisleList.get(0) < (warehouse.getNumberOfAisles() / 2.0))) {

                        drawLine(g, xx1, yy1, xx2, yy1, graphicPosition);
                        drawLine(g, xx1 + 5, yy1 + 5, xx2, yy1 + 5, graphicPosition);
                        drawLine(g, xx2, yy1, xx2, yy1 + 5, graphicPosition); //une la doble vuelta en mismo pasillo

                        drawLine(g, xx1, yy1, p_ini.x, p_ini.y, graphicPosition);//ida desde origen 1
                        drawLine(g, xx1 + 5, yy1 + 5, p_ini.x, p_ini.y, graphicPosition);//vuelta al origen 2
                    } else {
                        drawLine(g, xx1, yy1, xx2, yy1, graphicPosition);
                        drawLine(g, xx1, yy1 + 5, xx2, yy1 + 5, graphicPosition);
                        drawLine(g, xx2, yy1, xx2, yy1 + 5, graphicPosition); //une la doble vuelta en mismo pasillo
                    }
                } else {
                    drawLine(g, xx1, yy1, xx2, yy1, graphicPosition);
                    drawLine(g, xx1, yy1 + 5, xx2, yy1 + 5, graphicPosition);
                    drawLine(g, xx2, yy1, xx2, yy1 + 5, graphicPosition); //une la doble vuelta en mismo pasillo
                }
            } else if (primero) {
                yy1 = (int) (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasillo)));
                xx1 = (int) (5 + Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf));
                xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplierOneHalf) + (warehouse.getShelfLength() * scaleMultiplier)));

                if (warehouse.getDepotPlacement() == DEPOT_CENTER && (aisleList.get(aisleList.size() - 1) < (warehouse.getNumberOfAisles() / 2.0))) {
                    drawLine(g, xx1, yy1, p_ini.x, p_ini.y, graphicPosition);//ida desde origen 1
                    drawLine(g, xx1 + 5, yy1 + 5, p_ini.x, p_ini.y, graphicPosition);//vuelta al origen 2
                    drawLine(g, xx1 + 5, yy1, xx2, yy1, graphicPosition);

                } else if (warehouse.getDepotPlacement() == DEPOT_CENTER && (aisleList.get(0) >= (warehouse.getNumberOfAisles() / 2.0))) {
                    drawLine(g, xx1 + 5, yy1, p_ini.x + 5, p_ini.y, graphicPosition);//ida desde origen 1
                    drawLine(g, xx1, yy1 + 5, p_ini.x, p_ini.y, graphicPosition);//vuelta al origen 2
                    drawLine(g, xx1 + 5, yy1, xx2, yy1, graphicPosition);

                } else {
                    drawLine(g, xx1, yy1, xx2, yy1, graphicPosition);
                }
                p_R = new Point(xx1, yy1);
                p_L = new Point(xx2, yy1);

            } else if (!aisleIterator.hasNext()) {
                yy1 = (int) (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasillo)));
                xx1 = (int) (5 + Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf));
                xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplierOneHalf) + (warehouse.getShelfLength() * scaleMultiplier)));
                if (aisleList.get(0) > 0) { //recorrido desde el final al primer pasillo
                    if (warehouse.getDepotPlacement() == DEPOT_CORNER) {
                        drawLine(g, xx1 - 5, yy1, p_ini.x - 5, p_ini.y - 5, graphicPosition);
                        drawLine(g, xx1 - 5, yy1, xx2, yy1, graphicPosition);
                    } else {
                        drawLine(g, xx1, yy1, p_R.x, p_R.y, graphicPosition);
                        drawLine(g, xx1, yy1, xx2, yy1, graphicPosition);
                    }
                    if (Pasillos_max_dis.size() > 1) {
                        drawLine(g, p_L.x, p_L.y, xx2, yy1, graphicPosition);
                    }
                } else {
                    drawLine(g, xx1, yy1, xx2, yy1, graphicPosition);
                    if (Pasillos_max_dis.size() > 1) {
                        drawLine(g, p_L.x, p_L.y, xx2, yy1, graphicPosition);
                        drawLine(g, xx1, yy1, p_R.x, p_R.y, graphicPosition);
                    }
                }
            } else {
                List<Double> lista_alturas = Pasillos_max_dis.get(pasillo);
                Collections.sort(lista_alturas);
                double max_gap = 0;
                double ini_gap = 0;
                double fin_gap = 0;
                for (int i = 1; i < lista_alturas.size(); i++) {
                    double dd = (double) lista_alturas.get(i) - lista_alturas.get(i - 1);
                    if (max_gap < dd) {
                        max_gap = dd;
                        ini_gap = lista_alturas.get(i - 1);
                        fin_gap = lista_alturas.get(i);
                    }
                }
                if (ini_gap == 0) { // gap al principio
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

                    drawLine(g, p_L.x, p_L.y, xx2, yy1, graphicPosition);
                    p_L = new Point(xx2, yy1 + 5);

                } else if (fin_gap == warehouse.getShelfLength()) { // gap al final
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

                    drawLine(g, xx1, yy1, p_R.x, p_R.y, graphicPosition);
                    p_R = new Point(xx1, yy1 + 5);
                } else { // gap en medio
                    yy1 = (int) (2 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasillo)));
                    xx1 = (int) (5 + Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf));
                    xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplier) + (ini_gap * scaleMultiplier)));
                    drawLine(g, xx1, yy1, xx2, yy1, graphicPosition);
                    drawLine(g, xx1, yy1 + 5, xx2, yy1 + 5, graphicPosition);
                    drawLine(g, xx2, yy1, xx2, yy1 + 5, graphicPosition); //une la doble vuelta en mismo pasillo
                    drawLine(g, xx1, yy1, p_R.x, p_R.y, graphicPosition);
                    p_R = new Point(xx1, yy1 + 5);

                    g.setColor(measuresColor);
                    double valor = (warehouse.getAisleWidth() / 2.0) + ini_gap;
                    drawLineMeasureH(g, formatString(valor), xx1, xx2, yy1 - ((int) Math.round(warehouse.getAisleWidth() * 0.25 * scaleMultiplier)), graphicPosition);
                    g.setColor(routesColor);

                    xx1 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplier) + (fin_gap * scaleMultiplier)));
                    xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplierOneHalf) + (warehouse.getShelfLength() * scaleMultiplier)));
                    drawLine(g, xx1, yy1, xx2, yy1, graphicPosition);
                    drawLine(g, xx1, yy1 + 5, xx2, yy1 + 5, graphicPosition);
                    drawLine(g, xx1, yy1, xx1, yy1 + 5, graphicPosition); //une la doble vuelta en mismo pasillo
                    drawLine(g, p_L.x, p_L.y, xx2, yy1, graphicPosition);
                    p_L = new Point(xx2, yy1 + 5);

                    g.setColor(measuresColor);
                    valor = (warehouse.getAisleWidth() / 2.0) + (warehouse.getShelfLength() - fin_gap);
                    drawLineMeasureH(g, formatString(valor), xx1, xx2, yy1 - ((int) Math.round(warehouse.getAisleWidth() * 0.25 * scaleMultiplier)), graphicPosition);
                    g.setColor(routesColor);
                }
            }

            primero = false;
        }

        //dibujo las distancias Verticales recorridas
        int pasilloIni;
        if (warehouse.getDepotPlacement() == DEPOT_CORNER) {
            pasilloIni = 0;
            int pasilloFin = aisleList.get(aisleList.size() - 1);
            if (pasilloIni != pasilloFin) {
                yy1 = (int) (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasilloIni)));
                yy2 = (int) (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasilloFin)));
                xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplier * 0.25) + (warehouse.getAisleWidth() * scaleMultiplierOneHalf) + (warehouse.getShelfLength() * scaleMultiplier)));
                double valor = (warehouse.getAisleWidth() + warehouse.getShelfWidth()) * (pasilloFin - pasilloIni);
                g.setColor(measuresColor);
                drawLineMeasureV(g, formatString(valor), yy1, yy2, xx2, graphicPosition);
            }
        } else if (aisleList.get(0) < (warehouse.getNumberOfAisles() / 2.0)) {
            pasilloIni = aisleList.get(0);
            int pasilloFin = aisleList.get(aisleList.size() - 1);
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
            int pasilloFin = aisleList.get(aisleList.size() - 1);

            if (warehouse.getNumberOfAisles() % 2 == 0) {
                valor = ((warehouse.getAisleWidth() + warehouse.getShelfWidth()) / 2.0) + (warehouse.getAisleWidth() + warehouse.getShelfWidth()) * (pasilloFin - warehouse.getNumberOfAisles() / 2.0);

            } else {
                valor = (warehouse.getAisleWidth() + warehouse.getShelfWidth()) * (pasilloFin - warehouse.getNumberOfAisles() / 2.0);

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
        yy1 = (int) (-15 + Math.round((warehouse.getAisleWidth() * scaleMultiplier * 0.25) + (warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf)));//+ (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * (warehouse.getNumberOfAisles() - 1))));
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
