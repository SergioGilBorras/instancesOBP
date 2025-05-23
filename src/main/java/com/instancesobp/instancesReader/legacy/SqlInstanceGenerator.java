package com.instancesobp.instancesReader.legacy;

import java.io.File;
import java.io.FileWriter;
import java.sql.*;

public class SqlInstanceGenerator {
    public void crearTXT(String month, int year) {
        String jdbcURL = "jdbc:mysql://localhost:3306/Instancias";
        String usuario = "root";
        String contraseña = "password";

        // Construcción de la consulta SQL
        StringBuilder sbl = new StringBuilder();
        sbl.append("SELECT s.product_id, s.time_id, s.customer_id, s.unit_sales ").append("FROM sales_fact_").append(year)
                .append(" s JOIN time_by_day t ON s.time_id = t.time_id ").append("WHERE t.the_year = ").append(year)
                .append(" AND t.the_month = '").append(month).append("'");

        String consulta = sbl.toString();

        // Ruta del archivo .txt
        String archivoTXT = "./Warehouses_instances/legacy/W6B_SQL/" + month + "/sales_fact_" + year
                + "_" + month + ".txt";

        // Crear el objeto File para la ruta del archivo
        File archivo = new File(archivoTXT);

        // Obtener la carpeta del mes
        File carpetaMes = new File(archivo.getParent());

        // Crear la carpeta si no existe
        if (!carpetaMes.exists()) {
            boolean creado = carpetaMes.mkdirs(); // Crea las carpetas necesarias
            if (creado) {
                System.out.println("Carpeta creada: " + month);
            } else {
                System.out.println("Error al crear la carpeta.");
            }
        }

        try (Connection conexion = DriverManager.getConnection(jdbcURL, usuario, contraseña);
             Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(consulta);
             FileWriter txtWriter = new FileWriter(archivoTXT)) {

            // Escribir encabezados
            ResultSetMetaData meta = rs.getMetaData();
            int columnas = meta.getColumnCount();
            for (int i = 1; i <= columnas; i++) {
                txtWriter.append(meta.getColumnName(i));
                if (i < columnas)
                    txtWriter.append("\t");
            }
            txtWriter.append("\n");

            // Escribir filas
            while (rs.next()) {
                for (int i = 1; i <= columnas; i++) {
                    txtWriter.append(rs.getString(i));
                    if (i < columnas)
                        txtWriter.append("\t");
                }
                txtWriter.append("\n");
            }

            System.out.println("Archivo TXT creado correctamente del mes: " + month + " de " + year);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SqlInstanceGenerator sql = new SqlInstanceGenerator();

        String meses[] = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
                "October", "November", "December" };

        for (int i = 0; i < meses.length; i++) {

            sql.crearTXT(meses[i], 1997);

        }
    }
}
