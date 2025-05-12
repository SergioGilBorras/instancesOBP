package com.instancesobp.models;

import java.io.Serializable;

public class Location implements Cloneable, Serializable {

   private int location_id;

   private int aislePos;

   private int aisleSide;

    /**
     * Constructs a new {@code Location} with the specified attributes.
     *
     * @param location_id  The unique identifier for the location.
     * @param aislePos  The position of location on the aisle.
     * @param aisleSide The side of location on the aisle.
     */
   public Location(int location_id,int aislePos,int aisleSide){
       this.location_id=location_id;
       this.aislePos=aislePos;
       this.aisleSide=aisleSide;
   }

    public int getLocation() {
        return location_id;
    }

    public void setLocation(int location_id) {
        this.location_id = location_id;
    }


    public int getAislePos() {
        return aislePos;
    }

    public void setAislePos(int aislePos) {
        this.aislePos = aislePos;
    }


    public int getAisleSide() {
        return aisleSide;
    }

    public void setAisleSide(int aisleSide) {
        this.aisleSide = aisleSide;
    }


    @Override
    public String toString() {
        return "Location{" +
                "location_id=" + location_id +
                ", aislePos=" + aislePos +
                ", aisleSide=" + aisleSide +
                '}';
    }

    @Override
    public Location clone() {
        try {
            return (Location) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
