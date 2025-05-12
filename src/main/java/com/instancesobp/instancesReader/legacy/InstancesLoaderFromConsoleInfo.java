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

import com.instancesobp.models.Warehouse;

/**
 * This class is responsible for loading warehouse instances based on the provided configuration.
 * It supports two types of instance loaders: Henn and Albareda.
 * The class initializes the warehouse object and assigns the instance name.
 * The warehouse instance can be retrieved using the provided getter methods.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class InstancesLoaderFromConsoleInfo {

    /**
     * The name of the instance being loaded.
     */
    private String instanceName;

    /**
     * The warehouse object associated with the loaded instance.
     */
    private Warehouse warehouse;

    /**
     * Constructor for the InstancesLoader class.
     * It determines the type of instance loader to use based on the example number
     * provided in the ReadConsoleInfo object.
     *
     * @param consoleInfo The ReadConsoleInfo object containing the configuration details.
     */
    public InstancesLoaderFromConsoleInfo(ReadConsoleInfo consoleInfo) {
        if (consoleInfo.getExampleNumber() == 2) {
            InstancesLoaderHenn hennLoader = new InstancesLoaderHenn(consoleInfo);
            hennLoader.run();
            warehouse = hennLoader.getWarehouse();
            instanceName = hennLoader.getName();
            warehouse.setInstanceName(instanceName);
        } else if (consoleInfo.getExampleNumber() == 1) {
            InstancesLoaderAlbareda albaredaLoader = new InstancesLoaderAlbareda(consoleInfo);
            albaredaLoader.run();
            warehouse = albaredaLoader.getWarehouse();
            instanceName = albaredaLoader.getName();
            warehouse.setInstanceName(instanceName);
        } else if (consoleInfo.getExampleNumber() == 3) {
            InstancesLoaderArbex arbexLoader = new InstancesLoaderArbex(consoleInfo);
            arbexLoader.run();
            warehouse = arbexLoader.getWarehouse();
            instanceName = arbexLoader.getName();
            warehouse.setInstanceName(instanceName);
        }
    }

    /**
     * Retrieves the name of the loaded instance.
     *
     * @return The name of the instance.
     */
    public String getName() {
        return this.instanceName;
    }

    /**
     * Retrieves the warehouse object associated with the loaded instance.
     *
     * @return The Warehouse object.
     */
    public Warehouse getWarehouse() {
        return this.warehouse;
    }
}
