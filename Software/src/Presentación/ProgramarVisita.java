/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Presentación;
import Datos.ConexionOracle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author thain
 */
public class ProgramarVisita extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ProgramarVisita.class.getName());
    private int filaSeleccionada = -1;
    /**
     * Creates new form ProgramarVisita
     */
    public ProgramarVisita() {
        initComponents();
        
        
             setLocationRelativeTo(null);

    jTextPane1.setText(
        java.time.LocalDate.now().format(
            java.time.format.DateTimeFormatter
                .ofPattern("dd 'de' MMMM 'de' yyyy",
                    new java.util.Locale("es", "CO"))
        )
    );

    javax.swing.table.DefaultTableModel modelo =
        new javax.swing.table.DefaultTableModel(
            new Object[]{
                "Código hogar",
                "Ubicación",
                "Fecha inicio",
                "Acción"
            }, 0) {

        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    tblHogares.setModel(modelo);
    tblHogares.setRowHeight(58);
    tblHogares.setSelectionMode(
        javax.swing.ListSelectionModel.SINGLE_SELECTION
    );

    // Cargar hogares registrados
    cargarHogares();

    // Configurar botones Programar
    configurarBotones();
    }
    
private void configurarBotones() {

    // Apariencia del botón dentro de la tabla
    tblHogares.getColumnModel()
        .getColumn(3)
        .setCellRenderer((tabla, valor, seleccionado,
                enfocado, fila, columna) -> {

            javax.swing.JButton boton =
                new javax.swing.JButton();

            boolean programado =
                !"Sin programar".equals(
                    tabla.getValueAt(fila, 2));

            boton.setText(
                programado ? "Programada" : "Programar"
            );

            boton.setBackground(
                new java.awt.Color(228, 250, 247)
            );

            boton.setForeground(
                new java.awt.Color(0, 130, 125)
            );

            boton.setFont(
                new java.awt.Font("Arial", 1, 12)
            );

            boton.setFocusPainted(false);
            boton.setEnabled(!programado);

            return boton;
        });

    // Detectar clic en la columna Acción
    tblHogares.addMouseListener(
        new java.awt.event.MouseAdapter() {

        @Override
        public void mouseClicked(
                java.awt.event.MouseEvent evt) {

            int filaVista =
                tblHogares.rowAtPoint(evt.getPoint());

            int columnaVista =
                tblHogares.columnAtPoint(evt.getPoint());

            if (filaVista < 0 || columnaVista < 0) {
                return;
            }

            int columnaModelo =
                tblHogares.convertColumnIndexToModel(
                    columnaVista);

            if (columnaModelo != 3) {
                return;
            }

            int filaModelo =
                tblHogares.convertRowIndexToModel(
                    filaVista);

            Object fechaActual =
                tblHogares.getModel().getValueAt(
                    filaModelo, 2);

            if (!"Sin programar".equals(fechaActual)) {
                return;
            }

            abrirProgramacion(filaModelo);
        }
    });
}

private void abrirProgramacion(int fila) {

    filaSeleccionada = fila;

    // Mostrar la fecha actual como valor inicial
    jTextPane1.setText(
        java.time.LocalDate.now().format(
            java.time.format.DateTimeFormatter
                .ofPattern("dd/MM/yyyy")
        )
    );

    // Configurar el diálogo existente
    jDialog1.setTitle("Primera visita");
    jDialog1.setModal(true);
    jDialog1.pack();
    jDialog1.setLocationRelativeTo(this);

    // Mostrar ventana emergente
    jDialog1.setVisible(true);
}

    private void cargarHogares() {

    DefaultTableModel modelo =
            (DefaultTableModel) tblHogares.getModel();

    modelo.setRowCount(0);

String sql =
    "SELECT h.ID, b.NOMBRE AS BARRIO, "
    + "v.DIRECCION, "
    + "(SELECT TO_CHAR(MIN(vi.FECHA_HORA_INICIO), "
    + "'DD/MM/YYYY') "
    + "FROM ENCUESTA e "
    + "JOIN VISITA vi ON vi.ID_ENCUESTA = e.ID "
    + "WHERE e.ID_HOGAR = h.ID) AS FECHA_VISITA "
    + "FROM HOGAR h "
    + "JOIN VIVIENDA v ON h.ID_VIVIENDA = v.ID "
    + "JOIN BARRIO b ON v.ID_BARRIO = b.ID "
    + "ORDER BY h.ID";

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

                int idHogar = rs.getInt("ID");
                String barrio = rs.getString("BARRIO");
                String direccion = rs.getString("DIRECCION");

                String codigo = "HG-"
                        + String.format("%03d", idHogar);
                String fechaGuardada = rs.getString("FECHA_VISITA");

                String estadoFecha = fechaGuardada == null
                        ? "Sin programar"
                        : fechaGuardada;

                String ubicacion = barrio + " - " + direccion;

                modelo.addRow(new Object[]{
                codigo,
                ubicacion,
                estadoFecha,
                fechaGuardada == null ? "Programar" : "Programada"
            });
            }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Error al cargar hogares:\n"
                + e.getMessage());
    }
}
    
