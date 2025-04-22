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
package com.instancesobp.evaluationResults.graphics;

import com.instancesobp.Configuration;
import com.instancesobp.evaluationResults.graphics.algorithms.*;
import com.instancesobp.models.Batch;
import com.instancesobp.models.Order;
import com.instancesobp.models.Product;
import com.instancesobp.models.Warehouse;

import javax.swing.*;
import java.awt.*;
import java.awt.Graphics;
import java.util.List;
import java.util.Random;

import static com.instancesobp.evaluationResults.graphics.Utils.*;
import static com.instancesobp.utils.Constants.DEPOT_CORNER;

/**
 * Custom JPanel for rendering the warehouse layout and visualizing routes, items, and batches.
 * This class provides methods to draw the warehouse structure, items, and routing paths
 * based on the selected algorithm and user input.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class JPanelColor extends JPanel {

    /**
     * The warehouse object containing layout and configuration details.
     */
    private final Warehouse warehouse;

    /**
     * List of batches to be visualized.
     */
    private final List<Batch> batchList;

    /**
     * Multiplier for scaling the warehouse dimensions in the graphical representation.
     */
    private final int scaleMultiplier;
    /**
     * Precomputed value for 0.5 times the scale multiplier.
     */
    private final double scaleMultiplierHalf;
    /**
     * Coefficient for width adjustment in graphical representation.
     */
    private final double widthCoefficient = 1.0;
    /**
     * Offset for width adjustment in graphical representation.
     */
    private final int widthOffset = 0;
    /**
     * Coefficient for height adjustment in graphical representation.
     */
    private final double heightCoefficient = 1.0;
    /**
     * Current graphical position (rotated or default).
     */
    protected int graphicPosition = HORIZONTAL_GRAPHIC_POSITION;
    /**
     * Indicates the current item, batch, or order being visualized.
     */
    protected int currentItem = 0;
    /**
     * Indicates the algorithm being used for routing visualization.
     */
    protected int algorithm = 0;
    /**
     * Indicates whether all items, batches, or orders should be visualized.
     */
    protected int viewAll = 0;

    /**
     * Constructor for the JPanelColor class.
     *
     * @param warehouse       The warehouse object containing layout and configuration details.
     * @param batchList       The list of batches to be visualized.
     * @param scaleMultiplier The multiplier for scaling the warehouse dimensions.
     */
    public JPanelColor(Warehouse warehouse, List<Batch> batchList, int scaleMultiplier) {
        this.warehouse = warehouse;
        this.batchList = batchList;
        this.scaleMultiplier = scaleMultiplier;
        this.scaleMultiplierHalf = scaleMultiplier * 0.5;
    }


    /**
     * Draws the measurements of the warehouse layout for visualization purposes.
     *
     * @param g The Graphics object used for drawing.
     */
    public void drawMeasurements(Graphics g) {
        int numberOfSlotsInAisle = (warehouse.getNumberOfSlots() / warehouse.getNumberOfAisles()) / 2;
        double tam_hueco = (warehouse.getShelfLength() / numberOfSlotsInAisle);

        g.setColor(Color.BLACK);
        drawLineMeasureV(g,
                formatString(warehouse.getShelfWidth()),
                6 + (int) (((warehouse.getShelfWidth() * 0.5) + (warehouse.getAisleWidth())) * scaleMultiplier),
                5 + (int) (((warehouse.getShelfWidth() * 1.5) + (warehouse.getAisleWidth())) * scaleMultiplier),
                5 + (int) (((warehouse.getAisleWidth() * 1.5) + warehouse.getShelfLength()) * scaleMultiplier), graphicPosition);
        drawLineMeasureH(g,

                formatString(warehouse.getAisleWidth()),
                5,
                5 + (int) (warehouse.getAisleWidth() * scaleMultiplier),
                5 + (int) (warehouse.getAisleWidth() * scaleMultiplier), graphicPosition);
        drawLineMeasureH(g,
                formatString(tam_hueco),
                5 + (int) (warehouse.getAisleWidth() * scaleMultiplier),
                5 + (int) ((warehouse.getAisleWidth() + tam_hueco) * scaleMultiplier),
                5 + (int) ((warehouse.getAisleWidth() + warehouse.getShelfWidth()) * scaleMultiplier * 1.5), graphicPosition);
        drawLineMeasureH(g,
                formatString(warehouse.getShelfLength()),
                5 + (int) (warehouse.getAisleWidth() * scaleMultiplier),
                5 + (int) ((warehouse.getShelfLength() + warehouse.getAisleWidth()) * scaleMultiplier),
                5 + (int) (warehouse.getAisleWidth() * scaleMultiplier), graphicPosition);
    }

    /**
     * Draws the warehouse layout, including shelves, aisles, and depot.
     *
     * @param g The Graphics object used for drawing.
     */
    public void drawWarehouse(Graphics g) {
        double ancho_warehouse = (((warehouse.getShelfWidth() + warehouse.getAisleWidth()) * warehouse.getNumberOfAisles()) * scaleMultiplier);// + (warehouse.getAncho_pasillos() * scaleMultiplier);
        double largo_warehouse = (warehouse.getShelfLength() + (warehouse.getAisleWidth() * 2)) * scaleMultiplier;
        int numberOfSlotsInAisle = (warehouse.getNumberOfSlots() / warehouse.getNumberOfAisles()) / 2;
        double tam_hueco = ((warehouse.getShelfLength() / numberOfSlotsInAisle) * scaleMultiplier);

        this.setBackground(Color.WHITE);
        g.setColor(Color.BLACK);
        drawRect(g, 4, 4, (int) Math.round(largo_warehouse) + 1, (int) Math.round(ancho_warehouse) + 1, graphicPosition);

        g.setColor(new Color(255, 120, 120));
        fillRect(g, (int) (Math.round(warehouse.getAisleWidth() * scaleMultiplier) + 5),
                5,
                (int) Math.round(warehouse.getShelfLength() * scaleMultiplier),
                (int) Math.round(warehouse.getShelfWidth() * scaleMultiplierHalf), graphicPosition);

        fillRect(g, (int) (Math.round(warehouse.getAisleWidth() * scaleMultiplier) + 5),
                (int) ((Math.round(ancho_warehouse - (warehouse.getShelfWidth() * scaleMultiplierHalf))) + 5),
                (int) Math.round(warehouse.getShelfLength() * scaleMultiplier),
                (int) Math.round(warehouse.getShelfWidth() * scaleMultiplierHalf), graphicPosition);

        for (int x = 0; x < warehouse.getNumberOfAisles() - 1; x++) {
            g.setColor(new Color(255, 120, 120));
            fillRect(g, (int) (Math.round(warehouse.getAisleWidth() * scaleMultiplier) + 5),
                    (int) (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplier) + (x * ((warehouse.getAisleWidth() * scaleMultiplier) + (warehouse.getShelfWidth() * scaleMultiplier))))),
                    (int) Math.round(warehouse.getShelfLength() * scaleMultiplier),
                    (int) Math.round(warehouse.getShelfWidth() * scaleMultiplier), graphicPosition
            );
            g.setColor(Color.BLACK);
            for (int i = 0; i <= numberOfSlotsInAisle; i++) {
                drawLine(g, (int) (Math.round((warehouse.getAisleWidth() * scaleMultiplier + (i * tam_hueco))) + 5),
                        (int) ((5 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplier) + (x * ((warehouse.getAisleWidth() * scaleMultiplier) + (warehouse.getShelfWidth() * scaleMultiplier)))))),
                        (int) Math.round((warehouse.getAisleWidth() * scaleMultiplier) + 5 + (i * tam_hueco)),
                        (int) (4 + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplier) + (x * ((warehouse.getAisleWidth() * scaleMultiplier) + (warehouse.getShelfWidth() * scaleMultiplier))) + (warehouse.getShelfWidth() * scaleMultiplier))), graphicPosition);
            }
            drawLine(g, (int) (Math.round(warehouse.getAisleWidth() * scaleMultiplier) + 5),
                    (int) (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier) + (x * ((warehouse.getAisleWidth() * scaleMultiplier) + (warehouse.getShelfWidth() * scaleMultiplier))))),
                    (int) (Math.round((warehouse.getAisleWidth() * scaleMultiplier) + (warehouse.getShelfLength() * scaleMultiplier)) + 5),
                    (int) (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplier) + (warehouse.getAisleWidth() * scaleMultiplier) + (x * ((warehouse.getAisleWidth() * scaleMultiplier) + (warehouse.getShelfWidth() * scaleMultiplier))))), graphicPosition);
        }

        g.setColor(Color.BLACK);
        for (int i = 0; i <= numberOfSlotsInAisle; i++) {
            drawLine(g, (int) (Math.round((warehouse.getAisleWidth() * scaleMultiplier) + 5 + (i * tam_hueco))),
                    5,
                    (int) Math.round((warehouse.getAisleWidth() * scaleMultiplier) + 5 + (i * tam_hueco)),
                    (int) Math.round(warehouse.getShelfWidth() * scaleMultiplierHalf) + 4, graphicPosition);
            drawLine(g, (int) (Math.round((warehouse.getAisleWidth() * scaleMultiplier) + 5 + (i * tam_hueco))),
                    (int) (Math.round(ancho_warehouse - (warehouse.getShelfWidth() * scaleMultiplierHalf)) + 5),
                    (int) Math.round((warehouse.getAisleWidth() * scaleMultiplier) + 5 + (i * tam_hueco)),
                    (int) (Math.round(ancho_warehouse) + 5), graphicPosition);

        }
    }

    /**
     * Draws the depot area in the warehouse.
     *
     * @param g The Graphics object used for drawing.
     */
    public void drawDepot(Graphics g) {
        g.setColor(new Color(0, 255, 0));

        if (warehouse.getDepotPlacement() == DEPOT_CORNER) {
            fillRect(g, 5, (int) (5 + Math.round(warehouse.getShelfWidth() * scaleMultiplierHalf)), (int) Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf), (int) Math.round(warehouse.getAisleWidth() * scaleMultiplier), graphicPosition);
        } else {
            double ancho_warehouse = ((warehouse.getShelfWidth() + warehouse.getAisleWidth()) * warehouse.getNumberOfAisles()) * scaleMultiplier;
            fillRect(g, 5, (int) (6 + Math.round((ancho_warehouse / 2.0) - (warehouse.getAisleWidth() * scaleMultiplierHalf))), (int) Math.round(warehouse.getAisleWidth() * scaleMultiplierHalf), (int) Math.round(warehouse.getAisleWidth() * scaleMultiplier), graphicPosition);
        }
    }

    /**
     * Draws the items in the warehouse based on the current selection.
     *
     * @param g The Graphics object used for drawing.
     */
    public void drawItems(Graphics g) {
        Random rnd = new Random(Configuration.SEED);
        int numberOfSlotsInAisle = (warehouse.getNumberOfSlots() / warehouse.getNumberOfAisles()) / 2;
        double tam_hueco = ((warehouse.getShelfLength() / numberOfSlotsInAisle) * scaleMultiplier);
        switch (viewAll) {
            case 1, 2 -> {
                for (Batch lBatch : batchList) {
                    if (viewAll == 1) {
                        g.setColor(new Color(0, 0, 0));
                    } else if (viewAll == 2) {
                        int cr = (int) Math.round(rnd.nextDouble() * 255);
                        int cg = (int) Math.round(rnd.nextDouble() * 255);
                        int cb = (int) Math.round(rnd.nextDouble() * 255);
                        g.setColor(new Color(cr, cg, cb));
                    }

                    for (Order order : lBatch.getOrders()) {
                        for (Product product : order.getProducts()) {
                            int ini_t = getIniT(product);
                            int ini_l = (int) (Math.round((warehouse.getAisleWidth() + (product.getHeightPosition() * widthCoefficient)) * scaleMultiplier) + widthOffset);
                            fillRect(g, ini_l, ini_t, (int) Math.round(tam_hueco / 2.0), (int) Math.round((warehouse.getShelfWidth() * scaleMultiplier) / 4), graphicPosition);
                        }
                    }
                }
            }
            case 3 -> {
                int cr = (int) Math.round(rnd.nextDouble() * 255);
                int cg = (int) Math.round(rnd.nextDouble() * 255);
                int cb = (int) Math.round(rnd.nextDouble() * 255);
                g.setColor(new Color(cr, cg, cb));
                for (Order order : batchList.get(currentItem).getOrders()) {
                    for (Product product : order.getProducts()) {
                        int ini_t = getIniT(product);
                        int ini_l = (int) (Math.round((warehouse.getAisleWidth() + (product.getHeightPosition() * widthCoefficient)) * scaleMultiplier) + widthOffset);
                        fillRect(g, ini_l, ini_t, (int) Math.round(tam_hueco / 2.0), (int) Math.round((warehouse.getShelfWidth() * scaleMultiplier) / 4), graphicPosition);
                    }
                }
            }
            case 4 -> {
                int cr = (int) Math.round(rnd.nextDouble() * 255);
                int cg = (int) Math.round(rnd.nextDouble() * 255);
                int cb = (int) Math.round(rnd.nextDouble() * 255);
                g.setColor(new Color(cr, cg, cb));
                for (Product product : warehouse.getOrders().get(currentItem).getProducts()) {
                    int ini_t = getIniT(product);
                    int ini_l = (int) (Math.round((warehouse.getAisleWidth() + (product.getHeightPosition() * widthCoefficient)) * scaleMultiplier) + widthOffset);
                    fillRect(g, ini_l, ini_t, (int) Math.round(tam_hueco / 2.0), (int) Math.round((warehouse.getShelfWidth() * scaleMultiplier) / 4), graphicPosition);
                }
            }
            case 5 -> {
                int contador = 0;
                int cr = (int) Math.round(rnd.nextDouble() * 255);
                int cg = (int) Math.round(rnd.nextDouble() * 255);
                int cb = (int) Math.round(rnd.nextDouble() * 255);
                g.setColor(new Color(cr, cg, cb));
                for (Order order : warehouse.getOrders()) {
                    for (Product product : order.getProducts()) {
                        double ini_t;
                        if (contador == currentItem) {
                            if (product.getSide() == 0) {
                                ini_t = (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplier) / 8) + Math.round(product.getAisle() * ((warehouse.getAisleWidth() + warehouse.getShelfWidth()) * scaleMultiplier)));
                            } else {
                                ini_t = (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplier) / 8) + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplier) + (product.getAisle() * ((warehouse.getAisleWidth() + warehouse.getShelfWidth()) * scaleMultiplier))));

                            }
                            ini_t *= heightCoefficient;
                            int ini_l = (int) (Math.round((warehouse.getAisleWidth() + (product.getHeightPosition() * widthCoefficient)) * scaleMultiplier) + widthOffset);
                            fillRect(g, ini_l, (int) ini_t, (int) Math.round(tam_hueco / 2.0), (int) Math.round((warehouse.getShelfWidth() * scaleMultiplier) / 4), graphicPosition);

                        }
                        contador++;
                    }
                }
            }
            default -> {
            }
        }
    }

    /**
     * Calculates the initial vertical position (iniT) of a product in the warehouse layout.
     * The position is determined based on the product's side (left or right), aisle, and
     * scaling factors for graphical representation.
     *
     * @param product The product whose vertical position is to be calculated.
     * @return The calculated vertical position as an integer.
     */
    private int getIniT(Product product) {
        double ini_t;
        if (product.getSide() == 0) {
            ini_t = (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplier) / 8) + Math.round(product.getAisle() * ((warehouse.getAisleWidth() + warehouse.getShelfWidth()) * scaleMultiplier)));
        } else {
            ini_t = (5 + Math.round((warehouse.getShelfWidth() * scaleMultiplier) / 8) + Math.round((warehouse.getShelfWidth() * scaleMultiplierHalf) + (warehouse.getAisleWidth() * scaleMultiplier) + (product.getAisle() * ((warehouse.getAisleWidth() + warehouse.getShelfWidth()) * scaleMultiplier))));

        }
        ini_t *= heightCoefficient;
        return (int) ini_t;
    }

    /**
     * Draws the routing path based on the selected algorithm.
     *
     * @param g The Graphics object used for drawing.
     */
    public void drawRoute(Graphics g) {
        switch (algorithm) {
            case 1:
                //S-Shape
                SShape sShape = new SShape(warehouse, batchList, currentItem, scaleMultiplier, graphicPosition);
                sShape.drawPathS_shape(g);
                break;
            case 2:
                //Largest Gap
                LargestGap largestGap = new LargestGap(warehouse, batchList, currentItem, scaleMultiplier, graphicPosition);
                largestGap.drawPathLargestGap(g);
                break;
            case 3:
                //combine
                Combined combined = new Combined(warehouse, batchList, currentItem, scaleMultiplier, graphicPosition);
                combined.drawPathCombine(g);
                break;
            case 4:
                //combine Plus
                CombinedPlus combinedPlus = new CombinedPlus(warehouse, batchList, currentItem, scaleMultiplier, graphicPosition);
                combinedPlus.drawPathCombinedPlus(g);
                break;
            case 5:
                //Exacto Ratliff
                RatliffAndRosenthal ratliffAndRosenthal = new RatliffAndRosenthal(warehouse, batchList, currentItem, scaleMultiplier, graphicPosition);
                ratliffAndRosenthal.drawExactPathRatliff(g);
                break;
            default:
                break;
        }
    }

    /**
     * Overrides the paintComponent method to render the warehouse layout, depot, items, routes,
     * and measurements on the panel. This method ensures that all graphical elements are drawn
     * based on the current state and user selections.
     *
     * @param g The Graphics object used for drawing.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Calls the paintComponent method of the superclass

        drawWarehouse(g);
        drawDepot(g);
        drawItems(g);
        drawRoute(g);

        if (algorithm == 0) {
            drawMeasurements(g);
        }

    }

}
