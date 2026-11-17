/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Datos;

import Modelo.Encuestador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Modelo.Region;
import java.util.ArrayList;
import Modelo.Departamento;
import Modelo.Municipio;
import Modelo.Barrio;
import Modelo.Vivienda;
import Modelo.Hogar;
import Modelo.JefeHogar;
import Modelo.Encuesta;
import Modelo.Visita;
import Modelo.FormaAdquisicion;
import Modelo.LugarAdquisicion;
import Modelo.UnidadMedida;
import Modelo.GastosDiarios;
import java.sql.Types;
import java.text.Normalizer;
import java.util.Locale;
import java.sql.Date;
/**
 *
 * @author thain
 */
public class Persistencia {
     public Encuestador grabarEncuestador(Encuestador pEncuestador) {

        String sqlVerificar =
                "SELECT COUNT(*) FROM ENCUESTADOR "
                + "WHERE NUM_IDENTIFICACION = ?";

        String sqlInsertar =
                "INSERT INTO ENCUESTADOR "
                + "(NUM_IDENTIFICACION, NOMBRE, APELLIDO, TELEFONO) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conexion =
                ConexionOracle.obtenerConexion()) {

            if (conexion == null) {
                throw new IllegalStateException(
                        "No se pudo conectar con Oracle.");
            }

            // Verificar si ya existe
            try (PreparedStatement ps =
                    conexion.prepareStatement(sqlVerificar)) {

                ps.setString(1,
                        pEncuestador.getIdentificacion());

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new IllegalArgumentException(
                                "El encuestador ya está registrado.");
                    }
                }
            }

            // Guardar el encuestador
            try (PreparedStatement ps =
                    conexion.prepareStatement(sqlInsertar)) {

                ps.setString(1,
                        pEncuestador.getIdentificacion());

                ps.setString(2,
                        pEncuestador.getNombres());

                ps.setString(3,
                        pEncuestador.getApellidos());

                ps.setString(4,
                        pEncuestador.getTelefono());

                ps.executeUpdate();
            }

            return pEncuestador;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al guardar el encuestador en Oracle.", e);
        }
    }
    
     
    public Encuestador[] recuperarEncuestadores() {

    ArrayList<Encuestador> encuestadores =
            new ArrayList<>();

    String sql = "SELECT NUM_IDENTIFICACION, "
            + "NOMBRE, APELLIDO, TELEFONO "
            + "FROM ENCUESTADOR";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Encuestador encuestador =
                        new Encuestador(
                            rs.getString("NUM_IDENTIFICACION"),
                            rs.getString("NOMBRE"),
                            rs.getString("APELLIDO"),
                            rs.getString("TELEFONO")
                        );

                encuestadores.add(encuestador);
            }
        }

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al recuperar encuestadores.", e);
    }

    return encuestadores.toArray(new Encuestador[0]);
}
    public Region[] recuperarRegiones() {

    ArrayList<Region> regiones = new ArrayList<>();

    String sql = "SELECT ID, NOMBRE FROM REGION ORDER BY ID";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Region region = new Region(
                        rs.getInt("ID"),
                        rs.getString("NOMBRE")
                );

                regiones.add(region);
            }
        }

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al recuperar las regiones.", e);
    }

    return regiones.toArray(new Region[0]);
}
    //RECUPERARDEPARTAMENTO
    
    public Departamento[] recuperarDepartamentos(Region pRegion) {

    ArrayList<Departamento> departamentos = new ArrayList<>();

    String sql = "SELECT ID, NOMBRE FROM DEPARTAMENTO "
            + "WHERE ID_REGION = ? ORDER BY NOMBRE";

    try (Connection conexion = ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, pRegion.getId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    Departamento departamento = new Departamento(
                            rs.getInt("ID"),
                            rs.getString("NOMBRE")
                    );

                    departamentos.add(departamento);
                }
            }
        }

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al recuperar departamentos.", e);
    }

    return departamentos.toArray(new Departamento[0]);
}
    
public Municipio[] recuperarMunicipios(
        Departamento pDepartamento) {

    ArrayList<Municipio> municipios = new ArrayList<>();

    String sql = "SELECT ID, NOMBRE FROM MUNICIPIO "
            + "WHERE ID_DEPARTAMENTO = ? ORDER BY NOMBRE";

    try (Connection conexion = ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, pDepartamento.getId());

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Municipio municipio = new Municipio(
                            rs.getInt("ID"),
                            rs.getString("NOMBRE")
                    );

                    municipios.add(municipio);
                }
            }
        }

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al recuperar municipios.", e);
    }

    return municipios.toArray(new Municipio[0]);
}


public Barrio[] recuperarBarrios(Municipio pMunicipio) {

    ArrayList<Barrio> barrios = new ArrayList<>();

    String sql = "SELECT ID, NOMBRE FROM BARRIO "
            + "WHERE ID_MUNICIPIO = ? ORDER BY NOMBRE";

    try (Connection conexion = ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, pMunicipio.getId());

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Barrio barrio = new Barrio(
                            rs.getInt("ID"),
                            rs.getString("NOMBRE")
                    );

                    barrios.add(barrio);
                }
            }
        }

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al recuperar barrios.", e);
    }

    return barrios.toArray(new Barrio[0]);
}

public Barrio[] recuperarBarriosPorNombre(
        String pNombreBarrio) {

    ArrayList<Barrio> barrios = new ArrayList<>();

    String sql =
            "SELECT ID, NOMBRE FROM BARRIO "
            + "WHERE UPPER(NOMBRE) LIKE UPPER(?) "
            + "ORDER BY NOMBRE";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setString(1, "%" + pNombreBarrio.trim() + "%");

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Barrio barrio = new Barrio(
                            rs.getInt("ID"),
                            rs.getString("NOMBRE")
                    );

                    barrios.add(barrio);
                }
            }
        }

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al buscar barrios por nombre.", e);
    }

    return barrios.toArray(new Barrio[0]);
}


public Vivienda[] recuperarViviendasPorBarrios(
        Barrio[] pBarrios) {

    ArrayList<Vivienda> viviendas = new ArrayList<>();

    if (pBarrios == null || pBarrios.length == 0) {
        return new Vivienda[0];
    }

    StringBuilder sql = new StringBuilder(
        "SELECT ID, DIRECCION, ID_BARRIO "
        + "FROM VIVIENDA WHERE ID_BARRIO IN ("
    );

    for (int i = 0; i < pBarrios.length; i++) {
        if (i > 0) {
            sql.append(", ");
        }
        sql.append("?");
    }

    sql.append(") ORDER BY DIRECCION");

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(sql.toString())) {

            for (int i = 0; i < pBarrios.length; i++) {
                ps.setInt(i + 1, pBarrios[i].getId());
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Barrio barrioVivienda = null;

                    for (Barrio barrio : pBarrios) {
                        if (barrio.getId()
                                == rs.getInt("ID_BARRIO")) {
                            barrioVivienda = barrio;
                            break;
                        }
                    }

                    Vivienda vivienda = new Vivienda(
                        rs.getString("DIRECCION"),
                        barrioVivienda
                    );

                    vivienda.setId(rs.getInt("ID"));

                    viviendas.add(vivienda);
                }
            }
        }

    } catch (SQLException e) {
        throw new IllegalStateException(
            "Error al recuperar las viviendas por barrios.", e);
    }

    return viviendas.toArray(new Vivienda[0]);
}


