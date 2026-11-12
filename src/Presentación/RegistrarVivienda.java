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
import Negocio.ViviendaService;

public class RegistrarVivienda extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RegistrarVivienda.class.getName());

    /**
     * Creates new form RegistrarVivienda
     */
    private boolean cargandoUbicaciones = true;
    public RegistrarVivienda() {
       initComponents();

    cargarRegiones();

    cmbDepartamento.removeAllItems();
    cmbDepartamento.addItem("Seleccione departamento");

    cmbMunicipio.removeAllItems();
    cmbMunicipio.addItem("Seleccione municipio");

    cmbBarrio.removeAllItems();
    cmbBarrio.addItem("Seleccione barrio");

    cmbDepartamento.setEnabled(false);
    cmbMunicipio.setEnabled(false);
    cmbBarrio.setEnabled(false);

    cargandoUbicaciones = false;

    setLocationRelativeTo(null);
    }
    
    private void cargarRegiones() {
    cmbRegion.removeAllItems();
    cmbRegion.addItem("Seleccione región");

    String sql = "SELECT NOMBRE FROM REGION ORDER BY ID";

    try (Connection con = ConexionOracle.obtenerConexion();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            cmbRegion.addItem(rs.getString("NOMBRE"));
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Error al cargar las regiones: " + e.getMessage());
    }
}
private void cargarDepartamentos() {

    cmbDepartamento.removeAllItems();
    cmbDepartamento.addItem("Seleccione departamento");

    cmbMunicipio.removeAllItems();
    cmbMunicipio.addItem("Seleccione municipio");

    cmbBarrio.removeAllItems();
    cmbBarrio.addItem("Seleccione barrio");

    cmbDepartamento.setEnabled(false);
    cmbMunicipio.setEnabled(false);
    cmbBarrio.setEnabled(false);

    if (cmbRegion.getSelectedIndex() <= 0) {
        return;
    }

    String region = cmbRegion.getSelectedItem().toString();

    String sql =
        "SELECT d.NOMBRE "
        + "FROM DEPARTAMENTO d "
        + "JOIN REGION r ON d.ID_REGION = r.ID "
        + "WHERE r.NOMBRE = ? "
        + "ORDER BY d.NOMBRE";

    try (Connection con = ConexionOracle.obtenerConexion()) {

        if (con == null) {
            JOptionPane.showMessageDialog(this,
                "No se pudo conectar con Oracle.");
            return;
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, region);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    cmbDepartamento.addItem(
                        rs.getString("NOMBRE"));
                }
            }
        }

        cmbDepartamento.setEnabled(
            cmbDepartamento.getItemCount() > 1);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
            "Error al cargar departamentos:\n"
            + e.getMessage());
    }
}


private void cargarMunicipios() {

    cmbMunicipio.removeAllItems();
    cmbMunicipio.addItem("Seleccione municipio");

    cmbBarrio.removeAllItems();
    cmbBarrio.addItem("Seleccione barrio");

    if (cmbDepartamento.getSelectedIndex() <= 0) {
        return;
    }

    String departamento = cmbDepartamento.getSelectedItem().toString();

    String sql = "SELECT m.NOMBRE "
        + "FROM MUNICIPIO m "
        + "JOIN DEPARTAMENTO d ON m.ID_DEPARTAMENTO = d.ID "
        + "WHERE d.NOMBRE = ? "
        + "ORDER BY m.NOMBRE";

    try (Connection conexion = ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo conectar con Oracle.");
            return;
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, departamento);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    cmbMunicipio.addItem(rs.getString("NOMBRE"));
                }
            }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Error al cargar municipios:\n" + e.getMessage());
    }
}

