package Presentación;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import Presentación.AccesoOficina;
import Datos.ConexionOracle;
import Negocio.RecorridoService;
/**
 *
 * @author thain
 */
public class CronogramaVisitas extends javax.swing.JFrame {

    private String codigoEncuestador;
    private boolean recorridoIniciado = false;

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(CronogramaVisitas.class.getName());

    public CronogramaVisitas() {
        this(null);
    }

    public CronogramaVisitas(String codigoEncuestador) {
        initComponents();

        this.codigoEncuestador = codigoEncuestador;

        setLocationRelativeTo(null);

        jLabel8.setText(
                java.time.LocalDate.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
        );

        jButton2.addActionListener(e -> iniciarRecorrido());

        jButton1.setEnabled(false);

        cargarCronograma();
    }
private void cargarCronograma() {
  // Crear el modelo de la tabla
    javax.swing.table.DefaultTableModel modelo =
            new javax.swing.table.DefaultTableModel(
                    new Object[]{
                        "Hogar",
                        "Ubicación",
                        "Día de recolección",
                        "Estado",
                        "Seleccionar"
                    }, 0) {

        @Override
        public Class<?> getColumnClass(int columna) {

            if (columna == 4) {
                return Boolean.class;
            }

            return String.class;
        }

        @Override
        public boolean isCellEditable(int fila, int columna) {

            if (columna != 4 || recorridoIniciado) {
                return false;
            }

            String estado = getValueAt(fila, 3).toString();

            return estado.equals("Programada");
        }
    };

    jTable1.setModel(modelo);

    // Validar que exista un encuestador
    if (codigoEncuestador == null
            || codigoEncuestador.trim().isEmpty()) {

        javax.swing.JOptionPane.showMessageDialog(this,
                "No se ha identificado al encuestador.");

        jButton2.setEnabled(false);
        jButton1.setEnabled(false);
        return;
    }

    try (java.sql.Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            throw new java.sql.SQLException(
                    "No se pudo conectar con Oracle.");
        }

        // CAPA DE NEGOCIO
        RecorridoService recorridoService =
                new RecorridoService();

        // Consultar el nombre del encuestador
        String nombreEncuestador =
                recorridoService.obtenerNombreEncuestador(
                        conexion,
                        codigoEncuestador
                );

        jLabel6.setText(nombreEncuestador);

        // Obtener los hogares desde la capa de negocio
        java.util.List<Object[]> hogares =
                recorridoService.obtenerCronograma(
                        conexion,
                        codigoEncuestador
                );

        // Mostrar los hogares en la tabla
        for (Object[] hogar : hogares) {
            modelo.addRow(hogar);
        }

        // Verificar si quedan hogares programados
        boolean hayPendientes = false;

        for (int i = 0; i < modelo.getRowCount(); i++) {

            if ("Programada".equals(
                    modelo.getValueAt(i, 3))) {

                hayPendientes = true;
                break;
            }
        }

        // Estado inicial de los botones
        recorridoIniciado = false;

        jButton2.setEnabled(hayPendientes);
        jButton1.setEnabled(false);

        jTable1.repaint();

    } catch (java.sql.SQLException
            | IllegalArgumentException e) {

        jButton2.setEnabled(false);
        jButton1.setEnabled(false);

        javax.swing.JOptionPane.showMessageDialog(this,
                "Error al cargar el cronograma:\n"
                + e.getMessage());
    }

