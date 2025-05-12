package com.instancesobp.instancesReader.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.instancesobp.instancesReader.legacy.GeneralInstancesLoader;
import com.instancesobp.instancesReader.legacy.InstancesLoaderFromConsoleInfo;
import com.instancesobp.models.Warehouse;
import com.instancesobp.models.WarehouseWrapper;

import java.io.File;
import java.io.IOException;

public class InstanceToJson {

    public static void main(String[] args) {
        generateJson();
    }

    public static void generateJson() {
        GeneralInstancesLoader generalInstancesLoader = new GeneralInstancesLoader();
        String carpeta="";
        for (InstancesLoaderFromConsoleInfo instancesLoaderFromConsoleInfo : generalInstancesLoader.getAllInstancesAlbareda()) {

        String jsonName = instancesLoaderFromConsoleInfo.getName();

        Warehouse warehouse = instancesLoaderFromConsoleInfo.getWarehouse();
        WarehouseWrapper wp = new WarehouseWrapper(warehouse, jsonName);

        ObjectMapper mapper = new ObjectMapper();
        if (jsonName.contains("ARB_W6A")){
            carpeta="./Warehouses_instances/json/W6A_Arbex/";
        } else if (jsonName.contains("H_W5A_abc1")){
            carpeta="./Warehouses_instances/json/W5A_Henn/abc1/";
        } else if (jsonName.contains("H_W5A_abc2")) {
            carpeta="./Warehouses_instances/json/W5A_Henn/abc2/";
        }else if (jsonName.contains("H_W5A_ran1")){
            carpeta="./Warehouses_instances/json/W5A_Henn/ran1/";
        } else if (jsonName.contains("H_W5A_ran2")) {
            carpeta="./Warehouses_instances/json/W5A_Henn/ran2/";
        }else if (jsonName.contains("H_W5B_abc1")){
            carpeta="./Warehouses_instances/json/W5B_Henn/abc1/";
        } else if (jsonName.contains("H_W5B_ran1")) {
            carpeta="./Warehouses_instances/json/W5B_Henn/ran1/";
        }else if(jsonName.contains("A_W1")){
            carpeta="./Warehouses_instances/json/W1-W2-W3-W4_Albareda/W1/";
        }else if(jsonName.contains("A_W2")){
            carpeta="./Warehouses_instances/json/W1-W2-W3-W4_Albareda/W2/";
        }else if(jsonName.contains("A_W3")){
            carpeta="./Warehouses_instances/json/W1-W2-W3-W4_Albareda/W3/";
        }else if(jsonName.contains("A_W4")){
            carpeta="./Warehouses_instances/json/W1-W2-W3-W4_Albareda/W4/";
        }


        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(carpeta + jsonName + ".json"), wp);
            System.out.println("Archivo JSON creado con éxito: " + jsonName);
        } catch (IOException e) {
            e.printStackTrace();
        }


        }
    }
}
