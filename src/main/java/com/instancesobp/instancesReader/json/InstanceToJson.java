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
        String carpeta = "";
        int contador = 1;

        for (InstancesLoaderFromConsoleInfo instancesLoaderFromConsoleInfo : generalInstancesLoader.getAllInstancesArbex()) {

            String jsonName = instancesLoaderFromConsoleInfo.getName();
            Warehouse warehouse = instancesLoaderFromConsoleInfo.getWarehouse();
            WarehouseWrapper wp = new WarehouseWrapper(warehouse, jsonName);

            ObjectMapper mapper = new ObjectMapper();

            // Definir la carpeta de destino según el nombre
            //ARBEX
            if (jsonName.contains("ARB_W6A")) {
                carpeta = "./Warehouses_instances/json/W6A_Arbex/W6/ran/";
            }

            //HENN_A
            else if (jsonName.contains("H_W5A_abc1")) {
                carpeta = "./Warehouses_instances/json/W5A_Henn/W5/abc1/";
            } else if (jsonName.contains("H_W5A_abc2")) {
                carpeta = "./Warehouses_instances/json/W5A_Henn/W5/abc2/";
            } else if (jsonName.contains("H_W5A_ran1")) {
                carpeta = "./Warehouses_instances/json/W5A_Henn/W5/ran1/";
            } else if (jsonName.contains("H_W5A_ran2")) {
                carpeta = "./Warehouses_instances/json/W5A_Henn/W5/ran2/";
            }

            //HENN_B
            else if (jsonName.contains("H_W5B_abc1")) {
                carpeta = "./Warehouses_instances/json/W5B_Henn/W5/abc1/";
            } else if (jsonName.contains("H_W5B_ran1")) {
                carpeta = "./Warehouses_instances/json/W5B_Henn/W5/ran1/";
            }

            //ALBAREDA
            else if (jsonName.contains("A_W1")) {
                if (jsonName.contains("A_W1_ABC")) {
                    carpeta = "./Warehouses_instances/json/W1-W2-W3-W4_Albareda/W1/abc/";
                } else {
                    carpeta = "./Warehouses_instances/json/W1-W2-W3-W4_Albareda/W1/ran/";
                }
            } else if (jsonName.contains("A_W2")) {
                if (jsonName.contains("A_W2_ABC")) {
                    carpeta = "./Warehouses_instances/json/W1-W2-W3-W4_Albareda/W2/abc/";
                } else {
                    carpeta = "./Warehouses_instances/json/W1-W2-W3-W4_Albareda/W2/ran/";
                }
            } else if (jsonName.contains("A_W3")) {
                if (jsonName.contains("A_W3_ABC")) {
                    carpeta = "./Warehouses_instances/json/W1-W2-W3-W4_Albareda/W3/abc/";
                } else {
                    carpeta = "./Warehouses_instances/json/W1-W2-W3-W4_Albareda/W3/ran/";
                }
            } else if (jsonName.contains("A_W4")) {
                if (jsonName.contains("A_W4_ABC")) {
                    carpeta = "./Warehouses_instances/json/W1-W2-W3-W4_Albareda/W4/abc/";
                } else {
                    carpeta = "./Warehouses_instances/json/W1-W2-W3-W4_Albareda/W4/ran/";
                }
            }

            // Crear la carpeta si no existe
            File folder = new File(carpeta);
            if (!folder.exists()) {
                boolean created = folder.mkdirs();
                if (created) {
                    System.out.println("Carpeta creada: " + carpeta);
                } else {
                    System.err.println("Error al crear la carpeta: " + carpeta);
                }
            }

            try {
                String fileName = carpeta + jsonName + "_ID" + contador + ".json";
                mapper.writerWithDefaultPrettyPrinter().writeValue(new File(fileName), wp);
                System.out.println("Archivo JSON creado con éxito: " + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }

            contador++;
        }
    }

}
