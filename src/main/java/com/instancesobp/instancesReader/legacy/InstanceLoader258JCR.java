package com.instancesobp.instancesReader.legacy;

import com.instancesobp.models.*;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static com.instancesobp.utils.Constants.ABC_ITEMS_LOCATION;
import static com.instancesobp.utils.Constants.DEPOT_CENTER;


public class InstanceLoader258JCR {
    /**
     * Configuration object containing instance details.
     */
    private final ReadConsoleInfo consoleInfo;

    private String fichero_pedido;
    private String fichero_picker;
    /**
     * Warehouse object constructed from the loaded data.
     */
    private Warehouse warehouse;
    /**
     * Name of the instance being loaded.
     */
    private String instanceName;

    private int pesoTotalTotal;

    double workerCapacity=0;

    /**
     * Constructor for the InstancesLoaderArbex class.
     * Initializes the loader with the provided configuration.
     *
     * @param consoleInfo The configuration object containing instance details.
     */
    public InstanceLoader258JCR(ReadConsoleInfo consoleInfo) {
        this.warehouse = new Warehouse();
        this.consoleInfo = consoleInfo;
    }

    public void run(){
        List<Aisles>pasillos=new ArrayList<>();
        double distancia_origen_derecho = 0;
        double distancia_origen_izquierdo = 0;
        int numero_pasillos=7;

        for (int i = 0; i < numero_pasillos; i++) {

            int lado=-1;
            if (i==3) {
                lado=0;
            }else if(i>3) {
                lado=1;
            }

            if(i==0) {
                distancia_origen_izquierdo = 1.5f;
                distancia_origen_derecho=15.0f;
            }else if(i>0&&i<3){
                distancia_origen_izquierdo +=5.0f;
                distancia_origen_derecho-=5.0f;
            }else if(i==3) {
                distancia_origen_izquierdo=0;
                distancia_origen_derecho=0;
            }else if(i>3) {
                distancia_origen_izquierdo += 5.0f;
                if(i==4) {
                    distancia_origen_derecho=11.5f;
                }else if(i<7&&i>4) {
                    distancia_origen_derecho-=5.0f;
                }
            }



            Aisles a=new Aisles(i,distancia_origen_derecho,distancia_origen_izquierdo,lado);
            pasillos.add(a);

        }

//		for (Aisles aisles : pasillos) {
//			aisles.toPrint();
//		}


        List<Picker> pickers = new ArrayList<>();

        StringBuilder sbl = new StringBuilder();
        sbl.append("./Warehouses_instances/legacy/W7_258JCR/picking line.xlsx");

        fichero_picker = sbl.toString();
        File f = new File(fichero_picker);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("0.00000",symbols);

        try (FileInputStream fis = new FileInputStream(f); Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet hoja = workbook.getSheetAt(1); // Segunda hoja (índice 1)

            Row rowID = hoja.getRow(0);
            Row rowInitial = hoja.getRow(1);
            Row rowFatigue = hoja.getRow(2);
            Row rowStabilization = hoja.getRow(3);
            Row rowFinal = hoja.getRow(4);

            int numeroPickers = rowID.getLastCellNum();

            for (int col = 1; col < numeroPickers; col++) {
                int id = (int) rowID.getCell(col).getNumericCellValue();
                int initialPickTime = (int) rowInitial.getCell(col).getNumericCellValue();

                String fatiguingRate = df.format(rowFatigue.getCell(col).getNumericCellValue());
                Double fatiga=Double.parseDouble(fatiguingRate);



                int stabilization = (int) rowStabilization.getCell(col).getNumericCellValue();
                int finalPickTime = (int) rowFinal.getCell(col).getNumericCellValue();

                Picker picker = new Picker(id, initialPickTime, fatiga, stabilization, finalPickTime);
                pickers.add(picker);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Imprimir los pickers cargados
//		for (Picker p : pickers) {
//			System.out.println(p);
//		}


        StringBuilder sbp=new StringBuilder();
        sbp.append("./Warehouses_instances/legacy/W7_258JCR/"+consoleInfo.getInstanceFileName());
        fichero_pedido=sbp.toString();
        int numero_huecos=60;
        File f2=new File(fichero_pedido);


        try (FileInputStream fisp=new FileInputStream(f2);Workbook wk2=new XSSFWorkbook(fisp)){
            List<Order> pedidos = new ArrayList<>();
            int[] limitesPasillos = {5, 10, 10, 10, 10, 10, 5};

            Sheet hoja = wk2.getSheetAt(0);
            for (Row fila : hoja) {
                if (fila.getRowNum()>0) {
                    List<Product> productos = new ArrayList<>();
                    int orderId=0;

                    for (Cell celda : fila) {
                        if (celda.getColumnIndex()==0) {
                            orderId=(int)celda.getNumericCellValue();
//							System.out.println(orderId);
                        }else {
                            int pesoProd=(int)celda.getNumericCellValue();
                            if (pesoProd>0) {
                                int id=(int)celda.getColumnIndex();
                                double peso=celda.getNumericCellValue();
                                int lado=0;
                                int posicion=id-1;
                                int indexInColumn = posicion % 5;

                                if (posicion%10<5) {
                                    lado=1;
                                }
                                int pasillo=0;

                                for (int i = 0; i < limitesPasillos.length; i++) {
                                    if (posicion <= limitesPasillos[i]) {
                                        pasillo= i;
                                        break;// El pasillo es el índice donde se cumple la condición
                                    }
                                }
                                Product p=new Product(id,pasillo,lado,indexInColumn+0.5f,peso);
                                productos.add(p);
                            }

                        }

                    }
                    int numReferencia=productos.size();
                    double pesoTotal = 0;
                    for (Product prod : productos) {
                        pesoTotal += prod.getWeight();
                    }
                    pesoTotalTotal += pesoTotal;
                    Order o=new Order(orderId,productos,numReferencia,pesoTotal,0,0,0.0,0.0);
                    pedidos.add(o);
                }
            }
            int numero_items=0;

            for (Order orden : pedidos) {
                numero_items+=orden.getNumReferences();
            }

            int colocacion_mesa=DEPOT_CENTER;
            int localizacion_pedidos=ABC_ITEMS_LOCATION;

            double largo_estanterias=5.0;
            double ancho_estanterias=2.0;

            double ancho_pasillos=3.0;
            int pasillos_transversales=2;
            double tiempo_picking=0.0;
            double tiempo_giro_fuera=0.0;
            double tiempo_giro_dentro=0.0;

            int numero_pedidos=pedidos.size();

           workerCapacity = Math.floor(((double) pesoTotalTotal / numero_pedidos) * Math.min(8, Math.max(3, numero_pedidos * 0.1)));

           warehouse=new Warehouse(numero_pedidos,numero_pasillos,numero_items,colocacion_mesa,localizacion_pedidos,
                    largo_estanterias,ancho_estanterias,ancho_pasillos,pasillos_transversales,workerCapacity,tiempo_picking,tiempo_giro_fuera,
                    tiempo_giro_dentro,numero_huecos,pasillos,pedidos,pickers);

           instanceName= consoleInfo.toStringInstanceName(warehouse);

            //System.out.println(warehouse.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Warehouse getWarehouse() {return this.warehouse;}

    public String getInstanceName() {return this.instanceName;}

    //public static void main(String[] args) {
      //  InstanceLoader258JCR i=new InstanceLoader258JCR(new ReadConsoleInfo());
       // i.run();
   // }
}
