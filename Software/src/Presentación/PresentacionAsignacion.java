package Presentación;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author thain
 */

import Datos.ConexionOracle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class PresentacionAsignacion extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PresentacionAsignacion.class.getName());

    /**
     * Creates new form AsignarHogar
     */
    public PresentacionAsignacion() {
      initComponents();

    // Permitir seleccionar una sola fila
    tblHogaresDisponibles.setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION
    );

    tblEncuestadores.setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION
    );

    // Cargar los datos registrados en Oracle
    cargarHogaresDisponibles();
    cargarEncuestadores();
    }
    
    // Métodos del diagrama de Astah; el botón Distribuir sigue utilizando Main.
    public Modelo.Hogar[] capturarAsignacion(
            Modelo.Hogar[] pHogares, Modelo.Encuestador[] pEncuestadores) {
        if (pHogares == null || pEncuestadores == null || pEncuestadores.length == 0) {
            throw new IllegalArgumentException("Debe haber hogares y encuestadores disponibles.");
        }
        int[] cargas = new int[pEncuestadores.length];
        for (Modelo.Hogar hogar : pHogares) {
            if (hogar == null) continue;
            Modelo.Encuestador actual = hogar.getEncuestador();
            if (actual != null) {
                for (int i = 0; i < pEncuestadores.length; i++) {
                    if (actual.getIdentificacion().equals(pEncuestadores[i].getIdentificacion())) {
                        cargas[i]++;
                        break;
                    }
                }
            }
        }
        for (Modelo.Hogar hogar : pHogares) {
            if (hogar == null || hogar.getEncuestador() != null) continue;
            int elegido = 0;
            for (int i = 1; i < cargas.length; i++) {
                if (cargas[i] < cargas[elegido]) elegido = i;
            }
            hogar.setEncuestador(pEncuestadores[elegido]);
            cargas[elegido]++;
        }
        return pHogares;
    }

    public void presentarAsignacion(Modelo.Hogar[] pHogares,
            Modelo.Encuestador[] pEncuestadores) {
        // Se muestran los datos actuales de Oracle, con sus ubicaciones y cargas.
        cargarHogaresDisponibles();
        cargarEncuestadores();
    }

    public void presentarResultadoAsignacion(int hogaresAsignados) {
        if (hogaresAsignados < 0) {
            throw new IllegalArgumentException("Cantidad inválida de hogares asignados.");
        }
        JOptionPane.showMessageDialog(this,
                hogaresAsignados + " hogar(es) asignado(s) correctamente.",
                "ENPH - Asignación", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cargarHogaresDisponibles() {
   DefaultTableModel modelo =
            (DefaultTableModel) tblHogaresDisponibles.getModel();

    modelo.setColumnIdentifiers(new Object[]{
        "Código", "Ubicación", "Responsable",
        "Teléfono", "Primera visita"
    });

    modelo.setRowCount(0);

    String sql =
        "SELECT h.ID, v.DIRECCION, "
        + "b.NOMBRE AS BARRIO, "
        + "j.NOMBRE, j.APELLIDO, j.TELEFONO "
        + "FROM HOGAR h "
        + "JOIN VIVIENDA v ON h.ID_VIVIENDA = v.ID "
        + "JOIN BARRIO b ON v.ID_BARRIO = b.ID "
        + "JOIN JEFE_HOGAR j ON j.ID_HOGAR = h.ID "
        + "WHERE h.NUM_IDENTIFICACION_ENCUESTADOR IS NULL "
        + "ORDER BY h.ID";

    try (Connection conexion = ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo conectar con Oracle.");
            return;
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                int idHogar = rs.getInt("ID");

                modelo.addRow(new Object[]{
                    "HG-" + String.format("%03d", idHogar),
                    rs.getString("BARRIO") + " - "
                            + rs.getString("DIRECCION"),
                    rs.getString("NOMBRE") + " "
                            + rs.getString("APELLIDO"),
                    rs.getString("TELEFONO"),
                    "Pendiente"
                });
            }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Error al cargar hogares:\n" + e.getMessage());
    }
}
    
private void cargarEncuestadores() {
 
    DefaultTableModel modelo =
            (DefaultTableModel) tblEncuestadores.getModel();

    modelo.setColumnIdentifiers(new Object[]{
        "Identificación", "Nombre",
        "Teléfono", "Hogares asignados"
    });

    modelo.setRowCount(0);

    String sql =
        "SELECT e.NUM_IDENTIFICACION, "
        + "e.NOMBRE, e.APELLIDO, e.TELEFONO, "
        + "COUNT(h.ID) AS HOGARES_ASIGNADOS "
        + "FROM ENCUESTADOR e "
        + "LEFT JOIN HOGAR h "
        + "ON h.NUM_IDENTIFICACION_ENCUESTADOR = "
        + "e.NUM_IDENTIFICACION "
        + "GROUP BY e.NUM_IDENTIFICACION, "
        + "e.NOMBRE, e.APELLIDO, e.TELEFONO "
        + "ORDER BY e.NOMBRE, e.APELLIDO";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo conectar con Oracle.");
            return;
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                String nombreCompleto =
                        rs.getString("NOMBRE") + " "
                        + rs.getString("APELLIDO");

                modelo.addRow(new Object[]{
                    rs.getString("NUM_IDENTIFICACION"),
                    nombreCompleto,
                    rs.getString("TELEFONO"),
                    rs.getInt("HOGARES_ASIGNADOS")
                });
            }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Error al cargar encuestadores:\n"
                + e.getMessage());
    }
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        scrollHogares = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblHogaresDisponibles = new javax.swing.JTable();
        scrollEncuestadores = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblEncuestadores = new javax.swing.JTable();
        Volver = new javax.swing.JButton();
        btnDistribuir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setBackground(new java.awt.Color(168, 221, 215));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("ENPH- Cuadernillo 2");

        jLabel8.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        jLabel8.setText("Asignación de hogares");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addGap(17, 17, 17))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Asignar hogar");

        jLabel9.setFont(new java.awt.Font("Cambria", 0, 16)); // NOI18N
        jLabel9.setText("Seleccione un hogar disponible y el encuestador al que será asignado.");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("Hogares disponibles");

        tblHogaresDisponibles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Código", "Ubicación", "Responsable", "Teléfono", "Primera visita"
            }
        ));
        tblHogaresDisponibles.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                tblHogaresDisponiblesAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        jScrollPane1.setViewportView(tblHogaresDisponibles);

        javax.swing.GroupLayout scrollHogaresLayout = new javax.swing.GroupLayout(scrollHogares);
        scrollHogares.setLayout(scrollHogaresLayout);
        scrollHogaresLayout.setHorizontalGroup(
            scrollHogaresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scrollHogaresLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(scrollHogaresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 509, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        scrollHogaresLayout.setVerticalGroup(
            scrollHogaresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scrollHogaresLayout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setText("Encuestadores");

        tblEncuestadores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Código", "Nombre", "Identificación", "Hogares asignados"
            }
        ));
        jScrollPane2.setViewportView(tblEncuestadores);

        javax.swing.GroupLayout scrollEncuestadoresLayout = new javax.swing.GroupLayout(scrollEncuestadores);
        scrollEncuestadores.setLayout(scrollEncuestadoresLayout);
        scrollEncuestadoresLayout.setHorizontalGroup(
            scrollEncuestadoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scrollEncuestadoresLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(scrollEncuestadoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 593, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        scrollEncuestadoresLayout.setVerticalGroup(
            scrollEncuestadoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scrollEncuestadoresLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        Volver.setText("Volver");
        Volver.addActionListener(this::VolverActionPerformed);

        btnDistribuir.setText("Distribuir automáticamente");
        btnDistribuir.addActionListener(this::btnDistribuirActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scrollHogares, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollEncuestadores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(77, 77, 77))
            .addGroup(layout.createSequentialGroup()
                .addComponent(Volver, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(446, 446, 446)
                .addComponent(btnDistribuir, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Volver, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollHogares, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scrollEncuestadores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDistribuir, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblHogaresDisponiblesAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_tblHogaresDisponiblesAncestorAdded
        // TODO add your handling code here:
        
    }//GEN-LAST:event_tblHogaresDisponiblesAncestorAdded

    private void VolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VolverActionPerformed
        // TODO add your handling code here:
        
    MenuPrincipalOficina ventana =
         new MenuPrincipalOficina();

 ventana.setLocationRelativeTo(null);
 ventana.setVisible(true);

 this.dispose();

    }//GEN-LAST:event_VolverActionPerformed

    private void btnDistribuirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDistribuirActionPerformed
        // Siempre refrescar los datos antes de distribuir: evita mostrar hogares
        // que ya fueron asignados en otra ventana o sesión.
        btnDistribuir.setEnabled(false);
        try {
            cargarHogaresDisponibles();
            cargarEncuestadores();

            int pendientesAntes = tblHogaresDisponibles.getRowCount();
            if (pendientesAntes == 0) {
                JOptionPane.showMessageDialog(this,
                        "No hay hogares pendientes de asignación en Oracle.",
                        "Sin hogares pendientes",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Mantener la distribución equilibrada existente en Main y Persistencia.
            software.Main controlador = new software.Main();
            int asignados = controlador.asignarHogares();

            // Leer otra vez la base de datos tras confirmar la operación.
            cargarHogaresDisponibles();
            cargarEncuestadores();

            if (asignados == 0) {
                JOptionPane.showMessageDialog(this,
                        "Los hogares pendientes cambiaron antes de asignarse. "
                        + "Se actualizaron las tablas con los datos de Oracle.",
                        "Sin asignaciones nuevas",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "Asignación completa\n\n"
                    + "Los hogares se distribuyeron correctamente "
                    + "entre los encuestadores.\n\n"
                    + asignados + " hogar(es) asignado(s) correctamente.",
                    "ENPH - Asignación de hogares",
                    JOptionPane.INFORMATION_MESSAGE);

            ResultadosAsignacion ventana = new ResultadosAsignacion();
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
            this.dispose();

        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo completar la asignación:\n" + ex.getMessage(),
                    "Error de asignación",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            btnDistribuir.setEnabled(true);
        }
    }//GEN-LAST:event_btnDistribuirActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new PresentacionAsignacion().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Volver;
    private javax.swing.JButton btnDistribuir;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel scrollEncuestadores;
    private javax.swing.JPanel scrollHogares;
    private javax.swing.JTable tblEncuestadores;
    private javax.swing.JTable tblHogaresDisponibles;
    // End of variables declaration//GEN-END:variables
}
