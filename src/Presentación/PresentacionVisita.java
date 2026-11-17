package Presentación;


import software.DatosRecoleccion;
import Presentación.MenuPrincipal;
import Datos.ConexionOracle;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author thain
 */
public class PresentacionVisita extends javax.swing.JFrame {
    
    private Integer idVisitaActual = null;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PresentacionVisita.class.getName());

    /**
     * Creates new form RegistrarVisita
     */
    public PresentacionVisita() {
    initComponents();

    setLocationRelativeTo(null);

    JlFechaVisita.setText(
            java.time.LocalDate.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
            )
    );

    Horainicio.setText("Pendiente");
    Horafin.setText("Pendiente");

    iniciar.setEnabled(true);
    Finalizar.setEnabled(false);

    jLabel4.setText(java.time.LocalDate.now().format(
            java.time.format.DateTimeFormatter.ofPattern(
                    "dd 'de' MMMM 'de' uuuu", new java.util.Locale("es", "CO"))));
    cargarNombreJefeHogar();
    cargarEstadoVisita();
}
    
    private String formatoPantalla(String hora24) {

    if (hora24 == null || hora24.trim().isEmpty()) {
        return "Pendiente";
    }

    java.time.LocalTime hora = java.time.LocalTime.parse(hora24);

    return hora.format(
            java.time.format.DateTimeFormatter.ofPattern(
                    "hh:mm a",
                    new java.util.Locale("es", "CO")
            )
    );
}

// Cargar el nombre real del hogar que inició sesión, sin mostrar un nombre fijo.
private void cargarNombreJefeHogar() {
    String codigo = DatosRecoleccion.codigoHogar;
    jLabel2.setText(DatosRecoleccion.obtenerNombreJefe());
    if (codigo == null || !codigo.matches("HG-\\d+")) return;
    String sql = "SELECT j.NOMBRE, j.APELLIDO FROM JEFE_HOGAR j WHERE j.ID_HOGAR = ?";
    try (java.sql.Connection con = ConexionOracle.obtenerConexion();
         java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, Integer.parseInt(codigo.substring(3)));
        try (java.sql.ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String nombre = (rs.getString(1) == null ? "" : rs.getString(1).trim())
                        + " " + (rs.getString(2) == null ? "" : rs.getString(2).trim());
                DatosRecoleccion.registrarNombreJefe(nombre);
                jLabel2.setText(DatosRecoleccion.obtenerNombreJefe());
            }
        }
    } catch (java.sql.SQLException ex) {
        // Un error de etiqueta no debe impedir registrar la visita.
    }
}

