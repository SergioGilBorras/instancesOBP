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
import com.instancesobp.utils.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Integer.parseInt;

/**
 * This class is responsible for loading warehouse instances from files
 * following the Henn format. It reads layout and order files to construct
 * a warehouse object with its associated orders, aisles, and products.
 * The class uses the configuration provided by the `ReadConsoleInfo` object
 * to determine the file paths and instance details.
 * The Henn format is specific to a legacy system and includes details
 * about the warehouse layout, item locations, and order configurations.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class InstancesLoaderHenn {

    /**
     * Configuration object containing instance details.
     */
    private final ReadConsoleInfo consoleInfo;
    /**
     * Path to the layout file.
     */
    private String layoutFile;
    /**
     * Path to the order file.
     */
    private String orderFile;
    /**
     * Name of the instance being loaded.
     */
    private String instanceName;
    /**
     * Warehouse object constructed from the loaded data.
     */
    private Warehouse warehouse;

    /**
     * Constructor for the InstancesLoaderHenn class.
     * Initializes the loader with the provided configuration.
     *
     * @param consoleInfo The configuration object containing instance details.
     */
    public InstancesLoaderHenn(ReadConsoleInfo consoleInfo) {
        this.warehouse = new Warehouse();
        this.consoleInfo = consoleInfo;
    }

    /**
     * Determines the side of an aisle relative to the depot.
     *
     * @param numberOfAisles The total number of aisles.
     * @param depotPosition  The position of the depot.
     * @param aisleIndex     The index of the current aisle.
     * @return The side of the aisle (-1 for left, 0 for front, 1 for right).
     */
    private static int getSideAisleFromDepot(int numberOfAisles, int depotPosition, int aisleIndex) {
        int side;
        double middleAisle = Math.floor(numberOfAisles / 2.0);

        if (depotPosition == Constants.DEPOT_CORNER && aisleIndex == 0) {
            side = Constants.FRONT_SIDE_DEPOT;
        } else if (depotPosition == Constants.DEPOT_CORNER) {
            side = Constants.LEFT_SIDE_DEPOT;
        } else if (depotPosition == Constants.DEPOT_CENTER && aisleIndex < middleAisle) {
            side = Constants.RIGHT_SIDE_DEPOT;
        } else if (depotPosition == Constants.DEPOT_CENTER && aisleIndex == middleAisle) {
            side = Constants.FRONT_SIDE_DEPOT;
        } else {
            side = Constants.LEFT_SIDE_DEPOT;
        }
        return side;
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
        int depotPosition = Constants.DEPOT_CORNER;
        int itemsLocation = Constants.ABC_ITEMS_LOCATION;
        double shelfLength = 0;
        double shelfWidth = 0;
        double aisleWidth = 0;
        double workerCapacity = 0;
        double pickingTime = 0;
        double outsideTurnTime = 0;
        double insideTurnTime = 0;
        List<Order> orders = new ArrayList<>();
        List<Aisles> aisles = new ArrayList<>();

        // Locate layout and order files
        File folder = new File("./Warehouses_instances/legacy/W5A_Henn/" + consoleInfo.getItemsLocationString());
        if (!folder.isDirectory()) {
            System.out.println("Error: The entered path is not a valid directory " + folder.getAbsolutePath());
        } else if (folder.listFiles() == null) {
            System.out.println("Error: The directory is empty " + folder.getAbsolutePath());
        } else {
            for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
                if (!fileEntry.isDirectory() && fileEntry.getName().startsWith("sett" + consoleInfo.getSettingNumber())) {
                    layoutFile = "./Warehouses_instances/legacy/W5A_Henn/" + consoleInfo.getItemsLocationString() + "/" + fileEntry.getName();
                }

                if (!fileEntry.isDirectory() && (fileEntry.getName().startsWith(consoleInfo.getSettingNumber() + "s") || fileEntry.getName().startsWith(consoleInfo.getSettingNumber() + "l"))) {
                    orderFile = "./Warehouses_instances/legacy/W5A_Henn/" + consoleInfo.getItemsLocationString() + "/" + fileEntry.getName();
                }
            }
        }

        String line;
        int itemsPerSide = 0;
        float cellSize = 0;

        // Read layout file
        try (BufferedReader reader = new BufferedReader(new FileReader(layoutFile))) {

            // Determine item location (0 -> ABC, 1 -> Random)
            if (!consoleInfo.getItemsLocationString().contains("abc")) {
                itemsLocation = Constants.RND_ITEMS_LOCATION;
            }

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.contains(":")) {
                    break;
                }
                String[] pair = line.split(":");
                String key = pair[0].trim();
                pair[1] = pair[1].trim();
                switch (key) {
                    case "no_aisles_":
                        numberOfAisles = parseInt(pair[1]);
                        break;
                    case "no_cells__":
                        numberOfSlots = numberOfAisles * 2 * parseInt(pair[1]);
                        itemsPerSide = parseInt(pair[1]);
                        break;
                    case "cell_lengt":
                        cellSize = Float.parseFloat(pair[1]);
                        shelfLength = Float.parseFloat(pair[1]) * itemsPerSide;
                        break;
                    case "cell_width":
                        shelfWidth = Float.parseFloat(pair[1]);
                        break;
                    case "aisle_widt":
                        aisleWidth = Float.parseFloat(pair[1]);
                        break;
                    case "m_no_a_p_b":
                        workerCapacity = Float.parseFloat(pair[1]);
                        break;
                    case "speed_pick":
                        pickingTime = Float.parseFloat(pair[1]);
                        break;
                    case "dis_ais_wa":
                        depotPosition = parseInt(pair[1]);
                        break;
                    case "no_orders_":
                        numberOfOrders = parseInt(pair[1]);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("[" + e.getClass() + "] Error reading layout file: " + e.getMessage());
        }

        // Read order file
        try (BufferedReader reader = new BufferedReader(new FileReader(orderFile))) {
            boolean isReadingItems = false;
            Order order = null;
            int orderId = 0;
            int itemsInOrder = 0;
            int itemCounter = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (isReadingItems) {
                    String[] parts = line.split("\t");
                    String[] aisleData = parts[1].split(" ");
                    int aisle = parseInt(aisleData[1].trim()) / 2;
                    int side = (parseInt(aisleData[1].trim()) % 2 == 0) ? 0 : 1;
                    String[] heightData = parts[2].split(" ");
                    float height = (parseInt(heightData[1].trim()) * cellSize) + (cellSize / 2);
                    Product product = new Product(itemCounter, aisle, side, height, 1);
                    itemCounter++;
                    order.addProduct(product);
                    itemsInOrder--;
                    if (itemsInOrder == 0) {
                        isReadingItems = false;
                    }
                } else {
                    if (order != null) {
                        orders.add(order);
                    }
                    String[] parts = line.split(" ");
                    itemsInOrder = parseInt(parts[4]);
                    numberOfItems += itemsInOrder;
                    order = new Order(orderId, 0, itemsInOrder);
                    orderId++;
                    isReadingItems = true;
                }
            }
            if (order != null) {
                orders.add(order);
            }
        } catch (Exception e) {
            System.out.println("Error reading order file: " + e.getMessage());
        }

        // Construct aisles
        for (int i = 0; i < numberOfAisles; i++) {
            int side = getSideAisleFromDepot(numberOfAisles, depotPosition, i);
            double start = ((shelfWidth + aisleWidth) / 2) + ((shelfWidth + aisleWidth) * i);
            double end = ((shelfWidth + aisleWidth) * numberOfAisles);
            Aisles aisle = new Aisles(i, end - start, start, side);
            aisles.add(aisle);
        }

        // Construct warehouse object

        warehouse = new Warehouse(numberOfOrders, numberOfAisles, numberOfItems, depotPosition, itemsLocation, shelfLength, shelfWidth, aisleWidth, workerCapacity, pickingTime, outsideTurnTime, insideTurnTime, numberOfSlots, aisles, orders);
        instanceName = consoleInfo.toStringInstanceName(warehouse);
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
     * Retrieves the constructed warehouse object.
     *
     * @return The warehouse object.
     */
    public Warehouse getWarehouse() {
        return this.warehouse;
    }
}
