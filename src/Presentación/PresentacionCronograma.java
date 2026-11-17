package Presentación;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import Presentación.AccesoOficina;
import Datos.ConexionOracle;

/**
 *
 * @author thain
 */
public class PresentacionCronograma extends javax.swing.JFrame {

    private String codigoEncuestador;
    private boolean recorridoIniciado = false;
    private final java.util.List<Modelo.Visita> visitasDeFilas = new java.util.ArrayList<>();

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(PresentacionCronograma.class.getName());

    public PresentacionCronograma() {
        this(null, false);
    }

    public PresentacionCronograma(String codigoEncuestador) {
        this(codigoEncuestador, true);
    }

    // false permite que Main coordine la carga, como indica Astah.
    public PresentacionCronograma(String codigoEncuestador, boolean cargaDirecta) {
        initComponents();
        this.codigoEncuestador = codigoEncuestador;
        setLocationRelativeTo(null);
        jLabel8.setText(java.time.LocalDate.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        jButton2.addActionListener(e -> {
            if ("Programar primera visita".equals(jButton2.getText())) {
                ProgramarVisita ventana = new ProgramarVisita();
                ventana.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                ventana.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override public void windowClosed(java.awt.event.WindowEvent ev) {
                        cargarCronograma();
                    }
                });
                ventana.setLocationRelativeTo(this);
                ventana.setVisible(true);
            } else {
                iniciarRecorrido();
            }
        });
        jButton1.setText("Finalizar / Confirmar");
        jButton1.setEnabled(false);
        if (cargaDirecta && codigoEncuestador != null) {
            cargarCronograma();
        }
    }

    private void cargarCronograma() {
        try {
            // Ruta compatible para recargar los datos del cronograma.
            Datos.Persistencia p = new Datos.Persistencia();
            Modelo.Hogar[] hogares = p.recuperarHogaresAsignados(codigoEncuestador);
            Modelo.Encuesta[] encuestas = new Modelo.Encuesta[hogares.length];
            java.util.List<Modelo.Visita> visitas = new java.util.ArrayList<>();
            for (int i = 0; i < hogares.length; i++) {
                encuestas[i] = p.recuperarEncuestaHogar(hogares[i].getId());
                if (encuestas[i] != null) {
                    java.util.Collections.addAll(visitas,
                            p.recuperarVisitasEncuesta(encuestas[i].getId()));
                }
            }
            presentarCronograma(hogares, encuestas, visitas.toArray(new Modelo.Visita[0]));
            presentarNombreEncuestador(p.recuperarNombreEncuestador(codigoEncuestador));
        } catch (RuntimeException ex) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Error al cargar el cronograma: " + ex.getMessage(),
                    "Cronograma", javax.swing.JOptionPane.ERROR_MESSAGE);
            jButton2.setEnabled(false);
            jButton1.setEnabled(false);
        }
    }

    public void presentarNombreEncuestador(String nombre) {
        jLabel6.setText(nombre);
    }

    // Firma solicitada por el diagrama Astah.
    public void presentarCronograma(Modelo.Hogar[] hogares,
            Modelo.Encuesta[] encuestas, Modelo.Visita[] visitas) {
        javax.swing.table.DefaultTableModel modelo =
            new javax.swing.table.DefaultTableModel(
                new Object[]{"Hogar", "Ubicación", "Día de recolección", "Estado", "Seleccionar"}, 0) {
                @Override public Class<?> getColumnClass(int col) {
                    return col == 4 ? Boolean.class : String.class;
                }
                @Override public boolean isCellEditable(int fila, int col) {
                    return col == 4 &&
                            ("Programada".equals(getValueAt(fila, 3)) ||
                             "En ejecución".equals(getValueAt(fila, 3)));
                }
            };
        java.time.LocalDate hoy = java.time.LocalDate.now();
        boolean hayProgramadas = false;
        boolean hayEnEjecucion = false;
        visitasDeFilas.clear();
        for (int i = 0; i < hogares.length; i++) {
            Modelo.Hogar hogar = hogares[i];
            Modelo.Encuesta encuesta = encuestas[i];
            Modelo.Visita ultima = null;
            if (encuesta != null) {
                for (Modelo.Visita visita : visitas) {
                    if (visita.getEncuesta() != null &&
                            visita.getEncuesta().getId() == encuesta.getId() &&
                            (ultima == null || visita.getId() > ultima.getId())) {
                        ultima = visita;
                    }
                }
            }
            String estado = ultima == null ? "Sin programar" : ultima.getEstadoVisita();
            String dia = "Sin programar";
            if (ultima != null && ultima.getFechaVisita() != null) {
                dia = ultima.getFechaVisita().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                if ("Programada".equals(estado) && !hoy.equals(ultima.getFechaVisita())) {
                    estado = ultima.getFechaVisita().isAfter(hoy) ? "Próxima" : "Vencida";
                }
            }
            if ("Programada".equals(estado)) hayProgramadas = true;
            if ("En ejecución".equals(estado)) hayEnEjecucion = true;
            visitasDeFilas.add(ultima);
            String ubicacion = hogar.getVivienda() == null ? "Sin dirección" :
                    hogar.getVivienda().getDireccion();
            modelo.addRow(new Object[]{
                String.format("HG-%03d", hogar.getId()), ubicacion, dia, estado,
                Boolean.FALSE
            });
        }
        jTable1.setModel(modelo);
        recorridoIniciado = false;
        jButton2.setText(hayProgramadas ? "Iniciar recorrido" : "Programar primera visita");
        jButton2.setEnabled(true);
        jButton1.setEnabled(hayEnEjecucion);
        jButton2.setToolTipText(hayProgramadas ? "Seleccione visitas programadas para hoy" :
                "Abra la programación de primeras visitas y vuelva al cronograma");
    }

    // El formulario captura solamente las visitas que ya se iniciaron en Oracle.
    public Modelo.Visita[] capturarVisitasAtendidas() {
        java.util.List<Modelo.Visita> seleccionadas = new java.util.ArrayList<>();
        javax.swing.table.DefaultTableModel modelo =
                (javax.swing.table.DefaultTableModel) jTable1.getModel();
        for (int fila = 0; fila < modelo.getRowCount(); fila++) {
            if (Boolean.TRUE.equals(modelo.getValueAt(fila, 4)) &&
                    "En ejecución".equals(modelo.getValueAt(fila, 3))) {
                Modelo.Visita visita = visitasDeFilas.get(fila);
                if (visita != null) seleccionadas.add(visita);
            }
        }
        return seleccionadas.toArray(new Modelo.Visita[0]);
    }

    private void iniciarRecorrido() {
        javax.swing.table.DefaultTableModel modelo =
                (javax.swing.table.DefaultTableModel) jTable1.getModel();
        int seleccionadas = 0;
        for (int i = 0; i < modelo.getRowCount(); i++) {
            if (Boolean.TRUE.equals(modelo.getValueAt(i, 4)) &&
                    "Programada".equals(modelo.getValueAt(i, 3))) {
                seleccionadas++;
            }
        }
        if (seleccionadas == 0) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Seleccione al menos un hogar programado para hoy.");
            return;
        }
        // El recorrido se marca solo a nivel de interfaz. Cada visita se inicia
        // realmente desde PresentacionVisita y se actualiza en Oracle.
        recorridoIniciado = true;
        jButton2.setEnabled(false);
        jButton1.setEnabled(true);
        jTable1.repaint();
        javax.swing.JOptionPane.showMessageDialog(this,
                "Recorrido abierto. Registre el inicio de cada visita desde Registro de visita.");
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
        if (recorridoIniciado) {
            // Cerrar el recorrido no confirma automáticamente todas las visitas.
            recorridoIniciado = false;
            cargarCronograma();
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Recorrido cerrado. Seleccione las visitas 'En ejecución' "
                    + "para confirmarlas como atendidas.");
            return;
        }
        Modelo.Visita[] atendidas = capturarVisitasAtendidas();
        if (atendidas.length == 0) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Seleccione las visitas con estado 'En ejecución'.");
            return;
        }
        int respuesta = javax.swing.JOptionPane.showConfirmDialog(this,
                "¿Confirmar como atendidas " + atendidas.length + " visita(s)?",
                "Confirmar visitas", javax.swing.JOptionPane.YES_NO_OPTION);
        if (respuesta != javax.swing.JOptionPane.YES_OPTION) return;
        try {
            new software.Main().confirmarVisitasAtendidas(atendidas);
            cargarCronograma();
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Visitas confirmadas en Oracle.");
        } catch (RuntimeException ex) {
            cargarCronograma();
            javax.swing.JOptionPane.showMessageDialog(this,
                    "No se pudieron confirmar todas las visitas: " + ex.getMessage(),
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
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
        java.awt.EventQueue.invokeLater(() -> new PresentacionCronograma().setVisible(true));
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
