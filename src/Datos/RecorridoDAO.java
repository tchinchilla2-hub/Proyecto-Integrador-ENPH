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
public class RecorridoDAO {
    
    // Consultar el nombre del encuestador
    public String obtenerNombreEncuestador(
            Connection conexion,
            String codigoEncuestador) throws SQLException {

        String sql =
                "SELECT NOMBRE, APELLIDO "
                + "FROM ENCUESTADOR "
                + "WHERE CODIGO = ?";

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setString(1, codigoEncuestador);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getString("NOMBRE")
                            + " "
                            + rs.getString("APELLIDO");
                }
            }
        }

        throw new SQLException(
                "El encuestador no está registrado.");
    }

    // Consultar los hogares asignados y el estado de sus visitas
    public ResultSet consultarHogares(
            Connection conexion,
            String codigoEncuestador) throws SQLException {

        String sql =
                "SELECT h.ID AS ID_HOGAR, "
                + "j.NOMBRE AS NOMBRE_JEFE, "
                + "j.APELLIDO AS APELLIDO_JEFE, "
                + "v.DIRECCION, "
                + "b.NOMBRE AS BARRIO, "
                + "(SELECT MAX(e.FECHA_INICIO) "
                + " FROM ENCUESTA e "
                + " WHERE e.ID_HOGAR = h.ID) AS FECHA_INICIO, "
                + "CASE WHEN EXISTS ( "
                + " SELECT 1 FROM VISITA vi "
                + " JOIN ENCUESTA en ON vi.ID_ENCUESTA = en.ID "
                + " WHERE en.ID_HOGAR = h.ID "
                + " AND vi.ESTADO_VISITA = 'Finalizada' "
                + " AND TRUNC(vi.FECHA_VISITA) = TRUNC(SYSDATE) "
                + ") THEN 'Atendida' ELSE 'Programada' END AS ESTADO "
                + "FROM HOGAR h "
                + "JOIN JEFE_HOGAR j ON j.ID_HOGAR = h.ID "
                + "JOIN VIVIENDA v ON v.ID = h.ID_VIVIENDA "
                + "JOIN BARRIO b ON b.ID = v.ID_BARRIO "
                + "WHERE h.COD_ENCUESTADOR = ? "
                + "ORDER BY h.ID";

        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setString(1, codigoEncuestador);

        return ps.executeQuery();
    }
}