public Vivienda grabarVivienda(Vivienda pVivienda) {

    String sqlId =
            "SELECT SEQ_VIVIENDA.NEXTVAL FROM DUAL";

    String sqlGuardar =
            "INSERT INTO VIVIENDA "
            + "(ID, DIRECCION, ID_BARRIO) "
            + "VALUES (?, ?, ?)";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        boolean autoCommitAnterior =
                conexion.getAutoCommit();

        try {
            conexion.setAutoCommit(false);

            int idVivienda;

            // Obtener el siguiente ID de Oracle
            try (PreparedStatement ps =
                    conexion.prepareStatement(sqlId);
                 ResultSet rs = ps.executeQuery()) {

                rs.next();
                idVivienda = rs.getInt(1);
            }

            // Guardar la vivienda
            try (PreparedStatement ps =
                    conexion.prepareStatement(sqlGuardar)) {

                ps.setInt(1, idVivienda);

                ps.setString(2,
                        pVivienda.getDireccion());

                ps.setInt(3,
                        pVivienda.getBarrio().getId());

                ps.executeUpdate();
            }

            conexion.commit();

            pVivienda.setId(idVivienda);

            return pVivienda;

        } catch (SQLException | RuntimeException e) {
            try {
                conexion.rollback();
            } catch (SQLException ex) {
                e.addSuppressed(ex);
            }
            throw e;

        } finally {
            conexion.setAutoCommit(autoCommitAnterior);
        }

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al guardar la vivienda en Oracle.", e);
    }
}


public Vivienda[] recuperarViviendas(int pIdBarrio) {

    ArrayList<Vivienda> viviendas = new ArrayList<>();

    String sql =
            "SELECT v.ID, v.DIRECCION, "
            + "b.ID AS ID_BARRIO, b.NOMBRE AS NOMBRE_BARRIO "
            + "FROM VIVIENDA v "
            + "JOIN BARRIO b ON v.ID_BARRIO = b.ID "
            + "WHERE v.ID_BARRIO = ? "
            + "ORDER BY v.DIRECCION";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setInt(1, pIdBarrio);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Barrio barrio = new Barrio(
                            rs.getInt("ID_BARRIO"),
                            rs.getString("NOMBRE_BARRIO")
                    );

                    Vivienda vivienda = new Vivienda(
                            rs.getString("DIRECCION"),
                            barrio
                    );

                    vivienda.setId(rs.getInt("ID"));

                    viviendas.add(vivienda);
                }
            }
        }

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al recuperar las viviendas.", e);
    }

    return viviendas.toArray(new Vivienda[0]);
}
public Hogar grabarHogar(Hogar pHogar) {

    String sqlId =
            "SELECT SEQ_HOGAR.NEXTVAL FROM DUAL";

    String sqlGuardar =
            "INSERT INTO HOGAR "
            + "(ID, CANTIDAD_PERSONAS, ID_VIVIENDA) "
            + "VALUES (?, ?, ?)";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        int idHogar;

        // Obtener el ID del nuevo hogar
        try (PreparedStatement ps =
                conexion.prepareStatement(sqlId);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            idHogar = rs.getInt(1);
        }

        // Guardar el hogar en Oracle
        try (PreparedStatement ps =
                conexion.prepareStatement(sqlGuardar)) {

            ps.setInt(1, idHogar);

            ps.setInt(2,
                    pHogar.getCantidadPersonas());

            ps.setInt(3,
                    pHogar.getVivienda().getId());

            ps.executeUpdate();
        }

        pHogar.setId(idHogar);

        return pHogar;

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al guardar el hogar.", e);
    }
}

public JefeHogar grabarJefeHogar(JefeHogar pJefeHogar) {

    String sql = "INSERT INTO JEFE_HOGAR "
            + "(NUM_IDENTIFICACION, NOMBRE, APELLIDO, "
            + "TELEFONO, CORREO, ID_HOGAR) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setString(1, pJefeHogar.getIdentificacion());
            ps.setString(2, pJefeHogar.getNombres());
            ps.setString(3, pJefeHogar.getApellidos());
            ps.setString(4, pJefeHogar.getTelefono());
            ps.setString(5, pJefeHogar.getCorreo());
            ps.setInt(6, pJefeHogar.getHogar().getId());

            ps.executeUpdate();
        }

        return pJefeHogar;

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al guardar el jefe del hogar.", e);
    }
}

public Hogar grabarHogarYJefe(Hogar pHogar, JefeHogar pJefe) {

    String sqlId =
            "SELECT SEQ_HOGAR.NEXTVAL FROM DUAL";

    String sqlHogar =
            "INSERT INTO HOGAR "
            + "(ID, CANTIDAD_PERSONAS, ID_VIVIENDA) "
            + "VALUES (?, ?, ?)";

    String sqlJefe =
            "INSERT INTO JEFE_HOGAR "
            + "(NUM_IDENTIFICACION, NOMBRE, APELLIDO, "
            + "TELEFONO, CORREO, ID_HOGAR) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        conexion.setAutoCommit(false);

        try {
            int idHogar;

            // Obtener el identificador del hogar
            try (PreparedStatement ps =
                    conexion.prepareStatement(sqlId);
                 ResultSet rs = ps.executeQuery()) {

                rs.next();
                idHogar = rs.getInt(1);
            }

            // Registrar el hogar
            try (PreparedStatement ps =
                    conexion.prepareStatement(sqlHogar)) {

                ps.setInt(1, idHogar);
                ps.setInt(2, pHogar.getCantidadPersonas());
                ps.setInt(3, pHogar.getVivienda().getId());

                ps.executeUpdate();
            }

            // Registrar el jefe del hogar
            try (PreparedStatement ps =
                    conexion.prepareStatement(sqlJefe)) {

                ps.setString(1, pJefe.getIdentificacion());
                ps.setString(2, pJefe.getNombres());
                ps.setString(3, pJefe.getApellidos());
                ps.setString(4, pJefe.getTelefono());
                ps.setString(5, pJefe.getCorreo());
                ps.setInt(6, idHogar);

                ps.executeUpdate();
            }

            // Confirmar ambos registros
            conexion.commit();

            // Actualizar los objetos en memoria
            pHogar.setId(idHogar);
            pJefe.setHogar(pHogar);

            return pHogar;

        } catch (SQLException | RuntimeException e) {

            try {
                conexion.rollback();
            } catch (SQLException ex) {
                e.addSuppressed(ex);
            }

            throw e;
        }

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al registrar hogar y jefe.", e);
    }
}

