/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package software;
import Presentación.AccesoOficina;
import javax.swing.SwingUtilities;

/**
 *
 * @author thain
 */
public class Main {
      public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            AccesoOficina ventana = new AccesoOficina();

            ventana.setLocationRelativeTo(null);

            ventana.setVisible(true);
        });
    }
}
