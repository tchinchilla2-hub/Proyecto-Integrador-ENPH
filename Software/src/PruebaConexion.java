/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author thain
 */

import java.sql.Connection;

public class PruebaConexion {

    public static void main(String[] args) {

        Connection conexion = ConexionOracle.obtenerConexion();

        if (conexion != null) {

            System.out.println("LA BASE DE DATOS ESTÁ CONECTADA");

            ConexionOracle.cerrarConexion(conexion);

        } else {

            System.out.println("NO SE PUDO CONECTAR A ORACLE");
        }
    }
}