//Revisar
// Recuperar la visita del hogar desde Oracle
private void cargarEstadoVisita() {
    // Estado inicial: se permite iniciar; finalizar solo si existe una visita iniciada.
    idVisitaActual = null;
    iniciar.setEnabled(true);
    Finalizar.setEnabled(false);
    JlFechaVisita.setText(java.time.LocalDate.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    Horainicio.setText("Pendiente");
    Horafin.setText("Pendiente");

    String codigo = DatosRecoleccion.codigoHogar;
    if (codigo == null || !codigo.matches("HG-\\d+")) {
        iniciar.setEnabled(false);
        iniciar.setToolTipText("Primero debe ingresar con un hogar válido");
        return;
    }

    // Si una visita quedó iniciada antes de volver a abrir la ventana,
    // recuperar su estado para poder finalizarla sin duplicarla.
    String sql = "SELECT v.ID, v.FECHA_HORA_INICIO "
            + "FROM VISITA v JOIN ENCUESTA e ON e.ID = v.ID_ENCUESTA "
            + "WHERE e.ID_HOGAR = ? AND e.FECHA_CIERRE IS NULL "
            + "AND v.FECHA_HORA_FIN IS NULL "
            + "AND v.FECHA_HORA_INICIO <> TRUNC(v.FECHA_HORA_INICIO) "
            + "ORDER BY v.FECHA_HORA_INICIO DESC";
    try (java.sql.Connection con = ConexionOracle.obtenerConexion();
         java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, Integer.parseInt(codigo.substring(3)));
        try (java.sql.ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                idVisitaActual = rs.getInt("ID");
                java.time.LocalDateTime inicio = rs.getTimestamp("FECHA_HORA_INICIO").toLocalDateTime();
                JlFechaVisita.setText(inicio.toLocalDate().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                Horainicio.setText(formatoPantalla(inicio.toLocalTime().format(
                        java.time.format.DateTimeFormatter.ofPattern("HH:mm"))));
                iniciar.setEnabled(false);
                Finalizar.setEnabled(true);
            }
        }
    } catch (java.sql.SQLException ex) {
        iniciar.setEnabled(false);
        javax.swing.JOptionPane.showMessageDialog(this,
                "No fue posible comprobar el estado de la visita en Oracle:\n" + ex.getMessage(),
                "Consulta de visita", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
}

    // Operaciones de presentación requeridas por los incrementos 8 y 9 de Astah.
    public Modelo.Visita capturarInicioVisita() {
        if (software.DatosRecoleccion.codigoHogar == null ||
                !software.DatosRecoleccion.codigoHogar.matches("HG-\\d+")) {
            throw new IllegalStateException("Seleccione un hogar válido.");
        }
        int idHogar = Integer.parseInt(software.DatosRecoleccion.codigoHogar.substring(3));
        Modelo.Encuesta encuesta = new Datos.Persistencia().recuperarEncuestaHogar(idHogar);
        if (encuesta == null) throw new IllegalStateException("El hogar no tiene encuesta.");
        return new Modelo.Visita(0, java.time.LocalDate.now(), null, encuesta);
    }

    public Modelo.Visita capturarFinVisita(Modelo.Visita pVisita) {
        if (pVisita == null || pVisita.getId() <= 0) {
            throw new IllegalArgumentException("La visita debe estar iniciada.");
        }
        return pVisita;
    }

    public void presentarVisita(Modelo.Visita pVisita) {
        if (pVisita == null) return;
        JlFechaVisita.setText(pVisita.getFechaVisita().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        Horainicio.setText(formatoPantalla(pVisita.getHoraInicio()));
        Horafin.setText(pVisita.getHoraFin() == null ? "Pendiente" : pVisita.getHoraFin());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        volver = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        FechaVisita = new javax.swing.JLabel();
        HoraI = new javax.swing.JLabel();
        Horaf = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        Horafin = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        JlFechaVisita = new javax.swing.JLabel();
        Horainicio = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        iniciar = new javax.swing.JButton();
        Finalizar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(168, 221, 215));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("ENPH- Cuadernillo 2");

        jLabel2.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel2.setText("Ana Torres Guerrero");

        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Jefe Hogar");

        jLabel4.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        jLabel4.setText("26 de septiembre de 2026");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel5.setFont(new java.awt.Font("Arial", 1, 28)); // NOI18N
        jLabel5.setText("Registrar visita");

        volver.setText("Volver");
        volver.addActionListener(this::volverActionPerformed);

        jPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), null));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        FechaVisita.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        FechaVisita.setText("Fecha de visita:");
        jPanel1.add(FechaVisita, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 138, -1));

        HoraI.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        HoraI.setText("Hora inicio:");
        jPanel1.add(HoraI, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 138, 20));

        Horaf.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        Horaf.setText("Hora fin:");
        jPanel1.add(Horaf, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 80, -1));

        jSeparator2.setBackground(new java.awt.Color(133, 205, 255));
        jSeparator2.setBorder(javax.swing.BorderFactory.createCompoundBorder(null, javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0))));
        jPanel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 330, 50));

        Horafin.setFont(new java.awt.Font("Segoe UI", 0, 19)); // NOI18N
        jPanel1.add(Horafin, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 130, 230, 50));

        jSeparator4.setBorder(javax.swing.BorderFactory.createCompoundBorder(null, javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0))));
        jPanel1.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 330, 60));

        JlFechaVisita.setFont(new java.awt.Font("Segoe UI", 0, 19)); // NOI18N
        jPanel1.add(JlFechaVisita, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 0, 190, 50));

        Horainicio.setFont(new java.awt.Font("Segoe UI", 0, 19)); // NOI18N
        jPanel1.add(Horainicio, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, 220, 40));

        jSeparator3.setBorder(javax.swing.BorderFactory.createCompoundBorder(null, javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0))));
        jPanel1.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 330, 70));

        iniciar.setBackground(new java.awt.Color(102, 204, 0));
        iniciar.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        iniciar.setText("Iniciar visita");
        iniciar.addActionListener(this::iniciarActionPerformed);

        Finalizar.setBackground(new java.awt.Color(102, 204, 0));
        Finalizar.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        Finalizar.setText("Finalizar visita");
        Finalizar.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Finalizar.addActionListener(this::FinalizarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(volver)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 453, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(iniciar, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(Finalizar, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(171, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(volver)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(82, 82, 82)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iniciar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Finalizar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void iniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iniciarActionPerformed
        if (!iniciar.isEnabled()) return;
        String codigo = DatosRecoleccion.codigoHogar;
        if (codigo == null || !codigo.matches("HG-\\d+")) {
            javax.swing.JOptionPane.showMessageDialog(this, "Hogar no identificado.");
            return;
        }
        int idHogar = Integer.parseInt(codigo.substring(3));
        String sqlEncuesta = "SELECT ID FROM ENCUESTA "
                + "WHERE ID_HOGAR = ? AND FECHA_CIERRE IS NULL";
        String sqlActiva = "SELECT v.ID FROM VISITA v "
                + "WHERE v.ID_ENCUESTA = ? AND v.FECHA_HORA_FIN IS NULL "
                + "AND v.FECHA_HORA_INICIO <> TRUNC(v.FECHA_HORA_INICIO)";
        String sqlProgramada = "SELECT ID FROM VISITA "
                + "WHERE ID_ENCUESTA = ? AND FECHA_HORA_FIN IS NULL "
                + "AND FECHA_HORA_INICIO = TRUNC(SYSDATE) "
                + "ORDER BY ID";
        try (java.sql.Connection con = ConexionOracle.obtenerConexion()) {
            if (con == null) throw new java.sql.SQLException("Sin conexión a Oracle.");
            con.setAutoCommit(false);
            try {
                int idEncuesta;
                try (java.sql.PreparedStatement ps = con.prepareStatement(sqlEncuesta)) {
                    ps.setInt(1, idHogar);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new java.sql.SQLException("El hogar no tiene una encuesta abierta.");
                        }
                        idEncuesta = rs.getInt(1);
                    }
                }
                try (java.sql.PreparedStatement ps = con.prepareStatement(sqlActiva)) {
                    ps.setInt(1, idEncuesta);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            throw new java.sql.SQLException(
                                    "Ya existe una visita iniciada para este hogar. Vuelva a abrir la pantalla para finalizarla.");
                        }
                    }
                }

                Integer id = null;
                try (java.sql.PreparedStatement ps = con.prepareStatement(sqlProgramada)) {
                    ps.setInt(1, idEncuesta);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) id = rs.getInt(1);
                    }
                }
                if (id != null) {
                    // Aprovechar la programación hecha desde el administrador.
                    String sql = "UPDATE VISITA SET FECHA_HORA_INICIO = SYSDATE "
                            + "WHERE ID = ? AND FECHA_HORA_FIN IS NULL "
                            + "AND FECHA_HORA_INICIO = TRUNC(FECHA_HORA_INICIO)";
                    try (java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setInt(1, id);
                        if (ps.executeUpdate() != 1) {
                            throw new java.sql.SQLException("La visita programada ya cambió de estado.");
                        }
                    }
                } else {
                    // Visita adicional: no requiere programación previa ni modifica otras visitas.
                    int respuesta = javax.swing.JOptionPane.showConfirmDialog(this,
                            "No hay una visita programada para hoy.\n"
                            + "¿Desea registrar una visita adicional para este hogar?",
                            "Confirmar visita adicional",
                            javax.swing.JOptionPane.YES_NO_OPTION);
                    if (respuesta != javax.swing.JOptionPane.YES_OPTION) {
                        con.rollback();
                        return;
                    }
                    try (java.sql.PreparedStatement ps = con.prepareStatement(
                            "SELECT SEQ_VISITA.NEXTVAL FROM DUAL");
                         java.sql.ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new java.sql.SQLException("No fue posible generar el ID de visita.");
                        id = rs.getInt(1);
                    }
                    try (java.sql.PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO VISITA (ID, FECHA_HORA_INICIO, FECHA_HORA_FIN, ID_ENCUESTA) "
                            + "VALUES (?, SYSDATE, NULL, ?)")) {
                        ps.setInt(1, id);
                        ps.setInt(2, idEncuesta);
                        if (ps.executeUpdate() != 1) {
                            throw new java.sql.SQLException("No se pudo registrar la visita adicional.");
                        }
                    }
                }
                con.commit();
                idVisitaActual = id;
                JlFechaVisita.setText(java.time.LocalDate.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                Horainicio.setText(formatoPantalla(java.time.LocalTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("HH:mm"))));
                Horafin.setText("Pendiente");
                iniciar.setEnabled(false);
                Finalizar.setEnabled(true);
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Visita iniciada correctamente y guardada en Oracle.");
            } catch (java.sql.SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (java.sql.SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Error al iniciar la visita: " + e.getMessage(),
                    "No se pudo iniciar", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_iniciarActionPerformed

    private void FinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FinalizarActionPerformed
        if (idVisitaActual == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Primero debe iniciar la visita.");
            return;
        }
        String codigo = DatosRecoleccion.codigoHogar;
        if (codigo == null || !codigo.matches("HG-\\d+")) {
            javax.swing.JOptionPane.showMessageDialog(this, "Hogar no identificado.");
            return;
        }
        int idHogar = Integer.parseInt(codigo.substring(3));
        String sql = "UPDATE VISITA v SET FECHA_HORA_FIN = SYSDATE "
                + "WHERE v.ID = ? AND v.FECHA_HORA_FIN IS NULL "
                + "AND v.FECHA_HORA_INICIO <> TRUNC(v.FECHA_HORA_INICIO) "
                + "AND EXISTS (SELECT 1 FROM ENCUESTA e "
                + "WHERE e.ID = v.ID_ENCUESTA AND e.ID_HOGAR = ?)";
        try (java.sql.Connection con = ConexionOracle.obtenerConexion()) {
            if (con == null) throw new java.sql.SQLException("Sin conexión a Oracle.");
            try (java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idVisitaActual);
                ps.setInt(2, idHogar);
                if (ps.executeUpdate() != 1) {
                    throw new java.sql.SQLException("La visita no está iniciada o ya fue finalizada.");
                }
            }
            String hora = java.time.LocalTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
            Horafin.setText(formatoPantalla(hora));
            idVisitaActual = null;
            iniciar.setEnabled(false);
            Finalizar.setEnabled(false);
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Visita finalizada y guardada en Oracle.");
        } catch (java.sql.SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Error al finalizar la visita: " + e.getMessage());
        }
    }//GEN-LAST:event_FinalizarActionPerformed

    private void volverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_volverActionPerformed
        // TODO add your handling code here:
        
        MenuPrincipal menu=new MenuPrincipal();
        
        menu.setLocationRelativeTo(null);
        
        menu.setVisible(true);
        
        this.dispose();
    }//GEN-LAST:event_volverActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new PresentacionVisita().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel FechaVisita;
    private javax.swing.JButton Finalizar;
    private javax.swing.JLabel HoraI;
    private javax.swing.JLabel Horaf;
    private javax.swing.JLabel Horafin;
    private javax.swing.JLabel Horainicio;
    private javax.swing.JLabel JlFechaVisita;
    private javax.swing.JButton iniciar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JButton volver;
    // End of variables declaration//GEN-END:variables
}