private void guardarVisitaOracle(int idHogar,
        java.time.LocalDate fecha) throws SQLException {

    String sql =
        "INSERT INTO VISITA "
        + "(ID, FECHA_HORA_INICIO, FECHA_HORA_FIN, ID_ENCUESTA) "
        + "SELECT SEQ_VISITA.NEXTVAL, ?, NULL, e.ID "
        + "FROM ENCUESTA e "
        + "WHERE e.ID_HOGAR = ? "
        + "AND e.FECHA_CIERRE IS NULL "
        + "AND NOT EXISTS ("
        + " SELECT 1 FROM VISITA v "
        + " WHERE v.ID_ENCUESTA = e.ID"
        + ")";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new SQLException(
                "No se pudo conectar con Oracle.");
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(fecha));
            ps.setInt(2, idHogar);

            int filas = ps.executeUpdate();

            if (filas != 1) {
                throw new SQLException(
                    "No se programó la visita. "
                    + "Compruebe que el hogar tenga "
                    + "una encuesta abierta y "
                    + "que no tenga una visita registrada.");
            }
        }
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

        jDialog1 = new javax.swing.JDialog();
        spnFechaVisita = new javax.swing.JPanel();
        lblPrimeraVisita = new javax.swing.JLabel();
        lblFechaVisita = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        btnCancelarVisita = new javax.swing.JButton();
        btnProgramarVisita = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblHogares = new javax.swing.JTable();
        lblFecha = new javax.swing.JLabel();

        lblPrimeraVisita.setFont(new java.awt.Font("Segoe UI Emoji", 1, 24)); // NOI18N
        lblPrimeraVisita.setText("Primer Visita");

        lblFechaVisita.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblFechaVisita.setText("Fecha de la visita");

        jScrollPane2.setViewportView(jTextPane1);

        btnCancelarVisita.setText("Cancelar");
        btnCancelarVisita.addActionListener(this::btnCancelarVisitaActionPerformed);

        btnProgramarVisita.setText("Programar visita ");
        btnProgramarVisita.addActionListener(this::btnProgramarVisitaActionPerformed);

        javax.swing.GroupLayout spnFechaVisitaLayout = new javax.swing.GroupLayout(spnFechaVisita);
        spnFechaVisita.setLayout(spnFechaVisitaLayout);
        spnFechaVisitaLayout.setHorizontalGroup(
            spnFechaVisitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spnFechaVisitaLayout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addGroup(spnFechaVisitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPrimeraVisita, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(spnFechaVisitaLayout.createSequentialGroup()
                        .addComponent(btnCancelarVisita, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnProgramarVisita, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(spnFechaVisitaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(spnFechaVisitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFechaVisita, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        spnFechaVisitaLayout.setVerticalGroup(
            spnFechaVisitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spnFechaVisitaLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(lblPrimeraVisita)
                .addGap(18, 18, 18)
                .addComponent(lblFechaVisita)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(spnFechaVisitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnProgramarVisita, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelarVisita, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spnFechaVisita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spnFechaVisita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton3.setText("Volver");
        jButton3.addActionListener(this::jButton3ActionPerformed);

        jPanel3.setBackground(new java.awt.Color(168, 221, 215));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("ENPH- Cuadernillo 2");

        jLabel10.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        jLabel10.setText("Administrador");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Programar primera visita");

        tblHogares.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Código hogar", "Ubicación", "Fecha Inicio", "Acción"
            }
        ));
        jScrollPane1.setViewportView(tblHogares);

        lblFecha.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 717, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(26, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        MenuPrincipalOficina menu = new MenuPrincipalOficina();
        menu.setLocationRelativeTo(null);
        menu.setVisible(true);

        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void btnProgramarVisitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProgramarVisitaActionPerformed
        // TODO add your handling code here:

    if (filaSeleccionada < 0) {
    JOptionPane.showMessageDialog(
        jDialog1, "Seleccione un hogar.");
    return;
}

// Obtener fecha del campo actual
String fechaTexto = jTextPane1.getText().trim();

java.time.LocalDate fecha;

try {
    java.time.format.DateTimeFormatter formato =
        java.time.format.DateTimeFormatter
            .ofPattern("dd/MM/uuuu")
            .withResolverStyle(
                java.time.format.ResolverStyle.STRICT);

    fecha = java.time.LocalDate.parse(
        fechaTexto, formato);

} catch (java.time.format.DateTimeParseException e) {
    JOptionPane.showMessageDialog(
        jDialog1,
        "Ingrese una fecha válida: dd/MM/yyyy");
    return;
}

if (fecha.isBefore(java.time.LocalDate.now())) {
    JOptionPane.showMessageDialog(
        jDialog1,
        "La fecha no puede ser anterior a hoy.");
    return;
}

// Obtener el ID real del hogar
String codigoHogar = tblHogares.getModel()
    .getValueAt(filaSeleccionada, 0).toString();

int idHogar;

try {
    idHogar = Integer.parseInt(
        codigoHogar.replace("HG-", "").trim());

} catch (NumberFormatException e) {
    JOptionPane.showMessageDialog(
        jDialog1, "Código de hogar inválido.");
    return;
}

// Guardar en Oracle antes de actualizar la pantalla
try {
    guardarVisitaOracle(idHogar, fecha);

    tblHogares.getModel().setValueAt(
        fechaTexto, filaSeleccionada, 2);

    tblHogares.repaint();

    filaSeleccionada = -1;
    jDialog1.dispose();

    JOptionPane.showMessageDialog(
        this,
        "Primera visita guardada correctamente en Oracle.");

} catch (SQLException e) {
    JOptionPane.showMessageDialog(
        jDialog1,
        "No se pudo guardar la visita:\n"
        + e.getMessage());
}
    }//GEN-LAST:event_btnProgramarVisitaActionPerformed

    private void btnCancelarVisitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarVisitaActionPerformed
        // TODO add your handling code here:
        filaSeleccionada = -1;
    jDialog1.dispose();
    }//GEN-LAST:event_btnCancelarVisitaActionPerformed

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
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new ProgramarVisita().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelarVisita;
    private javax.swing.JButton btnProgramarVisita;
    private javax.swing.JButton jButton3;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblFechaVisita;
    private javax.swing.JLabel lblPrimeraVisita;
    private javax.swing.JPanel spnFechaVisita;
    private javax.swing.JTable tblHogares;
    // End of variables declaration//GEN-END:variables
}
