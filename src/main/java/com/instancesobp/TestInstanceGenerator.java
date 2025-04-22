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

import java.util.ArrayList;
import java.util.List;

import com.instancesobp.batchingAlgorithm.constructiveHeuristic.BasicConstructive;
import com.instancesobp.batchingAlgorithm.sortOrderList.SortByWeight;
import com.instancesobp.evaluationResults.graphics.Graphics;
import com.instancesobp.instancesReader.legacy.InstancesLoaderFromConsoleInfo;
import com.instancesobp.instancesReader.legacy.GeneralInstancesLoader;
import com.instancesobp.models.Batch;
import com.instancesobp.models.Warehouse;
import com.instancesobp.instancesGenerator.InstanceGenerator;

/**
 * This class is used to test instances generation, can generate new instances
 * and show the graphics of the results. *
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class TestInstanceGenerator {

    /**
     * Private constructor to prevent instantiation.
     */
    private TestInstanceGenerator() {
        throw new UnsupportedOperationException("This is a main class and cannot be instantiated");
    }

    /**
     * Main method to test the instance generation and show the graphics generated.
     *
     * @param args for command line arguments (not used).
     * @throws Exception for any exception that may occur
     */
    public static void main(String[] args) throws Exception {
        GeneralInstancesLoader generalInstancesLoader = new GeneralInstancesLoader();
        ArrayList<String> instancesList = new ArrayList<>();

        instancesList.add("A_W1_50_060");
        for (InstancesLoaderFromConsoleInfo instancesLoaderFromConsoleInfo : generalInstancesLoader.getInstancesByName(instancesList)) {
            Warehouse warehouse = instancesLoaderFromConsoleInfo.getWarehouse();
            InstanceGenerator instanceGenerator = new InstanceGenerator();
            Warehouse warehouse1 = instanceGenerator.generateWarehouse(warehouse, true, 5, 1, false);

            BasicConstructive BSH = new BasicConstructive(warehouse1, new SortByWeight(), false);

            List<Batch> batchList = BSH.run(warehouse1.getOrders());

            Graphics G = new Graphics(warehouse1, batchList);
            G.renderControls();
        }
    }
}
