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
package com.instancesobp.instancesGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Generates arrival times for orders based on an exponential distribution.
 * This class creates a file with the generated arrival times for a given number of orders
 * within a specified time horizon. The arrival times are calculated using a random seed
 * and an exponential distribution formula.
 * The generated data is saved in a structured file for further use in simulations.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class ArrivalTimesGenerator {

    /**
     * Random number generator for generating arrival times.
     */
    private final Random random;

    /**
     * Time horizon in milliseconds for generating arrival times.
     */
    private final long timeHorizon;

    /**
     * The seed used for the random number generator.
     */
    private final long randomSeed;
    /**
     * Total number of orders to generate.
     */
    private final int totalOrdersToServe;
    /**
     * The name of the instance for which arrival times are generated.
     */
    private final String instanceName;
    /**
     * Counter for the number of orders generated.
     */
    private int orderCount = 0;

    /**
     * Constructor for the ArrivalTimesGenerator class.
     * Initializes the generator with the specified parameters and prepares the output file.
     *
     * @param randomSeed         The seed for the random number generator.
     * @param totalOrdersToServe The total number of orders to generate.
     * @param timeHorizon        The time horizon in milliseconds for generating arrival times.
     * @param instanceName       The name of the instance for which arrival times are generated.
     */
    public ArrivalTimesGenerator(long randomSeed, int totalOrdersToServe, long timeHorizon, String instanceName) {
        this.random = new Random(randomSeed);
        this.timeHorizon = timeHorizon;
        this.totalOrdersToServe = totalOrdersToServe;
        this.instanceName = instanceName;
        this.randomSeed = randomSeed;
    }

    /**
     * Creates the output file for storing the generated arrival times.
     *
     * @param totalOrdersToServe The total number of orders to generate.
     * @param instanceName       The name of the instance for which arrival times are generated.
     * @return The created file.
     * @throws Exception If the file or directory cannot be created.
     */
    private static File createOutputFile(int totalOrdersToServe, String instanceName) throws Exception {
        File directory = new File("./ArrivalTimeOrders/");
        if (!directory.exists() && !directory.mkdir()) {
            throw new Exception("Cannot write to the directory ./ArrivalTimeOrders/. Check permissions.");
        }

        directory = new File("./ArrivalTimeOrders/" + instanceName.split("_")[0] + "/");
        if (!directory.exists() && !directory.mkdir()) {
            throw new Exception("Cannot write to the directory ./ArrivalTimeOrders/. Check permissions.");
        }

        String filePath = "./ArrivalTimeOrders/" + instanceName.split("_")[0] + "/ArrivalTimeOrders_Exp_" + totalOrdersToServe + "_" + instanceName + ".txt";
        return new File(filePath);
    }

    /**
     * Runs the arrival time generation process.
     * Generates arrival times for the specified number of orders and writes them to the output file.
     *
     * @return True if the total time is within the time horizon, false otherwise.
     * @throws IOException If an error occurs while writing to the file.
     */
    public boolean run() throws Exception {
        long totalTime;
        long maxWaitTime;


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(createOutputFile(totalOrdersToServe, instanceName)))) {
            writer.write("Starting exponential distribution of package arrivals.\r\n");
            writer.write("Number of orders to deliver: " + totalOrdersToServe + "\r\n");
            writer.write("Time horizon: " + timeHorizon + "ms.\r\n");
            writer.write("Seed: " + randomSeed + "\r\n");

            long millisToNextArrival = Math.round(-(timeHorizon * Math.log(random.nextFloat())) / totalOrdersToServe);

            maxWaitTime = millisToNextArrival;
            totalTime = millisToNextArrival;

            System.out.println("Order: " + orderCount + " - Next order in: " + millisToNextArrival + "ms.");
            writer.write(millisToNextArrival + "\r\n");

            while (orderCount < totalOrdersToServe) {
                orderCount++;
                millisToNextArrival = Math.round(-(timeHorizon * Math.log(random.nextFloat())) / totalOrdersToServe);

                if (maxWaitTime < millisToNextArrival) {
                    maxWaitTime = millisToNextArrival;
                }
                totalTime += millisToNextArrival;

                System.out.println("Order: " + orderCount + " - Next order in: " + millisToNextArrival + "ms.");
                writer.write(millisToNextArrival + "\r\n");
            }

            if (totalTime <= timeHorizon) {
                writer.write("---------------------------------\r\n");
                writer.write("Total time: " + totalTime + "ms.\r\n");
            }

        }

        return timeHorizon - maxWaitTime <= totalTime && totalTime <= timeHorizon;
    }
}