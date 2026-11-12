/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Datos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
/**
 *
 * @author thain
 */
public class VisitaDAO {
    // Buscar la encuesta activa de un hogar
    public int buscarEncuestaActiva(
            Connection conexion,
            int idHogar) throws SQLException {

        String sql =
                "SELECT ID FROM ENCUESTA "
                + "WHERE ID_HOGAR = ? "
                + "AND ESTADO_ENCUESTA = 'EN PROCESO' "
                + "AND FECHA_CIERRE IS NULL "
                + "ORDER BY ID DESC";

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setInt(1, idHogar);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("ID");
                }
            }
        }

        return -1;
    }

    // Buscar una visita que no ha terminado
    public int buscarVisitaAbierta(
            Connection conexion,
            int idEncuesta) throws SQLException {

        String sql =
                "SELECT ID FROM VISITA "
                + "WHERE ID_ENCUESTA = ? "
                + "AND HORA_FIN IS NULL "
                + "ORDER BY ID DESC";

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setInt(1, idEncuesta);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("ID");
                }
            }
        }

        return -1;
    }

    // Calcular el siguiente número de visita
    public int obtenerSiguienteNumero(
            Connection conexion,
            int idEncuesta) throws SQLException {

        String sql =
                "SELECT NVL(MAX(NUMERO_VISITA), 0) + 1 "
                + "FROM VISITA WHERE ID_ENCUESTA = ?";

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setInt(1, idEncuesta);

            try (ResultSet rs = ps.executeQuery()) {

                rs.next();
                return rs.getInt(1);
            }
        }
    }

    // Obtener el siguiente ID de la secuencia
    public int generarIdVisita(
            Connection conexion) throws SQLException {

        String sql =
                "SELECT SEQ_VISITA.NEXTVAL FROM DUAL";

        try (PreparedStatement ps =
                conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        }
    }

    // Insertar una nueva visita
    public void guardarVisita(
            Connection conexion,
            int idVisita,
            int numeroVisita,
            LocalDate fecha,
            String horaInicio,
            int idEncuesta) throws SQLException {

        String sql =
                "INSERT INTO VISITA "
                + "(ID, NUMERO_VISITA, FECHA_VISITA, "
                + "HORA_INICIO, HORA_FIN, ESTADO_VISITA, "
                + "ID_ENCUESTA) "
                + "VALUES (?, ?, ?, ?, NULL, ?, ?)";

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setInt(1, idVisita);
            ps.setInt(2, numeroVisita);
            ps.setDate(3, java.sql.Date.valueOf(fecha));
            ps.setString(4, horaInicio);
            ps.setString(5, "En proceso");
            ps.setInt(6, idEncuesta);

            ps.executeUpdate();
        }
    }

    // Finalizar una visita existente
    public int finalizarVisita(
            Connection conexion,
            int idVisita,
            int idHogar,
            String horaFin) throws SQLException {

        String sql =
                "UPDATE VISITA "
                + "SET HORA_FIN = ?, "
                + "ESTADO_VISITA = 'Finalizada' "
                + "WHERE ID = ? "
                + "AND HORA_FIN IS NULL "
                + "AND ID_ENCUESTA IN ( "
                + "SELECT ID FROM ENCUESTA "
                + "WHERE ID_HOGAR = ?)";

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setString(1, horaFin);
            ps.setInt(2, idVisita);
            ps.setInt(3, idHogar);

            return ps.executeUpdate();
        }
    }
     // Recuperar los datos de una visita sin finalizar
    public Object[] obtenerDatosVisitaAbierta(
            Connection conexion,
            int idEncuesta) throws SQLException {

        String sql =
                "SELECT ID, NUMERO_VISITA, "
                + "FECHA_VISITA, HORA_INICIO "
                + "FROM VISITA "
                + "WHERE ID_ENCUESTA = ? "
                + "AND HORA_FIN IS NULL "
                + "ORDER BY ID DESC";

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setInt(1, idEncuesta);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Object[]{
                        rs.getInt("ID"),
                        rs.getInt("NUMERO_VISITA"),
                        rs.getDate("FECHA_VISITA"),
                        rs.getString("HORA_INICIO")
                    };
                }
            }
        }

        return null;
    }
}
