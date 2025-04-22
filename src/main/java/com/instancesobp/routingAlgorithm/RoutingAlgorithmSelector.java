package com.instancesobp.routingAlgorithm;


import com.instancesobp.models.Warehouse;

/**
 * Selector for routing algorithms based on the specified type.
 */
public class RoutingAlgorithmSelector {

    /**
     * Private constructor to prevent instantiation.
     */
    private RoutingAlgorithmSelector() {
        throw new UnsupportedOperationException("This is a main class and cannot be instantiated");
    }

    /**
     * Returns an instance of the selected routing algorithm.
     *
     * @param type      The type of routing algorithm to use.
     * @param warehouse The warehouse object containing layout and configuration details.
     * @return An instance of the selected routing algorithm.
     */
    public static RoutingAlgorithm selectAlgorithm(RoutingAlgorithmType type, Warehouse warehouse) {
        return switch (type) {
            case RATLIFF_ROSENTHAL -> new RatliffRosenthal(warehouse);
            case LARGEST_GAP -> new Largest_Gap(warehouse);
            case S_SHAPE -> new S_Shape(warehouse);
            case COMBINED_PLUS -> new CombinedPlus(warehouse);
            case COMBINED -> new Combined(warehouse);
        };
    }

    /**
     * Enum representing the available routing algorithms.
     */
    public enum RoutingAlgorithmType {
        /**
         * Ratliff-Rosenthal routing algorithm.
         */
        RATLIFF_ROSENTHAL,
        /**
         * Largest gap routing algorithm.
         */
        LARGEST_GAP,
        /**
         * S-shape routing algorithm.
         */
        S_SHAPE,
        /**
         * Combined Plus routing algorithm.
         */
        COMBINED_PLUS,
        /**
         * Combined routing algorithm.
         */
        COMBINED
    }
}