private void cargarBarrios() {

    cmbBarrio.removeAllItems();
    cmbBarrio.addItem("Seleccione barrio");

    if (cmbMunicipio.getSelectedIndex() <= 0) {
        return;
    }

    String municipio = cmbMunicipio.getSelectedItem().toString();

    String sql = "SELECT b.NOMBRE "
            + "FROM BARRIO b "
            + "JOIN MUNICIPIO m ON b.ID_MUNICIPIO = m.ID "
            + "WHERE m.NOMBRE = ? "
            + "ORDER BY b.NOMBRE";

    try (Connection conexion = ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo conectar con Oracle.");
            return;
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, municipio);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    cmbBarrio.addItem(rs.getString("NOMBRE"));
                }
            }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Error al cargar barrios:\n" + e.getMessage());
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
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        cmbDepartamento = new javax.swing.JComboBox<>();
        cmbMunicipio = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        cmbBarrio = new javax.swing.JComboBox<>();
        txtDireccion = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        cmbRegion = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setBackground(new java.awt.Color(168, 221, 215));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("ENPH- Cuadernillo 2");

        jLabel8.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        jLabel8.setText("Administrador");

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

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setText("Registrar vivienda");

        jLabel11.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel11.setText("Departamento");

        jLabel12.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel12.setText("Municipio");

        cmbDepartamento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione departamento" }));
        cmbDepartamento.addActionListener(this::cmbDepartamentoActionPerformed);

        cmbMunicipio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione municipio" }));
        cmbMunicipio.addActionListener(this::cmbMunicipioActionPerformed);

        jLabel13.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel13.setText("Barrio");

        jLabel14.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel14.setText("Dirección de la vivienda");

        cmbBarrio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccion barrio" }));

        btnGuardar.setText("Guardar vivienda");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        jButton3.setText("Volver");
        jButton3.addActionListener(this::jButton3ActionPerformed);

        jLabel15.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel15.setText("Región");

        cmbRegion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione municipio" }));
        cmbRegion.addActionListener(this::cmbRegionActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDireccion, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cmbMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(21, 21, 21)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(cmbBarrio, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(421, 421, 421))
                        .addComponent(jButton3, javax.swing.GroupLayout.Alignment.LEADING))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbDepartamento, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(cmbDepartamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(cmbRegion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(cmbBarrio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(cmbMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(37, 37, 37)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLimpiar)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 3, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
    
    cmbRegion.setSelectedIndex(0);
    txtDireccion.setText("");

    txtDireccion.requestFocusInWindow();
     // TODO add your handling code here:
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        //Viviendas menu = new Viviendas();
        //menu.setLocationRelativeTo(null);
         //menu.setVisible(true);

        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
      // Obtener la dirección
      
    String direccion = txtDireccion.getText().trim();

    // Validar región, departamento, municipio y barrio
    if (cmbRegion.getSelectedIndex() <= 0
            || cmbDepartamento.getSelectedIndex() <= 0
            || cmbMunicipio.getSelectedIndex() <= 0
            || cmbBarrio.getSelectedIndex() <= 0) {

        JOptionPane.showMessageDialog(this,
                "Seleccione región, departamento, municipio y barrio.");
        return;
    }

    // Validar dirección
    if (direccion.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Ingrese la dirección de la vivienda.");
        return;
    }

    // Obtener municipio y barrio seleccionados
    String municipio = cmbMunicipio.getSelectedItem().toString();
    String barrio = cmbBarrio.getSelectedItem().toString();

    try (Connection conexion = ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo conectar con Oracle.");
            return;
        }

        // Registrar mediante la capa de negocio
        ViviendaService viviendaService = new ViviendaService();

        int idVivienda = viviendaService.registrarVivienda(
                conexion,
                direccion,
                barrio,
                municipio
        );

        JOptionPane.showMessageDialog(this,
                "Vivienda registrada correctamente.\n"
                + "Código de vivienda: " + idVivienda);

        // Limpiar el formulario después de guardar
cmbRegion.setSelectedIndex(0);
txtDireccion.setText("");

// Dejar lista la pantalla para registrar otra vivienda
JOptionPane.showMessageDialog(this,
        "Puede registrar una nueva vivienda.");

    } catch (IllegalArgumentException e) {

        JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Validación",
                JOptionPane.WARNING_MESSAGE);

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(this,
                "Error al guardar la vivienda:\n"
                + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void cmbDepartamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbDepartamentoActionPerformed
        // TODO add your handling code here:
        
    if (cargandoUbicaciones) {
     return;
 }

 cargandoUbicaciones = true;

 try {
     cmbMunicipio.removeAllItems();
     cmbMunicipio.addItem("Seleccione municipio");

     cmbBarrio.removeAllItems();
     cmbBarrio.addItem("Seleccione barrio");

     cmbMunicipio.setEnabled(false);
     cmbBarrio.setEnabled(false);

     if (cmbDepartamento.getSelectedIndex() > 0) {

         cargarMunicipios();

         cmbMunicipio.setEnabled(
             cmbMunicipio.getItemCount() > 1
         );
     }

 } finally {
     cargandoUbicaciones = false;
 }
    }//GEN-LAST:event_cmbDepartamentoActionPerformed

    private void cmbMunicipioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMunicipioActionPerformed
        // TODO add your handling code here:
        
        if (cargandoUbicaciones) {
        return;
    }

    cargandoUbicaciones = true;

    try {
        cmbBarrio.removeAllItems();
        cmbBarrio.addItem("Seleccione barrio");

        cmbBarrio.setEnabled(false);

        if (cmbMunicipio.getSelectedIndex() > 0) {

            cargarBarrios();

            cmbBarrio.setEnabled(
                cmbBarrio.getItemCount() > 1
            );
        }

    } finally {
        cargandoUbicaciones = false;
    }
    }//GEN-LAST:event_cmbMunicipioActionPerformed

    private void cmbRegionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbRegionActionPerformed
        // TODO add your handling code here:
        if (cargandoUbicaciones) {
    return;
}

cargandoUbicaciones = true;

try {
    cargarDepartamentos();
} finally {
    cargandoUbicaciones = false;
}
    }//GEN-LAST:event_cmbRegionActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new RegistrarVivienda().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JComboBox<String> cmbBarrio;
    private javax.swing.JComboBox<String> cmbDepartamento;
    private javax.swing.JComboBox<String> cmbMunicipio;
    private javax.swing.JComboBox<String> cmbRegion;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextField txtDireccion;
    // End of variables declaration//GEN-END:variables
}
