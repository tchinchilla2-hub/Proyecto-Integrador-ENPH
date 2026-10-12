/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author thain
 */

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.Normalizer;
import java.util.Locale;

public class GastoDiarioDAO {

    // Normalizar textos para comparar las opciones
    // del formulario con los catálogos de Oracle
    private static String normalizar(String texto) {

        if (texto == null) {
            return "";
        }

        String resultado = Normalizer.normalize(
                texto.trim(), Normalizer.Form.NFD);

        resultado = resultado.replaceAll("\\p{M}", "");

        return resultado.replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }

    // Buscar el identificador de una opción del catálogo
    private static Integer buscarId(Connection con,
            String tabla, String nombre) throws SQLException {

        if (nombre == null
                || normalizar(nombre).equals("no aplica")
                || nombre.trim().isEmpty()) {
            return null;
        }

        // Equivalencias entre las opciones del formulario y Oracle
        String buscado = normalizar(nombre);

        if (tabla.equals("LUGAR_ADQUISICION")
                && buscado.equals("supermercado")) {
            buscado = normalizar("Supermercado de cadena");
        }

        String sql = "SELECT id, nombre FROM " + tabla;

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                String nombreOracle = rs.getString("nombre");

                if (normalizar(nombreOracle).equals(buscado)) {
                    return rs.getInt("id");
                }
            }
        }

        throw new SQLException(
                "No se encontró '" + nombre
                + "' en " + tabla
                + ". Revisa las opciones del formulario.");
    }

    // Guardar el gasto diario en Oracle 10g
    public static void guardar(GastoDiario gasto,
            String codigoHogar) throws SQLException {

        if (gasto == null
                || codigoHogar == null
                || !codigoHogar.matches("HG-\\d+")) {

            throw new SQLException(
                    "Información del hogar no válida");
        }

        int idHogar = Integer.parseInt(
                codigoHogar.substring(3));

        Connection con = ConexionOracle.obtenerConexion();

        if (con == null) {
            throw new SQLException(
                    "No se pudo conectar con Oracle");
        }

        try (con) {

            int idEncuesta;
            String identificacion;

            // Consultar la encuesta y el jefe del hogar
            String consultaHogar =
                    "SELECT e.id, j.num_identificacion "
                    + "FROM encuesta e "
                    + "JOIN jefe_hogar j "
                    + "ON j.id_hogar = e.id_hogar "
                    + "WHERE e.id_hogar = ?";

            try (PreparedStatement ps =
                    con.prepareStatement(consultaHogar)) {

                ps.setInt(1, idHogar);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        throw new SQLException(
                                "El hogar no tiene una encuesta registrada");
                    }

                    idEncuesta = rs.getInt("id");
                    identificacion =
                            rs.getString("num_identificacion");
                }
            }

            // Consultar identificadores de los catálogos
            Integer tipo = buscarId(con,
                    "TIPO_GASTO", gasto.tipoGasto);

            Integer frecuencia = buscarId(con,
                    "FRECUENCIA_COMPRA", gasto.frecuenciaCompra);

            Integer forma = buscarId(con,
                    "FORMA_ADQUISICION", gasto.formaAdquisicion);

            Integer lugar = buscarId(con,
                    "LUGAR_ADQUISICION", gasto.lugarCompra);

            Integer unidad = buscarId(con,
                    "UNIDAD_MEDIDA", gasto.unidadMedida);

            Integer contenido = buscarId(con,
                    "UNIDAD_CONTENIDO", gasto.unidadContenido);

            // Verificar los campos obligatorios
            if (tipo == null || forma == null
                    || lugar == null || unidad == null) {

                throw new SQLException(
                        "Faltan datos obligatorios del gasto");
            }

            if (gasto.fecha == null) {
                throw new SQLException(
                        "El gasto no tiene una fecha válida");
            }

            // Insertar el nuevo gasto en Oracle
            String sql =
                    "INSERT INTO GASTO_DIARIO "
                    + "(ID, FECHA_GASTO, NUMERO_ORDEN, "
                    + "NOMBRE_ALIMENTO_BEBIDA, CANTIDAD, "
                    + "CONTENIDO_POR_UNIDAD, "
                    + "VALOR_PAGADO_ESTIMADO, ESTADO_REVISION, "
                    + "ID_ENCUESTA, NUM_IDENTIFICACION_JEFE_HOGAR, "
                    + "ID_TIPO_GASTO, ID_FRECUENCIA_COMPRA, "
                    + "ID_FORMA_ADQUISICION, ID_LUGAR_ADQUISICION, "
                    + "ID_UNIDAD_MEDIDA, ID_UNIDAD_CONTENIDO) "
                    + "VALUES (SEQ_GASTO_DIARIO.NEXTVAL, ?, "
                    + "(SELECT NVL(MAX(NUMERO_ORDEN), 0) + 1 "
                    + "FROM GASTO_DIARIO "
                    + "WHERE ID_ENCUESTA = ? "
                    + "AND FECHA_GASTO = ?), "
                    + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps =
                    con.prepareStatement(sql)) {

                Date fecha = Date.valueOf(gasto.fecha);

                ps.setDate(1, fecha);
                ps.setInt(2, idEncuesta);
                ps.setDate(3, fecha);

                ps.setString(4, gasto.nombreAlimento);
                ps.setDouble(5, gasto.cantidad);

                if (contenido != null) {
                    ps.setDouble(6, gasto.contenidoUnidad);
                } else {
                    ps.setNull(6, Types.NUMERIC);
                }

                ps.setDouble(7, gasto.valorPagado);
                ps.setString(8, "Pendiente");
                ps.setInt(9, idEncuesta);
                ps.setString(10, identificacion);
                ps.setInt(11, tipo);

                if (frecuencia != null) {
                    ps.setInt(12, frecuencia);
                } else {
                    ps.setNull(12, Types.NUMERIC);
                }

                ps.setInt(13, forma);
                ps.setInt(14, lugar);
                ps.setInt(15, unidad);

                if (contenido != null) {
                    ps.setInt(16, contenido);
                } else {
                    ps.setNull(16, Types.NUMERIC);
                }

                ps.executeUpdate();
            }
        }
    }
}