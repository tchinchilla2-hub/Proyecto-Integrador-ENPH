/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author thain
 */
public class Vivienda {
    
   private int id;
    private String direccion;
    private Barrio barrio;

    public Vivienda(String direccion, Barrio barrio) {
        this.direccion = direccion;
        this.barrio = barrio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public Barrio getBarrio() {
        return barrio;
    }
}
