/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author thain
 */
// Esta clase respresenta lso datos de un gasto. Mas adelante la podremos utilizar para insertar la informacion en Oracle
public class GastoDiario {
    public String nombreAlimento;
    public double cantidad;
    public String unidadMedida;
    public double contenidoUnidad;
    public String unidadContenido;

    public String formaAdquisicion;
    public String frecuenciaCompra;
    public String lugarCompra;
    public String tipoGasto;
    public double valorPagado;

    public java.time.LocalDate fecha;

    public GastoDiario() {
        fecha = java.time.LocalDate.now();
    }
}
