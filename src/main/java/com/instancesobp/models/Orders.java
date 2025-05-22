package com.instancesobp.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Orders {
    @JsonProperty("numOrders")
    private int numOrders;

    @JsonProperty("OrderList")
    private List<Order> orderList;

    public Orders() {
    }

    public Orders(Warehouse warehouse) {
        this.numOrders = warehouse.getNumberOfOrders();
        this.orderList = warehouse.getOrders();
    }

    public int getNumOrders() {
        return numOrders;
    }

    public void setNumOrders(int numOrders) {
        this.numOrders = numOrders;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }
}
