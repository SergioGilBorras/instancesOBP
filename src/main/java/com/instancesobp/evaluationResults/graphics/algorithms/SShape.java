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
 * Implements the S-Shape routing algorithm for visualizing paths in a warehouse.
 * This algorithm calculates and draws the optimal path for picking items in a batch
 * using the S-Shape strategy, which traverses aisles in a systematic manner.
 * The class also calculates distances and visualizes the path using graphical components.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class SShape {

    /** The warehouse object containing layout and configuration details. */
    private final Warehouse warehouse;

    /** List of batches to be visualized. */
    private final List<Batch> batchList;

    /** The index of the current batch being processed. */
    private final int currentBatchIndex;

    /** Multiplier for scaling the warehouse dimensions in the graphical representation. */
    private final double scaleMultiplier;

    /** Pre-calculated value for 1.5 times the scale multiplier. */
    private final double scaleMultiplierOneHalf;

    /** Pre-calculated value for half the scale multiplier. */
    private final double scaleMultiplierHalf;

    /** The orientation of the graphical representation (horizontal or vertical). */
    private final int graphicPosition;

    /**
     * Constructor for the SShape class.
     *
     * @param warehouse The warehouse object containing layout and configuration details.
     * @param batchList The list of batches to be visualized.
     * @param currentBatchIndex The index of the current batch being processed.
     * @param scaleMultiplier The multiplier for scaling the warehouse dimensions.
     * @param graphicPosition The orientation of the graphical representation.
     */
    public SShape(Warehouse warehouse, List<Batch> batchList, int currentBatchIndex, int scaleMultiplier, int graphicPosition) {
        this.warehouse = warehouse;
        this.batchList = batchList;
        this.currentBatchIndex = currentBatchIndex;
        this.scaleMultiplier = scaleMultiplier;
        this.scaleMultiplierOneHalf = scaleMultiplier * 1.5;
        this.scaleMultiplierHalf = scaleMultiplier * 0.5;
        this.graphicPosition = graphicPosition;
    }

    /**
     * Draws the S-Shape path for the current batch on the provided graphics context.
     * The path is calculated based on the positions of items in the batch and the
     * layout of the warehouse.
     *
     * @param g The graphics context used for drawing.
     */
    public void drawPathS_shape(Graphics g) {
        int max_pasillo = 0;
        int min_pasillo = Integer.MAX_VALUE;
        HashMap<Integer, Double> Pasillos_max_dis = new HashMap<>();

        for (Order order : batchList.get(currentBatchIndex).getOrders()) {
            for (Product p : order.getProducts()) {
                if (Pasillos_max_dis.containsKey(p.getAisle())) {
                    if (Pasillos_max_dis.get(p.getAisle()) < (p.getHeightPosition())) {
                        Pasillos_max_dis.replace(p.getAisle(), p.getHeightPosition());
                    }
                } else {
                    Pasillos_max_dis.put(p.getAisle(), p.getHeightPosition());
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
        int corredor = 0;
        Point p1 = new Point();
        int pInitY;
        int pInitX;
        ArrayList<Integer> sortedKeys = new ArrayList<>(Pasillos_max_dis.keySet());
        Collections.sort(sortedKeys);
        Iterator<Integer> aisleIterator = sortedKeys.iterator();
        int yy1, yy2, xx1, xx2;
        double ancho_warehouse = ((warehouse.getShelfWidth() + warehouse.getAisleWidth()) * warehouse.getNumberOfAisles()) * scaleMultiplier;

        // Fijo punto de inicio. (pIni)
        if (warehouse.getDepotPlacement() == DEPOT_CORNER) {
            pInitY = (int) (5 + Math.round((warehouse.getShelfWidth() + warehouse.getAisleWidth()) * scaleMultiplierHalf));
        } else {
            pInitY = (int) (5 + Math.round(ancho_warehouse / 2.0));
        }
        pInitX = (int) (Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf));

        //recorrido desde el inicio al primer pasillo
        if (warehouse.getDepotPlacement() == DEPOT_CENTER) {

            if (sortedKeys.get(0) >= (warehouse.getNumberOfAisles() / 2.0)) {
                xx1 = pInitX + 5;
                yy1 = pInitY;
                yy2 = (int) (Math.round(((warehouse.getShelfWidth() + warehouse.getAisleWidth()) * scaleMultiplierHalf) + ((((warehouse.getShelfWidth() + warehouse.getAisleWidth()) * scaleMultiplier)) * sortedKeys.get(0))));
                if (sortedKeys.size() > 1) {
                    yy2 += 5;
                }
            } else {
                xx1 = pInitX;
                yy1 = pInitY;
                yy2 = (int) (5 + Math.round(((warehouse.getShelfWidth() + warehouse.getAisleWidth()) * scaleMultiplierHalf) + ((((warehouse.getShelfWidth() + warehouse.getAisleWidth()) * scaleMultiplier)) * sortedKeys.get(0))));
            }

            drawLine(g, xx1, yy1, xx1, yy2, graphicPosition);
        } else if (sortedKeys.get(0) > 0) {

            xx1 = pInitX + 5;
            yy1 = pInitY;
            yy2 = (int) (5 + Math.round(((warehouse.getShelfWidth() + warehouse.getAisleWidth()) * scaleMultiplierHalf) + ((((warehouse.getShelfWidth() + warehouse.getAisleWidth()) * scaleMultiplier)) * sortedKeys.get(0))));

            drawLine(g, xx1, yy1, xx1, yy2, graphicPosition);
        }

        double DistanciaUltimoPasilloImpar = 0;
        boolean primero = true;
        while (aisleIterator.hasNext()) {

            int pasillo = aisleIterator.next();
            if (Pasillos_max_dis.size() % 2 == 1 && !aisleIterator.hasNext()) {
                yy1 = (int) (Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasillo)));
                xx1 = (int) (5 + Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf));
                xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplier) + (Pasillos_max_dis.get(pasillo) * scaleMultiplier)));
                drawLine(g, pInitX, yy1 + 5, xx2, yy1 + 5, graphicPosition);
                drawLine(g, xx2, yy1, xx2, yy1 + 5, graphicPosition);//une la doble vuelta en mismo pasillo
                DistanciaUltimoPasilloImpar = Pasillos_max_dis.get(pasillo);
            } else if (!aisleIterator.hasNext()) {
                yy1 = (int) (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasillo)));
                xx1 = (int) (Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf));
                xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplierOneHalf) + (warehouse.getShelfLength() * scaleMultiplier)));
            } else {
                yy1 = (int) (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplierHalf) + (((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier)) * pasillo)));
                xx1 = (int) (5 + Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf));
                xx2 = (int) (5 + Math.round((warehouse.getAisleWidth() * scaleMultiplierOneHalf) + (warehouse.getShelfLength() * scaleMultiplier)));
            }

            if (primero && warehouse.getDepotPlacement() == DEPOT_CENTER && (sortedKeys.get(0) < (warehouse.getNumberOfAisles() / 2.0))) {
                xx1 -= 5;
            }

            drawLine(g, xx1, yy1, xx2, yy1, graphicPosition);
            corredor++;
            if (corredor == 1 && !aisleIterator.hasNext()) {
                p1 = new Point(pInitX, yy1 + 5);
            } else if (corredor == 1) {
                p1 = new Point(xx2, yy1);
            } else if (corredor % 2 == 0) {
                drawLine(g, p1.x, p1.y, xx2, yy1, graphicPosition);
                p1 = new Point(xx1, yy1);
            } else if (corredor % 2 == 1 && !aisleIterator.hasNext()) {
                drawLine(g, xx1, yy1, p1.x, p1.y, graphicPosition);
                p1 = new Point(pInitX, yy1 + 5);
            } else if (corredor % 2 == 1) {
                drawLine(g, xx1, yy1, p1.x, p1.y, graphicPosition);
                p1 = new Point(xx2, yy1);
            }
            primero = false;
        }
        //recorrido desde el último pasillo al inicio
        drawLine(g, pInitX, pInitY, pInitX, p1.y, graphicPosition);

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
