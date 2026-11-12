/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Negocio;
import Datos.RecorridoDAO;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author thain
 */
public class RecorridoService {
      private final RecorridoDAO recorridoDAO = new RecorridoDAO();

    // Obtener el nombre del encuestador
    public String obtenerNombreEncuestador(
            Connection conexion,
            String codigoEncuestador) throws SQLException {

        validarEncuestador(codigoEncuestador);

        return recorridoDAO.obtenerNombreEncuestador(
                conexion, codigoEncuestador);
    }

    // Obtener los hogares del cronograma
    public List<Object[]> obtenerCronograma(
            Connection conexion,
            String codigoEncuestador) throws SQLException {

        validarEncuestador(codigoEncuestador);

        List<Object[]> filas = new ArrayList<>();

        ResultSet resultado = recorridoDAO.consultarHogares(
                conexion, codigoEncuestador);

        Statement sentencia = resultado.getStatement();

        try (Statement ps = sentencia;
             ResultSet rs = resultado) {

            while (rs.next()) {

                int idHogar = rs.getInt("ID_HOGAR");

                String hogar =
                        String.format("HG-%03d", idHogar)
                        + " - "
                        + rs.getString("NOMBRE_JEFE")
                        + " "
                        + rs.getString("APELLIDO_JEFE");

                String ubicacion =
                        rs.getString("DIRECCION")
                        + " - "
                        + rs.getString("BARRIO");

                // Calcular el día de recolección
                String diaRecoleccion = "Sin iniciar";

                java.sql.Date fechaOracle =
                        rs.getDate("FECHA_INICIO");

                if (fechaOracle != null) {

                    long dia = ChronoUnit.DAYS.between(
                            fechaOracle.toLocalDate(),
                            LocalDate.now()
                    ) + 1;

                    if (dia < 1) {
                        diaRecoleccion = "Por iniciar";
                    } else if (dia <= 14) {
                        diaRecoleccion = dia + " de 14";
                    } else {
                        diaRecoleccion = "Periodo terminado";
                    }
                }

                String estado = rs.getString("ESTADO");

                boolean seleccionado =
                        "Atendida".equals(estado);

                filas.add(new Object[]{
                    hogar,
                    ubicacion,
                    diaRecoleccion,
                    estado,
                    seleccionado
                });
            }
        }

        return filas;
    }

    // Validar el código del encuestador
    private void validarEncuestador(String codigoEncuestador) {

        if (codigoEncuestador == null
                || codigoEncuestador.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "No se ha identificado al encuestador.");
        }
    }
}
