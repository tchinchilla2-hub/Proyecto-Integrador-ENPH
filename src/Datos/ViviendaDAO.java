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
public class ViviendaDAO {
     // Buscar el ID del barrio en Oracle
    public int buscarIdBarrio(
            Connection conexion,
            String barrio,
            String municipio) throws SQLException {

        String sql =
                "SELECT b.ID FROM BARRIO b "
                + "JOIN MUNICIPIO m ON b.ID_MUNICIPIO = m.ID "
                + "WHERE b.NOMBRE = ? AND m.NOMBRE = ?";

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setString(1, barrio);
            ps.setString(2, municipio);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("ID");
                }
            }
        }

        return -1;
    }

    // Guardar vivienda y generar ID con la secuencia
    public int guardarVivienda(
            Connection conexion,
            String direccion,
            int idBarrio) throws SQLException {

        String sqlId =
                "SELECT SEQ_VIVIENDA.NEXTVAL FROM DUAL";

        int idVivienda;

        try (PreparedStatement ps =
                conexion.prepareStatement(sqlId);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            idVivienda = rs.getInt(1);
        }

        String sqlGuardar =
                "INSERT INTO VIVIENDA "
                + "(ID, DIRECCION, ID_BARRIO) "
                + "VALUES (?, ?, ?)";

        try (PreparedStatement ps =
                conexion.prepareStatement(sqlGuardar)) {

            ps.setInt(1, idVivienda);
            ps.setString(2, direccion);
            ps.setInt(3, idBarrio);

            ps.executeUpdate();
        }

        return idVivienda;
    }

}