public Hogar[] recuperarHogaresDisponibles() {

    ArrayList<Hogar> hogares = new ArrayList<>();

    String sql =
            "SELECT h.ID, h.CANTIDAD_PERSONAS, "
            + "v.ID AS ID_VIVIENDA, "
            + "v.DIRECCION, v.ID_BARRIO "
            + "FROM HOGAR h "
            + "JOIN VIVIENDA v ON h.ID_VIVIENDA = v.ID "
            + "WHERE h.NUM_IDENTIFICACION_ENCUESTADOR IS NULL "
            + "ORDER BY h.ID";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                // Recuperar el barrio relacionado
                Barrio barrio = new Barrio(
                        rs.getInt("ID_BARRIO"),
                        ""
                );

                // Recuperar la vivienda
                Vivienda vivienda = new Vivienda(
                        rs.getString("DIRECCION"),
                        barrio
                );

                vivienda.setId(rs.getInt("ID_VIVIENDA"));

                // Recuperar el hogar
                Hogar hogar = new Hogar(
                        rs.getInt("CANTIDAD_PERSONAS"),
                        vivienda
                );

                hogar.setId(rs.getInt("ID"));

                hogares.add(hogar);
            }
        }

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al recuperar los hogares.", e);
    }

    return hogares.toArray(new Hogar[0]);
}

public void actualizarAsignacionHogar(Hogar[] pHogares) {

    if (pHogares == null || pHogares.length == 0) {
        return;
    }

    String sql =
            "UPDATE HOGAR "
            + "SET NUM_IDENTIFICACION_ENCUESTADOR = ? "
            + "WHERE ID = ? "
            + "AND NUM_IDENTIFICACION_ENCUESTADOR IS NULL";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        conexion.setAutoCommit(false);

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            for (Hogar hogar : pHogares) {

                if (hogar.getEncuestador() == null) {
                    throw new IllegalArgumentException(
                            "Hay un hogar sin encuestador asignado.");
                }

                ps.setString(1,
                        hogar.getEncuestador().getIdentificacion());

                ps.setInt(2, hogar.getId());

                int filasActualizadas = ps.executeUpdate();

                if (filasActualizadas != 1) {
                    throw new IllegalStateException(
                            "No se pudo asignar el hogar "
                            + hogar.getId()
                            + ". Puede estar asignado previamente.");
                }
            }

            conexion.commit();

        } catch (SQLException | RuntimeException e) {

            try {
                conexion.rollback();
            } catch (SQLException ex) {
                e.addSuppressed(ex);
            }

            throw e;
        }

    } catch (SQLException e) {

    e.printStackTrace();

    throw new IllegalStateException(
            "Error al actualizar la asignación de hogares:\n"
            + e.getMessage(), e);
}
}


public Hogar[] recuperarHogaresAsignados(String pIdentificacion) {

    ArrayList<Hogar> hogares = new ArrayList<>();

    String sql =
            "SELECT h.ID, h.CANTIDAD_PERSONAS, "
            + "v.ID AS ID_VIVIENDA, "
            + "v.DIRECCION, v.ID_BARRIO "
            + "FROM HOGAR h "
            + "JOIN VIVIENDA v ON h.ID_VIVIENDA = v.ID "
            + "WHERE h.NUM_IDENTIFICACION_ENCUESTADOR = ? "
            + "ORDER BY h.ID";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setString(1, pIdentificacion);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Barrio barrio = new Barrio(
                            rs.getInt("ID_BARRIO"),
                            ""
                    );

                    Vivienda vivienda = new Vivienda(
                            rs.getString("DIRECCION"),
                            barrio
                    );

                    vivienda.setId(
                            rs.getInt("ID_VIVIENDA"));

                    Hogar hogar = new Hogar(
                            rs.getInt("CANTIDAD_PERSONAS"),
                            vivienda
                    );

                    hogar.setId(rs.getInt("ID"));

                    hogares.add(hogar);
                }
            }
        }
    } catch (SQLException e) {

    e.printStackTrace();

    throw new IllegalStateException(
            "Error al actualizar la asignación de hogares:\n"
            + e.getMessage(), e);
}

    return hogares.toArray(new Hogar[0]);
}

public Encuesta grabarEncuesta(Encuesta pEncuesta) {
if (pEncuesta == null
            || pEncuesta.getHogar() == null
            || pEncuesta.getHogar().getId() <= 0) {

        throw new IllegalArgumentException(
                "Debe seleccionar un hogar válido."
        );
    }

    int idHogar = pEncuesta.getHogar().getId();

    String sqlVerificar =
            "SELECT COUNT(*) FROM ENCUESTA "
            + "WHERE ID_HOGAR = ?";

    String sqlInsertar =
            "INSERT INTO ENCUESTA "
            + "(ID, ID_HOGAR, FECHA_INICIO, "
            + "FECHA_LIMITE, FECHA_CIERRE, ESTADO_ENCUESTA) "
            + "VALUES ("
            + "(SELECT NVL(MAX(ID), 0) + 1 FROM ENCUESTA), "
            + "?, TRUNC(SYSDATE), "
            + "TRUNC(SYSDATE) + 13, NULL, 'Iniciada')";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle."
            );
        }

        // Verificar que el hogar no tenga encuesta
        try (PreparedStatement ps =
                conexion.prepareStatement(sqlVerificar)) {

            ps.setInt(1, idHogar);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next() && rs.getInt(1) > 0) {
                    throw new IllegalArgumentException(
                            "Este hogar ya tiene una encuesta registrada."
                    );
                }
            }
        }

        // Registrar la encuesta
        try (PreparedStatement ps =
                conexion.prepareStatement(sqlInsertar)) {

            ps.setInt(1, idHogar);
            ps.executeUpdate();
        }

       // Recuperar el ID de la encuesta registrada
String sqlRecuperar =
        "SELECT ID FROM ENCUESTA WHERE ID_HOGAR = ?";

try (PreparedStatement ps =
        conexion.prepareStatement(sqlRecuperar)) {

    ps.setInt(1, idHogar);

    try (ResultSet rs = ps.executeQuery()) {

        if (rs.next()) {

            int idEncuesta = rs.getInt("ID");

            pEncuesta.setId(idEncuesta);

        } else {

            throw new IllegalStateException(
                    "No se pudo recuperar la encuesta registrada."
            );
        }
    }
}

// Devolver la encuesta con su ID
return pEncuesta;

    } catch (SQLException e) {

        throw new IllegalStateException(
                "Error al registrar la encuesta en Oracle.",
                e
        );
    }
}


