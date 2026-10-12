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
 // ==========================================================
    // CONFIGURACIÓN DE CONEXIÓN A ORACLE
    //
    // EN CASA:
    // HOST = "Thaina"
    // PUERTO = "1521"
    // SID = "XE"
    // USUARIO = "ENPH"
    //
    // EN LA UNIVERSIDAD:
    // 1. Cambiar HOST por la IP o nombre del servidor del profesor.
    // 2. Cambiar PUERTO si el servidor utiliza uno diferente.
    // 3. Cambiar SID por el identificador de la base de datos.
    // 4. Cambiar USUARIO por el usuario asignado.
    // 5. Configurar ENPH_DB_PASSWORD con la contraseña correcta.
    //
    // IMPORTANTE:
    // La base de datos de la universidad debe tener las tablas
    // del proyecto ENPH y sus registros necesarios.
    // ==========================================================
    
    // Parámetros de conexión a Oracle 10g
    private static final String HOST = "Thaina";
    private static final String PUERTO = "1521";
    private static final String SID = "XE";
    private static final String USUARIO = "ENPH";
    // La contraseña se obtiene de una variable de entorno
    // para evitar escribirla directamente en el código.
    private static final String CLAVE = System.getenv("ENPH_DB_PASSWORD");

    // URL de conexión a Oracle
    private static final String URL =
            "jdbc:oracle:thin:@" + HOST + ":" + PUERTO + ":" + SID;

    // Método para establecer la conexión
    public static Connection obtenerConexion() {

        try {

            // Cargar el driver JDBC
            Class.forName("oracle.jdbc.OracleDriver");

            // Establecer conexión
            Connection conexion = DriverManager.getConnection(
                    URL, USUARIO, CLAVE);

            System.out.println("CONEXIÓN EXITOSA CON ORACLE 10g");

            return conexion;

        } catch (ClassNotFoundException e) {

            System.out.println("ERROR: No se encontró el driver JDBC");
            e.printStackTrace();

        } catch (SQLException e) {

            System.out.println("ERROR AL CONECTAR CON ORACLE");
            System.out.println("Código: " + e.getErrorCode());
            System.out.println("Mensaje: " + e.getMessage());

        }

        return null;
    }

    // Método para cerrar la conexión
    public static void cerrarConexion(Connection conexion) {

        if (conexion != null) {

            try {
                conexion.close();
                System.out.println("CONEXIÓN CERRADA");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
