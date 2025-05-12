package com.instancesobp.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


import java.util.List;

@JsonPropertyOrder({ "NameInstanceSet", "URL", "Papers", "InstanceSet" })
public class WarehouseWrapper {

    @JsonProperty("NameInstanceSet")
    private String nameInstanceSet;

    @JsonProperty("URL")
    private String url;

    @JsonProperty("Papers")
    private List<String> papers;

    @JsonProperty("InstanceSet")
    private List<InstanceSet> instanceSet;

    public WarehouseWrapper(Warehouse warehouse, String instancia) {
        this.nameInstanceSet = instancia;
        this.url = "";
        this.papers = List.of();
        this.instanceSet = List.of(new InstanceSet(warehouse));
    }
}

class Layout {
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
}

class Orders {
    @JsonProperty("numOrders")
    private int numOrders;

    @JsonProperty("OrderList")
    private List<Order> orderList;

    public Orders(Warehouse warehouse) {
        this.numOrders = warehouse.getNumberOfOrders();
        this.orderList = warehouse.getOrders();
    }
}

@JsonPropertyOrder({ "Layout", "Orders" }) // Especificamos el orden
class InstanceSet {

    @JsonProperty("Layout")
    private List<Layout> layout;

    @JsonProperty("Orders")
    private List<Orders> orders;

    public InstanceSet(Warehouse warehouse) {
        this.layout = List.of(new Layout(warehouse));
        this.orders = List.of(new Orders(warehouse));
    }
}
