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

                ArrayList<Integer> batchNumbers = new ArrayList<>();
                batchNumbers.add(50);
                batchNumbers.add(100);
                batchNumbers.add(150);
                batchNumbers.add(200);
                batchNumbers.add(250);

                for (Integer batch : batchNumbers) {
                    consoleInfo.setBatchesNumber(batch);

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
}
