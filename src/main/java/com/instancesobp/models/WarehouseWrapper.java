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


    // Constructor sin argumentos
    public WarehouseWrapper() {
    }


    public WarehouseWrapper(Warehouse warehouse, String instancia) {
        this.nameInstanceSet = instancia;
        this.url = "";
        this.papers = List.of();
        this.instanceSet = List.of(new InstanceSet(warehouse));
    }

    public List<InstanceSet> getInstanceSet() {
        return instanceSet;
    }

    public void setInstanceSet(List<InstanceSet> instanceSet) {
        this.instanceSet = instanceSet;
    }

    public String getNameInstanceSet() {
        return nameInstanceSet;
    }

    public void setNameInstanceSet(String nameInstanceSet) {
        this.nameInstanceSet = nameInstanceSet;
    }

    public List<String> getPapers() {
        return papers;
    }

    public void setPapers(List<String> papers) {
        this.papers = papers;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

