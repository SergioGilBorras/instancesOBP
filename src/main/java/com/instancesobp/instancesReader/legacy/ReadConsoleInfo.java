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
package com.instancesobp.instancesReader.legacy;

import com.instancesobp.models.Warehouse;
import com.instancesobp.utils.Codigo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.lang.Integer.parseInt;

/**
 * This class is responsible for reading and managing console input to configure
 * warehouse instances and related parameters. It provides methods to select
 * various settings such as warehouse number, batches, and instance configurations.
 * The class also handles user input validation and ensures that the selected
 * options are valid for the current context.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class ReadConsoleInfo {

    /**
     * Number of warehouses to configure.
     */
    private int warehouseNumber;

    /**
     * Indicates if the test is online (0 for no, 1 for yes).
     */
    private int onlineTest;

    /**
     * Number of initial orders for the test.
     */
    private int initialOrders;

    /**
     * Number of orders to configure.
     */
    private int ordersNumber;

    /**
     * Identifier for the selected instance.
     */
    private String instanceNumber;

    /**
     * Example number to select (1 for Albareda, 2 for Henn, 3 for Arbex).
     */
    private int exampleNumber;

    /**
     * Setting number for the configuration.
     */
    private int settingNumber;

    /**
     * Location of items in the warehouse to configure.
     */
    private int itemLocationNumber;

    /**
     * String representation of the item order.
     */
    private String itemLocationString;

    /**
     * Indicates if the server or worker is selected (0 for server, 1 for worker).
     */
    private int serverWorker;

    private int warehouseType;

    private String warehouseTypeString;

    private int orderType;

    private String orderTypeString;

    private String instanceName;

    /**
     * Default constructor for the `ReadConsoleInfo` class.
     */
    public ReadConsoleInfo() {
        // Initialize instance variables
    }

    /**
     * Prompts the user to select a worker from a list of UUIDs.
     *
     * @param uuidList List of UUIDs representing available roles.
     * @return The selected role index.
     */
    public static int selectUserWorker(List<UUID> uuidList) {
        System.out.print("\nChoose roll (-1[Del&New User]-0[New User]");
        for (int i = 0; i < uuidList.size(); i++) {
            System.out.print("-" + (i + 1) + "[" + uuidList.get(i) + "]");
        }
        System.out.print(") :\n");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            int W = parseInt(br.readLine());
            if (W < -1 || W > uuidList.size()) {
                System.out.println("Error: Enter a valid number between 0-" + uuidList.size());
                return selectUserWorker(uuidList);
            } else {
                return W;
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: Enter a valid number. " + e.getMessage());
            return selectUserWorker(uuidList);
        }
        return 0;
    }

    /**
     * Main method to run the console input process. It guides the user through
     * selecting example types, warehouses, batches, and other configurations.
     */
    public void run() {
        this.selectExampleNumber();
        if (exampleNumber == 1) {
            this.selectWarehousesNumber();
            this.selectOrdersNumber();
            this.selectInstanceNumber();
        } else if (exampleNumber == 2) {
            this.selectItemsOrderNumber();

            switch (this.itemLocationNumber) {
                case 1:
                    this.itemLocationString = "abc1";
                    break;
                case 2:
                    this.itemLocationString = "abc2";
                    break;
                case 3:
                    this.itemLocationString = "ran1";
                    break;
                case 4:
                    this.itemLocationString = "ran2";
                    break;
                default:
                    break;
            }

            this.selectSettingNumber();
            this.getNumberOrdersHenn();
        } else if (exampleNumber == 3){
            this.selectWarehouseType();

            warehouseTypeToName();

            this.selectTypeOrder();
            switch (this.orderType){
                case 1:
                    this.orderTypeString="largeInstances";
                    break;
                case 2:
                    this.orderTypeString="smallInstances";
                    break;
            }


        }

        this.selectOnlineTest();
        if (this.onlineTest == 1) {
            this.selectNumInitOrders();
        }
    }

    public void warehouseTypeToName() {
        switch (this.warehouseType){
            case 1:
                this.warehouseTypeString="warehouse_8_0_3_1560";
                break;
            case 2:
                this.warehouseTypeString="warehouse_8_1_3_1560";
                break;
            case 3:
                this.warehouseTypeString="warehouse_16_0_3_1560";
                break;
            case 4:
                this.warehouseTypeString="warehouse_16_1_3_1560";
                break;
        }
    }

    /**
     * Prompts the user to select whether the test is online or not.
     */
    public final void selectOnlineTest() {
        System.out.println("Choose test Online (0[No]-1[Yes]): ");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            int input = parseInt(br.readLine());
            if (input < 0 || input > 1) {
                System.out.println("Error: Enter a valid number between 0-1.");
                selectOnlineTest();
            } else {
                onlineTest = input;
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error: Enter a valid number. " + e.getMessage());
            selectOnlineTest();
        }
    }

    /**
     * Prompts the user to select the role (server or worker).
     */
    public final void selectServerWorker() {
        System.out.println("Choose role (0[Server]-1[Worker]): ");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            int input = parseInt(br.readLine());
            if (input < 0 || input > 1) {
                System.out.println("Error: Enter a valid number between 0-1.");
                selectServerWorker();
            } else {
                serverWorker = input;
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error: Enter a valid number. " + e.getMessage());
            selectServerWorker();
        }
    }

    /**
     * Prompts the user to select the number of initial orders.
     */
    public final void selectNumInitOrders() {
        int max = this.ordersNumber / 2;
        System.out.println("Choose number of initial orders (0-" + max + "): ");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            int input = parseInt(br.readLine());
            if (input < 0 || input > max) {
                System.out.println("Error: Enter a valid number between 0-" + max + ".");
                selectNumInitOrders();
            } else {
                initialOrders = input;
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error: Enter a valid number. " + e.getMessage());
            selectNumInitOrders();
        }
    }

    /**
     * Retrieves the number of orders for the Henn example based on the selected
     * item order and setting.
     */
    public void getNumberOrdersHenn() {
        File folder = new File("./Warehouses_instances/legacy/W5A_Henn/" + itemLocationString);
        if (!folder.isDirectory()) {
            System.out.println("Error: The entered path is not a valid directory " + folder.getAbsolutePath());
        } else if (folder.listFiles() == null) {
            System.out.println("Error: The directory is empty " + folder.getAbsolutePath());
        } else {
            for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
                if (!fileEntry.isDirectory() && (fileEntry.getName().contains(settingNumber + "s") || fileEntry.getName().contains(settingNumber + "l"))) {
                    String filePath = "./Warehouses_instances/legacy/W5A_Henn/" + itemLocationString + "/" + fileEntry.getName();
                    String[] fileParts = filePath.split("-");
                    this.ordersNumber = parseInt(fileParts[1]);
                }
            }
        }
    }

    /**
     * Prompts the user to select a setting number from the available options in the directory.
     * The method validates the input and ensures the selected setting file exists.
     */
    public final void selectSettingNumber() {
        System.out.println("Choose a setting number: ");
        File folder = new File("./Warehouses_instances/legacy/W5A_Henn/" + itemLocationString);
        if (!folder.isDirectory()) {
            System.out.println("Error: The entered path is not a valid directory " + folder.getAbsolutePath());
            return;
        } else if (folder.listFiles() == null) {
            System.out.println("Error: The directory is empty " + folder.getAbsolutePath());
            return;
        } else {
            for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
                if (!fileEntry.isDirectory() && fileEntry.getName().contains("sett")) {
                    System.out.print(fileEntry.getName().replace("sett", "").replace(".txt", "") + " - ");
                }
            }
        }
        System.out.print(">\n");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            int W = parseInt(br.readLine());
            File settingFile = new File("./Warehouses_instances/legacy/W5A_Henn/" + itemLocationString + "/sett" + W + ".txt");
            if (!settingFile.exists()) {
                System.out.println("Error: Enter a valid number.");
                selectSettingNumber();
            } else {
                settingNumber = W;
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: Enter a valid number. " + e.getMessage());
            selectSettingNumber();
        }
    }

    /**
     * Prompts the user to select an order of items number.
     * The method validates the input to ensure it is within the valid range (1-4).
     */
    public final void selectItemsOrderNumber() {
        System.out.println("Choose an order of items number (1[abc1]-2[abc2]-3[ran1]-4[ran2]): ");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            int W = parseInt(br.readLine());
            if (W < 1 || W > 4) {
                System.out.println("Error: Enter a valid number between 1-4.");
                selectItemsOrderNumber();
            } else {
                itemLocationNumber = W;
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: Enter a valid number. " + e.getMessage());
            selectItemsOrderNumber();
        }
    }


    public final void selectWarehouseType(){
        System.out.println("Choose a type of warehouse (1[warehouse_8_0]-2[warehouse_8_1]-3[warehouse_16_0]-4[warehouse_16_1]): ");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            int W = parseInt(br.readLine());
            if (W < 1 || W > 4) {
                System.out.println("Error: Enter a valid number between 1-4.");
                selectWarehouseType();
            } else {
                warehouseType = W;
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: Enter a valid number. " + e.getMessage());
            selectWarehouseType();
        }
    }

    public final void selectTypeOrder(){
        System.out.println("Choose a type of warehouse (1[largeInstances]-2[smallInstances]): ");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            int W = parseInt(br.readLine());
            if (W < 1 || W > 2) {
                System.out.println("Error: Enter a valid number between 1-2.");
                selectTypeOrder();
            } else {
                orderType = W;
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: Enter a valid number. " + e.getMessage());
            selectTypeOrder();
        }
    }

    /**
     * Prompts the user to select an example number.
     * The method validates the input to ensure it is within the valid range (1-2).
     */
    public final void selectExampleNumber() {
        System.out.println("Choose an example number (1[Albareda]-2[Henn]-3[Arbex]): ");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            int W = parseInt(br.readLine());
            if (W < 1 || W > 3) {
                System.out.println("Error: Enter a valid number between 1-3.");
                selectExampleNumber();
            } else {
                exampleNumber = W;
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: Enter a valid number. " + e.getMessage());
            selectExampleNumber();
        }
    }

    /**
     * Prompts the user to select the number of warehouses.
     */
    public final void selectWarehousesNumber() {
        System.out.println("Choose a WareHouse number (1-4): ");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            int W = parseInt(br.readLine());
            if (W < 1 || W > 4) {
                System.out.println("Error: Enter a valid number between 1-4.");
                selectWarehousesNumber();
            } else {
                warehouseNumber = W;
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: Enter a valid number. " + e.getMessage());
            selectWarehousesNumber();
        }
    }

    /**
     * Prompts the user to select the number of batches.
     */
    public final void selectOrdersNumber() {
        System.out.println("Choose a number of orders (50,100,150,200,250): ");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            int W = parseInt(br.readLine());
            if (W != 50 && W != 100 && W != 150 && W != 200 && W != 250) {
                System.out.println("Error: Enter a valid number between (50,100,150,200,250).");
                selectOrdersNumber();
            } else {
                ordersNumber = W;
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: Enter a valid number. " + e.getMessage());
            selectOrdersNumber();
        }
    }

    /**
     * Prompts the user to select the instance number.
     */
    public final void selectInstanceNumber() {
        System.out.println("Choose a number of instance (000,030,060,090): ");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String W = br.readLine();
            if (!W.equals("000") && !W.equals("030") && !W.equals("060") && !W.equals("090")) {
                System.out.println("Error: Enter a valid number between (000,030,060,090).");
                selectInstanceNumber();
            } else {
                instanceNumber = W;
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Gets the instance number in Albareda instances.
     * [000, 030, 060, 090]
     *
     * @return The instance number as a string.
     */
    public String getInstanceNumber() {
        return instanceNumber;
    }

    /**
     * Sets the instance number in Albareda instances.
     * [000, 030, 060, 090]
     *
     * @param instanceNumber The instance number to set.
     */
    public void setInstanceNumber(String instanceNumber) {
        this.instanceNumber = instanceNumber;
    }

    /**
     * Gets the example number.
     * [1 for Albareda, 2 for Henn, 3 for Arbex]
     *
     * @return The example number as an integer.
     */
    public int getExampleNumber() {
        return exampleNumber;
    }

    /**
     * Sets the example number.
     * [1 for Albareda, 2 for Henn, 3 for Arbex]
     *
     * @param exampleNumber The example number to set.
     */
    public void setExampleNumber(int exampleNumber) {
        this.exampleNumber = exampleNumber;
    }

    /**
     * Gets the warehouse number in Albareda instances.
     * [W1, W2, W3, W4]
     *
     * @return The warehouse number as an integer.
     */
    public int getWarehouseNumber() {
        return warehouseNumber;
    }

    /**
     * Sets the warehouse number in Albareda instances.
     * [W1, W2, W3, W4]
     *
     * @param warehouseNumber The warehouse number to set.
     */
    public void setWarehouseNumber(int warehouseNumber) {
        this.warehouseNumber = warehouseNumber;
    }

    /**
     * Gets the online or offline experiment status.
     *
     * @return The online test status as an integer (0 for no, 1 for yes).
     */
    public int getOnlineTest() {
        return onlineTest;
    }

    /**
     * Gets the number of initial orders in the online experiments.
     *
     * @return The number of initial orders as an integer.
     */
    public int getNumInitOrders() {
        return initialOrders;
    }

    /**
     * Sets the number of initial orders in the online experiments.
     *
     * @param numInitOrders The number of initial orders to set.
     */
    public void setNumInitOrders(int numInitOrders) {
        this.initialOrders = numInitOrders;
    }

    /**
     * Gets the number of orders.
     *
     * @return The number of orders as an integer.
     */
    public int getOrdersNumber() {
        return ordersNumber;
    }

    /**
     * Sets the number of orders.
     *
     * @param ordersNumber The number of orders to set.
     */
    public void setOrdersNumber(int ordersNumber) {
        this.ordersNumber = ordersNumber;

    }

    /**
     * Gets the setting number in the Henn instances.
     *
     * @return The setting number as an integer.
     */
    public int getSettingNumber() {
        return settingNumber;
    }

    /**
     * Sets the setting number in the Henn instances.
     *
     * @param settingNumber The setting number to set.
     */
    public void setSettingNumber(int settingNumber) {
        this.settingNumber = settingNumber;
    }

    /**
     * Gets the item location in the warehouse as a number in the Henn instances.
     * 1[abc1]-2[abc2]-3[ran1]-4[ran2]
     *
     * @return The items location in the warehouse as a number.
     */
    public int getItemsLocation() {
        return itemLocationNumber;
    }

    /**
     * Sets the item location in the warehouse as a number in the Henn instances.
     * 1[abc1]-2[abc2]-3[ran1]-4[ran2]
     *
     * @param itemsLocation The item location in the warehouse as a number to set.
     */
    public void setItemsLocation(int itemsLocation) {
        this.itemLocationNumber = itemsLocation;
    }

    /**
     * Gets the item location in the warehouse as a string in the Henn instances.
     * 1[abc1]-2[abc2]-3[ran1]-4[ran2]
     *
     * @return The item order string.
     */
    public String getItemsLocationString() {
        return itemLocationString;
    }

    /**
     * Sets the item location in the warehouse as string in the Henn instances.
     * 1[abc1]-2[abc2]-3[ran1]-4[ran2]
     *
     * @param itemLocationString The item order string to set.
     */
    public void setItemsLocationString(String itemLocationString) {
        this.itemLocationString = itemLocationString;
    }

    /**
     * Gets the server or worker role.
     *
     * @return The role as an integer (0 for server, 1 for worker).
     */
    public int getServerWorker() {
        return serverWorker;
    }

    /**
     * Sets the server or worker role.
     *
     * @param serverWorker The role to set (0 for server, 1 for worker).
     */
    public void setServerWorker(int serverWorker) {
        this.serverWorker = serverWorker;
    }

    /**
     *
     * @return
     */
    public String getWarehouseTypeString() {
        return warehouseTypeString;
    }

    public void setWarehouseTypeString(String warehouseTypeString) {
        this.warehouseTypeString = warehouseTypeString;
    }

    public void setOrderTypeString(String orderTypeString) {
        this.orderTypeString = orderTypeString;
    }

    public int getWarehouseType() {
        return warehouseType;
    }

    public void setWarehouseType(int warehouseType) {
        this.warehouseType = warehouseType;
    }

    /**
     *
     * @return
     */
    public String getOrderTypeString() {
        return orderTypeString;
    }

    public void setInstanceFileName(String instanceFilename) {
        this.instanceName = instanceFilename;
    }
    public String getInstanceFileName() {
        return this.instanceName;
    }

    /**
     * Returns a string representation of the instance, including all its attributes.
     *
     * @return A string representation of the instance.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.exampleNumber == 1) {
            sb.append("Albareda Instance\r\n")
                    .append("WareHouse number: ").append(this.warehouseNumber).append("\r\n")
                    .append("Batches number: ").append(this.ordersNumber).append("\r\n")
                    .append("Instance number: ").append(this.instanceNumber).append("\r\n");
        } else {
            sb.append("Henn Instance\r\n")
                    .append("Items order: ").append(this.itemLocationString).append("\r\n")
                    .append("Batches number: ").append(this.ordersNumber).append("\r\n")
                    .append("Setting number: ").append(this.settingNumber).append("\r\n");
        }
        return sb.toString();
    }

    /**
     * Returns a short string representation of the instance, including only relevant identifiers.
     *
     * @return A short string representation of the instance.
     */
    public String toStringShort() {
        StringBuilder sb = new StringBuilder();
        if (this.exampleNumber == 1) {
            sb.append("A_W").append(this.warehouseNumber).append("_")
                    .append(this.ordersNumber).append("_").append(this.instanceNumber);
        }else if(this.exampleNumber==3){
            sb.append("ARB_W6A_").append(this.warehouseType).append("_")
                    .append(this.orderTypeString);
        } else {
            sb.append("H_W5A_").append(this.itemLocationString).append("_")
                    .append(this.ordersNumber).append("_").append(this.settingNumber);
        }
        return sb.toString();
    }

    public String toStringInstanceName(Warehouse wh){
        StringBuilder sb=new StringBuilder();
        if (this.exampleNumber == 1) {
            sb.append("A_W").append(this.warehouseNumber).append("_").append(Codigo.getTypeLocation(wh.getOrderLocation())).append("_")
                    .append(this.ordersNumber).append("_").append((int)wh.getWorkerCapacity());
        }else if(this.exampleNumber==3){
            sb.append("ARB_W6A_").append(Codigo.getTypeLocation(wh.getOrderLocation())).append("_")
                    .append(wh.getNumberOfOrders()).append("_").append((int)wh.getWorkerCapacity());
        } else {
            sb.append("H_W5A_").append(this.itemLocationString).append("_")
                    .append(wh.getNumberOfOrders()).append("_").append((int)wh.getWorkerCapacity());
        }

        return sb.toString();
    }

    /**
     * Prints the string representation of the instance to the console.
     */
    public void toPrint() {
        System.out.println(this);
    }

    /**
     * Prints the short string representation of the instance to the console.
     */
    public void toPrintShort() {
        System.out.println(this.toStringShort());
    }
}
