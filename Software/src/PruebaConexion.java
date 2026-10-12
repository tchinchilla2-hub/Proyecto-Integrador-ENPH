/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author thain
 */


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PruebaConexion {

    public static void main(String[] args) {

        Connection conexion = ConexionOracle.obtenerConexion();

        if (conexion != null) {

            try {
                Statement sentencia = conexion.createStatement();

                // Consultar los hogares registrados
                ResultSet resultado = sentencia.executeQuery(
                        "SELECT h.id, j.nombre, j.apellido, " +
                        "j.num_identificacion " +
                        "FROM hogar h " +
                        "JOIN jefe_hogar j ON h.id = j.id_hogar " +
                        "ORDER BY h.id"
                );

                System.out.println("HOGARES REGISTRADOS EN ORACLE:");

                while (resultado.next()) {

                    System.out.println(
                            "Hogar: HG-" +
                            String.format("%03d", resultado.getInt("id")) +
                            " | Jefe: " +
                            resultado.getString("nombre") + " " +
                            resultado.getString("apellido") +
                            " | Identificacion: " +
                            resultado.getString("num_identificacion")
                    );
                }

                resultado.close();
                sentencia.close();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ConexionOracle.cerrarConexion(conexion);
            }
        }
    }
}
