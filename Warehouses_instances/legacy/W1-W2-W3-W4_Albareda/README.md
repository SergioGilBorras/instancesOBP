# Warehouse Instance Dataset Documentation

This folder contains datasets divided into four groups: **W1**, **W2**, **W3**, and **W4**, corresponding to the four warehouses used in these dataset. Each warehouse is further divided into five folders, each related to the number of orders used.

Also, it can find the **ArrivalTimes** folder, which contains the arrival times of the orders.

## Instance Types

Each folder with datasets contains two types of files to represent each instance:

1. **Warehouse Layout Instances**  
   These files describe the layout of the warehouse.  
   **File naming convention**: `wsrp_input_layout_<number_of_warehouse>_<number_of_instance>.txt`
    - `<number_of_warehouse>`: A two-digit number representing the warehouse (e.g., `01` for W1).
    - `<number_of_instance>`: A three-digit number representing the instance (e.g., `090`).

   **Important lines in the file**:
    - **Line 2**: Number of aisles and total number of items.
    - **Line 4**: Depot location (`0` -> bottom left, `1` -> bottom center).
    - **Line 6**: Item location (`0` -> ABC, `1` -> Random).
    - **Line 8**: Length and width of shelves.
    - **Line 10**: Width of aisles.
    - **Line 12**: Picker capacity.
    - **Line 14**: Picking time (usually `0.0`).
    - **Line 16**: Out-turning and in-turning time (usually `0.0`).
    - **Line 18 and onwards**: Aisle number, distance to the right origin, distance to the left origin, and side relative to the depot (`-1` -> left, `0` -> in front, `1` -> right).

   **Note**: The last line is not relevant.


2. **Order Instances**  
   These files describe the orders and their items.  
   **File naming convention**: `wsrp_input_pedido_<number_of_warehouse>_<number_of_instance>.txt`
    - `<number_of_warehouse>`: A two-digit number representing the warehouse (e.g., `01` for W1).
    - `<number_of_instance>`: A three-digit number representing the instance (e.g., `090`).

   **Important lines in the file**:
    - **Line 2**: Number of orders.
    - **From Line 4 onwards**: For each order:
        - `<duedate>`: Due date of the order.
        - `<number_of_items>`: Number of items in the order.
        - For each item:
            - `<aisle>`: Integer representing the aisle (0 to number of aisles - 1).
            - `<side>`: `0` -> left side, `1` -> right side.
            - `<position>`: Float representing the position of the item in the aisle (0 to aisle length).
            - `<weight>`: Float representing the weight of the item.
            - `<id>`: Integer representing the unique item identifier.


3. **ArrivalTimes Folder**
   The files in this folder contain the arrival times of the orders.
   
   **File naming convention**: `TiemposOrders_<type_of_distribution>_<number_of_orders>_H<operationalHours>.txt`
     - `<type_of_distribution>`: Type of distribution followed by the arrival of orders.
       - `E`: Exponential
       - `U`: Uniform
     - `<number_of_orders>`: Number of orders (50, 100, 150, 200, and 250 orders).
     - `<operationalHours>`: The number of operational hours during which orders arrive at the warehouse. (1...4 Hours)

   **Important lines in the file**:
     - `<Numero de pedidos iniciales>`: Initial number of orders (usually 0).
     - `<Numero de pedidos entregados>`: Number of delivered orders (usually equal to the number of orders).
     - The remaining lines: Each line contains the arrival time (in milliseconds) of the next order.

## Related Publications

The datasets in this folder have been used in the following publications:

### Original Dataset:

- Albareda-Sambola, M., Alonso-Ayuso, A., Molina, E., De Blas, C.S., 2009. Variable neighborhood search for order batching in a warehouse. Asia-Pacific Journal of Operational Research 26, 655–683. doi:10.1142/S0217595909002390.
  
This dataset was developed based on the previous datasets presented in the following studies:

- De Koster, R.B.M., Roodbergen, K.J., Van Voorden, R., 1999. Reduction of walking time in the distribution center of De Bijenkorf. Lecture Notes in economics and mathematical systems. New Trends in Distribution Logistics 480, 215–234. doi:10.1007/978-3-642-58568-511.

- Ho, Y.C., Tseng, Y.Y., 2006. A study on order-batching methods of order picking in a distribution centre with two cross-aisles. International Journal of Production Research 44, 3391–3417. doi:10.1080/00207540600558015.

### Derived Studies:

- Menéndez, B., Pardo, E.G., Duarte, A., Alonso-Ayuso, A., Molina, E., 2015. General variable neighborhood search applied to the picking process in a warehouse. Electronic Notes in Discrete Mathematics 47, 77–84. doi:10.1016/j.endm.2014.11.011

- Menéndez, B., Bustillo, M., Pardo, E.G., Duarte, A., 2017. General Variable Neighborhood Search for the Order Batching and Sequencing Problem. European Journal of Operational Research 263, 82–93. doi:10.1016/j.ejor.2017.05.001

- Menéndez, B., Pardo, E.G., Alonso-Ayuso, A., Molina, E., Duarte, A., 2017. Variable neighborhood search strategies for the order batching problem. Computers & Operations Research 78, 500–512. doi:10.1016/j.cor.2016.01.020

- Menéndez, B., Pardo, E.G., Sánchez-Oro, J., Duarte, A., 2017. Parallel variable neighborhood search for the min-max order batching problem. International Transactions in Operational Research 24, 635–662. doi:10.1111/itor.12309

- Gil-Borrás, S., Pardo, E.G., Alonso-Ayuso, A., Duarte, A., 2018. New VNS Variants for the Online Order Batching Problem. Lecture Notes in Computer Science 11328, 89–100. doi:10.1007/978-3-030-15843-9_8

- Gil-Borrás, S., Pardo, E.G., Alonso-Ayuso, A., Duarte, A., 2019. Basic VNS for a variant of the Online Order Batching Problem. Lecture Notes in Computer Science 12010, 17–36. doi:10.1007/978-3-030-44932-2_2

- Gil-Borrás, S., Pardo, E.G., Alonso-Ayuso, A., Duarte, A., 2020. Fixed versus variable time window warehousing strategies in real time. Progress in Artificial Intelligence 9, 315–324. doi:10.1007/s13748-020-00215-1

- Gil-Borrás, S., Pardo, E.G., Alonso-Ayuso, A., Duarte, A., 2020. GRASP with Variable Neighborhood Descent for the Online Order Batching Problem. Journal of Global Optimization 78, 295–325. doi:10.1007/s10898-020-00910-2.1.

- Gil-Borrás, S., Pardo, E.G., Alonso-Ayuso, A., Duarte, A., 2021. A heuristic approach for the online order batching problem with multiple pickers. Computers & Industrial Engineering 160, 107517. doi:10.1016/j.cie.2021.107517.

- Gil Borrás, S., 2022. Online Order Batching Problem: a heuristic approach for single and multiple pickers. Ph.D. thesis. ETSI Sistemas Informáticos Universidad Politécnica de Madrid. doi:10.20868/UPM.thesis.72538.

- Gil-Borrás, S., Pardo, E.G., Jiménez, E., and, K.S., 2024. The time-window strategy in the online order batching problem. International Journal of Production Research 62, 4446–4469. doi:10.1080/00207543.2023.2263884.

## Notes

This dataset is provided for non-commercial research purposes only. Please cite the relevant articles when using this dataset in your work.