package Datos;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author thain
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionOracle {
      // Parámetros de conexión a la base de datos remota
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String HOST = "localhost"; // Sustituir por la IP del servidor remoto
    //private static final String HOST = "Localhost"; // Sustituir por la IP del servidor remoto
    private static final String PUERTO = "1521";        // Puerto predeterminado de Oracle
    private static final String SID = "XE";           // Nombre de la base de datos / SID
    private static final String USUARIO = "ENPH_NUEVO";  // Reemplazar por tu usuario
    private static final String CLAVE = "EnphNuevo2026";     // Reemplazar por tu contraseña

    // Cadena de conexión (URL JDBC Thin)
    private static final String URL = "jdbc:oracle:thin:@" + HOST + ":" + PUERTO + ":" + SID;

    /**
     * Establece y retorna la conexión con la base de datos Oracle 10g.
     * @return Connection objeto de conexión o null si falla.
     */
    public static Connection obtenerConexion() {
        Connection conexion = null;

        try {
            // 1. Registrar/Cargar el Driver de Oracle
            Class.forName(DRIVER);

            // 2. Establecer el enlace remoto
            conexion = DriverManager.getConnection(URL, USUARIO, CLAVE);
            System.out.println("¡Conexión establecida con éxito a Oracle 10g!");

        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver JDBC de Oracle (ojdbc14.jar / ojdbc6.jar).");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error de conexión a la base de datos Oracle.");
            System.err.println("Código de error Oracle: " + e.getErrorCode());
            System.err.println("Mensaje: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ocurrió un error inesperado al intentar conectar.");
            e.printStackTrace();
        }

        return conexion;
    }

    /**
     * Cierra de manera segura una conexión activa.
     * @param conexion Objeto Connection a cerrar.
     */
    public static void cerrarConexion(Connection conexion) {
        if (conexion != null) {
            try {
                if (!conexion.isClosed()) {
                    conexion.close();
                    System.out.println("Conexión cerrada correctamente.");
                }
            } catch (SQLException e) {
                System.err.println("Error al intentar cerrar la conexión.");
                e.printStackTrace();
            }
        }
    }
}