public Visita grabarVisita(Visita pVisita) {

      if (pVisita == null
            || pVisita.getEncuesta() == null
            || pVisita.getEncuesta().getId() <= 0) {

        throw new IllegalArgumentException(
                "La visita debe tener una encuesta válida.");
    }

    String sql =
            "INSERT INTO VISITA "
            + "(ID, FECHA_HORA_INICIO, FECHA_HORA_FIN, ID_ENCUESTA) "
            + "VALUES (SEQ_VISITA.NEXTVAL, ?, NULL, ?)";

    String sqlId =
            "SELECT SEQ_VISITA.CURRVAL FROM DUAL";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        // Registrar la fecha y hora real del inicio
        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setTimestamp(
                    1,
                    java.sql.Timestamp.valueOf(
                            java.time.LocalDateTime.now())
            );

            ps.setInt(
                    2,
                    pVisita.getEncuesta().getId()
            );

            ps.executeUpdate();
        }

        // Recuperar el ID generado por Oracle
        try (PreparedStatement ps =
                conexion.prepareStatement(sqlId);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                pVisita.setId(rs.getInt(1));
            }
        }

        return pVisita;

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al guardar la visita: "
                + e.getMessage(), e);
    }
}


public void actualizarVisita(Visita pVisita) {

 if (pVisita == null || pVisita.getId() <= 0) {
        throw new IllegalArgumentException(
                "Debe proporcionar una visita registrada.");
    }

    String sql =
            "UPDATE VISITA "
            + "SET FECHA_HORA_FIN = ? "
            + "WHERE ID = ? "
            + "AND FECHA_HORA_FIN IS NULL "
            + "AND FECHA_HORA_INICIO <> TRUNC(FECHA_HORA_INICIO)";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setTimestamp(
                    1,
                    java.sql.Timestamp.valueOf(
                            java.time.LocalDateTime.now())
            );

            ps.setInt(2, pVisita.getId());

            int filasActualizadas =
                    ps.executeUpdate();

            if (filasActualizadas == 0) {
                throw new IllegalStateException(
                        "La visita no existe o ya fue finalizada.");
            }
        }

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al finalizar la visita: "
                + e.getMessage(), e);
    }
}


// Recupera la encuesta existente del hogar sin crear otra.
public Encuesta recuperarEncuestaHogar(int pIdHogar) {
    if (pIdHogar <= 0) {
        throw new IllegalArgumentException("El hogar no es válido.");
    }
    String sql = "SELECT ID FROM ENCUESTA WHERE ID_HOGAR = ?";
    try (Connection con = ConexionOracle.obtenerConexion()) {
        if (con == null) throw new IllegalStateException("Sin conexión con Oracle.");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pIdHogar);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Hogar hogar = new Hogar(0, null);
                hogar.setId(pIdHogar);
                Encuesta encuesta = new Encuesta(hogar);
                encuesta.setId(rs.getInt("ID"));
                return encuesta;
            }
        }
    } catch (SQLException e) {
        throw new IllegalStateException("Error al recuperar la encuesta del hogar.", e);
    }
}

// Usa solamente las columnas que existen en VISITA.
public Visita[] recuperarVisitasEncuesta(int pIdEncuesta) {
    ArrayList<Visita> visitas = new ArrayList<>();
    String sql = "SELECT ID, FECHA_HORA_INICIO, FECHA_HORA_FIN, ID_ENCUESTA "
            + "FROM VISITA WHERE ID_ENCUESTA = ? ORDER BY FECHA_HORA_INICIO, ID";
    try (Connection con = ConexionOracle.obtenerConexion()) {
        if (con == null) throw new IllegalStateException("Sin conexión con Oracle.");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pIdEncuesta);
            try (ResultSet rs = ps.executeQuery()) {
                int numero = 0;
                while (rs.next()) {
                    numero++;
                    java.sql.Timestamp inicio = rs.getTimestamp("FECHA_HORA_INICIO");
                    java.sql.Timestamp fin = rs.getTimestamp("FECHA_HORA_FIN");
                    Encuesta encuesta = new Encuesta(null);
                    encuesta.setId(rs.getInt("ID_ENCUESTA"));
                    java.time.LocalDateTime fechaInicio = inicio.toLocalDateTime();
                    boolean programada = fechaInicio.toLocalTime().equals(java.time.LocalTime.MIDNIGHT);
                    Visita visita = new Visita(numero, fechaInicio.toLocalDate(),
                            programada ? null : fechaInicio.toLocalTime().toString(), encuesta);
                    visita.setId(rs.getInt("ID"));
                    visita.setHoraFin(fin == null ? null : fin.toLocalDateTime().toString());
                    visita.setEstadoVisita(fin != null ? "Atendida" :
                            programada ? "Programada" : "En ejecución");
                    visitas.add(visita);
                }
            }
        }
    } catch (SQLException e) {
        throw new IllegalStateException("Error al recuperar las visitas de la encuesta.", e);
    }
    return visitas.toArray(new Visita[0]);
}

// Datos de lectura para la etiqueta del encuestador en el cronograma.
public String recuperarNombreEncuestador(String identificacion) {
    String sql = "SELECT NOMBRE, APELLIDO FROM ENCUESTADOR WHERE NUM_IDENTIFICACION = ?";
    try (Connection con = ConexionOracle.obtenerConexion()) {
        if (con == null) throw new IllegalStateException("Sin conexión con Oracle.");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, identificacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new IllegalArgumentException("Encuestador no registrado.");
                return rs.getString("NOMBRE") + " " + rs.getString("APELLIDO");
            }
        }
    } catch (SQLException e) {
        throw new IllegalStateException("Error al consultar el encuestador.", e);
    }
}

public FormaAdquisicion grabarFormaAdquisicion(
        FormaAdquisicion pForma) {

    String sql =
            "INSERT INTO FORMA_ADQUISICION "
            + "(ID, NOMBRE) "
            + "VALUES ("
            + "(SELECT NVL(MAX(ID), 0) + 1 "
            + "FROM FORMA_ADQUISICION), ?)";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setString(1, pForma.getNombre());

            ps.executeUpdate();
        }

        return pForma;

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al guardar la forma de adquisición.", e);
    }
}


public LugarAdquisicion grabarLugarAdquisicion(
        LugarAdquisicion pLugar) {

    String sql =
            "INSERT INTO LUGAR_ADQUISICION "
            + "(NOMBRE) VALUES (?)";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setString(1, pLugar.getNombre());

            ps.executeUpdate();
        }

        return pLugar;

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al guardar el lugar de adquisición.", e);
    }
}


public UnidadMedida grabarUnidadMedida(
        UnidadMedida pUnidad) {

    String sql =
            "INSERT INTO UNIDAD_MEDIDA "
            + "(NOMBRE) VALUES (?)";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setString(1, pUnidad.getNombre());

            ps.executeUpdate();
        }

        return pUnidad;

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al guardar la unidad de medida.", e);
    }
}


