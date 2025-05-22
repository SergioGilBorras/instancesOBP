package com.instancesobp.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({ "Layout", "Orders" })
public// Especificamos el orden
class InstanceSet {

    @JsonProperty("Layout")
    private List<Layout> layout;

    @JsonProperty("Orders")
    private List<Orders> orders;

    public InstanceSet() {
    }

    public InstanceSet(Warehouse warehouse) {
        this.layout = List.of(new Layout(warehouse));
        this.orders = List.of(new Orders(warehouse));
    }

    public List<Layout> getLayout() {
        return layout;
    }

    public void setLayout(List<Layout> layout) {
        this.layout = layout;
    }

    public List<Orders> getOrders() {
        return orders;
    }

    public void setOrders(List<Orders> orders) {
        this.orders = orders;
    }
}
