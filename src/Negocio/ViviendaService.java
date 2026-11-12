/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Negocio;
import Datos.ViviendaDAO;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 * @author thain
 */
public class ViviendaService {
       private final ViviendaDAO viviendaDAO = new ViviendaDAO();

    public int registrarVivienda(
            Connection conexion,
            String direccion,
            String barrio,
            String municipio) throws SQLException {

        // Validar la dirección
        if (direccion == null || direccion.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe ingresar la dirección de la vivienda.");
        }

        if (direccion.length() > 150) {
            throw new IllegalArgumentException(
                    "La dirección no puede superar los 150 caracteres.");
        }

        // Validar barrio y municipio
        if (barrio == null || barrio.trim().isEmpty()
                || municipio == null || municipio.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un municipio y un barrio.");
        }

        boolean autoCommitAnterior = conexion.getAutoCommit();

        try {
            conexion.setAutoCommit(false);

            // Obtener el ID real del barrio
            int idBarrio = viviendaDAO.buscarIdBarrio(
                    conexion, barrio, municipio);

            if (idBarrio == -1) {
                throw new IllegalArgumentException(
                        "No se encontró el barrio en Oracle.");
            }

            // Registrar la vivienda
            int idVivienda = viviendaDAO.guardarVivienda(
                    conexion, direccion, idBarrio);

            // Confirmar el registro
            conexion.commit();

            return idVivienda;

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
