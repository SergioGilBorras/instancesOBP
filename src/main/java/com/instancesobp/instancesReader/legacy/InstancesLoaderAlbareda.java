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
package com.instancesobp.instancesReader.legacy;

import com.instancesobp.models.Aisles;
import com.instancesobp.models.Order;
import com.instancesobp.models.Product;
import com.instancesobp.models.Warehouse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for loading warehouse instances from files
 * following the Albareda format. It reads layout and order files to
 * construct a warehouse object with its associated orders, aisles, and products.
 * The class uses the configuration provided by the `ReadConsoleInfo` object
 * to determine the file paths and instance details.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class InstancesLoaderAlbareda {

    /**
     * Configuration object containing instance details.
     */
    private final ReadConsoleInfo consoleInfo;
    /**
     * Name of the instance being loaded.
     */
    private String instanceName;
    /**
     * Warehouse object constructed from the loaded data.
     */
    private Warehouse warehouse;

    /**
     * Constructor for the InstancesLoader of Albareda class.
     * Initializes the loader with the provided configuration.
     *
     * @param consoleInfo The configuration object containing instance details.
     */
    public InstancesLoaderAlbareda(ReadConsoleInfo consoleInfo) {
        this.warehouse = new Warehouse();
        this.consoleInfo = consoleInfo;
    }

    /**
     * Retrieves the constructed warehouse object.
     *
     * @return The warehouse object.
     */
    public Warehouse getWarehouse() {
        return this.warehouse;
    }

    /**
     * Retrieves the name of the loaded instance.
     *
     * @return The name of the instance.
     */
    public String getName() {
        return this.instanceName;
    }

    /**
     * Executes the loading process for the instance.
     * Reads the layout and order files, parses their content, and constructs
     * the warehouse object with its associated aisles, orders, and products.
     */
    public void run() {
        int numberOfOrders = 0;
        int numberOfAisles = 0;
        int numberOfItems = 0;
        int numberOfSlots = 0;
        int depotPosition = 0;
        int itemsLocation = 0;
        double shelfLength = 0;
        double shelfWidth = 0;
        double aisleWidth = 0;
        double workerCapacity = 0;
        double pickingTime = 0;
        double outsideTurnTime = 0;
        double insideTurnTime = 0;
        List<Order> orders = new ArrayList<>();
        List<Aisles> aisles = new ArrayList<>();

        // Construct layout file path
        String layoutFile = "./Warehouses_instances/legacy/albareda/W" +
                consoleInfo.getWarehouseNumber() +
                "/" +
                consoleInfo.getBatchesNumber() +
                "/wsrp_input_layout_0" +
                consoleInfo.getWarehouseNumber() +
                "_" +
                consoleInfo.getInstanceNumber() +
                ".txt";

        // Construct order file path
        String orderFile = "./Warehouses_instances/legacy/albareda/W" +
                consoleInfo.getWarehouseNumber() +
                "/" +
                consoleInfo.getBatchesNumber() +
                "/wsrp_input_pedido_0" +
                consoleInfo.getWarehouseNumber() +
                "_" +
                consoleInfo.getInstanceNumber() +
                ".txt";

        // Read layout file
        try (BufferedReader reader = new BufferedReader(new FileReader(layoutFile))) {
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                switch (lineNumber) {
                    case 2: {
                        String[] values = line.split(" ");
                        numberOfAisles = Integer.parseInt(values[0]);
                        numberOfSlots = Integer.parseInt(values[1]);
                        break;
                    }
                    case 4:
                        // depot location (0 -> at bottom left, 1 -> at botton centre)
                        depotPosition = Integer.parseInt(line);
                        break;
                    case 6:
                        //item location (0 -> ABC, 1 -> Random)
                        itemsLocation = Integer.parseInt(line);
                        break;
                    case 8: {
                        String[] values = line.split(" ");
                        shelfLength = Float.parseFloat(values[0]) - Float.parseFloat(values[1]);
                        shelfWidth = Float.parseFloat(values[1]);
                        break;
                    }
                    case 10:
                        aisleWidth = Float.parseFloat(line);
                        break;
                    case 12:
                        workerCapacity = Float.parseFloat(line);
                        break;
                    case 14:
                        pickingTime = Float.parseFloat(line);
                        break;
                    case 16: {
                        String[] values = line.split(" ");
                        outsideTurnTime = Float.parseFloat(values[0]);
                        insideTurnTime = Float.parseFloat(values[1]);
                        break;
                    }
                    default:
                        break;
                }

                if (lineNumber > 17 && lineNumber < (17 + numberOfAisles)) {
                    String[] values = line.split(" ");
                    Aisles aisle = new Aisles(
                            Integer.parseInt(values[0]),
                            Float.parseFloat(values[1]),
                            Float.parseFloat(values[2]),
                            Integer.parseInt(values[3])
                    );
                    aisles.add(aisle);
                }

                if (line.equals("9999")) {
                    break;
                }

                lineNumber++;
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("[Error] Layout file: " + e.getMessage());
            //e.printStackTrace();
        }

        // Read order file
        try (BufferedReader reader = new BufferedReader(new FileReader(orderFile))) {
            String line;
            int lineNumber = 1;
            int nextBatchLine = 4;
            Order order = null;
            int orderId = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (lineNumber == 2) {
                    numberOfOrders = Integer.parseInt(line);
                } else if (lineNumber == nextBatchLine) {
                    if (order != null) {
                        orders.add(order);
                    }
                    String[] values = line.split(" ");
                    order = new Order(orderId, (long) Double.parseDouble(values[0]), Integer.parseInt(values[1]));
                    orderId++;
                    nextBatchLine += Integer.parseInt(values[1]) + 1;
                } else if (order != null) {
                    String[] values = line.split(" ");
                    Product product = new Product(
                            Integer.parseInt(values[4]),
                            Integer.parseInt(values[0]),
                            Integer.parseInt(values[1]),
                            Double.parseDouble(values[2]),
                            Double.parseDouble(values[3])
                    );
                    order.addProduct(product);
                    numberOfItems++;
                }

                lineNumber++;
            }
            if (order != null) {
                orders.add(order);
            }
        } catch (Exception e) {
            System.err.println("[Error] Order list file: " + e.getMessage());
            //e.printStackTrace();
        }

        // Construct warehouse object
        instanceName = consoleInfo.toStringShort();
        warehouse = new Warehouse(
                numberOfOrders,
                numberOfAisles,
                numberOfItems,
                depotPosition,
                itemsLocation,
                shelfLength,
                shelfWidth,
                aisleWidth,
                workerCapacity,
                pickingTime,
                outsideTurnTime,
                insideTurnTime,
                numberOfSlots,
                aisles,
                orders
        );
    }
}