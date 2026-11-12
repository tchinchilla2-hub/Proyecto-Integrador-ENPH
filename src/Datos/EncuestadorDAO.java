/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Datos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 * @author thain
 */
public class EncuestadorDAO {
     // Verificar si el encuestador ya existe
    public boolean existeIdentificacion(
            Connection conexion,
            String identificacion) throws SQLException {

        String sql =
                "SELECT COUNT(*) FROM ENCUESTADOR "
                + "WHERE NUM_IDENTIFICACION = ?";

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setString(1, identificacion);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // Guardar el encuestador en Oracle
    public void guardarEncuestador(
            Connection conexion,
            String identificacion,
            String nombres,
            String apellidos,
            String telefono) throws SQLException {

        String sql =
                "INSERT INTO ENCUESTADOR "
                + "(NUM_IDENTIFICACION, NOMBRE, APELLIDO, TELEFONO) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setString(1, identificacion);
            ps.setString(2, nombres);
            ps.setString(3, apellidos);
            ps.setString(4, telefono);

            ps.executeUpdate();
        }
    }
}
