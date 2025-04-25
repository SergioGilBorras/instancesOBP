# Warehouse Instance Dataset Documentation

This folder contains datasets divided into four groups: **abc1**, **abc2**, **ran1**, and **ran2**, which correspond to the four datasets used. "ABC" and "RAN" indicate the item placement strategy within the warehouse.

Also, it can find the **ArrivalTimes** folder, which contains the arrival times of the orders.

## Instance Types

Each folder with datasets contains two types of files to represent each instance:

1. **Warehouse Layout Instances**
    - **File naming convention**: `sett<number_of_setting>.txt`
        - `<number_of_setting>`: An integer number related to a specific layout configuration of the warehouse.
    - **Structure**: The structure of the setting file is described in the section `Structure of the Setting Files`.


2. **Order Instances**
    - **File naming convention**: `<setting_number>[s|l]-<number_of_orders>-<picker_capacity>-<number_of_instance>.txt`
        - `<setting_number>`: The number of the setting.
        - `[s|l]`: Inherited from the set of instances of Sebastian Henn.
        - `<number_of_orders>`: The number of orders in that instance (40, 60, 80, 100).
        - `<picker_capacity>`: The picker capacity (30, 45, 60, 75).
        - `<number_of_instance>`: The number of the instance (just 0 in this case).

   **Important lines in the file**:
    - For every order:
        - `Order <number_of_order> number of articles <number_of_articles>`:
            - `<number_of_order>`: The number of the order.
            - `<number_of_articles>`: The number of articles in that order.
        - A list of `<number_of_articles>` with the following information:
            - `<number_of_item_inside_the_order>`: Integer number with the number of the item.
            - `Aisle <number_of_aisle>`: The aisle number where the article is located (note: there are twice as many aisles as in reality).
            - `Location <position_in_aisle>`: The storage location of the item within an aisle, from 0 to the length of the aisle.

## Structure of the Setting Files

| Abbreviation | Description                                                   |
|--------------|---------------------------------------------------------------|
| `no_aisles_` | Number of aisles                                              |
| `no_cells`   | Number of storage locations on each side of an aisle          |
| `opgrade`    | Not relevant for these experiments                            |
| `cell_lengt` | Length of a storage location                                  |
| `cell_width` | Width of a storage location                                   |
| `aisle_widt` | Width of an aisle                                             |
| `dis_ais_wa` | Distance between depot and front cross aisle                  |
| `no_aisle_a` | Number of aisles containing articles of class a               |
| `no_aisle_b` | Number of aisles containing articles of class b               |
| `no_aisle_c` | Number of aisles containing articles of class c               |
| `arrangemen` | Not relevant for these experiments                            |
| `routing`    | Routing scheme: (s): S-Shape-Routing (l): Largest Gap-Routing |
| `type`       | Not relevant for these experiments                            |
| `no_orders_` | Number of customer orders                                     |
| `a_p_or_mea` | Average number of articles per customer order                 |
| `a_p_or_var` | Variance for the number of articles per customer order        |
| `qntity_exp` | Not relevant for these experiments                            |
| `prop_cla_a` | Percent of demand belonging to articles of class a            |
| `prop_cla_b` | Percent of demand belonging to articles of class b            |
| `prop_cla_c` | Percent of demand belonging to articles of class c            |
| `no_instanc` | Number of instances per problem class                         |
| `m_no_o_p_b` | Not relevant for these experiments                            |
| `m_no_a_p_b` | Capacity of the picking device in number of articles          |
| `m_ca_p_b`   | Not relevant for these experiments                            |
| `no_of_work` | Not relevant for these experiments                            |
| `speed_move` | Not relevant for these experiments                            |
| `speed_pick` | Not relevant for these experiments                            |
| `art_capacy` | Not relevant for these experiments                            |
| `empty_posi` | Not relevant for these experiments                            |
| `chaotical_` | Not relevant for these experiments                            |

## Related Publications

The datasets in this folder have been used in the following publications:

### Original Dataset:

- **Sebastian Henn's Dataset**: Henn, S., et al. (Year). *Title of the original article*. Journal Name, Volume(Issue), Pages. DOI/Link.

### Derived Studies:

- [Add derived studies here if applicable.]

## Notes

This dataset is provided for non-commercial research purposes only. Please cite the relevant articles when using this dataset in your work.