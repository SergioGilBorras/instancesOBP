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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * This class is responsible for loading the arrival times of orders for a given warehouse.
 * It reads the arrival times from a file and calculates the cumulative arrival times
 * based on the operational hours of the warehouse.
 * The arrival times are stored in seconds and are set in the warehouse object.
 * This class supports loading arrival times for different operational hours.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class ArrivalTimeLoader {

    /**
     * The warehouse object for which arrival times are being loaded.
     */
    private final Warehouse warehouse;
    /**
     * List of arrival times in seconds.
     */
    private final ArrayList<Integer> arrivalTimes;
    /**
     * The number of operational hours for the warehouse.
     */
    private int operationalHours = 1;

    /**
     * Constructor for the ArrivalTimeLoader class.
     * Initializes the loader with the given warehouse and sets the default operational hours to 1.
     *
     * @param warehouse The warehouse object for which arrival times are being loaded.
     */
    public ArrivalTimeLoader(Warehouse warehouse) {
        this.warehouse = warehouse;
        this.arrivalTimes = new ArrayList<>();
    }

    /**
     * Constructor for the ArrivalTimeLoader class.
     * Initializes the loader with the given warehouse and operational hours.
     *
     * @param warehouse        The warehouse object for which arrival times are being loaded.
     * @param operationalHours The number of operational hours for the warehouse.
     */
    public ArrivalTimeLoader(Warehouse warehouse, int operationalHours) {
        this.warehouse = warehouse;
        this.operationalHours = operationalHours;
        this.arrivalTimes = new ArrayList<>();
        this.warehouse.setOperationalHours(operationalHours);
    }

    /**
     * Loads the arrival times of orders from a file.
     * The file is expected to be located in the `./Warehouses_instances/legacy/ArrivalTimes/` directory
     * and named according to the number of orders and operational hours of the warehouse.
     * The method reads the file line by line, calculates the cumulative arrival times,
     * and stores them in the `arrivalTimes` list. The arrival times are then set in the warehouse object.
     */
    public void load() {
        String filePath = "./Warehouses_instances/legacy/ArrivalTimes/TiemposOrders_E_"
                + warehouse.getNumberOfOrders() + "_H" + operationalHours + ".txt";

        // Construct the file path based on the warehouse configuration
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {

            // Skip the first two lines of the file
            bufferedReader.readLine();
            bufferedReader.readLine();

            boolean isFirstLine = true;

            // Read and process each line of the file
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();

                if (isFirstLine) {
                    // Add the first arrival time
                    arrivalTimes.add(Integer.parseInt(line) / 1000);
                    isFirstLine = false;
                } else {
                    // Add the cumulative arrival time
                    arrivalTimes.add(arrivalTimes.get(arrivalTimes.size() - 1) + (Integer.parseInt(line) / 1000));
                }
            }

        } catch (Exception ex) {
            // Handle exceptions and print the error message
            System.err.println("Exception in ArrivalTimeLoader: " + ex.getMessage());
            //ex.printStackTrace();
        }

        // Set the arrival times in the warehouse object
        warehouse.setArrivalTimes(arrivalTimes);
    }
}