/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author thain
 */
public class Encuesta {
    private int id;
    private Hogar hogar;

    public Encuesta(Hogar hogar) {
        this.hogar = hogar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Hogar getHogar() {
        return hogar;
    }
}
