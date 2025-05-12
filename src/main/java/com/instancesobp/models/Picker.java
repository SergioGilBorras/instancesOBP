package com.instancesobp.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Picker implements Cloneable, Serializable {
    @JsonProperty("")
    private int id;
    @JsonProperty("")
    private int initialPickTime;
    @JsonProperty("")
    private double fatiguingRate;
    @JsonProperty("")
    private int timeStabilization;
    @JsonProperty("")
    private int finalPickTime;

    public Picker() {

    }

    public Picker(int id, int initialPickTime, double fatiguingRate, int timeStabilization, int finalPickTime) {
        this.id = id;
        this.initialPickTime = initialPickTime;
        this.fatiguingRate = fatiguingRate;
        this.timeStabilization = timeStabilization;
        this.finalPickTime = finalPickTime;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append("id picker: ");
        sb.append(this.id);
        sb.append("\n\r");

        sb.append("Initial picking time: ");
        sb.append(this.initialPickTime);
        sb.append("\n\r");

        sb.append("Fatiguing rate: ");
        sb.append(this.fatiguingRate);
        sb.append("\n\r");

        sb.append("Time Stabilization: ");
        sb.append(this.timeStabilization);
        sb.append("\n\r");

        sb.append("Final picking time: ");
        sb.append(this.finalPickTime);
        sb.append("\n\r");


        return sb.toString();
    }

    public void toPrint() {
        System.out.println(this.toString());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInitialPickTime() {
        return initialPickTime;
    }

    public void setInitialPickTime(int initialPickTime) {
        this.initialPickTime = initialPickTime;
    }



    public double getFatiguingRate() {
        return fatiguingRate;
    }

    public void setFatiguingRate(double fatiguingRate) {
        this.fatiguingRate = fatiguingRate;
    }

    public int getTimeStabilization() {
        return timeStabilization;
    }

    public void setTimeStabilization(int timeStabilization) {
        this.timeStabilization = timeStabilization;
    }

    public int getFinalPickTime() {
        return finalPickTime;
    }

    public void setFinalPickTime(int finalPickTime) {
        this.finalPickTime = finalPickTime;
    }

}
