/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author thain
 */

import java.util.ArrayList;
public class RegistroGastos {

    // Lista donde se almacenarán los gastos temporalmente
    public static ArrayList<GastoDiario> gastos = new ArrayList<>();

    // Método para guardar un gasto
    public static void guardar(GastoDiario gasto) {
        gastos.add(gasto);
    }
    
    public static java.util.Set<java.time.LocalDate> diasConfirmados =
        new java.util.HashSet<>();
    
    public static void confirmarDia(java.time.LocalDate fecha) {
    diasConfirmados.add(fecha);
    }

public static boolean estaConfirmado(java.time.LocalDate fecha) {
    return diasConfirmados.contains(fecha);
    }
}

