package com.instancesobp.instancesReader.json;

import com.instancesobp.instancesReader.legacy.InstancesLoaderFromConsoleInfo;
import com.instancesobp.instancesReader.legacy.ReadConsoleInfo;

import java.io.File;
import java.util.ArrayList;

public class GeneralJsonLoader {

    /**
     * Default constructor for the GeneralJsonLoader class.
     * This constructor initializes the Json loader.
     */
    public GeneralJsonLoader() {
        // Default constructor
    }

    /**
     * Retrieves all Json for the Arbex example.
     *
     * @return A list of Arbex instances.
     */
    public ArrayList<String> getAllJsonArbex() {
        ArrayList<String> fileNames = new ArrayList<>();

        String directoryPath="Warehouses_instances/json/W6A_Arbex/W6/ran";

        File folder = new File(directoryPath);

        // Verifica si la ruta es una carpeta
        if (folder.isDirectory()) {
            // Lista todos los archivos en la carpeta
            File[] files = folder.listFiles();

            if (files != null) {
                // Itera sobre los archivos y agrega sus nombres al ArrayList
                for (File file : files) {
                    if (file.isFile()) {
                        fileNames.add(file.getName());
                    }
                }
            }
        } else {
            System.out.println("La ruta proporcionada no es una carpeta.");
        }

        return fileNames;
    }


    /**
     * Retrieves all Json for the Albareda example.
     *
     * @return A list of Json file names.
     */
    public ArrayList<String> getAllJsonAlbareda() {
        ArrayList<String> fileNames = new ArrayList<>();

        String baseDirectoryPath = "Warehouses_instances/json/W1-W2-W3-W4_Albareda";

        File baseFolder = new File(baseDirectoryPath);

        // Verifica si la ruta base es una carpeta
        if (baseFolder.isDirectory()) {
            // Lista todas las carpetas W1, W2, W3, W4
            File[] mainFolders = baseFolder.listFiles(File::isDirectory);

            if (mainFolders != null) {
                // Itera sobre cada carpeta W1, W2, W3, W4
                for (File mainFolder : mainFolders) {
                    // Lista las subcarpetas dentro de cada carpeta W
                    File[] subFolders = mainFolder.listFiles(File::isDirectory);

                    if (subFolders != null) {
                        for (File subFolder : subFolders) {
                            // Lista todos los archivos en la subcarpeta
                            File[] files = subFolder.listFiles();

                            if (files != null) {
                                for (File file : files) {
                                    if (file.isFile() && file.getName().endsWith(".json")) {
                                        fileNames.add(file.getName());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            System.out.println("La ruta proporcionada no es una carpeta.");
        }

        return fileNames;
    }



    /**
     * Retrieves all Json for the Henn example.
     *
     * @return A list of Json file names.
     */
    public ArrayList<String> getAllJsonHenn() {
        ArrayList<String> fileNames = new ArrayList<>();

        String baseDirectoryPath = "Warehouses_instances/json/W5A_Henn/W5";

        File baseFolder = new File(baseDirectoryPath);

        // Verifica si la ruta base es una carpeta
        if (baseFolder.isDirectory()) {
            // Lista todas las subcarpetas en la carpeta W5
            File[] subFolders = baseFolder.listFiles(File::isDirectory);

            if (subFolders != null) {
                // Itera sobre cada subcarpeta
                for (File subFolder : subFolders) {
                    // Lista todos los archivos en la subcarpeta
                    File[] files = subFolder.listFiles();

                    if (files != null) {
                        for (File file : files) {
                            // Verifica que sea un archivo y tenga extensión .json
                            if (file.isFile() && file.getName().endsWith(".json")) {
                                fileNames.add(file.getName());
                            }
                        }
                    }
                }
            }
        } else {
            System.out.println("La ruta proporcionada no es una carpeta.");
        }

        return fileNames;
    }

}
