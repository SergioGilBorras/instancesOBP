/*
 * Copyright (c) 2025 Sergio Gil Borrás
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to use
 * the Software for non-commercial research purposes only, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.instancesobp.instancesReader.legacy;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * This class is responsible for loading and managing instances of experiments
 * for the HENN and Albareda examples. It provides methods to retrieve instances
 * by name, load all instances, and handle specific configurations for each example.
 * The class ensures that the instances are properly initialized and ready for use
 * in further processing or simulations.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class GeneralInstancesLoader {

    /**
     * Default constructor for the GeneralInstancesLoader class.
     * This constructor initializes the instance loader.
     */
    public GeneralInstancesLoader() {
        // Default constructor
    }

    /**
     * Retrieves a list of instances that match the given names.
     *
     * @param instanceNames A list of instance names to search for.
     * @return A list of instances that match the given names.
     */
    public ArrayList<InstancesLoaderFromConsoleInfo> getInstancesByName(ArrayList<String> instanceNames) {
        ArrayList<InstancesLoaderFromConsoleInfo> matchingInstances = new ArrayList<>();

        for (InstancesLoaderFromConsoleInfo instance : getAllInstances()) {
            if (instanceNames.contains(instance.getName())) {
                matchingInstances.add(instance);
            }
        }
        return matchingInstances;
    }

    /**
     * Retrieves a single instance that matches the given name.
     *
     * @param instanceName The name of the instance to search for.
     * @return The instance that matches the given name, or null if not found.
     */
    public InstancesLoaderFromConsoleInfo getInstanceByName(String instanceName) {
        for (InstancesLoaderFromConsoleInfo instance : getAllInstances()) {
            if (instanceName.matches(instance.getName())) {
                return instance;
            }
        }
        return null;
    }

    /**
     * Retrieves all instances from both HENN and Albareda examples.
     *
     * @return A list of all instances.
     */
    public ArrayList<InstancesLoaderFromConsoleInfo> getAllInstances() {
        ArrayList<InstancesLoaderFromConsoleInfo> allInstances = getAllInstancesHENN();
        allInstances.addAll(getAllInstancesAlbareda());
        return allInstances;
    }

    /**
     * Retrieves all instances for the HENN example.
     *
     * @return A list of HENN instances.
     */
    public ArrayList<InstancesLoaderFromConsoleInfo> getAllInstancesHENN() {
        ArrayList<InstancesLoaderFromConsoleInfo> hennInstances = new ArrayList<>();

        try {
            ArrayList<String> itemOrders = new ArrayList<>();
            itemOrders.add("abc1");
            itemOrders.add("abc2");
            itemOrders.add("ran1");
            itemOrders.add("ran2");

            for (String order : itemOrders) {
                ReadConsoleInfo consoleInfo = new ReadConsoleInfo();
                consoleInfo.setExampleNumber(2); // HENN example
                consoleInfo.setItemsLocationString(order);

                ArrayList<Integer> settingNumbers = new ArrayList<>();
                if (order.equals("abc1") || order.equals("ran1")) {
                    settingNumbers.add(29);
                    settingNumbers.add(30);
                    settingNumbers.add(31);
                    settingNumbers.add(32);
                    settingNumbers.add(37);
                    settingNumbers.add(38);
                    settingNumbers.add(39);
                    settingNumbers.add(40);
                    settingNumbers.add(61);
                    settingNumbers.add(62);
                    settingNumbers.add(63);
                    settingNumbers.add(64);
                    settingNumbers.add(69);
                    settingNumbers.add(70);
                    settingNumbers.add(71);
                    settingNumbers.add(72);
                } else {
                    settingNumbers.add(9);
                    settingNumbers.add(10);
                    settingNumbers.add(11);
                    settingNumbers.add(12);
                    settingNumbers.add(17);
                    settingNumbers.add(18);
                    settingNumbers.add(19);
                    settingNumbers.add(20);
                    settingNumbers.add(45);
                    settingNumbers.add(46);
                    settingNumbers.add(47);
                    settingNumbers.add(48);
                    settingNumbers.add(53);
                    settingNumbers.add(54);
                    settingNumbers.add(55);
                    settingNumbers.add(56);
                }

                for (int setting : settingNumbers) {
                    consoleInfo.setSettingNumber(setting);
                    consoleInfo.getNumberOrdersHenn();
                    hennInstances.add(new InstancesLoaderFromConsoleInfo(consoleInfo));
                }
            }
        } catch (Exception e) {
            System.err.println("Exception in LoadExperiments HENN: " + e.getMessage());
        }

        return hennInstances;
    }

    /**
     * Retrieves all instances for the Albareda example.
     *
     * @return A list of Albareda instances.
     */
    public ArrayList<InstancesLoaderFromConsoleInfo> getAllInstancesAlbareda() {
        ArrayList<InstancesLoaderFromConsoleInfo> albaredaInstances = new ArrayList<>();

        try {
            ArrayList<Integer> warehouseNumbers = new ArrayList<>();
            warehouseNumbers.add(1);
            warehouseNumbers.add(2);
            warehouseNumbers.add(3);
            warehouseNumbers.add(4);

            for (Integer warehouse : warehouseNumbers) {
                ReadConsoleInfo consoleInfo = new ReadConsoleInfo();
                consoleInfo.setExampleNumber(1); // Albareda example
                consoleInfo.setWarehouseNumber(warehouse);

                ArrayList<Integer> ordersNumbers = new ArrayList<>();
                ordersNumbers.add(50);
                ordersNumbers.add(100);
                ordersNumbers.add(150);
                ordersNumbers.add(200);
                ordersNumbers.add(250);

                for (Integer orders : ordersNumbers) {
                    consoleInfo.setOrdersNumber(orders);

                    ArrayList<String> instanceNumbers = new ArrayList<>();
                    instanceNumbers.add("000");
                    instanceNumbers.add("030");
                    instanceNumbers.add("060");
                    instanceNumbers.add("090");

                    for (String instance : instanceNumbers) {
                        consoleInfo.setInstanceNumber(instance);
                        albaredaInstances.add(new InstancesLoaderFromConsoleInfo(consoleInfo));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Exception in LoadExperiments ALBAREDA: " + e.getMessage());
        }

        return albaredaInstances;
    }
    /**
     * Retrieves all instances for the Arbex example.
     *
     * @return A list of Arbex instances.
     */
    public ArrayList<InstancesLoaderFromConsoleInfo> getAllInstancesArbex(){
        ArrayList<InstancesLoaderFromConsoleInfo> arbexInstances=new ArrayList<>();
        ReadConsoleInfo consoleInfo = new ReadConsoleInfo();

        for(int w=1; w<5 ; w++) {
            consoleInfo.setWarehouseType(w);
            consoleInfo.warehouseTypeToName();
            System.out.println("Cargando instancias almacen: " + consoleInfo.getWarehouseTypeString());


            for (int i = 0; i < 2; i++) {
                StringBuilder sb = new StringBuilder();
                String ruta = "./Warehouses_instances/legacy/W6_Arbex/";
                String small = "smallInstances";
                String large = "largeInstances";
                sb.append(ruta);

                if (i == 0) {
                    sb.append(small);
                    consoleInfo.setOrderTypeString(small);
                } else {
                    sb.append(large);
                    consoleInfo.setOrderTypeString(large);
                }
                System.out.println(" -- Cargando instancias order tipo: " + consoleInfo.getOrderTypeString());

                consoleInfo.setExampleNumber(3); // Arbex example
                File carpeta = new File(sb.toString());
                if (carpeta.isDirectory()) {
                    // Obtener la lista de archivos en la carpeta
                    String[] archivos = carpeta.list();

                    // Verificar si hay archivos en la carpeta
                    if (archivos != null) {
                        for (String archivo : archivos) {
                            if (!archivo.equals("readme.txt")) {
                                consoleInfo.setInstanceFileName(archivo);

                                arbexInstances.add(new InstancesLoaderFromConsoleInfo(consoleInfo));
                            }

                        }
                    } else {
                        System.out.println("La carpeta está vacía o no se pudo acceder a ella.");
                    }
                } else {
                    System.out.println("La ruta proporcionada no es una carpeta.");
                }

            }
        }

        return arbexInstances;
    }

    /**
     * Retrieves all instances for the JCR example.
     *
     * @return A list of JCR instances.
     */
    public ArrayList<InstancesLoaderFromConsoleInfo> getAllInstances258JCR(){
        ArrayList<InstancesLoaderFromConsoleInfo> jcrInstances = new ArrayList<>();
        String ruta = "./Warehouses_instances/legacy/W7_258JCR";
        File carpeta = new File(ruta);

        if (carpeta.isDirectory()) {
            String[] archivos = carpeta.list();

            if (archivos != null) {
                for (String archivo : archivos) {
                    if (archivo.endsWith(".xlsx") && !archivo.equals("picking line.xlsx")) {
                        ReadConsoleInfo consoleInfo = new ReadConsoleInfo();
                        consoleInfo.setExampleNumber(4); // JCR example
                        consoleInfo.setInstanceFileName(archivo);

                        //System.out.println("Cargando archivo: " + archivo); // debug
                        jcrInstances.add(new InstancesLoaderFromConsoleInfo(consoleInfo));
                    }
                }
            } else {
                System.out.println("La carpeta está vacía o no se pudo acceder a ella.");
            }
        } else {
            System.out.println("La ruta proporcionada no es una carpeta.");
        }

        return jcrInstances;
    }


    public ArrayList<InstancesLoaderFromConsoleInfo> getAllInstancesOBSPPS() {
        ArrayList<InstancesLoaderFromConsoleInfo> obsppsInstances = new ArrayList<>();
        ReadConsoleInfo consoleInfo = new ReadConsoleInfo();

        String basePath = "./Warehouses_instances/legacy/W8_OBSPPS/";
        String[] problemTypes = { "SmallProblemClasses", "LargeProblemClasses" };

        for (String problemType : problemTypes) {
            consoleInfo.setProblemClassString(problemType);  // Guardamos tipo de orden
            System.out.println(" -- Cargando instancias tipo: " + problemType);

            File tipoDir = new File(basePath + problemType);
            if (tipoDir.isDirectory()) {
                File[] subDirs = tipoDir.listFiles(File::isDirectory);
                if (subDirs != null) {
                    for (File subDir : subDirs) {
                        consoleInfo.setNumberClassString(subDir.getName()+"/");
                        File[] instanceFiles = subDir.listFiles((dir, name) -> !name.equalsIgnoreCase("readme.txt"));
                        if (instanceFiles != null) {
                            for (File instanceFile : instanceFiles) {

                                consoleInfo.setInstanceFileName(instanceFile.getName());
                                consoleInfo.setExampleNumber(5); // OBS_PPS example
                                obsppsInstances.add(new InstancesLoaderFromConsoleInfo(consoleInfo));

                                // Mostrar nombre del archivo guardado
                                //System.out.println("   -> Añadido archivo: " + instanceFile.getName());
                            }
                        } else {
                            System.out.println("No se pudieron listar archivos de: " + subDir.getName());
                        }
                    }
                } else {
                    System.out.println("No se pudieron listar subdirectorios de: " + tipoDir.getName());
                }
            } else {
                System.out.println("Ruta no válida o no es directorio: " + tipoDir.getPath());
            }
        }

        return obsppsInstances;
    }




}
