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

public class HogarDAO {
    // Verificar si ya existe un jefe de hogar
    // con el número de identificación indicado.
    public boolean existeIdentificacion(
            Connection conexion,
            String identificacion) throws SQLException {

        String sql = "SELECT COUNT(*) FROM JEFE_HOGAR " + "WHERE NUM_IDENTIFICACION = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, identificacion);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }
    
    // Obtener el siguiente ID del hogar
    public int generarIdHogar(Connection conexion)
            throws SQLException {

        String sql = "SELECT SEQ_HOGAR.NEXTVAL FROM DUAL";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        }
    }

    // Guardar un hogar
    public void guardarHogar(
            Connection conexion,
            int idHogar,
            int cantidadPersonas,
            int idVivienda) throws SQLException {

        String sql = "INSERT INTO HOGAR "
                + "(ID, CANTIDAD_PERSONAS, ID_VIVIENDA) "
                + "VALUES (?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idHogar);
            ps.setInt(2, cantidadPersonas);
            ps.setInt(3, idVivienda);

            ps.executeUpdate();
        }
    }

    // Guardar al jefe del hogar
    public void guardarJefeHogar(
            Connection conexion,
            String identificacion,
            String nombres,
            String apellidos,
            String telefono,
            String correo,
            int idHogar) throws SQLException {

        String sql = "INSERT INTO JEFE_HOGAR "
                + "(NUM_IDENTIFICACION, NOMBRE, APELLIDO, "
                + "TELEFONO, CORREO, ID_HOGAR) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, identificacion);
            ps.setString(2, nombres);
            ps.setString(3, apellidos);
            ps.setString(4, telefono);

            if (correo == null || correo.trim().isEmpty()) {
                ps.setNull(5, java.sql.Types.VARCHAR);
            } else {
                ps.setString(5, correo);
            }

            ps.setInt(6, idHogar);

            ps.executeUpdate();
        }
    }

    // Crear la encuesta activa del hogar
    public void crearEncuesta(
            Connection conexion,
            int idHogar) throws SQLException {

        String sql = "INSERT INTO ENCUESTA "
                + "(ID, ID_HOGAR, FECHA_INICIO, "
                + "FECHA_LIMITE, FECHA_CIERRE, ESTADO_ENCUESTA) "
                + "VALUES ("
                + "(SELECT NVL(MAX(ID), 0) + 1 FROM ENCUESTA), "
                + "?, TRUNC(SYSDATE), "
                + "TRUNC(SYSDATE) + 13, NULL, 'Iniciada')";

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setInt(1, idHogar);

            ps.executeUpdate();
        }
}
}