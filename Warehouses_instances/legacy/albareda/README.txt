This folder is divided into four groups W1, W2, W3, and W4 corresponding to the four warehouses used in these experiments.

Each warehouse contains in turn five folders related to the number of orders used for that experiments.

Each number of orders contains two types of instances: those that describe the layout of the warehouse and those that describe the orders with their items:

- For the first ones:
    * They are named as: wsrp_input_layout_<number_of_warehouse>_<number_of_instance>.txt where:
        + <number_of_warehouse> is a two-digit number with the warehouse. For instance, for W1 warehouse it would have a value of 01.
        + <number_of_instance> is a three-digit number with the number of instance. For example, it could have a value of 090.
    * The important lines are those which has numbers:
        + Line 2: number of aisles and number of items (total)
        + Line 4: depot location (0 -> at bottom left, 1 -> at botton centre)
        + Line 6: item location (0 -> ABC, 1 -> Random)
        + Line 8: length and width of shelves
        + Line 10: width of aisles
        + Line 12: picker capacity
        + Line 14: picking time (usually 0.0)
        + Line 16: out-turning time and in-turning time (usually 0.0)
        + Line 18 and nexts: number of aisle, distance to the right origin, distance to the left origin and side respect to the depot (-1 -> left, 0 -> no side, in front of it, 1 -> right)
    * Last line is not important
        
- For the second ones:
    * They are named as: wsrp_input_pedido_<number_of_warehouse>_<number_of_instance>.txt with the same criteria as for the layout instances.
    * Important lines:
        + Line 2: number of orders
        + For every order (from the fourth line) we have <duedate> and <number_of_items>. The <duedate> is not used for this variant.
        + For each item in each order we have: <aisle> <side> <position> <weight> <id> where:
            -- <aisle> is an integer number between 0 and the number of aisles - 1, corresponding to the aisle in which the item is located
            -- <side> is 0 -> if it is located in the left side of the aisle, or 1 -> if it is located in the right side of the aisle
            -- <position> is a float number between 0 and the length of the aisle which contains the position of an item in an aisle
            -- <weight> is a float number with the weight of the item
            -- <id> an integer number with the identification number of the item (unique)
