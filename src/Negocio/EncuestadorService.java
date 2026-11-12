/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Negocio;

import Datos.EncuestadorDAO;
import Datos.ConexionOracle;
import java.sql.Connection;
import java.sql.SQLException;

public class EncuestadorService {

     private final EncuestadorDAO encuestadorDAO =
            new EncuestadorDAO();

    public void registrarEncuestador(
            Connection conexion,
            String identificacion,
            String nombres,
            String apellidos,
            String telefono) throws SQLException {

        // Validar campos obligatorios
        if (identificacion == null || identificacion.trim().isEmpty()
                || nombres == null || nombres.trim().isEmpty()
                || apellidos == null || apellidos.trim().isEmpty()
                || telefono == null || telefono.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Debe completar todos los campos obligatorios.");
        }

        // Eliminar espacios al principio y al final
        identificacion = identificacion.trim();
        nombres = nombres.trim();
        apellidos = apellidos.trim();
        telefono = telefono.trim();

        // Validar longitud de los campos
        if (identificacion.length() > 20
                || nombres.length() > 80
                || apellidos.length() > 80
                || telefono.length() > 20) {

            throw new IllegalArgumentException(
                    "Uno de los campos supera la longitud permitida.");
        }

        boolean autoCommitAnterior = conexion.getAutoCommit();

        try {
            conexion.setAutoCommit(false);

            // Verificar que no exista la identificación
            if (encuestadorDAO.existeIdentificacion(
                    conexion, identificacion)) {

                throw new IllegalArgumentException(
                        "Ya existe un encuestador con esa identificación.");
            }

            // Guardar el encuestador
            encuestadorDAO.guardarEncuestador(
                    conexion,
                    identificacion,
                    nombres,
                    apellidos,
                    telefono
            );

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
