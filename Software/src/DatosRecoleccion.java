/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author thain
 */
public class DatosRecoleccion {
    
    // Datos del hogar durante la recolección
    public static String codigoHogar;

    public static java.time.LocalDate fechaInicio;

    // Registrar los datos del hogar
    public static void iniciarRecoleccion(
            String codigo,
            java.time.LocalDate fecha) {

        codigoHogar = codigo;
        fechaInicio = fecha;
    }

    // Calcular el día actual de la encuesta
    public static long obtenerDiaActual() {

        if (fechaInicio == null) {
            return 0;
        }

        return java.time.temporal.ChronoUnit.DAYS.between(
                fechaInicio,
                java.time.LocalDate.now()) + 1;
    }
}
