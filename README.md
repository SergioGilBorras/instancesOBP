# Standardized Repository for Order Batching Problem Instances

**OBP-Instances** is an open-source collection of all known instances from the academic literature on the *Order Batching Problem (OBP)*. 
All instances are transformed into a unified, machine-readable **JSON** format to ensure consistency and ease of use.

In addition to being a standardized dataset hub, this project provides a basic testing environment and interactive visualization tools to evaluate batching and routing algorithms commonly used in warehouse operations.

## 📦 Project Overview

This project was developed to consolidate and evaluate OBP datasets published by operations research experts.  
It currently includes **five major datasets**, unified under a single JSON schema, and provides tools to:

- **Parse and Load**: Read standardized JSON files and load them into Java model classes.
- **Visualize**: Display instances and routing paths graphically, including warehouse layouts and picking routes.
- **Evaluate and Optimize**: Use predefined batching constructors, routing heuristics, and objective functions to analyze and compare solutions across datasets.

Additionally, the project includes an **instance generator**, which allows:
- Creating new instances from scratch.
- Modifying existing instances to test custom scenarios or stress-test algorithms.

## 🔍 Purpose

This tool is ideal for:
- Researchers comparing OBP heuristics or metaheuristics.
- Educators teaching order picking and warehouse optimization.
- Developers testing algorithmic approaches in a controlled environment.


## ✨ API Documentation

You can browse the full Javadoc documentation for this project online at:

<p align="center"><a target="javadoc" href="https://sergiogilborras.github.io/instancesOBP/target/site/apidocs/index.html"><strong>[Ver Javadoc]</strong></a></p>

This documentation is automatically generated and published and it includes:

- Class descriptions
- Method usage
- Data structures and models used in the OBP instances
- Useful utilities for loading, evaluating and visualizing instances
---

## JSON Schema Documentation

This document explains the structure of the JSON configuration for a warehouse instance. The schema contains details about the instance, warehouse configuration, orders, and products.
Top-Level Object

    instance_set_name: string
    The name of the instance set (e.g., "Albareda").

    URL: string
    A URL associated with the instance (empty if not provided).

    papers: array
    An array for papers or documents (currently empty).

    instance: object
    The main object containing all instance-specific data.

### Instance Object

The instance object includes:

    name: string
    The name of the instance (e.g., "A_W1_50_000").

    warehouse: object
    Contains the warehouse configuration and operational details.

### Warehouse Object

The warehouse object includes the following fields:

    order_count: number
    Total number of orders (originally numero_pedidos).

    aisle_count: number
    Total number of aisles (originally numero_pasillos).

    item_count: number
    Total number of items (originally numero_items).

    slot_count: number
    Total number of slots (originally numero_huecos).

    depot_location: number
    Depot location indicator (0 -> at bottom left, 1 -> at botton centre).

    order_location: number
    Order location indicator (0 -> ABC, 1 -> Random).

    shelf_length: number
    Length of the shelves (originally largo_estanterias).

    shelf_width: number
    Width of the shelves (originally ancho_estanterias).

    aisle_width: number
    Width of the aisles (originally ancho_pasillos).

    picker_capacity: number
    Capacity of the picker (originally capacidad_trabajador).

    picking_time: number
    Time allocated for picking (originally tiempo_picking).

    outside_turn_time: number
    Time required for turning outside (originally tiempo_giro_fuera).

    inside_turn_time: number
    Time required for turning inside (originally tiempo_giro_dentro).

    depot_time: number
    Time allocated for depot operations (originally tiempo_depot).

    travel_speed: number
    Speed of travel within the warehouse (originally velocidad_travel).

    aisles: array
    An array of aisle objects.

    hours: number
    The number of operating hours.

    orders: array
    An array of order objects.


### Aisles Array

Each object in the aisles array represents an aisle and contains:

    aisle_id: number
    Identifier for the aisle (originally id_pasillo).

    right_origin_distance: number
    Distance from the right origin (originally distancia_origen_derecho).

    left_origin_distance: number
    Distance from the left origin (originally distancia_origen_izquierdo).

    side: number
    Side indicator -1 -> if the depot is located in the left side of the aisle, 
                    0 -> if the depot is located in front of the aisle, 
                    1 -> if the depot is located in the right side of the aisle

### Orders Array

Each object in the orders array represents an individual order with the following fields:

    id: number
    The order identifier.

    due_date: number
    The due date of the order (originally duedate).

    arrival_time: number
    The arrival time of the order (originally arrivalTime).

    weight: number
    The weight of the order.

    products: array
    An array of product objects included in the order.

    number_of_references: number
    The number of references (originally numReferences).

### Products Array

Each object in the products array represents a product within an order and includes:

    id: number
    The product identifier.

    aisle: number
    The aisle where the product is located (originally pasillo).

    side: number
    The side indicator 0 -> if it is located in the left side of the aisle, 
                       1 -> if it is located in the right side of the aisle.

    height_location: number
    The height loation of the product in this side of the aisle (originally altura).

    weight: number
    The weight of the product.:

## Summary of the Instance Datasets Used

ALBAREDA ID13 \cite{Albareda2009}

W1: ID 16 \cite{Koster1999b}
W2: ID 16 \cite{Koster1999b}
W3: ID 16 \cite{Koster1999b}
W4: ID 151 \cite{ho2006study}



## Project Structure

- **src/instancesobp**: Contains the main classes of the project.
- **src/instancesobp/batchingAlgorithm**: Contains the order batching algorithms.
- **src/instancesobp/evaluationResults**: Contains the classes to evaluate and display results.
- **src/instancesobp/instancesReader**: Contains the classes to load test instances.
- **src/instancesobp/models**: Contains the data models used in the project.
- **src/instancesobp/objectiveFunction**: Contains the objective functions to evaluate solutions.
- **src/instancesobp/routingAlgorithm**: Contains the routing algorithms.

## Test Classes

### TestExampleUse

This class is used to test the functionality of the project. It evaluates all instances in the given path and calculates the objective function for each instance. It also generates a graphical representation of the results.

- **Methods**:
    - `main(String[] args)`: Entry point of the program. Calls the `EvaluateAllInstances()` and `EvaluateInstanceGraphics()` methods.
    - `EvaluateAllInstances()`: Evaluates all instances in the given path and calculates the objective function for each instance.
    - `EvaluateInstanceGraphics()`: Evaluates a specific instance and generates a graphical representation of the results.

### TestInstanceGenerator

This class is used to test instance generation. It can generate new instances and display the graphics of the results.

- **Methods**:
    - `main(String[] args)`: Entry point of the program. Generates new instances and displays the graphics of the results.

## Configuration

### Configuration

This class contains the configuration of the project.

- **Attributes**:
    - `public static final int SEED`: Seed used to generate random numbers for the algorithm. It is used to ensure that the results are reproducible.

## License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to use
the Software for non-commercial research purposes only, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE

Copyright (c) 2025 Sergio Gil Borrás