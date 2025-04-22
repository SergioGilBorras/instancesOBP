package com.instancesobp.utils;

/**
 * This class contains constant values used throughout the application.
 * It provides predefined values for specific configurations, such as
 * the placement of the depot in the warehouse.
 * These constants help improve code readability and maintainability
 * by avoiding the use of hardcoded values.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class Constants {

    /**
     * Private constructor to prevent instantiation.
     */
    private Constants() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }

    /**
     * Represents the configuration where the depot is located in the corner of the warehouse.
     */
    public static final int DEPOT_CORNER = 0;

    /**
     * Represents the configuration where the depot is located in the center of the warehouse.
     */
    public static final int DEPOT_CENTER = 1;

    /**
     * Represents the configuration where the items location of the warehouse is ABC.
     */
    public static final int ABC_ITEMS_LOCATION = 0;

    /**
     * Represents the configuration where the items location of the warehouse is random.
     */
    public static final int RND_ITEMS_LOCATION = 1;

    /**
     * Represents the configuration where the items location on the left side of the aisle.
     */
    public static final int LEFT_SIDE_AISLE = 0;

    /**
     * Represents the configuration where the items location on the right side of the aisle.
     */
    public static final int RIGHT_SIDE_AISLE = 1;


    /**
     * Represents the configuration where the depot is located on the left side of the aisle.
     */
    public static final int LEFT_SIDE_DEPOT = -1;

    /**
     * Represents the configuration where the depot is located on the right side of the aisle.
     */
    public static final int RIGHT_SIDE_DEPOT = 1;

    /**
     * Represents the configuration where the depot is located on the front of the aisle.
     */
    public static final int FRONT_SIDE_DEPOT = 0;
}