    // Consultar los hogares asignados al encuestador
    String sql =
        "SELECT h.ID AS ID_HOGAR, "
        + "j.NOMBRE AS NOMBRE_JEFE, "
        + "j.APELLIDO AS APELLIDO_JEFE, "
        + "v.DIRECCION, "
        + "b.NOMBRE AS BARRIO, "
        + "(SELECT MAX(e.FECHA_INICIO) "
        + " FROM ENCUESTA e "
        + " WHERE e.ID_HOGAR = h.ID) AS FECHA_INICIO, "
        + "CASE WHEN EXISTS ( "
        + " SELECT 1 FROM VISITA vi "
        + " JOIN ENCUESTA en ON vi.ID_ENCUESTA = en.ID "
        + " WHERE en.ID_HOGAR = h.ID "
        + " AND vi.ESTADO_VISITA = 'Finalizada' "
        + " AND TRUNC(vi.FECHA_VISITA) = TRUNC(SYSDATE) "
        + ") THEN 'Atendida' ELSE 'Programada' END AS ESTADO "
        + "FROM HOGAR h "
        + "JOIN JEFE_HOGAR j ON j.ID_HOGAR = h.ID "
        + "JOIN VIVIENDA v ON v.ID = h.ID_VIVIENDA "
        + "JOIN BARRIO b ON b.ID = v.ID_BARRIO "
        + "WHERE h.COD_ENCUESTADOR = ? "
        + "ORDER BY h.ID";

    try (java.sql.Connection con =
            ConexionOracle.obtenerConexion()) {

        if (con == null) {
            throw new java.sql.SQLException(
                    "No se pudo conectar con Oracle.");
        }

        // Obtener el nombre del encuestador
        String sqlEncuestador =
            "SELECT NOMBRE, APELLIDO "
            + "FROM ENCUESTADOR "
            + "WHERE CODIGO = ?";

        try (java.sql.PreparedStatement ps =
                con.prepareStatement(sqlEncuestador)) {

            ps.setString(1, codigoEncuestador);

            try (java.sql.ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    throw new java.sql.SQLException(
                        "El encuestador no está registrado.");
                }

                jLabel6.setText(
                    rs.getString("NOMBRE") + " "
                    + rs.getString("APELLIDO")
                );
            }
        }

        // Obtener los hogares asignados
        try (java.sql.PreparedStatement ps =
                con.prepareStatement(sql)) {

            ps.setString(1, codigoEncuestador);

            try (java.sql.ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    int idHogar = rs.getInt("ID_HOGAR");

                    String hogar =
                        String.format("HG-%03d", idHogar)
                        + " - "
                        + rs.getString("NOMBRE_JEFE")
                        + " "
                        + rs.getString("APELLIDO_JEFE");

                    String ubicacion =
                        rs.getString("DIRECCION")
                        + " - "
                        + rs.getString("BARRIO");

                    // Calcular el día de recolección
                    String diaRecoleccion = "Sin iniciar";

                    java.sql.Date fechaOracle =
                        rs.getDate("FECHA_INICIO");

                    if (fechaOracle != null) {

                        long dia =
                            java.time.temporal.ChronoUnit.DAYS.between(
                                fechaOracle.toLocalDate(),
                                java.time.LocalDate.now()
                            ) + 1;

                        if (dia < 1) {
                            diaRecoleccion = "Por iniciar";

                        } else if (dia <= 14) {
                            diaRecoleccion = dia + " de 14";

                        } else {
                            diaRecoleccion = "Periodo terminado";
                        }
                    }

                    // Estado real consultado desde Oracle
                    String estado = rs.getString("ESTADO");

                    // Si ya fue atendido, dejar casilla marcada
                    boolean seleccionado =
                        "Atendida".equals(estado);

                    modelo.addRow(new Object[]{
                        hogar,
                        ubicacion,
                        diaRecoleccion,
                        estado,
                        seleccionado
                    });
                }
            }
        }

        // Comprobar si quedan hogares programados
        boolean hayPendientes = false;

        for (int i = 0; i < modelo.getRowCount(); i++) {

            if ("Programada".equals(
                    modelo.getValueAt(i, 3))) {

                hayPendientes = true;
                break;
            }
        }

        // Estado inicial de los botones
        recorridoIniciado = false;

        jButton2.setEnabled(hayPendientes);
        jButton1.setEnabled(false);

        jTable1.repaint();

    } catch (java.sql.SQLException e) {

        jButton2.setEnabled(false);
        jButton1.setEnabled(false);

        javax.swing.JOptionPane.showMessageDialog(this,
                "Error al cargar el cronograma: "
                + e.getMessage());
    }
}


