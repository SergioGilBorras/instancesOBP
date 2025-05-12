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

import com.instancesobp.batchingAlgorithm.constructiveHeuristic.BasicConstructive;
import com.instancesobp.batchingAlgorithm.constructiveHeuristic.CWSavingConstructive;
import com.instancesobp.batchingAlgorithm.sortOrderList.SortByWeight;
import com.instancesobp.evaluationResults.graphics.Graphics;
import com.instancesobp.instancesReader.legacy.InstancesLoaderFromConsoleInfo;
import com.instancesobp.instancesReader.legacy.GeneralInstancesLoader;
import com.instancesobp.models.Batch;
import com.instancesobp.models.Order;
import com.instancesobp.models.Warehouse;
import com.instancesobp.objectiveFunction.ObjectiveFunction;
import com.instancesobp.objectiveFunction.PickingTime;
import com.instancesobp.routingAlgorithm.*;

import java.util.List;

import static com.instancesobp.routingAlgorithm.RoutingAlgorithmSelector.selectAlgorithm;
import static com.instancesobp.utils.BatchOperations.validateSolution;

/**
 * This class is used to test the functionality of the project.
 * It evaluates all instances in the given path and calculates the
 * objective function for each instance.
 * It also generates a graphics representation of the results.
 * The main method is the entry point of the program.
 * It calls the EvaluateAllInstances() method to evaluate all instances
 * It calls the EvaluateInstanceGraphics() method to evaluate one instances and show the graphics.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class TestExampleUse {

    /**
     * Private constructor to prevent instantiation.
     */
    private TestExampleUse() {
        throw new UnsupportedOperationException("This is a main class and cannot be instantiated");
    }

    /**
     * Main method to test the instances and show the graphics of them.
     *
     * @param args the command line arguments (not used).
     */
    public static void main(String[] args) {
        //EvaluateAllInstances();
        EvaluateInstanceGraphics();
    }

    /**
     * This method evaluates all instances in the given path and calculates the
     * objective function for each instance.
     * It prints the results to the console.
     */
    private static void EvaluateAllInstances() {

        GeneralInstancesLoader generalInstancesLoader = new GeneralInstancesLoader();

        //ArrayList<String> instanciasL = new ArrayList<>();
        //instanciasL.add("A_W1_50_000");
        //instanciasL.add("A_W2_50_000");
        //for (InstancesLoader instancesLoader : listInstancesLoader.getInstancesByName(instanciasL)) {
        double final_solution = 0;
        for (InstancesLoaderFromConsoleInfo instancesLoaderFromConsoleInfo : generalInstancesLoader.getAllInstancesArbex()) {
            System.out.println("Instancia: " + instancesLoaderFromConsoleInfo.getName());

            Warehouse warehouse = instancesLoaderFromConsoleInfo.getWarehouse();
            warehouse.setPickingTime(0);


            List<Order> LO = warehouse.getOrders();

            try {
                ObjectiveFunction objectiveFunction = new PickingTime(warehouse, selectAlgorithm(RoutingAlgorithmSelector.RoutingAlgorithmType.COMBINED, warehouse));

                CWSavingConstructive basicConstructive = new CWSavingConstructive(warehouse, objectiveFunction);


                long time = System.currentTimeMillis();

                List<Batch> batchList = basicConstructive.run(LO);
                double solution = objectiveFunction.run(batchList);
                System.out.println(instancesLoaderFromConsoleInfo.getName() + ";" + solution + ";" + (System.currentTimeMillis() - time));
                final_solution += solution;

                validateSolution(warehouse, batchList);

            } catch (Exception ex) {
                System.err.println("Error!! Execute.main() " + ex.getMessage());
                //ex.printStackTrace();
            }

        }
        System.out.println("FINAL:: " + final_solution);

    }

    /**
     * This method evaluates a specific instance and generates a
     * graphics representation of the results.
     */
    private static void EvaluateInstanceGraphics() {

        GeneralInstancesLoader LIL = new GeneralInstancesLoader();
        InstancesLoaderFromConsoleInfo IL = LIL.getAllInstancesArbex().get(143);//getAllInstancesHENN().get(0);//getInstanceByName("A_W4_50_060");
        if (IL != null) {
            System.out.println("Instancia: " + IL.getName());

            Warehouse warehouse = IL.getWarehouse();
            warehouse.setPickingTime(0);


            List<Order> orderList = warehouse.getOrders();

            BasicConstructive basicConstructive = new BasicConstructive(warehouse, new SortByWeight(), false);
            try {
                ObjectiveFunction objectiveFunction = new PickingTime(warehouse, selectAlgorithm(RoutingAlgorithmSelector.RoutingAlgorithmType.LARGEST_GAP, warehouse));

                long time = System.currentTimeMillis();

                List<Batch> batchList = basicConstructive.run(orderList);
                double solution = objectiveFunction.run(batchList);
                System.out.println(IL.getName() + ";" + solution + ";" + (System.currentTimeMillis() - time));

                validateSolution(warehouse, batchList);

                Graphics G = new Graphics(warehouse, batchList);
                G.renderControls();

            } catch (Exception ex) {
                System.err.println("Error!! Execute.main() " + ex.getMessage());
                //ex.printStackTrace();
            }

        }

    }

}