public FormaAdquisicion[] recuperarFormasAdquisicion() {

    ArrayList<FormaAdquisicion> formas = new ArrayList<>();

    String sql = "SELECT ID, NOMBRE "
            + "FROM FORMA_ADQUISICION "
            + "ORDER BY NOMBRE";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                FormaAdquisicion forma =
                        new FormaAdquisicion(
                                rs.getString("NOMBRE")
                        );

                forma.setId(rs.getInt("ID"));

                formas.add(forma);
            }
        }

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al recuperar las formas de adquisición.", e);
    }

    return formas.toArray(new FormaAdquisicion[0]);
}


public LugarAdquisicion[] recuperarLugaresAdquisicion() {

    ArrayList<LugarAdquisicion> lugares = new ArrayList<>();

    String sql = "SELECT ID, NOMBRE "
            + "FROM LUGAR_ADQUISICION "
            + "ORDER BY NOMBRE";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                LugarAdquisicion lugar =
                        new LugarAdquisicion(
                                rs.getString("NOMBRE")
                        );

                lugar.setId(rs.getInt("ID"));

                lugares.add(lugar);
            }
        }

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al recuperar los lugares de adquisición.", e);
    }

    return lugares.toArray(new LugarAdquisicion[0]);
}



public UnidadMedida[] recuperarUnidadesMedida() {

    ArrayList<UnidadMedida> unidades = new ArrayList<>();

    String sql = "SELECT ID_UNIDAD_MEDIDA, NOMBRE "
            + "FROM UNIDAD_MEDIDA "
            + "ORDER BY NOMBRE";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                UnidadMedida unidad =
                        new UnidadMedida(
                                rs.getString("NOMBRE")
                        );

                unidad.setId(rs.getInt("ID_UNIDAD_MEDIDA"));

                unidades.add(unidad);
            }
        }

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al recuperar las unidades de medida.", e);
    }

    return unidades.toArray(new UnidadMedida[0]);
}


public GastosDiarios grabarGastoDiario(GastosDiarios pGasto) {


    if (pGasto == null || pGasto.getEncuesta() == null
            || pGasto.getEncuesta().getId() <= 0) {
        throw new IllegalArgumentException(
                "El gasto debe tener una encuesta válida.");
    }

    if (pGasto.getFecha() == null) {
        throw new IllegalArgumentException(
                "Debe indicar la fecha del gasto.");
    }

    String[] tablas = {
        "FORMA_ADQUISICION",
        "LUGAR_ADQUISICION",
        "UNIDAD_MEDIDA"
    };

    String[] columnasId = {
        "ID",
        "ID_LUGAR_ADQUISICION",
        "ID_UNIDAD_MEDIDA"
    };

    String[] nombres = {
        pGasto.getFormaAdquisicion(),
        pGasto.getLugarCompra(),
        pGasto.getUnidadMedida()
    };

    Integer[] ids = new Integer[3];

    String sql =
            "INSERT INTO GASTO_DIARIO "
            + "(ID, FECHA_GASTO, NOMBRE_ALIMENTO_BEBIDA, "
            + "CANTIDAD, CONTENIDO_POR_UNIDAD, "
            + "VALOR_PAGADO_ESTIMADO, ESTADO_REVISION, "
            + "ID_ENCUESTA, ID_FORMA_ADQUISICION, "
            + "ID_LUGAR_ADQUISICION, ID_UNIDAD_MEDIDA, "
            + "TIPO_GASTO, FRECUENCIA_COMPRA, "
            + "UNIDAD_CONTENIDO) "
            + "VALUES (SEQ_GASTO_DIARIO.NEXTVAL, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        // Recuperar los identificadores de los catálogos
        for (int i = 0; i < tablas.length; i++) {

            String nombre = nombres[i];

            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Falta información de " + tablas[i]);
            }

            String buscado = Normalizer.normalize(
                    nombre.trim(), Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "")
                    .replaceAll("\\s+", " ")
                    .toLowerCase(Locale.ROOT);

            String consulta =
                    "SELECT " + columnasId[i]
                    + ", NOMBRE FROM " + tablas[i];

            try (PreparedStatement ps =
                    conexion.prepareStatement(consulta);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    String nombreOracle = Normalizer.normalize(
                            rs.getString("NOMBRE"),
                            Normalizer.Form.NFD)
                            .replaceAll("\\p{M}", "")
                            .replaceAll("\\s+", " ")
                            .toLowerCase(Locale.ROOT);

                    if (nombreOracle.equals(buscado)) {
                        ids[i] = rs.getInt(columnasId[i]);
                        break;
                    }
                }
            }

            if (ids[i] == null) {
                throw new IllegalArgumentException(
                        "No se encontró '" + nombre
                        + "' en la tabla " + tablas[i]);
            }
        }

        java.sql.Date fecha =
                java.sql.Date.valueOf(pGasto.getFecha());

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setDate(1, fecha);
            ps.setString(2, pGasto.getNombreAlimento());
            ps.setDouble(3, pGasto.getCantidad());

            if (pGasto.getUnidadContenido() != null
                    && !pGasto.getUnidadContenido().isBlank()) {
                ps.setDouble(4, pGasto.getContenidoUnidad());
            } else {
                ps.setNull(4, Types.NUMERIC);
            }

            ps.setDouble(5, pGasto.getValorPagado());
            ps.setString(6, "Pendiente");
            ps.setInt(7, pGasto.getEncuesta().getId());

            ps.setInt(8, ids[0]);
            ps.setInt(9, ids[1]);
            ps.setInt(10, ids[2]);

            ps.setString(11, pGasto.getTipoGasto());

            if (pGasto.getFrecuenciaCompra() != null
                    && !pGasto.getFrecuenciaCompra()
                            .equalsIgnoreCase("No aplica")) {
                ps.setString(12, pGasto.getFrecuenciaCompra());
            } else {
                ps.setNull(12, Types.VARCHAR);
            }

            if (pGasto.getUnidadContenido() != null
                    && !pGasto.getUnidadContenido().isBlank()) {
                ps.setString(13, pGasto.getUnidadContenido());
            } else {
                ps.setNull(13, Types.VARCHAR);
            }

            ps.executeUpdate();
        }

        // Recuperar el ID generado por la secuencia
        try (PreparedStatement ps =
                conexion.prepareStatement(
                        "SELECT SEQ_GASTO_DIARIO.CURRVAL FROM DUAL");
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                pGasto.setId(rs.getInt(1));
            }
        }

        return pGasto;

    } catch (SQLException e) {
        e.printStackTrace();
        throw new IllegalStateException(
                "Error al guardar el gasto diario en Oracle: "
                + e.getMessage(), e);
    }
}


