package com.instancesobp.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Layout {
    @JsonProperty("numberOfAisles")
    private int numberOfAisles;

    @JsonProperty("numberOfItems")
    private int numberOfItems;

    @JsonProperty("numberOfSlots")
    private int numberOfSlots;

    @JsonProperty("depotPlacement")
    private int depotPlacement;

    @JsonProperty("orderLocation")
    private int orderLocation = 0; // Valor por defecto

    @JsonProperty("shelfLength")
    private double shelfLength;

    @JsonProperty("shelfWidth")
    private double shelfWidth;

    @JsonProperty("aisleWidth")
    private double aisleWidth;

    @JsonProperty("crossAisles")
    private int crossAisles;

    @JsonProperty("workerCapacity")
    private double workerCapacity;

    @JsonProperty("pickingTime")
    private double pickingTime;

    @JsonProperty("outsideTurnTime")
    private double outsideTurnTime;

    @JsonProperty("insideTurnTime")
    private double insideTurnTime;

    @JsonProperty("travelSpeed")
    private double travelSpeed;

    @JsonProperty("aisles")
    private List<Aisles> aisles;

    @JsonProperty("pickers")
    private List<Picker> pickers;

    public Layout() {
    }

    public Layout(Warehouse warehouse) {
        this.numberOfAisles = warehouse.getNumberOfAisles();
        this.numberOfItems = warehouse.getNumberOfItems();
        this.numberOfSlots = warehouse.getNumberOfSlots();
        this.depotPlacement = warehouse.getDepotPlacement();
        this.shelfLength = warehouse.getShelfLength();
        this.shelfWidth = warehouse.getShelfWidth();
        this.aisleWidth = warehouse.getAisleWidth();
        this.crossAisles = warehouse.getCrossAisles();
        this.workerCapacity = warehouse.getWorkerCapacity();
        this.pickingTime = warehouse.getPickingTime();
        this.outsideTurnTime = warehouse.getOutsideTurnTime();
        this.insideTurnTime = warehouse.getInsideTurnTime();
        this.travelSpeed = 1.0;
        this.aisles = warehouse.getAisles();
        this.pickers = warehouse.getPickers();
    }

    public List<Aisles> getAisles() {
        return aisles;
    }

    public void setAisles(List<Aisles> aisles) {
        this.aisles = aisles;
    }

    public double getAisleWidth() {
        return aisleWidth;
    }

    public void setAisleWidth(double aisleWidth) {
        this.aisleWidth = aisleWidth;
    }

    public int getCrossAisles() {
        return crossAisles;
    }

    public void setCrossAisles(int crossAisles) {
        this.crossAisles = crossAisles;
    }

    public int getDepotPlacement() {
        return depotPlacement;
    }

    public void setDepotPlacement(int depotPlacement) {
        this.depotPlacement = depotPlacement;
    }

    public double getInsideTurnTime() {
        return insideTurnTime;
    }

    public void setInsideTurnTime(double insideTurnTime) {
        this.insideTurnTime = insideTurnTime;
    }

    public int getNumberOfAisles() {
        return numberOfAisles;
    }

    public void setNumberOfAisles(int numberOfAisles) {
        this.numberOfAisles = numberOfAisles;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public int getNumberOfSlots() {
        return numberOfSlots;
    }

    public void setNumberOfSlots(int numberOfSlots) {
        this.numberOfSlots = numberOfSlots;
    }

    public int getOrderLocation() {
        return orderLocation;
    }

    public void setOrderLocation(int orderLocation) {
        this.orderLocation = orderLocation;
    }

    public double getOutsideTurnTime() {
        return outsideTurnTime;
    }

    public void setOutsideTurnTime(double outsideTurnTime) {
        this.outsideTurnTime = outsideTurnTime;
    }

    public List<Picker> getPickers() {
        return pickers;
    }

    public void setPickers(List<Picker> pickers) {
        this.pickers = pickers;
    }

    public double getPickingTime() {
        return pickingTime;
    }

    public void setPickingTime(double pickingTime) {
        this.pickingTime = pickingTime;
    }

    public double getShelfLength() {
        return shelfLength;
    }

    public void setShelfLength(double shelfLength) {
        this.shelfLength = shelfLength;
    }

    public double getShelfWidth() {
        return shelfWidth;
    }

    public void setShelfWidth(double shelfWidth) {
        this.shelfWidth = shelfWidth;
    }

    public double getTravelSpeed() {
        return travelSpeed;
    }

    public void setTravelSpeed(double travelSpeed) {
        this.travelSpeed = travelSpeed;
    }

    public double getWorkerCapacity() {
        return workerCapacity;
    }

    public void setWorkerCapacity(double workerCapacity) {
        this.workerCapacity = workerCapacity;
    }
}
