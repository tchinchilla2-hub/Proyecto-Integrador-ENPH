/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Negocio;
import Datos.VisitaDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
/**
 *
 * @author thain
 */
public class VisitaService {
     private final VisitaDAO visitaDAO = new VisitaDAO();

    // Obtener el ID numérico del hogar
    private int obtenerIdHogar(String codigoHogar) {

        if (codigoHogar == null
                || !codigoHogar.matches("HG-\\d+")) {

            throw new IllegalArgumentException(
                    "No se ha identificado correctamente el hogar.");
        }

        return Integer.parseInt(codigoHogar.substring(3));
    }

    // Consultar la encuesta activa
    public int obtenerEncuestaActiva(
            Connection conexion,
            String codigoHogar) throws SQLException {

        int idHogar = obtenerIdHogar(codigoHogar);

        int idEncuesta = visitaDAO.buscarEncuestaActiva(
                conexion, idHogar);

        if (idEncuesta == -1) {
            throw new IllegalArgumentException(
                    "El hogar no tiene una encuesta activa.");
        }

        return idEncuesta;
    }

    // Consultar si hay una visita pendiente
    public int obtenerVisitaAbierta(
            Connection conexion,
            int idEncuesta) throws SQLException {

        return visitaDAO.buscarVisitaAbierta(
                conexion, idEncuesta);
    }

    // Obtener el número de la próxima visita
    public int obtenerSiguienteNumero(
            Connection conexion,
            int idEncuesta) throws SQLException {

        return visitaDAO.obtenerSiguienteNumero(
                conexion, idEncuesta);
    }

    // Iniciar una nueva visita
    public int iniciarVisita(
            Connection conexion,
            String codigoHogar) throws SQLException {

        int idEncuesta = obtenerEncuestaActiva(
                conexion, codigoHogar);

        boolean autoCommitAnterior = conexion.getAutoCommit();

        try {
            conexion.setAutoCommit(false);

            int visitaAbierta = visitaDAO.buscarVisitaAbierta(
                    conexion, idEncuesta);

            if (visitaAbierta != -1) {
                throw new IllegalArgumentException(
                        "Ya existe una visita sin finalizar.");
            }

            int numeroVisita = visitaDAO.obtenerSiguienteNumero(
                    conexion, idEncuesta);

            int idVisita = visitaDAO.generarIdVisita(conexion);

            LocalDate fecha = LocalDate.now();

            String horaInicio = LocalTime.now().format(
                    DateTimeFormatter.ofPattern("HH:mm"));

            visitaDAO.guardarVisita(
                    conexion,
                    idVisita,
                    numeroVisita,
                    fecha,
                    horaInicio,
                    idEncuesta
            );

            conexion.commit();

            return idVisita;

        } catch (SQLException | RuntimeException e) {

            try {
                conexion.rollback();
            } catch (SQLException errorRollback) {
                e.addSuppressed(errorRollback);
            }

            throw e;

        } finally {
            conexion.setAutoCommit(autoCommitAnterior);
        }
    }

    // Finalizar una visita
    public void finalizarVisita(
            Connection conexion,
            String codigoHogar,
            int idVisita) throws SQLException {

        int idHogar = obtenerIdHogar(codigoHogar);

        String horaFin = LocalTime.now().format(
                DateTimeFormatter.ofPattern("HH:mm"));

        boolean autoCommitAnterior = conexion.getAutoCommit();

        try {
            conexion.setAutoCommit(false);

            int filas = visitaDAO.finalizarVisita(
                    conexion,
                    idVisita,
                    idHogar,
                    horaFin
            );

            if (filas != 1) {
                throw new IllegalArgumentException(
                        "No se encontró una visita abierta para finalizar.");
            }

            conexion.commit();

        } catch (SQLException | RuntimeException e) {

            try {
                conexion.rollback();
            } catch (SQLException errorRollback) {
                e.addSuppressed(errorRollback);
            }

            throw e;

        } finally {
            conexion.setAutoCommit(autoCommitAnterior);
        }
    }
}