public GastosDiarios[] recuperarGastosEncuesta(int pIdEncuesta) {

    ArrayList<GastosDiarios> gastos = new ArrayList<>();

    String sql =
            "SELECT g.ID, g.FECHA_GASTO, "
            + "g.NOMBRE_ALIMENTO_BEBIDA, g.CANTIDAD, "
            + "g.CONTENIDO_POR_UNIDAD, "
            + "g.VALOR_PAGADO_ESTIMADO, "
            + "t.NOMBRE AS TIPO_GASTO, "
            + "f.NOMBRE AS FRECUENCIA_COMPRA, "
            + "a.NOMBRE AS FORMA_ADQUISICION, "
            + "l.NOMBRE AS LUGAR_COMPRA, "
            + "u.NOMBRE AS UNIDAD_MEDIDA, "
            + "CAST(NULL AS VARCHAR2(100)) AS UNIDAD_CONTENIDO "
            + "FROM GASTO_DIARIO g "
            + "LEFT JOIN TIPO_GASTO t "
            + "ON g.ID_TIPO_GASTO = t.ID "
            + "LEFT JOIN FRECUENCIA_COMPRA f "
            + "ON g.ID_FRECUENCIA_COMPRA = f.ID "
            + "LEFT JOIN FORMA_ADQUISICION a "
            + "ON g.ID_FORMA_ADQUISICION = a.ID "
            + "LEFT JOIN LUGAR_ADQUISICION l "
            + "ON g.ID_LUGAR_ADQUISICION = l.ID_LUGAR_ADQUISICION "
            + "LEFT JOIN UNIDAD_MEDIDA u "
            + "ON g.ID_UNIDAD_MEDIDA = u.ID_UNIDAD_MEDIDA "
            
            + "WHERE g.ID_ENCUESTA = ? "
            + "ORDER BY g.FECHA_GASTO, g.NUMERO_ORDEN";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setInt(1, pIdEncuesta);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    GastosDiarios gasto = new GastosDiarios();

                    gasto.setId(rs.getInt("ID"));

                    java.sql.Date fecha =
                            rs.getDate("FECHA_GASTO");

                    if (fecha != null) {
                        gasto.setFecha(fecha.toLocalDate());
                    }

                    gasto.setNombreAlimento(
                            rs.getString("NOMBRE_ALIMENTO_BEBIDA"));

                    gasto.setCantidad(
                            rs.getDouble("CANTIDAD"));

                    gasto.setContenidoUnidad(
                            rs.getDouble("CONTENIDO_POR_UNIDAD"));

                    gasto.setValorPagado(
                            rs.getDouble("VALOR_PAGADO_ESTIMADO"));

                    gasto.setTipoGasto(
                            rs.getString("TIPO_GASTO"));

                    gasto.setFrecuenciaCompra(
                            rs.getString("FRECUENCIA_COMPRA"));

                    gasto.setFormaAdquisicion(
                            rs.getString("FORMA_ADQUISICION"));

                    gasto.setLugarCompra(
                            rs.getString("LUGAR_COMPRA"));

                    gasto.setUnidadMedida(
                            rs.getString("UNIDAD_MEDIDA"));

                    gasto.setUnidadContenido(
                            rs.getString("UNIDAD_CONTENIDO"));

                    // Asociar el gasto con su encuesta
                    Hogar hogar = new Hogar(0, null);

                    Encuesta encuesta = new Encuesta(hogar);
                    encuesta.setId(pIdEncuesta);

                    gasto.setEncuesta(encuesta);

                    gastos.add(gasto);
                }
            }
        }

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al recuperar los gastos de la encuesta.", e);
    }

    return gastos.toArray(new GastosDiarios[0]);
}



public void actualizarGastoDiario(GastosDiarios pGasto) {

    if (pGasto == null || pGasto.getId() <= 0) {
        throw new IllegalArgumentException(
                "El gasto debe tener un ID válido.");
    }

    if (pGasto.getFecha() == null) {
        throw new IllegalArgumentException(
                "Debe indicar la fecha del gasto.");
    }

    String[] tablas = {
        "TIPO_GASTO",
        "FRECUENCIA_COMPRA",
        "FORMA_ADQUISICION",
        "LUGAR_ADQUISICION",
        "UNIDAD_MEDIDA"
    };

    String[] columnasId = {
        "ID",
        "ID",
        "ID",
        "ID_LUGAR_ADQUISICION",
        "ID_UNIDAD_MEDIDA"
    };

    String[] nombres = {
        pGasto.getTipoGasto(),
        pGasto.getFrecuenciaCompra(),
        pGasto.getFormaAdquisicion(),
        pGasto.getLugarCompra(),
        pGasto.getUnidadMedida()
    };

    Integer[] ids = new Integer[5];

    String sql =
            "UPDATE GASTO_DIARIO SET "
            + "FECHA_GASTO = ?, "
            + "NOMBRE_ALIMENTO_BEBIDA = ?, "
            + "CANTIDAD = ?, "
            + "CONTENIDO_POR_UNIDAD = ?, "
            + "VALOR_PAGADO_ESTIMADO = ?, "
            + "ID_TIPO_GASTO = ?, "
            + "ID_FRECUENCIA_COMPRA = ?, "
            + "ID_FORMA_ADQUISICION = ?, "
            + "ID_LUGAR_ADQUISICION = ?, "
            + "ID_UNIDAD_MEDIDA = ? "
            + "WHERE ID = ?";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        // Buscar los identificadores de los catálogos
        for (int i = 0; i < tablas.length; i++) {

            String nombre = nombres[i];

            if (nombre == null
                    || nombre.trim().isEmpty()
                    || nombre.trim().equalsIgnoreCase("No aplica")) {
                continue;
            }

            String buscado = java.text.Normalizer.normalize(
                    nombre.trim(),
                    java.text.Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "")
                    .replaceAll("\\s+", " ")
                    .toLowerCase(java.util.Locale.ROOT);

            if (i == 3 && buscado.equals("supermercado")) {
                buscado = "supermercado de cadena";
            }

            String consulta =
                    "SELECT " + columnasId[i]
                    + ", NOMBRE FROM " + tablas[i];

            try (PreparedStatement ps =
                    conexion.prepareStatement(consulta);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    String nombreOracle =
                            java.text.Normalizer.normalize(
                                    rs.getString("NOMBRE"),
                                    java.text.Normalizer.Form.NFD)
                            .replaceAll("\\p{M}", "")
                            .replaceAll("\\s+", " ")
                            .toLowerCase(java.util.Locale.ROOT);

                    if (nombreOracle.equals(buscado)) {
                        ids[i] = rs.getInt(columnasId[i]);
                        break;
                    }
                }
            }

            if (ids[i] == null) {
                throw new IllegalArgumentException(
                        "No se encontró '" + nombre
                        + "' en " + tablas[i]);
            }
        }

        if (ids[0] == null || ids[2] == null
                || ids[3] == null || ids[4] == null) {
            throw new IllegalArgumentException(
                    "Faltan datos obligatorios del gasto.");
        }

        // Actualizar los datos
        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setDate(1,
                    java.sql.Date.valueOf(pGasto.getFecha()));

            ps.setString(2, pGasto.getNombreAlimento());
            ps.setDouble(3, pGasto.getCantidad());

            if (pGasto.getUnidadContenido() != null
                    && !pGasto.getUnidadContenido().isBlank()) {
                ps.setDouble(4, pGasto.getContenidoUnidad());
            } else {
                ps.setNull(4, java.sql.Types.NUMERIC);
            }

            ps.setDouble(5, pGasto.getValorPagado());
            ps.setInt(6, ids[0]);

            if (ids[1] != null) {
                ps.setInt(7, ids[1]);
            } else {
                ps.setNull(7, java.sql.Types.NUMERIC);
            }

            ps.setInt(8, ids[2]);
            ps.setInt(9, ids[3]);
            ps.setInt(10, ids[4]);
            ps.setInt(11, pGasto.getId());

            int filasActualizadas = ps.executeUpdate();

            if (filasActualizadas == 0) {
                throw new IllegalArgumentException(
                        "No existe un gasto con ID "
                        + pGasto.getId());
            }
        }

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al actualizar el gasto diario en Oracle.", e);
    }
}


