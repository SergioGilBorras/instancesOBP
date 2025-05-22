package com.instancesobp.instancesReader.legacy;

import com.instancesobp.models.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstancesLoaderOBSPPS {
    /**
     * Configuration object containing instance details.
     */
    private final ReadConsoleInfo consoleInfo;

    private String fichero_pedido;
    /**
     * Warehouse object constructed from the loaded data.
     */
    private Warehouse warehouse;
    /**
     * Name of the instance being loaded.
     */
    private String instanceName;


    public InstancesLoaderOBSPPS(ReadConsoleInfo consoleInfo) {
        this.warehouse = new Warehouse();
        this.consoleInfo = consoleInfo;
    }

    public void run() {
        double pesoTotalTotal=0;
        StringBuilder sbl = new StringBuilder();
        String ruta = "./Warehouses_instances/legacy/W8_OBSPPS/"+consoleInfo.getProblemClassString()
                +"/"+consoleInfo.getNumberClassString();
        sbl.append(ruta);
        sbl.append(consoleInfo.getInstanceFileName());
        fichero_pedido = sbl.toString();

        String linea;
        List<Order> listpedidos = new ArrayList<>();
        List<Aisles> pasillos = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            double distancia_origen_derecho = 46.5d;
            double distancia_origen_izquierdo = 1.5d;

            int lado = 0;

            if (i > 0) {
                lado = 1;
                distancia_origen_izquierdo += i * 5.0d;
                distancia_origen_derecho -= i * 5.0d;
            }

            Aisles a = new Aisles(i, distancia_origen_derecho, distancia_origen_izquierdo, lado);
            pasillos.add(a);
        }
        int numero_items = 0;

        try {
            FileReader fr = new FileReader(fichero_pedido);
            try (BufferedReader br = new BufferedReader(fr)) {

                while ((linea = br.readLine()) != null) {
                    if (linea.trim().isEmpty()) continue; // Ignora líneas vacías

                    // 1️⃣ Eliminar los corchetes extra al inicio y final
                    linea = linea.replaceFirst("^\\[\\[\\[", "").replaceFirst("\\]\\]\\]$", "");

                    // 2️⃣ Separar cada orden eliminando los corchetes extra
                    String[] pedidos = linea.split("\\]\\], \\[\\[");

                    int idOrder = 0;
                    for (String pedido : pedidos) {
                        Map<String, Product> mapaProductos = new HashMap<>();

                        // 3️⃣ Limpiar corchetes restantes y dividir en parejas
                        pedido = pedido.replace("[[", "").replace("]]", "").replace("[", "").replace("]", "");
                        String[] pares = pedido.split(", ");

                        if (pares.length % 2 != 0) {
                            System.err.println("Error: número impar de elementos en la línea: " + pedido);
                            continue; // Salta este pedido porque está mal formado
                        }

                        boolean datosValidos = true;

                        for (int i = 0; i < pares.length; i += 2) {
                            if (pares[i].trim().isEmpty() || pares[i + 1].trim().isEmpty()) {
                                System.err.println("Error: valor vacío en línea: " + pedido);
                                datosValidos = false;
                                break;
                            }
                        }

                        if (!datosValidos) continue; // Evita procesar pares con datos vacíos

                        for (int i = 0; i < pares.length; i += 2) {
                            int pasillo = Integer.parseInt(pares[i].trim());
                            int posicion = Integer.parseInt(pares[i + 1].trim());
                            double altura;

                            if (posicion > 45) {
                                altura = posicion - 45.5d;
                            } else {
                                altura = posicion - 0.5d;
                            }

                            int lado = (posicion >= 1 && posicion <= 45) ? 0 : 1;
                            String clave = pasillo + "-" + posicion;

                            if (mapaProductos.containsKey(clave)) {
                                Product p = mapaProductos.get(clave);
                                p.setWeight(p.getWeight() + 1.0);
                            } else {
                                int idProducto = (pasillo - 1) * 90 + posicion;
                                mapaProductos.put(clave, new Product(idProducto, pasillo - 1, lado, altura, 1.0));
                            }
                        }

                        idOrder++;

                        List<Product> orden = new ArrayList<>(mapaProductos.values());
                        int num_referencia = orden.size();
                        double pesoTotal = orden.stream().mapToDouble(Product::getWeight).sum();
                        pesoTotalTotal +=pesoTotal;
                        Order o = new Order(idOrder, orden, num_referencia, pesoTotal, 0L, 0L, 0.0, 0.0);
                        listpedidos.add(o);
                        numero_items += (int) pesoTotal;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int numero_pedidos = listpedidos.size();
        int numero_pasillos = 10;
        int colocacion_mesa = 0;
        int localizacion_pedidos = 0;
        double largo_estanterias = 45.0;
        double ancho_estanterias = 1.0;
        double ancho_pasillos = 3.0;
        int pasillos_transversales = 2;
        double capacidad_trabajador = 0.48;
        double time_picking = 0.05;
        double tiempo_giro_fuera = 0.0;
        double tiempo_giro_dentro = 0.0;
        int numero_huecos = 900;

        capacidad_trabajador = Math.floor((pesoTotalTotal / numero_pedidos) * Math.min(8, Math.max(3, numero_pedidos * 0.1)));

        warehouse = new Warehouse(numero_pedidos, numero_pasillos, numero_items, colocacion_mesa,
                localizacion_pedidos, largo_estanterias, ancho_estanterias, ancho_pasillos, pasillos_transversales,
                capacidad_trabajador, time_picking, tiempo_giro_fuera, tiempo_giro_dentro, numero_huecos, pasillos,
                listpedidos);

        instanceName= consoleInfo.toStringInstanceName(warehouse);

        //System.out.println(warehouse.toString());
    }

    public Warehouse getWarehouse() {return this.warehouse;}

    public String getInstanceName() {return this.instanceName;}

    //public static void main(String[] args) {
      //InstancesLoaderOBSPPS i=new InstancesLoaderOBSPPS(new ReadConsoleInfo());
      //i.run();
      //GeneralInstancesLoader g=new GeneralInstancesLoader();
      //g.getAllInstancesOBSPPS();
    //}
}
