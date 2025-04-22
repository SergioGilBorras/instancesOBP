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

import com.instancesobp.models.Batch;
import com.instancesobp.models.Warehouse;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.instancesobp.evaluationResults.graphics.Utils.*;

/**
 * This class is responsible for generating graphical representations of the warehouse layout
 * and the batches of products. It provides controls for visualizing items, orders, and batches
 * in the warehouse, as well as applying routing algorithms.
 * The graphical interface is built using Swing components, and the layout is dynamically
 * adjusted based on the warehouse dimensions and user interactions.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public final class Graphics {

    /**
     * The warehouse object containing layout and configuration details.
     */
    private final Warehouse warehouse;
    /**
     * List of batches to be visualized.
     */
    private final List<Batch> batchList;
    /**
     * Main controlFrame for the graphical controls.
     */
    private final JFrame controlFrame;
    /**
     * controlFrame for displaying the warehouse layout.
     */
    private final JFrame warehouseFrame;
    /**
     * Multiplier for scaling the warehouse dimensions in the graphical representation.
     */
    private int scaleMultiplier = 10;
    /**
     * Custom JPanel for rendering the warehouse layout.
     */
    private JPanelColor warehousePanel = null;

    /**
     * Current graphical position (rotated or default).
     */
    private int graphicPosition = HORIZONTAL_GRAPHIC_POSITION;

    /**
     * Width of the warehouse in the graphical representation.
     */
    private double warehouseWidth;

    /**
     * Length of the warehouse in the graphical representation.
     */
    private double warehouseLength;

    /**
     * Constructor for the Graphics class.
     * Initializes the graphical interface and calculates the warehouse dimensions.
     *
     * @param warehouse The warehouse object containing layout and configuration details.
     * @param batchList The list of batches to be visualized.
     */
    public Graphics(Warehouse warehouse, List<Batch> batchList) {
        this.warehouse = warehouse;
        this.batchList = batchList;

        warehouseWidth = (((warehouse.getShelfWidth() + warehouse.getAisleWidth()) * warehouse.getNumberOfAisles()) * scaleMultiplier) + 40;
        warehouseLength = ((warehouse.getShelfLength() + (warehouse.getAisleWidth() * 2)) * scaleMultiplier) + 17;

        // crea marco para objeto JPanelColor
        warehouseFrame = new JFrame("Warehouse layout");
        warehouseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        warehouseFrame.setLayout(new BorderLayout());

        controlFrame = new JFrame("Control Warehouse layout");
        controlFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        controlFrame.setLayout(new BorderLayout());

        paintWareHouse();
        renderControls();
    }

    /**
     * Paints the warehouse layout by creating or updating the JPanelColor instance.
     */
    public void paintWareHouse() {
        if (warehousePanel != null) {
            warehouseFrame.remove(warehousePanel);
        }
        warehousePanel = new JPanelColor(warehouse, batchList, scaleMultiplier); // create JPanelColor
        warehouseFrame.add(warehousePanel, BorderLayout.CENTER); // agrega jPanelColor a marco
        warehouseFrame.setVisible(true); // muestra el marco
        warehouseFrame.setResizable(false);

        adjustGraphicPosition();

    }

    /**
     * Adjusts the graphical position of the warehouse layout based on the current state.
     * Updates the dimensions and orientation of the graphical representation.
     */
    private void adjustGraphicPosition() {
        warehousePanel.graphicPosition = graphicPosition;
        if (graphicPosition == VERTICAL_GRAPHIC_POSITION) {
            warehouseWidth = (((warehouse.getShelfWidth() + warehouse.getAisleWidth()) * warehouse.getNumberOfAisles()) * scaleMultiplier) + 17;
            warehouseLength = ((warehouse.getShelfLength() + (warehouse.getAisleWidth() * 2)) * scaleMultiplier) + 40;
            warehouseFrame.setSize((int) Math.round(warehouseWidth), (int) Math.round(warehouseLength)); // establece el tamaño del marco
            warehousePanel.setPreferredSize(new Dimension((int) Math.round(warehouseWidth), (int) Math.round(warehouseLength) - 20));
        } else {
            warehouseWidth = (((warehouse.getShelfWidth() + warehouse.getAisleWidth()) * warehouse.getNumberOfAisles()) * scaleMultiplier) + 40;
            warehouseLength = ((warehouse.getShelfLength() + (warehouse.getAisleWidth() * 2)) * scaleMultiplier) + 17;
            warehouseFrame.setSize((int) Math.round(warehouseLength) + 10, (int) Math.round(warehouseWidth) + 10); // establece el tamaño del marco
            warehousePanel.setPreferredSize(new Dimension((int) Math.round(warehouseLength), (int) Math.round(warehouseWidth) - 20));
        }

        warehousePanel.viewAll = 0;
        warehousePanel.algorithm = 0;
        warehousePanel.repaint();
    }

    /**
     * Renders the control panel for interacting with the graphical interface.
     * Includes buttons and input fields for various visualization and routing options.
     */
    public void renderControls() {
        JPanel pan = new JPanel();
        pan.setLayout(new GridLayout(0, 3, 7, 7));

        JPanel panIni = getJPanelInitial();

        JLabel JL_num_item = new JLabel("Item number: (0-" + (warehouse.getNumberOfItems() - 1) + ")");
        JL_num_item.setForeground(labelsColor);
        pan.add(JL_num_item);

        JTextField JT_num_item = new JTextField("0");
        pan.add(JT_num_item);

        JButton JB_num_item = getJButtonNumItems(JT_num_item);
        pan.add(JB_num_item);

        JLabel JL_num_pedido = new JLabel("Order number: (0-" + (warehouse.getOrders().size() - 1) + ")");
        JL_num_pedido.setForeground(labelsColor);
        pan.add(JL_num_pedido);

        JTextField JT_num_pedido = new JTextField("0");
        pan.add(JT_num_pedido);

        JButton JB_num_pedido = getJButtonNumOrders(JT_num_pedido);
        pan.add(JB_num_pedido);

        JLabel JL_num_lote = new JLabel("Batch number: (0-" + (batchList.size() - 1) + ")");
        JL_num_lote.setForeground(labelsColor);
        pan.add(JL_num_lote);

        JTextField JT_num_lote = new JTextField("0");
        pan.add(JT_num_lote);

        JButton JB_num_lote = getJButtonNumBatches(JT_num_lote);
        pan.add(JB_num_lote);

        JButton JB_all_items = new JButton("Show all items");
        JB_all_items.addActionListener(ae -> {
            warehousePanel.graphicPosition = graphicPosition;
            warehousePanel.viewAll = 1;
            warehousePanel.algorithm = 0;
            warehousePanel.repaint();
        });
        pan.add(JB_all_items);

        JButton JB_all_item_by_batch = new JButton("Show all batches");
        JB_all_item_by_batch.addActionListener(ae -> {
            warehousePanel.graphicPosition = graphicPosition;
            warehousePanel.viewAll = 2;
            warehousePanel.repaint();
        });
        pan.add(JB_all_item_by_batch);

        JButton JB_limpiar = new JButton("Clear all");
        JB_limpiar.addActionListener(ae -> {
            warehousePanel.graphicPosition = graphicPosition;
            warehousePanel.viewAll = 0;
            warehousePanel.algorithm = 0;
            warehousePanel.repaint();
        });
        pan.add(JB_limpiar);

        JButton JB_A_S_Shape = getJButtonRoutingAlg("S-Shape", JT_num_lote, 1);
        pan.add(JB_A_S_Shape);

        JButton JB_A_Largest_Gap = getJButtonRoutingAlg("Largest Gap", JT_num_lote, 2);
        pan.add(JB_A_Largest_Gap);

        JButton JB_A_Combined = getJButtonRoutingAlg("Combined", JT_num_lote, 3);
        pan.add(JB_A_Combined);

        JButton JB_A_CombinePlus = getJButtonRoutingAlg("Combine Plus", JT_num_lote, 4);
        pan.add(JB_A_CombinePlus);

        JButton JB_A_Exact_Ratliff = getJButtonRoutingAlg("Ratliff & Rosenthal", JT_num_lote, 5);
        pan.add(JB_A_Exact_Ratliff);

        controlFrame.add(panIni, BorderLayout.NORTH); // agrega panIni a marco
        controlFrame.add(pan, BorderLayout.SOUTH);
        controlFrame.setSize(500, 300); // establece el tamaño del marco
        controlFrame.setVisible(true); // muestra el marco
        controlFrame.setResizable(false);

    }

    /**
     * Creates a button for selecting a routing algorithm and assigns its action listener.
     *
     * @param algorithmName  The name of the routing algorithm.
     * @param batchTextField The text field for entering the batch number.
     * @param algorithmId    The ID of the routing algorithm.
     * @return A JButton configured for the specified routing algorithm.
     */
    private JButton getJButtonRoutingAlg(String algorithmName, JTextField batchTextField, int algorithmId) {
        JButton JB_A_Largest_Gap = new JButton(algorithmName);
        JB_A_Largest_Gap.addActionListener(ae -> {
            try {
                int nl = Integer.parseInt(batchTextField.getText());
                if (nl > (batchList.size() - 1) || nl < 0) {
                    JOptionPane.showMessageDialog(controlFrame,
                            "Introduce un valor de lote correcto (0-" + (batchList.size() - 1) + ")",
                            "Error",
                            JOptionPane.WARNING_MESSAGE);
                    batchTextField.setText("0");
                } else {
                    warehousePanel.graphicPosition = graphicPosition;
                    warehousePanel.currentItem = nl;
                    warehousePanel.viewAll = 3;
                    warehousePanel.algorithm = algorithmId;
                    warehousePanel.repaint();
                    batchTextField.setText(String.valueOf((nl + 1)));
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(controlFrame,
                        "Error: " + e.getMessage() + "\nIntroduce un valor de lote correcto (0-" + (batchList.size() - 1) + ")",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        return JB_A_Largest_Gap;
    }

    /**
     * Creates a button for loading a specific batch and assigns its action listener.
     *
     * @param batchTextField The text field for entering the batch number.
     * @return A JButton configured for loading a batch.
     */
    private JButton getJButtonNumBatches(JTextField batchTextField) {
        JButton JB_num_lote = new JButton("Load batch");
        JB_num_lote.addActionListener(ae -> {
            try {
                int nl = Integer.parseInt(batchTextField.getText());
                if (nl > (batchList.size() - 1) || nl < 0) {
                    JOptionPane.showMessageDialog(controlFrame,
                            "Enter a valid value (0-" + (batchList.size() - 1) + ")",
                            "Error",
                            JOptionPane.WARNING_MESSAGE);
                    batchTextField.setText("0");
                } else {
                    warehousePanel.graphicPosition = graphicPosition;
                    warehousePanel.currentItem = nl;
                    warehousePanel.viewAll = 3;
                    warehousePanel.repaint();
                    batchTextField.setText(String.valueOf((nl + 1) % batchList.size()));
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(controlFrame,
                        "Error: " + e.getMessage() + "\nEnter a valid value (0-" + (batchList.size() - 1) + ")",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        return JB_num_lote;
    }

    /**
     * Creates a button for loading a specific order and assigns its action listener.
     *
     * @param orderTextField The text field for entering the order number.
     * @return A JButton configured for loading an order.
     */
    private JButton getJButtonNumOrders(JTextField orderTextField) {
        JButton JB_num_pedido = new JButton("Load Order");
        JB_num_pedido.addActionListener(ae -> {
            try {
                int nl = Integer.parseInt(orderTextField.getText());
                if (nl > (warehouse.getOrders().size() - 1) || nl < 0) {
                    JOptionPane.showMessageDialog(controlFrame,
                            "Enter a valid value (0-" + (warehouse.getOrders().size() - 1) + ")",
                            "Error",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    warehousePanel.graphicPosition = graphicPosition;
                    warehousePanel.currentItem = nl;
                    warehousePanel.viewAll = 4;
                    warehousePanel.algorithm = 0;
                    warehousePanel.repaint();

                    orderTextField.setText(String.valueOf((nl + 1) % warehouse.getOrders().size()));
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(controlFrame,
                        "Error: " + e.getMessage() + "\nEnter a valid value (0-" + (warehouse.getOrders().size() - 1) + ")",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        return JB_num_pedido;
    }

    /**
     * Creates a button for loading a specific product and assigns its action listener.
     *
     * @param itemTextField The text field for entering the product number.
     * @return A JButton configured for loading a product.
     */
    private JButton getJButtonNumItems(JTextField itemTextField) {
        JButton JB_num_item = new JButton("Load product");
        JB_num_item.addActionListener(ae -> {
            try {
                int nl = Integer.parseInt(itemTextField.getText());
                if (nl > (warehouse.getNumberOfItems() - 1) || nl < 0) {
                    JOptionPane.showMessageDialog(controlFrame,
                            "Enter a valid value (0-" + (warehouse.getNumberOfItems() - 1) + ")",
                            "Error",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    warehousePanel.graphicPosition = graphicPosition;
                    warehousePanel.currentItem = nl;
                    warehousePanel.viewAll = 5;
                    warehousePanel.algorithm = 0;
                    warehousePanel.repaint();
                    itemTextField.setText(String.valueOf((nl + 1) % warehouse.getNumberOfItems()));
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(controlFrame,
                        "Error: " + e.getMessage() + "\nEnter a valid value (0-" + (warehouse.getNumberOfItems() - 1) + ")",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        return JB_num_item;
    }

    /**
     * Creates the initial control panel with options to rotate the warehouse and change the scale.
     *
     * @return A JPanel containing the initial controls.
     */
    private JPanel getJPanelInitial() {
        JPanel panIni = new JPanel();
        panIni.setLayout(new GridLayout(0, 3, 7, 7));

        JButton JB_rotar = new JButton("Rotate Warehouse");
        JB_rotar.addActionListener(ae -> {
            if (graphicPosition == HORIZONTAL_GRAPHIC_POSITION) {
                graphicPosition = VERTICAL_GRAPHIC_POSITION;
                adjustGraphicPosition();
            } else {
                graphicPosition = HORIZONTAL_GRAPHIC_POSITION;
                adjustGraphicPosition();
            }
        });
        panIni.add(JB_rotar);

        JTextField JT_multiplicador = new JTextField("10");
        panIni.add(JT_multiplicador);

        JButton JB_multiplicador = getJButtonChangeScala(JT_multiplicador);
        panIni.add(JB_multiplicador);
        return panIni;
    }

    /**
     * Creates a button for changing the scale multiplier and assigns its action listener.
     *
     * @param scaleTextField The text field for entering the new scale multiplier.
     * @return A JButton configured for changing the scale.
     */
    private JButton getJButtonChangeScala(JTextField scaleTextField) {
        JButton JB_multiplicador = new JButton("Load Scale");
        JB_multiplicador.addActionListener(ae -> {
            try {
                scaleMultiplier = Integer.parseInt(scaleTextField.getText());
                paintWareHouse();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(controlFrame,
                        "Error: " + e.getMessage() + "\nEnter a valid value.",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        return JB_multiplicador;
    }

}