public void actualizarRevisionGastosDia(
        int pIdEncuesta, Date pFecha) {

    if (pIdEncuesta <= 0) {
        throw new IllegalArgumentException(
                "El ID de la encuesta no es válido.");
    }

    if (pFecha == null) {
        throw new IllegalArgumentException(
                "Debe indicar la fecha de revisión.");
    }

    String sql =
            "UPDATE GASTO_DIARIO "
            + "SET ESTADO_REVISION = ? "
            + "WHERE ID_ENCUESTA = ? "
            + "AND FECHA_GASTO >= ? "
            + "AND FECHA_GASTO < ? "
            + "AND ESTADO_REVISION = ?";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setString(1, "Revisado");
            ps.setInt(2, pIdEncuesta);
            ps.setDate(3, pFecha);

            java.time.LocalDate diaSiguiente =
                    pFecha.toLocalDate().plusDays(1);

            ps.setDate(4,
                    Date.valueOf(diaSiguiente));

            ps.setString(5, "Pendiente");

            ps.executeUpdate();
        }

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al actualizar la revisión "
                + "de los gastos diarios.", e);
    }
}



// Incrementos Astah 7.2, 7.3, 7.4: consultas centralizadas en Datos.
public java.sql.Date[] recuperarHistorialGastos(int pIdEncuesta) {
    if (pIdEncuesta <= 0) throw new IllegalArgumentException("Encuesta inválida.");
    java.util.List<java.sql.Date> fechas = new java.util.ArrayList<>();
    String sql = "SELECT DISTINCT TRUNC(FECHA_GASTO) AS FECHA "
            + "FROM GASTO_DIARIO WHERE ID_ENCUESTA = ? ORDER BY FECHA";
    try (Connection c = ConexionOracle.obtenerConexion()) {
        if (c == null) throw new IllegalStateException("No hay conexión con Oracle.");
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, pIdEncuesta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) fechas.add(rs.getDate("FECHA"));
            }
        }
    } catch (SQLException e) {
        throw new IllegalStateException("No fue posible obtener las fechas del historial.", e);
    }
    return fechas.toArray(new java.sql.Date[0]);
}

public GastosDiarios[] recuperarGastosPorFecha(int pIdEncuesta, java.sql.Date pFecha) {
    if (pFecha == null) throw new IllegalArgumentException("Debe seleccionar una fecha.");
    java.util.List<GastosDiarios> salida = new java.util.ArrayList<>();
    for (GastosDiarios gasto : recuperarGastosEncuesta(pIdEncuesta)) {
        if (gasto.getFecha() != null && gasto.getFecha().equals(pFecha.toLocalDate())) {
            salida.add(gasto);
        }
    }
    return salida.toArray(new GastosDiarios[0]);
}

// El ID de encuesta se consulta explícitamente: no se inserta otro registro.
public Encuesta recuperarEncuesta(int pIdEncuesta) {
    if (pIdEncuesta <= 0) throw new IllegalArgumentException("Encuesta inválida.");
    String sql = "SELECT ID_HOGAR FROM ENCUESTA WHERE ID = ?";
    try (Connection c = ConexionOracle.obtenerConexion()) {
        if (c == null) throw new IllegalStateException("Sin conexión con Oracle.");
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, pIdEncuesta);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Hogar h = new Hogar(0, null);
                h.setId(rs.getInt("ID_HOGAR"));
                Encuesta e = new Encuesta(h);
                e.setId(pIdEncuesta);
                return e;
            }
        }
    } catch (SQLException e) {
        throw new IllegalStateException("Error al recuperar la encuesta.", e);
    }
}

public Visita recuperarVisitaEnCurso(int pIdEncuesta) {
    if (pIdEncuesta <= 0) throw new IllegalArgumentException("Encuesta inválida.");
    String sql = "SELECT ID, FECHA_HORA_INICIO FROM VISITA "
            + "WHERE ID_ENCUESTA = ? AND FECHA_HORA_FIN IS NULL "
            + "AND FECHA_HORA_INICIO <> TRUNC(FECHA_HORA_INICIO) "
            + "ORDER BY FECHA_HORA_INICIO DESC";
    try (Connection c = ConexionOracle.obtenerConexion()) {
        if (c == null) throw new IllegalStateException("Sin conexión con Oracle.");
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, pIdEncuesta);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                java.time.LocalDateTime inicio = rs.getTimestamp("FECHA_HORA_INICIO").toLocalDateTime();
                Encuesta encuesta = new Encuesta(null);
                encuesta.setId(pIdEncuesta);
                Visita visita = new Visita(0, inicio.toLocalDate(),
                        inicio.toLocalTime().toString(), encuesta);
                visita.setId(rs.getInt("ID"));
                visita.setEstadoVisita("En ejecución");
                return visita;
            }
        }
    } catch (SQLException e) {
        throw new IllegalStateException("Error al recuperar la visita iniciada.", e);
    }
}

