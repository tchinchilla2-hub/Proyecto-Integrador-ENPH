/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Negocio;

import Datos.HogarDAO;

import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 * @author thain
 */
public class HogarService {
    
    private final HogarDAO hogarDAO = new HogarDAO();

    public int registrarHogar(
            Connection conexion,
            int cantidadPersonas,
            int idVivienda,
            String identificacion,
            String nombres,
            String apellidos,
            String telefono,
            String correo) throws SQLException {

        // Validar campos obligatorios
        if (cantidadPersonas <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de personas debe ser mayor que cero.");
        }

        if (idVivienda <= 0) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una vivienda.");
        }

        if (identificacion == null || identificacion.trim().isEmpty()
                || nombres == null || nombres.trim().isEmpty()
                || apellidos == null || apellidos.trim().isEmpty()
                || telefono == null || telefono.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Complete todos los campos obligatorios.");
        }

        if (identificacion.length() > 20
                || nombres.length() > 80
                || apellidos.length() > 80
                || telefono.length() > 20
                || (correo != null && correo.length() > 100)) {

            throw new IllegalArgumentException(
                    "Uno de los campos supera la longitud permitida.");
        }

        // Guardar todos los registros en una transacción
        boolean autoCommitAnterior = conexion.getAutoCommit();

        try {
            conexion.setAutoCommit(false);

            // Verificar identificación duplicada
            if (hogarDAO.existeIdentificacion(
                    conexion, identificacion)) {

                throw new IllegalArgumentException(
                        "Ya existe un jefe de hogar con esa identificación.");
            }

            // Generar código del hogar
            int idHogar =
                    hogarDAO.generarIdHogar(conexion);

            // Registrar hogar
            hogarDAO.guardarHogar(
                    conexion,
                    idHogar,
                    cantidadPersonas,
                    idVivienda
            );

            // Registrar jefe de hogar
            hogarDAO.guardarJefeHogar(
                    conexion,
                    identificacion,
                    nombres,
                    apellidos,
                    telefono,
                    correo,
                    idHogar
            );

            // Crear automáticamente la encuesta
            hogarDAO.crearEncuesta(conexion, idHogar);

            // Confirmar todo
            conexion.commit();

            return idHogar;

        } catch (SQLException | RuntimeException e) {

            try {
                conexion.rollback();
            } catch (SQLException errorRollback) {
                e.addSuppressed(errorRollback);
            }

            throw e;
        }
    }
}