private void iniciarRecorrido() {

  javax.swing.table.DefaultTableModel modelo =
        (javax.swing.table.DefaultTableModel) jTable1.getModel();

    boolean haySeleccionados = false;

    for (int i = 0; i < modelo.getRowCount(); i++) {

        boolean seleccionado =
            Boolean.TRUE.equals(modelo.getValueAt(i, 4));

        String estado = modelo.getValueAt(i, 3).toString();

        if (seleccionado && estado.equals("Programada")) {

            modelo.setValueAt("En ejecución", i, 3);
            haySeleccionados = true;
        }
    }

    if (!haySeleccionados) {

        javax.swing.JOptionPane.showMessageDialog(
            this,
            "Seleccione al menos un hogar programado."
        );

        return;
    }

    recorridoIniciado = true;

    jButton2.setEnabled(false);
    jButton1.setEnabled(true);

    // Durante el recorrido no se pueden modificar las casillas
    jTable1.repaint();

    javax.swing.JOptionPane.showMessageDialog(
        this,
        "Recorrido iniciado correctamente."
    );
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
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 284, Short.MAX_VALUE)
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setBackground(new java.awt.Color(168, 221, 215));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("ENPH- Cuadernillo 2");

        jLabel6.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel6.setText("Carlos Guerrero");

        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Encuestador");

        jLabel8.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        jLabel8.setText("26 de septiembre de 2026");

        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Zona asignada: Santander: Bucaramanga");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addGap(14, 14, 14))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 56, Short.MAX_VALUE))
        );

        jLabel9.setFont(new java.awt.Font("Arial", 1, 28)); // NOI18N
        jLabel9.setText("Cronograma de visitas del día");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setText("Seleccione los hogares que visitó:");

        jTable1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Hogar", "Ubicación", "Día de recolección", "Estado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton1.setBackground(new java.awt.Color(51, 204, 0));
        jButton1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jButton1.setText("Finalizar recorrido");
        jButton1.setToolTipText("");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jButton2.setBackground(new java.awt.Color(0, 204, 255));
        jButton2.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jButton2.setText("Iniciar recorrido");

        jButton3.setText("Volver");
        jButton3.addActionListener(this::jButton3ActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(142, 142, 142)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 431, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 656, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3))))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
   
   if (!recorridoIniciado) {
        return;
    }

    int respuesta = javax.swing.JOptionPane.showConfirmDialog(
        this,
        "¿Desea finalizar el recorrido?",
        "Finalizar recorrido",
        javax.swing.JOptionPane.YES_NO_OPTION
    );

    if (respuesta != javax.swing.JOptionPane.YES_OPTION) {
        return;
    }

    javax.swing.table.DefaultTableModel modelo =
        (javax.swing.table.DefaultTableModel) jTable1.getModel();

    for (int i = 0; i < modelo.getRowCount(); i++) {

        String estado = modelo.getValueAt(i, 3).toString();

        if (estado.equals("En ejecución")) {

            modelo.setValueAt("Atendida", i, 3);

            // La casilla permanece marcada
            modelo.setValueAt(true, i, 4);
        }
    }

    recorridoIniciado = false;

    // Permitir otro recorrido si quedan hogares programados
    boolean hayPendientes = false;

    for (int i = 0; i < modelo.getRowCount(); i++) {

        if ("Programada".equals(modelo.getValueAt(i, 3))) {
            hayPendientes = true;
            break;
        }
    }

    jButton2.setEnabled(hayPendientes);
    jButton1.setEnabled(false);

    jTable1.repaint();

    javax.swing.JOptionPane.showMessageDialog(
        this,
        "Recorrido finalizado correctamente."
    );
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        AccesoOficina menu = new AccesoOficina();
        menu.setLocationRelativeTo(null);
        menu.setVisible(true);

        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new CronogramaVisitas().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
