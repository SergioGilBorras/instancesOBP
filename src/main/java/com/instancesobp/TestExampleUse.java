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
import com.instancesobp.instancesReader.json.JsonLoader;
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
        EvaluateAllInstances();
        //EvaluateAllJson();
        //EvaluateInstanceGraphics();
    }

    /**
     * This method evaluates all instances in the given path and calculates the
     * objective function for each instance.
     * It prints the results to the console.
     */
    private static void EvaluateAllInstances() {

        GeneralInstancesLoader generalInstancesLoader = new GeneralInstancesLoader();

        // Expresión regular para excluir los formatos no deseados
        //String excludedPattern = "ARB_W6A_RND_(500|1000|2000|5000)_\\d+";

        double finalSolution = 0;

        for (InstancesLoaderFromConsoleInfo instancesLoaderFromConsoleInfo : generalInstancesLoader.getAllInstancesOBSPPS()) {
            String instanceName = instancesLoaderFromConsoleInfo.getName();

            // Evitar los formatos no deseados
           // if (instanceName.matches(excludedPattern)) {
             //   System.out.println("Instancia excluida: " + instanceName);
              //  continue;
           // }

            System.out.println("Instancia: " + instanceName);

            Warehouse warehouse = instancesLoaderFromConsoleInfo.getWarehouse();
            warehouse.setPickingTime(0);

            List<Order> LO = warehouse.getOrders();

            try {
                ObjectiveFunction objectiveFunction = new PickingTime(warehouse, selectAlgorithm(RoutingAlgorithmSelector.RoutingAlgorithmType.COMBINED, warehouse));
                CWSavingConstructive basicConstructive = new CWSavingConstructive(warehouse, objectiveFunction);

                long time = System.currentTimeMillis();
                List<Batch> batchList = basicConstructive.run(LO);
                double solution = objectiveFunction.run(batchList);
                System.out.println(instanceName + ";" + solution + ";" + (System.currentTimeMillis() - time));
                finalSolution += solution;

                validateSolution(warehouse, batchList);

            } catch (Exception ex) {
                System.err.println("Error!! Execute.main() " + ex.getMessage());
            }
        }

        System.out.println("FINAL:: " + finalSolution);
    }


    /**
     * This method evaluates all Json in the given path and calculates the
     * objective function for each instance.
     * It prints the results to the console.
     */
    private static void EvaluateAllJson() {
        JsonLoader jsonLoader = new JsonLoader();
        jsonLoader.run();

        List<JsonLoader.WarehouseInstance> warehouseInstances = jsonLoader.getWarehouseInstances();

        // Expresión regular para excluir los números 200, 500, 1000, 2000 y 5000
        String excludedPattern = "ARB_W6A_RND_(500|1000|2000|5000)_\\d+_ID\\d+\\.json";

        double finalSolution = warehouseInstances.parallelStream()
                .filter(warehouseInstance -> !warehouseInstance.getName().matches(excludedPattern))
                .mapToDouble(warehouseInstance -> {
                    String instanceName = warehouseInstance.getName();
                    Warehouse warehouse = warehouseInstance.getWarehouse();
                    warehouse.setPickingTime(0);

                    List<Order> LO = warehouse.getOrders();

                    try {
                        ObjectiveFunction objectiveFunction = new PickingTime(warehouse, selectAlgorithm(RoutingAlgorithmSelector.RoutingAlgorithmType.COMBINED, warehouse));
                        CWSavingConstructive basicConstructive = new CWSavingConstructive(warehouse, objectiveFunction);

                        long startTime = System.currentTimeMillis();
                        List<Batch> batchList = basicConstructive.run(LO);
                        double solution = objectiveFunction.run(batchList);
                        long endTime = System.currentTimeMillis();

                        System.out.println(instanceName + ";" + solution + ";" + (endTime - startTime));

                        validateSolution(warehouse, batchList);

                        return solution;
                    } catch (Exception ex) {
                        System.err.println("Error!! Execute.main() " + ex.getMessage());
                        return 0;
                    }
                })
                .sum();

        System.out.println("FINAL:: " + finalSolution);
    }


    /**
     * This method evaluates a specific instance and generates a
     * graphics representation of the results.
     */
    private static void EvaluateInstanceGraphics() {

        GeneralInstancesLoader LIL = new GeneralInstancesLoader();
        InstancesLoaderFromConsoleInfo IL = LIL.getAllInstancesOBSPPS().get(10);//getAllInstancesHENN().get(0);//getInstanceByName("A_W4_50_060");
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
