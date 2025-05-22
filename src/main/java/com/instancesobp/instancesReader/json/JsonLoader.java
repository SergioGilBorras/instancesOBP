package com.instancesobp.instancesReader.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.instancesobp.models.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonLoader {

    private List<WarehouseInstance> warehouseInstances = new ArrayList<>();

    public void run() {
        ObjectMapper objectMapper = new ObjectMapper();
        GeneralJsonLoader gjl = new GeneralJsonLoader();
        ArrayList<String> jsons = gjl.getAllJsonArbex();

        try {
            for (String j : jsons) {

                String carpeta = "";
                if (j.contains("ARB")) {
                    carpeta = "W6A_Arbex/W6/ran/";
                }
                //ALBAREDA
                else if (j.contains("A_W1")) {
                    if (j.contains("A_W1_ABC")) {
                        carpeta = "W1-W2-W3-W4_Albareda/W1/abc/";
                    } else {
                        carpeta = "W1-W2-W3-W4_Albareda/W1/ran/";
                    }
                } else if (j.contains("A_W2")) {
                    if (j.contains("A_W2_ABC")) {
                        carpeta = "W1-W2-W3-W4_Albareda/W2/abc/";
                    } else {
                        carpeta = "W1-W2-W3-W4_Albareda/W2/ran/";
                    }
                } else if (j.contains("A_W3")) {
                    if (j.contains("A_W3_ABC")) {
                        carpeta = "W1-W2-W3-W4_Albareda/W3/abc/";
                    } else {
                        carpeta = "W1-W2-W3-W4_Albareda/W3/ran/";
                    }
                } else if (j.contains("A_W4")) {
                    if (j.contains("A_W4_ABC")) {
                        carpeta = "W1-W2-W3-W4_Albareda/W4/abc/";
                    } else {
                        carpeta = "W1-W2-W3-W4_Albareda/W4/ran/";
                    }
                }
                //HENN_A
                else if (j.contains("H_W5A_abc1")) {
                    carpeta = "W5A_Henn/W5/abc1/";
                } else if (j.contains("H_W5A_abc2")) {
                    carpeta = "W5A_Henn/W5/abc2/";
                } else if (j.contains("H_W5A_ran1")) {
                    carpeta = "W5A_Henn/W5/ran1/";
                } else if (j.contains("H_W5A_ran2")) {
                    carpeta = "W5A_Henn/W5/ran2/";
                }

                // Lee el archivo JSON y mapea a la clase WarehouseWrapper
                WarehouseWrapper warehouseWrapper = objectMapper.readValue(
                        new File("Warehouses_instances/json/" + carpeta + j),
                        WarehouseWrapper.class
                );

                // Extrae la información necesaria para crear un objeto Warehouse
                InstanceSet instanceSet = warehouseWrapper.getInstanceSet().get(0);
                Layout layout = instanceSet.getLayout().get(0);
                Orders orders = instanceSet.getOrders().get(0);

                // Crea un objeto Warehouse
                Warehouse warehouse = new Warehouse(
                        orders.getNumOrders(),
                        layout.getNumberOfAisles(),
                        layout.getNumberOfItems(),
                        layout.getDepotPlacement(),
                        layout.getOrderLocation(),
                        layout.getShelfLength(),
                        layout.getShelfWidth(),
                        layout.getAisleWidth(),
                        layout.getCrossAisles(),
                        layout.getWorkerCapacity(),
                        layout.getPickingTime(),
                        layout.getOutsideTurnTime(),
                        layout.getInsideTurnTime(),
                        layout.getNumberOfSlots(),
                        layout.getAisles(),
                        orders.getOrderList()
                );

                warehouseInstances.add(new WarehouseInstance(warehouse, j));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<WarehouseInstance> getWarehouseInstances() {
        return warehouseInstances;
    }

    public static class WarehouseInstance {
        private Warehouse warehouse;
        private String name;

        public WarehouseInstance(Warehouse warehouse, String name) {
            this.warehouse = warehouse;
            this.name = name;
        }

        public Warehouse getWarehouse() {
            return warehouse;
        }

        public String getName() {
            return name;
        }
    }
}
