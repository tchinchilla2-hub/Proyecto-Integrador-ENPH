/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author thain
 */
public class Hogar {
    private int id;
    private int cantidadPersonas;
    private Vivienda vivienda;
    private Encuestador encuestador;

    public Hogar(int cantidadPersonas, Vivienda vivienda) {
        this.cantidadPersonas = cantidadPersonas;
        this.vivienda = vivienda;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    public Vivienda getVivienda() {
        return vivienda;
    }

    public Encuestador getEncuestador() {
        return encuestador;
    }

    public void setEncuestador(Encuestador encuestador) {
        this.encuestador = encuestador;
    }
}
