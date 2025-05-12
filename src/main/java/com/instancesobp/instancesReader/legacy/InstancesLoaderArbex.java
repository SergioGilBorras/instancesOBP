package com.instancesobp.instancesReader.legacy;

import com.instancesobp.models.*;
import com.instancesobp.utils.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.*;

public class InstancesLoaderArbex {
    /**
     * Configuration object containing instance details.
     */
    private final ReadConsoleInfo consoleInfo;
    /**
     * Path to the layout file.
     */
    private String layoutFile;
    /**
     * Path to the order file.
     */
    private String orderFile;
    /**
     * Path to the locations file.
     */
    private String locationsFile;
    /**
     * Name of the instance being loaded.
     */
    private String instanceName;
    /**
     * Warehouse object constructed from the loaded data.
     */
    private Warehouse warehouse;
    private float alturaTemp;


    /**
     * Constructor for the InstancesLoaderArbex class.
     * Initializes the loader with the provided configuration.
     *
     * @param consoleInfo The configuration object containing instance details.
     */
    public InstancesLoaderArbex(ReadConsoleInfo consoleInfo) {
        this.warehouse = new Warehouse();
        this.consoleInfo = consoleInfo;
    }

    /**
     * Executes the loading process for the instance.
     * Reads the layout and order files, parses their content, and constructs
     * the warehouse object with its associated aisles, orders, and products.
     */
    public void run() {
        int numberOfOrders = 0;
        int numberOfAisles = 0;
        int numberOfItems = 0;
        int numberOfSlots = 0;
        int depotPosition = Constants.DEPOT_CORNER;
        int itemsLocation = Constants.RND_ITEMS_LOCATION;
        double shelfLength = 0;
        double shelfWidth = 0;
        int crossAisles = 0;
        double aisleWidth = 0;
        double workerCapacity = 40; // Segun se expecifica en el articulo de Arbex.
        double pickingTime = 0;
        double outsideTurnTime = 0;
        double insideTurnTime = 0;
        double rightOriginDistance = 0;
        double leftOriginDistance = 0;
        int id = 0;
        int side = 0;
        int aisle = 0;
        double weight = 0;
        double height = 0;
        List<Aisles> aisles = new ArrayList<>();
        double pesoTotalTotal = 0;


        // Locate layout, order,locations files
        locationsFile = "./Warehouses_instances/legacy/W6_Arbex/productsDB_1560_locations";
        //System.out.println(locationsFile);
        layoutFile = "./Warehouses_instances/legacy/W6_Arbex/" + consoleInfo.getWarehouseTypeString();//warehouse_8_0_3_1560
        //System.out.println(layoutFile);
        orderFile = "./Warehouses_instances/legacy/W6_Arbex/"+ consoleInfo.getOrderTypeString() + "/" + consoleInfo.getInstanceFileName();//smallInstances/instances_d5_ord5";
        //System.out.println(orderFile);
        /**Aisles information
         **/
        String lineAisles;
        try {

            try (BufferedReader br = new BufferedReader(new FileReader(layoutFile))) {
                int line_ais = 1;
                while ((lineAisles = br.readLine()) != null) {
                    String[] retais = lineAisles.split("\\s+");

                    if (line_ais == 2) {
                        int pasillos_cont = Integer.parseInt(retais[1]);
                        numberOfAisles = Integer.parseInt(retais[1]);
                        for (int i = 0; i < pasillos_cont; i++) {

                            if (i == 0) {
                                leftOriginDistance = (i + 1.5f);
                                rightOriginDistance = (((pasillos_cont - 1) * 5 + 3) - 1.5f);
                            } else {
                                leftOriginDistance += 5.0f;
                                rightOriginDistance -= 5f;
                            }

                            if (i == 0) {
                                side = 0;
                            } else {
                                side = 1;
                            }
                            Aisles a = new Aisles(i, rightOriginDistance, leftOriginDistance, side);
                            aisles.add(a);
                        }

                    }

                    if (line_ais==6) {
                        aisleWidth=Float.parseFloat(retais[1]);
                    }

                    if (line_ais == 7) {
                        shelfWidth = Float.parseFloat(retais[1])*2;
                    }

                    if (line_ais == 13) {
                        shelfLength = Float.parseFloat(retais[1]) ;
                    }

                    if (line_ais == 14) {
                        numberOfSlots = Integer.parseInt(retais[1]);
                        numberOfSlots=numberOfSlots/3;
                    }

                    line_ais++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         for (Aisles aisle : aisles) {
         System.out.println(aisle);
         }**/

        HashMap<Integer, Location> locationMap = new HashMap<>();
        HashMap<Integer, Location> productLocations = new HashMap<>();
        String linea_lo;
        String linea_ly;

        // === 1. Leer layoutFile para crear el mapa de locations ===
        try (BufferedReader brly = new BufferedReader(new FileReader(layoutFile))) {
            int line_number = 1;

            while ((linea_ly = brly.readLine()) != null) {
                linea_ly = linea_ly.trim();

                if (line_number <22) {
                    line_number++;
                    continue;
                }

                if (!linea_ly.isEmpty()) {
                    String[] retLy = linea_ly.split("\\s+");

                    if (retLy.length == 4) {
                        try {
                            int location_id = Integer.parseInt(retLy[0]);
                            int aislePos = Integer.parseInt(retLy[1]);
                            int aisleSide = Integer.parseInt(retLy[2]);

                            Location loc = new Location(location_id, aislePos, aisleSide);
                            locationMap.put(location_id, loc);

                            // System.out.println("Ubicación leída: " + loc); // Depuración

                        } catch (NumberFormatException e) {
                            System.err.println("Error al leer layoutFile en la línea " + line_number + ": " + e.getMessage());
                        }
                    }
                }
                line_number++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // === 2. Leer locationsFile para asociar productos a ubicaciones ===
        try (BufferedReader brl = new BufferedReader(new FileReader(locationsFile))) {
            int line_number = 1;

            while ((linea_lo = brl.readLine()) != null) {
                linea_lo = linea_lo.trim();

                if (line_number < 3) {
                    line_number++;
                    continue;
                }

                if (!linea_lo.isEmpty()) {
                    String[] retLo = linea_lo.split("\\s+");

                    if (retLo.length == 2) {
                        try {
                            int productId = Integer.parseInt(retLo[0]);
                            int location_id = Integer.parseInt(retLo[1]);

                            Location loc = locationMap.get(location_id);

                            if (loc != null) {
                                productLocations.put(productId, loc);
                                //System.out.println("Producto " + productId + " asignado a ubicación " + loc); // Depuración
                            } else {
                                System.err.println("Ubicación no encontrada para location_id: " + location_id);
                            }

                        } catch (NumberFormatException e) {
                            System.err.println("Error al leer locationsFile en la línea " + line_number + ": " + e.getMessage());
                        }
                    }
                }
                line_number++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Informacion Ordenes
        String linea;
        try {
            try (BufferedReader br = new BufferedReader(new FileReader(orderFile))) {
                int line_number = 1;
                List<Order> orders = new ArrayList<>();

                while ((linea = br.readLine()) != null) {

                    if (line_number == 1) {
                        numberOfOrders = Integer.parseInt(linea);
                    }

                    if (line_number > 2) {
                        List<Product> productos = new ArrayList<>();

                        String[] retval = linea.split("\\s+");
                        numberOfItems += Integer.parseInt(retval[0]);

                        int tempId = -1; // Variable temporal para ID
                        float tempPeso = 0.0f; // Variable temporal para Peso


                        for (int i = 1; i < retval.length; i++) {
                            Product p = new Product();
                            if (i % 2 != 0) {
                                tempId = Integer.parseInt(retval[i]);
                                id = tempId;
                                Location l=productLocations.get(id);
                                alturaTemp=l.getAislePos();


                                if (layoutFile.contains("warehouse_8_1")
                                        || layoutFile.contains("warehouse_16_1")) {
                                    crossAisles = 3;
                                } else {
                                    crossAisles = 2;
                                }


                                if (layoutFile.contains("warehouse_16_1")) {
                                    if (alturaTemp > 8) {
                                        alturaTemp += 3f;
                                    }
                                }


                                if (layoutFile.contains("warehouse_8_1")) {
                                    if (alturaTemp > 16) {
                                        alturaTemp += 3f;
                                    }
                                }


                                height = (alturaTemp + 0.5f);
                                side = l.getAisleSide();
                                aisle = (int) Math.floor(side / 2.0);

                                side %= 2;


                            } else {
                                tempPeso = Float.parseFloat(retval[i]);
                                weight = tempPeso;
                                // Solo imprimimos cuando todos los valores han sido leídos|| Comprobacion de datos
//		                        System.out.println("ID: " + id + " Peso: " + peso +" locacion: "+location+" altura:"+altura+" lado: "+lado+ " Pasillo: "+pasillo);

                                p = new Product(id, aisle, side, height, weight);
                                productos.add(p);

                            }

                        }

                        double pesoTotal = 0;
                        int numReferencia = productos.size();
                        for (Product product : productos) {
                            pesoTotal += product.getWeight();
                        }
                        pesoTotalTotal += pesoTotal;
                        Order o = new Order((line_number - 2), productos, numReferencia, pesoTotal, 0, 0, 0.0, 0.0);
                        orders.add(o);

                    }

                    line_number++;
                }

                /**
                 for (Order o : orders) {
                 System.out.println(o.toString());
                 }*/
                workerCapacity = Math.floor((pesoTotalTotal / numberOfOrders) * Math.min(8, Math.max(3, numberOfOrders * 0.1)));

                //System.out.println(instanceName);
                warehouse = new Warehouse(numberOfOrders, numberOfAisles, numberOfItems, depotPosition, itemsLocation,
                        shelfLength, shelfWidth, aisleWidth, crossAisles, workerCapacity, pickingTime, outsideTurnTime,
                        insideTurnTime, numberOfSlots, aisles, orders);

                instanceName = consoleInfo.toStringInstanceName(warehouse);

               // System.out.println(warehouse.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * public static void main(String[] args) {
     *       ReadConsoleInfo rdi=new ReadConsoleInfo();
     *       InstancesLoaderArbex ila=new InstancesLoaderArbex(rdi);ila.run();
     *     }
     */

    public Warehouse getWarehouse() {
        return this.warehouse;
    }

    public String getName() {
        return this.instanceName;
    }

}
