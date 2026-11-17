package Presentación;

import Modelo.Encuesta;
import Modelo.Hogar;
import javax.swing.JOptionPane;

/** Presentación del incremento 03.1 definida en el diagrama Astah. */
public class PresentacionEncuesta {

    public PresentacionEncuesta() { }

    /** Selecciona un hogar existente para crear una encuesta, sin modificar Oracle. */
    public Encuesta capturarEncuesta(Hogar[] pHogares) {
        if (pHogares == null || pHogares.length == 0) {
            throw new IllegalArgumentException("No hay hogares disponibles.");
        }
        String[] opciones = new String[pHogares.length];
        for (int i = 0; i < pHogares.length; i++) {
            if (pHogares[i] == null || pHogares[i].getId() <= 0) {
                throw new IllegalArgumentException("Hay un hogar inválido en el listado.");
            }
            opciones[i] = "HG-" + String.format("%03d", pHogares[i].getId());
        }
        Object seleccion = JOptionPane.showInputDialog(null,
                "Seleccione el hogar de la encuesta:", "Crear encuesta",
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (seleccion == null) return null;
        for (int i = 0; i < opciones.length; i++) {
            if (opciones[i].equals(seleccion)) return new Encuesta(pHogares[i]);
        }
        return null;
    }

    /** Presenta la encuesta que Main ya guardó mediante Persistencia. */
    public void presentarEncuesta(Encuesta pEncuesta) {
        if (pEncuesta == null || pEncuesta.getId() <= 0) {
            throw new IllegalArgumentException("La encuesta no está registrada.");
        }
        JOptionPane.showMessageDialog(null,
                "Encuesta registrada: " + pEncuesta.getId(),
                "ENPH - Encuesta", JOptionPane.INFORMATION_MESSAGE);
    }
}
