/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;
import java.time.LocalDate;
/**
 *
 * @author thain
 */
public class Visita {
    private int id;
    private int numeroVisita;
    private LocalDate fechaVisita;
    private String horaInicio;
    private Encuesta encuesta;
    private String horaFin;
    private String estadoVisita;

    public Visita(int numeroVisita,
                  LocalDate fechaVisita,
                  String horaInicio,
                  Encuesta encuesta) {

        this.numeroVisita = numeroVisita;
        this.fechaVisita = fechaVisita;
        this.horaInicio = horaInicio;
        this.encuesta = encuesta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumeroVisita() {
        return numeroVisita;
    }

    public LocalDate getFechaVisita() {
        return fechaVisita;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public Encuesta getEncuesta() {
        return encuesta;
    }
    public String getHoraFin() {
    return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public String getEstadoVisita() {
        return estadoVisita;
    }

    public void setEstadoVisita(String estadoVisita) {
        this.estadoVisita = estadoVisita;
    }
}
