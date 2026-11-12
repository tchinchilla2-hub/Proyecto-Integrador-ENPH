/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Negocio;
import Datos.GastoDiarioDAO;
import Modelo.GastoDiario;
import java.sql.SQLException;

/**
 *
 * @author thain
 */
public class GastoDiarioService {
    public void guardar(GastoDiario gasto, String codigoHogar)
            throws SQLException {

        if (gasto == null) {
            throw new SQLException("El gasto no puede estar vacío");
        }

        GastoDiarioDAO dao = new GastoDiarioDAO();

        dao.guardar(gasto, codigoHogar);
    }
}
