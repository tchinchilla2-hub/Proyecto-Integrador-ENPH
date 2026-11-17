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
import Modelo.Region;
import Datos.Persistencia;
import Modelo.Departamento;
import Modelo.Municipio;
import Modelo.Barrio;
import Modelo.Vivienda;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;



public class PresentacionVivienda extends javax.swing.JFrame {
    // Incremento 2: presenta las viviendas recuperadas sin modificar Oracle.
    public void presentarViviendas(Modelo.Vivienda[] viviendas) {
        if (viviendas == null || viviendas.length == 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "No hay viviendas registradas.");
            return;
        }
        StringBuilder listado = new StringBuilder();
        for (Modelo.Vivienda vivienda : viviendas) {
            if (vivienda != null) {
                listado.append("Vivienda ").append(vivienda.getId())
                        .append(": ").append(vivienda.getDireccion()).append('\n');
            }
        }
        javax.swing.JTextArea area = new javax.swing.JTextArea(listado.toString(), 12, 45);
        area.setEditable(false);
        javax.swing.JOptionPane.showMessageDialog(this, new javax.swing.JScrollPane(area),
                "Viviendas registradas", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PresentacionVivienda.class.getName());

    /**
     * Creates new form RegistrarVivienda
     */
    private boolean cargandoUbicaciones = true;
    public PresentacionVivienda() {
       initComponents();
       actualizarFechaActual();

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
    
    private void actualizarFechaActual() {
    LocalDate fechaActual = LocalDate.now();

    DateTimeFormatter formato = DateTimeFormatter.ofPattern(
        "dd 'de' MMMM 'de' yyyy",
        new Locale("es", "CO")
    );

    lblFecha.setText(fechaActual.format(formato));
}
    
    private void cargarRegiones() {
     cmbRegion.removeAllItems();
    cmbRegion.addItem("Seleccione región");

    Persistencia persistencia = new Persistencia();

    regiones = persistencia.recuperarRegiones();

    for (Region region : regiones) {
        cmbRegion.addItem(region.getNombre());
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

    departamentos = new Departamento[0];
    municipios = new Municipio[0];
    barrios = new Barrio[0];

    int indiceRegion = cmbRegion.getSelectedIndex();

    if (indiceRegion <= 0) {
        return;
    }

    Region regionSeleccionada = regiones[indiceRegion - 1];

    Persistencia persistencia = new Persistencia();

    departamentos = persistencia.recuperarDepartamentos(
            regionSeleccionada
    );

    for (Departamento departamento : departamentos) {
        cmbDepartamento.addItem(departamento.getNombre());
    }

    cmbDepartamento.setEnabled(
            cmbDepartamento.getItemCount() > 1
    );
}

private Region[] regiones;
private Departamento[] departamentos;

private Municipio[] municipios;

private void cargarMunicipios() {

    cmbMunicipio.removeAllItems();
    cmbMunicipio.addItem("Seleccione municipio");

    cmbBarrio.removeAllItems();
    cmbBarrio.addItem("Seleccione barrio");

    cmbMunicipio.setEnabled(false);
    cmbBarrio.setEnabled(false);

    int indice = cmbDepartamento.getSelectedIndex();

    if (indice <= 0) {
        return;
    }

    Departamento departamentoSeleccionado =
            departamentos[indice - 1];

    Persistencia persistencia = new Persistencia();

    municipios = persistencia.recuperarMunicipios(
            departamentoSeleccionado
    );

    for (Municipio municipio : municipios) {
        cmbMunicipio.addItem(municipio.getNombre());
    }

    cmbMunicipio.setEnabled(
            cmbMunicipio.getItemCount() > 1
    );
}

private Barrio[] barrios;
private void cargarBarrios() {

    cmbBarrio.removeAllItems();
    cmbBarrio.addItem("Seleccione barrio");

    cmbBarrio.setEnabled(false);

    int indice = cmbMunicipio.getSelectedIndex();

    if (indice <= 0) {
        return;
    }

    Municipio municipioSeleccionado =
            municipios[indice - 1];

    Persistencia persistencia = new Persistencia();

    barrios = persistencia.recuperarBarrios(
            municipioSeleccionado
    );

    for (Barrio barrio : barrios) {
        cmbBarrio.addItem(barrio.getNombre());
    }

    cmbBarrio.setEnabled(
            cmbBarrio.getItemCount() > 1
    );
    
}

// Métodos de captura previstos en el diagrama de clases Astah.
public Region capturarRegion(Region[] pRegiones) {
    int indice = cmbRegion.getSelectedIndex() - 1;
    if (pRegiones == null || indice < 0 || indice >= pRegiones.length) {
        throw new IllegalArgumentException("Seleccione una región válida.");
    }
    return pRegiones[indice];
}

public Departamento capturarDepartamento(Departamento[] pDepartamentos) {
    int indice = cmbDepartamento.getSelectedIndex() - 1;
    if (pDepartamentos == null || indice < 0 || indice >= pDepartamentos.length) {
        throw new IllegalArgumentException("Seleccione un departamento válido.");
    }
    return pDepartamentos[indice];
}

public Municipio capturarMunicipio(Municipio[] pMunicipios) {
    int indice = cmbMunicipio.getSelectedIndex() - 1;
    if (pMunicipios == null || indice < 0 || indice >= pMunicipios.length) {
        throw new IllegalArgumentException("Seleccione un municipio válido.");
    }
    return pMunicipios[indice];
}

public Barrio capturarBarrio(Barrio[] pBarrios) {
    int indice = cmbBarrio.getSelectedIndex() - 1;
    if (pBarrios == null || indice < 0 || indice >= pBarrios.length) {
        throw new IllegalArgumentException("Seleccione un barrio válido.");
    }
    return pBarrios[indice];
}

public Barrio capturarBarrio() {

    int indice = cmbBarrio.getSelectedIndex() - 1;

    if (barrios == null
            || indice < 0
            || indice >= barrios.length) {

        throw new IllegalArgumentException(
                "Debe seleccionar un barrio válido."
        );
    }

    return barrios[indice];
}
public Vivienda capturarVivienda(Barrio pBarrio) {

    String direccion = txtDireccion.getText().trim();

    if (direccion.isEmpty()) {
        throw new IllegalArgumentException(
                "Debe ingresar la dirección de la vivienda.");
    }

    if (direccion.length() > 150) {
        throw new IllegalArgumentException(
                "La dirección no puede superar 150 caracteres.");
    }

    if (pBarrio == null) {
        throw new IllegalArgumentException(
                "Debe seleccionar un barrio.");
    }

    Vivienda vivienda =
            new Vivienda(direccion, pBarrio);

    return vivienda;
}

public void presentarVivienda(Vivienda pVivienda) {

    javax.swing.JOptionPane.showMessageDialog(
            this,
            "Vivienda registrada correctamente.\n"
            + "Código de vivienda: " + pVivienda.getId()
    );

    cmbRegion.setSelectedIndex(0);
    txtDireccion.setText("");
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
        lblFecha = new javax.swing.JLabel();

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

        lblFecha.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
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
                                    .addGap(421, 421, 421)))
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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19))))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 20, Short.MAX_VALUE))
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

    // Recuperar el objeto Barrio seleccionado
    int indiceBarrio = cmbBarrio.getSelectedIndex() - 1;

    if (barrios == null
            || indiceBarrio < 0
            || indiceBarrio >= barrios.length) {

        JOptionPane.showMessageDialog(this,
                "No se pudo identificar el barrio seleccionado.");
        return;
    }

    Barrio barrioSeleccionado = barrios[indiceBarrio];

     try {

        // Validar las ubicaciones seleccionadas
        if (cmbRegion.getSelectedIndex() <= 0
                || cmbDepartamento.getSelectedIndex() <= 0
                || cmbMunicipio.getSelectedIndex() <= 0
                || cmbBarrio.getSelectedIndex() <= 0) {

            JOptionPane.showMessageDialog(this,
                    "Seleccione región, departamento, "
                    + "municipio y barrio.");
            return;
        }

        // Main coordina la captura, el guardado
        // y la presentación del resultado.
        software.Main controlador =
                new software.Main(this);

        controlador.crearVivienda();

    } catch (IllegalArgumentException
            | IllegalStateException e) {

        JOptionPane.showMessageDialog(this,
                "Error al registrar la vivienda:\n"
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
        java.awt.EventQueue.invokeLater(() -> new PresentacionVivienda().setVisible(true));
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
    private javax.swing.JLabel lblFecha;
    private javax.swing.JTextField txtDireccion;
    // End of variables declaration//GEN-END:variables
}
