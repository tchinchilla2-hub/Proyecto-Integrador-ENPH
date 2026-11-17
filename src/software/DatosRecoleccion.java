/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package software;

/**
 *
 * @author thain
 */
public class DatosRecoleccion {
    // Datos del hogar durante la recolección
    public static String codigoHogar;
    public static String nombreJefeHogar;

    public static java.time.LocalDate fechaInicio;

    // Registrar los datos del hogar
    public static void iniciarRecoleccion(
            String codigo,
            java.time.LocalDate fecha) {

        codigoHogar = codigo;
        fechaInicio = fecha;
    }

    // Conserva el nombre validado al entrar y al navegar entre pantallas.
    public static void registrarNombreJefe(String nombreCompleto) {
        nombreJefeHogar = nombreCompleto == null ? null : nombreCompleto.trim();
    }

    public static String obtenerNombreJefe() {
        return nombreJefeHogar == null || nombreJefeHogar.isBlank()
                ? "Jefe de hogar" : nombreJefeHogar;
    }

    public static String obtenerFechaActualTexto() {
        return java.time.LocalDate.now().format(
                java.time.format.DateTimeFormatter.ofPattern(
                        "dd 'de' MMMM 'de' uuuu", new java.util.Locale("es", "CO")));
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