// Cierre en una transacción, sin crear tablas o columnas no existentes.
// Solo pueden cerrar encuestas con 14 días completos y ningún gasto pendiente.
public void actualizarEncuesta(Encuesta pEncuesta) {
    if (pEncuesta == null || pEncuesta.getId() <= 0) {
        throw new IllegalArgumentException("Seleccione una encuesta válida.");
    }
    String bloqueo = "SELECT FECHA_INICIO, FECHA_CIERRE FROM ENCUESTA "
            + "WHERE ID = ? FOR UPDATE";
    String pendientes = "SELECT COUNT(*) FROM GASTO_DIARIO "
            + "WHERE ID_ENCUESTA = ? AND (ESTADO_REVISION IS NULL "
            + "OR UPPER(TRIM(ESTADO_REVISION)) <> 'REVISADO')";
    String cerrar = "UPDATE ENCUESTA SET FECHA_CIERRE = SYSDATE, "
            + "ESTADO_ENCUESTA = 'Finalizado' WHERE ID = ? AND FECHA_CIERRE IS NULL";
    try (Connection c = ConexionOracle.obtenerConexion()) {
        if (c == null) throw new IllegalStateException("Sin conexión con Oracle.");
        c.setAutoCommit(false);
        try {
            try (PreparedStatement ps = c.prepareStatement(bloqueo)) {
                ps.setInt(1, pEncuesta.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new IllegalStateException("La encuesta no existe.");
                    if (rs.getTimestamp("FECHA_CIERRE") != null) {
                        throw new IllegalStateException("La encuesta ya fue finalizada.");
                    }
                    java.sql.Date inicio = rs.getDate("FECHA_INICIO");
                    if (inicio == null || java.time.temporal.ChronoUnit.DAYS.between(
                            inicio.toLocalDate(), java.time.LocalDate.now()) < 14) {
                        throw new IllegalStateException("Deben completarse los 14 días antes del cierre.");
                    }
                }
            }
            try (PreparedStatement ps = c.prepareStatement(pendientes)) {
                ps.setInt(1, pEncuesta.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new IllegalStateException("Existen gastos diarios sin revisar.");
                    }
                }
            }
            try (PreparedStatement ps = c.prepareStatement(cerrar)) {
                ps.setInt(1, pEncuesta.getId());
                if (ps.executeUpdate() != 1) {
                    throw new IllegalStateException("No fue posible cerrar la encuesta.");
                }
            }
            c.commit();
        } catch (SQLException | RuntimeException e) {
            c.rollback();
            throw e;
        }
    } catch (SQLException e) {
        throw new IllegalStateException("Error al cerrar la encuesta.", e);
    }
}

public Hogar[] recuperarHogaresSinEncuesta() {

    ArrayList<Hogar> hogares = new ArrayList<>();

    String sql =
            "SELECT h.ID, h.CANTIDAD_PERSONAS, "
            + "v.ID AS ID_VIVIENDA, v.DIRECCION "
            + "FROM HOGAR h "
            + "INNER JOIN VIVIENDA v "
            + "ON h.ID_VIVIENDA = v.ID "
            + "WHERE NOT EXISTS ( "
            + "SELECT 1 FROM ENCUESTA e "
            + "WHERE e.ID_HOGAR = h.ID "
            + ") "
            + "ORDER BY h.ID";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                // Crear la vivienda asociada
                Vivienda vivienda = new Vivienda(
                        rs.getString("DIRECCION"),
                        null
                );

                vivienda.setId(
                        rs.getInt("ID_VIVIENDA")
                );

                // Crear el hogar
                Hogar hogar = new Hogar(
                        rs.getInt("CANTIDAD_PERSONAS"),
                        vivienda
                );

                hogar.setId(
                        rs.getInt("ID")
                );

                hogares.add(hogar);
            }
        }

    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al recuperar los hogares sin encuesta.",
                e
        );
    }

    return hogares.toArray(new Hogar[0]);
}

public int distribuirHogaresAutomaticamente() {

    String sqlHogares =
            "SELECT ID FROM HOGAR "
            + "WHERE NUM_IDENTIFICACION_ENCUESTADOR IS NULL "
            + "ORDER BY ID FOR UPDATE";

    String sqlEncuestadores =
            "SELECT e.NUM_IDENTIFICACION, "
            + "COUNT(h.ID) AS TOTAL "
            + "FROM ENCUESTADOR e "
            + "LEFT JOIN HOGAR h "
            + "ON h.NUM_IDENTIFICACION_ENCUESTADOR = "
            + "e.NUM_IDENTIFICACION "
            + "GROUP BY e.NUM_IDENTIFICACION "
            + "ORDER BY e.NUM_IDENTIFICACION";

    String sqlActualizar =
            "UPDATE HOGAR "
            + "SET NUM_IDENTIFICACION_ENCUESTADOR = ? "
            + "WHERE ID = ? "
            + "AND NUM_IDENTIFICACION_ENCUESTADOR IS NULL";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new IllegalStateException(
                    "No se pudo conectar con Oracle.");
        }

        conexion.setAutoCommit(false);

        try {

            ArrayList<Integer> hogares =
                    new ArrayList<>();

            // Recuperar hogares pendientes
            try (PreparedStatement ps =
                    conexion.prepareStatement(sqlHogares);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    hogares.add(rs.getInt("ID"));
                }
            }

            if (hogares.isEmpty()) {
                conexion.rollback();
                return 0;
            }

            ArrayList<String> identificaciones =
                    new ArrayList<>();

            ArrayList<Integer> cargas =
                    new ArrayList<>();

            // Recuperar encuestadores y cargas actuales
            try (PreparedStatement ps =
                    conexion.prepareStatement(sqlEncuestadores);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    identificaciones.add(
                            rs.getString("NUM_IDENTIFICACION"));

                    cargas.add(rs.getInt("TOTAL"));
                }
            }

            if (identificaciones.isEmpty()) {
                throw new IllegalStateException(
                        "No hay encuestadores registrados.");
            }

            int asignados = 0;

            // Distribuir según la menor carga
            try (PreparedStatement ps =
                    conexion.prepareStatement(sqlActualizar)) {

                for (int idHogar : hogares) {

                    int indiceMenor = 0;

                    for (int i = 1; i < cargas.size(); i++) {

                        if (cargas.get(i)
                                < cargas.get(indiceMenor)) {

                            indiceMenor = i;
                        }
                    }

                    ps.setString(
                            1,
                            identificaciones.get(indiceMenor));

                    ps.setInt(2, idHogar);

                    int filas = ps.executeUpdate();

                    if (filas != 1) {
                        throw new SQLException(
                                "No se pudo asignar el hogar "
                                + idHogar);
                    }

                    cargas.set(
                            indiceMenor,
                            cargas.get(indiceMenor) + 1);

                    asignados++;
                }
            }

            // Confirmar todas las asignaciones juntas
            conexion.commit();

            return asignados;

        } catch (SQLException | RuntimeException e) {

            try {
                conexion.rollback();
            } catch (SQLException ex) {
                e.addSuppressed(ex);
            }

            throw e;
        }

    } catch (SQLException e) {
    e.printStackTrace();

    throw new IllegalStateException(
            "Error al guardar el gasto diario en Oracle: "
            + e.getMessage(), e);
    }
}

}
