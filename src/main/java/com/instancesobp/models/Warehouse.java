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
package com.instancesobp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.instancesobp.utils.Constants.*;

/**
 * Represents a warehouse with its configuration, aisles, and orders.
 * This class provides methods to manage the warehouse's properties,
 * including its aisles, orders, and various operational parameters.
 * It also includes utility methods for printing and accessing warehouse details.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class Warehouse implements Serializable {

    /**
     * List of aisles in the warehouse.
     */
    private final List<Aisles> aisles;

    /**
     * List of orders in the warehouse.
     */
    private final List<Order> orders;

    /**
     * Number of orders in the warehouse.
     */
    private int numberOfOrders;

    /**
     * Number of aisles in the warehouse.
     */
    private int numberOfAisles;

    /**
     * Total number of items in the warehouse.
     */
    private int numberOfItems;

    /**
     * Number of slots available in the warehouse.
     */
    private int numberOfSlots;

    /**
     * Placement of the depot table (e.g., DEPOT_CORNER or DEPOT_CENTER constants).
     */
    private int depotPlacement;

    /**
     * Location of the orders in the warehouse. (e.g., ABC_ITEMS_LOCATION or RND_ITEMS_LOCATION constants).
     */
    private int orderLocation;

    /**
     * Length of the shelves in the warehouse.
     */
    private double shelfLength;

    /**
     * Width of the shelves in the warehouse.
     */
    private double shelfWidth;

    /**
     * Width of the aisles in the warehouse.
     */
    private double aisleWidth;

    /**
     * Capacity of the worker in the warehouse.
     */
    private double workerCapacity;

    /**
     * Time required for picking an item.
     */
    private double pickingTime;

    /**
     * Time required for turning outside the aisle.
     */
    private double outsideTurnTime;

    /**
     * Time required for turning inside the aisle.
     */
    private double insideTurnTime;

    /**
     * Time spent at the depot.
     */
    private double depotTime;

    /**
     * Travel speed of the worker.
     */
    private double travelSpeed = 1;

    /**
     * Name of the warehouse instance.
     */
    private String instanceName;

    /**
     * List of arrival times for orders.
     */
    private List<Integer> arrivalTimes = null;

    /**
     * Number of operational hours for the warehouse.
     */
    private int operationalHours = 4;

    /**
     * Default constructor for the Warehouse class.
     * Initializes empty lists for aisles and orders.
     */
    public Warehouse() {
        this.aisles = new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    /**
     * Parameterized constructor for the Warehouse class.
     * Initializes the warehouse with the specified properties.
     *
     * @param numberOfOrders  Number of orders in the warehouse.
     * @param numberOfAisles  Number of aisles in the warehouse.
     * @param numberOfItems   Total number of items in the warehouse.
     * @param depotPlacement  Placement of the depot table.
     * @param orderLocation   Location of the orders in the warehouse.
     * @param shelfLength     Length of the shelves.
     * @param shelfWidth      Width of the shelves.
     * @param aisleWidth      Width of the aisles.
     * @param workerCapacity  Capacity of the worker.
     * @param pickingTime     Time required for picking an item.
     * @param outsideTurnTime Time required for turning outside the aisle.
     * @param insideTurnTime  Time required for turning inside the aisle.
     * @param numberOfSlots   Number of slots available in the warehouse.
     * @param aisles          List of aisles in the warehouse.
     * @param orders          List of orders in the warehouse.
     */
    public Warehouse(int numberOfOrders, int numberOfAisles, int numberOfItems, int depotPlacement, int orderLocation,
                     double shelfLength, double shelfWidth, double aisleWidth, double workerCapacity, double pickingTime,
                     double outsideTurnTime, double insideTurnTime, int numberOfSlots, List<Aisles> aisles, List<Order> orders) {
        this.numberOfOrders = numberOfOrders;
        this.numberOfAisles = numberOfAisles;
        this.numberOfItems = numberOfItems;
        this.depotPlacement = depotPlacement;
        this.orderLocation = orderLocation;
        this.shelfLength = shelfLength;
        this.shelfWidth = shelfWidth;
        this.aisleWidth = aisleWidth;
        this.workerCapacity = workerCapacity;
        this.pickingTime = pickingTime;
        this.outsideTurnTime = outsideTurnTime;
        this.insideTurnTime = insideTurnTime;
        this.aisles = aisles;
        this.orders = orders;
        this.numberOfSlots = numberOfSlots;
    }

    /**
     * Converts the depot placement integer value to a string representation.
     *
     * @param depotPlacement The depot placement value.
     * @return A string representation of the depot placement.
     */
    private static String getDepotPlacementString(int depotPlacement) {
        return switch (depotPlacement) {
            case DEPOT_CORNER -> "DEPOT_CORNER";
            case DEPOT_CENTER -> "DEPOT_CENTER";
            default -> "Unknown";
        };
    }

    /**
     * Converts the order location integer value to a string representation.
     *
     * @param orderLocation The order location value.
     * @return A string representation of the order location.
     */
    private static String getOrderLocationString(int orderLocation) {
        return switch (orderLocation) {
            case ABC_ITEMS_LOCATION -> "ABC_ITEMS_LOCATION";
            case RND_ITEMS_LOCATION -> "RND_ITEMS_LOCATION";
            default -> "Unknown";
        };
    }

    /**
     * Adds an aisle to the warehouse.
     *
     * @param aisle The aisle to be added.
     * @throws Exception If the maximum number of aisles is exceeded.
     */
    public void addAisle(Aisles aisle) throws Exception {
        if (aisles.size() < numberOfAisles) {
            aisles.add(aisle);
        } else {
            throw new Exception("Limit exceeded. All aisles are already loaded.");
        }
    }

    /**
     * Adds an order to the warehouse.
     *
     * @param order The order to be added.
     * @throws Exception If the maximum number of orders is exceeded.
     */
    public void addOrder(Order order) throws Exception {
        if (orders.size() < numberOfOrders) {
            orders.add(order);
        } else {
            throw new Exception("Limit exceeded. All orders are already loaded.");
        }
    }

    /**
     * Returns a string representation of the warehouse, including its properties,
     * aisles, and orders.
     *
     * @return A string representation of the warehouse.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Number of orders: ").append(this.numberOfOrders).append("\n");
        sb.append("Number of aisles: ").append(this.numberOfAisles).append("\n");
        sb.append("Number of items: ").append(this.numberOfItems).append("\n");
        sb.append("Number of slots: ").append(this.numberOfSlots).append("\n");
        sb.append("Depot placement: ").append(getDepotPlacementString(this.depotPlacement)).append("\n");
        sb.append("Order location: ").append(getOrderLocationString(this.orderLocation)).append("\n");
        sb.append("Shelf length: ").append(this.shelfLength).append("\n");
        sb.append("Shelf width: ").append(this.shelfWidth).append("\n");
        sb.append("Aisle width: ").append(this.aisleWidth).append("\n");
        sb.append("Worker capacity: ").append(this.workerCapacity).append("\n");
        sb.append("Picking time: ").append(this.pickingTime).append("\n");
        sb.append("Outside turn time: ").append(this.outsideTurnTime).append("\n");
        sb.append("Inside turn time: ").append(this.insideTurnTime).append("\n");
        sb.append("Depot time: ").append(this.depotTime).append("\n");
        sb.append("** Aisle definitions: ").append("\n");
        for (Aisles aisle : aisles) {
            sb.append(aisle.toString()).append(" ******* \n");
        }
        sb.append("** Order definitions: ").append("\n");
        for (Order order : orders) {
            sb.append(order.toString()).append(" ******* \n");
        }
        return sb.toString();
    }

    // Getters and setters for various properties

    /**
     * Prints the warehouse details to the console.
     */
    public void printDetails() {
        System.out.println(this);
    }

    /**
     * Gets the number of aisles in the warehouse.
     *
     * @return The number of aisles.
     */
    public int getNumberOfAisles() {
        return numberOfAisles;
    }

    /**
     * Gets the number of orders in the warehouse.
     *
     * @return The number of orders.
     */
    public int getNumberOfOrders() {
        return numberOfOrders;
    }

    /**
     * Gets the list of orders in the warehouse.
     *
     * @return A list of orders.
     */
    public List<Order> getOrders() {
        return orders;
    }

    /**
     * Gets the total number of items in the warehouse.
     *
     * @return The total number of items.
     */
    public int getNumberOfItems() {
        return numberOfItems;
    }

    /**
     * Sets the total number of items in the warehouse.
     *
     * @param numberOfItems The total number of items.
     */
    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    /**
     * Gets the number of slots available in the warehouse.
     *
     * @return The number of slots.
     */
    public int getNumberOfSlots() {
        return numberOfSlots;
    }

    /**
     * Gets the placement of the depot in the warehouse.
     *
     * @return The depot placement.
     */
    public int getDepotPlacement() {
        return depotPlacement;
    }

    /**
     * Gets the worker's capacity in the warehouse.
     *
     * @return The worker's capacity.
     */
    public double getWorkerCapacity() {
        return workerCapacity;
    }

    /**
     * Gets the location of the orders in the warehouse.
     *
     * @return The order location.
     */
    public int getOrderLocation() {
        return orderLocation;
    }

    /**
     * Gets the length of the shelves in the warehouse.
     *
     * @return The shelf length.
     */
    public double getShelfLength() {
        return shelfLength;
    }

    /**
     * Gets the width of the shelves in the warehouse.
     *
     * @return The shelf width.
     */
    public double getShelfWidth() {
        return shelfWidth;
    }

    /**
     * Gets the width of the aisles in the warehouse.
     *
     * @return The aisle width.
     */
    public double getAisleWidth() {
        return aisleWidth;
    }

    /**
     * Gets the time required for picking an item.
     *
     * @return The picking time.
     */
    public double getPickingTime() {
        return pickingTime;
    }

    /**
     * Sets the time required for picking an item.
     *
     * @param pickingTime The picking time to set.
     */
    public void setPickingTime(double pickingTime) {
        this.pickingTime = pickingTime;
    }

    /**
     * Gets the time required for turning outside the aisle.
     *
     * @return The outside turn time.
     */
    public double getOutsideTurnTime() {
        return outsideTurnTime;
    }

    /**
     * Sets the time required for turning outside the aisle.
     *
     * @param outsideTurnTime The outside turn time to set.
     */
    public void setOutsideTurnTime(double outsideTurnTime) {
        this.outsideTurnTime = outsideTurnTime;
    }

    /**
     * Gets the time required for turning inside the aisle.
     *
     * @return The inside turn time.
     */
    public double getInsideTurnTime() {
        return insideTurnTime;
    }

    /**
     * Sets the time required for turning inside the aisle.
     *
     * @param insideTurnTime The inside turn time to set.
     */
    public void setInsideTurnTime(double insideTurnTime) {
        this.insideTurnTime = insideTurnTime;
    }

    /**
     * Gets the time spent at the depot.
     *
     * @return The depot time.
     */
    public double getDepotTime() {
        return depotTime;
    }

    /**
     * Sets the time spent at the depot.
     *
     * @param depotTime The depot time to set.
     */
    public void setDepotTime(double depotTime) {
        this.depotTime = depotTime;
    }

    /**
     * Gets the travel speed of the worker.
     *
     * @return The travel speed.
     */
    public double getTravelSpeed() {
        return travelSpeed;
    }

    /**
     * Sets the travel speed of the worker.
     *
     * @param travelSpeed The travel speed to set.
     */
    public void setTravelSpeed(double travelSpeed) {
        this.travelSpeed = travelSpeed;
    }

    /**
     * Gets the list of aisles in the warehouse.
     *
     * @return A list of aisles.
     */
    public List<Aisles> getAisles() {
        return aisles;
    }

    /**
     * Gets the name of the warehouse instance.
     *
     * @return The instance name.
     */
    public String getInstanceName() {
        return instanceName;
    }

    /**
     * Sets the name of the warehouse instance.
     *
     * @param instanceName The instance name to set.
     */
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    /**
     * Gets the list of arrival times for orders.
     *
     * @return A list of arrival times.
     */
    public List<Integer> getArrivalTimes() {
        return arrivalTimes;
    }

    /**
     * Sets the list of arrival times for orders.
     *
     * @param arrivalTimes The list of arrival times to set.
     */
    public void setArrivalTimes(List<Integer> arrivalTimes) {
        this.arrivalTimes = arrivalTimes;
    }

    /**
     * Gets the number of operational hours for the warehouse.
     *
     * @return The number of operational hours.
     */
    public int getOperationalHours() {
        return operationalHours;
    }

    /**
     * Sets the number of operational hours for the warehouse.
     *
     * @param operationalHours The number of operational hours to set.
     */
    public void setOperationalHours(int operationalHours) {
        this.operationalHours = operationalHours;
    }
}