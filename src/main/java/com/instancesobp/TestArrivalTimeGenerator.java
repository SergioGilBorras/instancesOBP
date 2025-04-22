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
package com.instancesobp;

import com.instancesobp.instancesGenerator.ArrivalTimesGenerator;
import com.instancesobp.instancesReader.legacy.InstancesLoaderFromConsoleInfo;
import com.instancesobp.instancesReader.legacy.GeneralInstancesLoader;
import com.instancesobp.models.Warehouse;

import java.util.Random;

import static com.instancesobp.Configuration.SEED;

/**
 * Test class for generating arrival times for orders in a warehouse.
 * This class iterates through all instances of warehouses, generates arrival times
 * for orders using an exponential distribution, and ensures that the generated
 * times meet the specified constraints.
 * The generated arrival times are written to a file for further analysis.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class TestArrivalTimeGenerator {

    /**
     * Private constructor to prevent instantiation.
     */
    private TestArrivalTimeGenerator() {
        throw new UnsupportedOperationException("This is a main class and cannot be instantiated");
    }

    /**
     * Main method to execute the arrival time generation process.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {

        // Define the time horizon in milliseconds (4 hours)
        long timeHorizonMs = 4 * 3600000; // 4 hours in milliseconds

        // Initialize a random generator with a predefined seed
        Random seedRandomGenerator = new Random(SEED);

        // Load all instances of warehouses
        GeneralInstancesLoader generalInstancesLoader = new GeneralInstancesLoader();

        // Iterate through each warehouse instance
        for (InstancesLoaderFromConsoleInfo instancesLoaderFromConsoleInfo : generalInstancesLoader.getAllInstances()) {
            Warehouse warehouse = instancesLoaderFromConsoleInfo.getWarehouse();

            // Generate a random seed for the arrival time generator
            long seedRand = seedRandomGenerator.nextInt(100000);
            boolean validSolution = false;

            // Get the number of orders in the warehouse
            int numberOfOrders = warehouse.getNumberOfOrders();

            // Generate arrival times until a valid solution is found
            while (!validSolution) {
                seedRand++;
                try {
                    // Create an instance of the ArrivalTimesGenerator
                    ArrivalTimesGenerator arrivalTimesGenerator = new ArrivalTimesGenerator(
                            seedRand, numberOfOrders, timeHorizonMs, instancesLoaderFromConsoleInfo.getName());

                    // Run the generator and check if the solution is valid
                    validSolution = arrivalTimesGenerator.run();
                } catch (Exception e) {
                    // Print an error message if the generation fails
                    System.out.println("Error generating arrival times: " + e.getMessage());
                }
            }

            // Print the instance name and the seed used for the valid solution
            System.out.println(instancesLoaderFromConsoleInfo.getName() + ":" + seedRand);
        }
    }
}