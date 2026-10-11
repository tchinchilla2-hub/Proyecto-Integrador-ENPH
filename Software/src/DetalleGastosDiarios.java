/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author thain
 */
public class DetalleGastosDiarios extends javax.swing.JFrame {
    private java.time.LocalDate fechaSeleccionada;
    private GastoDiario gastoEnEdicion;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DetalleGastosDiarios.class.getName());

    /**
     * Creates new form DetalleGastosDiarios
     */
    public DetalleGastosDiarios() {
        initComponents();
        jDialog1.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    }
   
    //SEGUNDO CONSTRUCTOR
    public DetalleGastosDiarios(java.time.LocalDate fecha) {
    this();

    this.fechaSeleccionada = fecha;
    cargarGastos();
    }
    
    //METODO PARA CARGAR LOS GASTOS DE LA FECHA
    private void cargarGastos() {

    javax.swing.table.DefaultTableModel modelo =
            new javax.swing.table.DefaultTableModel(
                    new Object[]{
                        "Nº", "Alimento", "Cantidad", "Unidad",
                        "Equivalencia (g / cm³)",
                        "Forma de adquisición", "Lugar",
                        "Frecuencia de compra", "Tipo de gasto",
                        "Valor", "Revisado", "Editar"
                    }, 0) {

                @Override
                public Class<?> getColumnClass(int columna) {
                    if (columna == 10) {
                        return Boolean.class;
                    }
                    return Object.class;
                }

                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return columna == 10;
                }
            };

    java.time.format.DateTimeFormatter formato =
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

    Fecha.setText(fechaSeleccionada.format(formato));

    int numero = 1;

    for (GastoDiario gasto : RegistroGastos.gastos) {

        if (fechaSeleccionada.equals(gasto.fecha)) {

            modelo.addRow(new Object[]{
                numero,
                gasto.nombreAlimento,
                gasto.cantidad,
                gasto.unidadMedida,
                "Pendiente de cálculo",
                gasto.formaAdquisicion,
                gasto.lugarCompra,
                gasto.frecuenciaCompra,
                gasto.tipoGasto,
                gasto.valorPagado,
                false,
                "Editar"
            });

            numero++;
        }
    }

    jTable1.setModel(modelo);

    // Deshabilitar inicialmente la confirmación
    jButton1.setEnabled(false);

    // Detectar cambios en las casillas
    modelo.addTableModelListener(e -> verificarGastos());
}
private void verificarGastos() {

    int totalFilas = jTable1.getRowCount();

    if (totalFilas == 0) {
        jButton1.setEnabled(false);
        return;
    }

    boolean todosMarcados = true;

    for (int i = 0; i < totalFilas; i++) {

        Boolean marcado =
                (Boolean) jTable1.getValueAt(i, 10);

        if (!Boolean.TRUE.equals(marcado)) {
            todosMarcados = false;
            break;
        }
    }

    jButton1.setEnabled(todosMarcados);
}

private void abrirEditarGasto(int fila) {

    gastoEnEdicion = null;
    // Buscar el gasto correspondiente a la fila
    int numero = 0;
 
    for (GastoDiario gasto : RegistroGastos.gastos) {

        if (fechaSeleccionada.equals(gasto.fecha)) {

            if (numero == fila) {
                gastoEnEdicion = gasto;
                break;
            }

            numero++;
        }
    }

    if (gastoEnEdicion == null) {
        javax.swing.JOptionPane.showMessageDialog(this,
                "No se encontró el gasto seleccionado");
        return;
    }

    // Mostrar los datos del gasto
    jTextField1.setText(gastoEnEdicion.nombreAlimento);
    jTextField2.setText(
            String.valueOf(gastoEnEdicion.cantidad));
    jTextField4.setText(
            String.valueOf(gastoEnEdicion.valorPagado));

    jComboBox1.setSelectedItem(gastoEnEdicion.unidadMedida);

    jTextField3.setText(
            gastoEnEdicion.contenidoUnidad > 0
                    ? String.valueOf(gastoEnEdicion.contenidoUnidad)
                    : "");

    if (gastoEnEdicion.unidadContenido != null) {
        jComboBox2.setSelectedItem(gastoEnEdicion.unidadContenido);
    }

    // Habilitar presentación solo cuando sea Unidad
    boolean esUnidad =
            "Unidad".equals(gastoEnEdicion.unidadMedida);

    jTextField3.setEnabled(esUnidad);
    jComboBox2.setEnabled(esUnidad);

    // Abrir ventana emergente
    jDialog1.pack();
    jDialog1.setLocationRelativeTo(this);
    jDialog1.setVisible(true);
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jTextField3 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        Fecha = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        jDialog1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel2.setText("Editar gasto");

        jLabel3.setText("Nº 2");

        jLabel4.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel4.setText("Cantidad adquirida");

        jLabel10.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel10.setText("Nombre del alimento");

        jLabel11.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel11.setText("Unidad de medida");

        jLabel12.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel12.setText("Valor pagado o estimado");

        jTextField4.setToolTipText("");

        jButton3.setText("X");
        jButton3.addActionListener(this::jButton3ActionPerformed);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione una unidad", "Unidad", "Centímetros cúbicos", "Litro", "Gramos", "Onzas", "Libra", "Kilo", "Arroba" }));
        jComboBox1.addActionListener(this::jComboBox1ActionPerformed);

        jLabel13.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel13.setText("Presentación por unidad (opcional)");

        jLabel14.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(204, 204, 204));
        jLabel14.setText("Solo se habilita cuando la unidad de medida es unidad");

        jLabel15.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel15.setText("Cantidad adquirida");

        jLabel16.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel16.setText("Unidad de medida");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione una unidad", "Gramos", "Mililitros", "Centímetros cúbicos" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31))
        );

        jButton4.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jButton4.setText("Cancelar");
        jButton4.addActionListener(this::jButton4ActionPerformed);

        jButton5.setBackground(new java.awt.Color(0, 204, 102));
        jButton5.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jButton5.setText("Guardar");
        jButton5.addActionListener(this::jButton5ActionPerformed);

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jDialog1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jDialog1Layout.createSequentialGroup()
                                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel12)
                                    .addComponent(jTextField4)
                                    .addComponent(jTextField2)
                                    .addComponent(jTextField1)
                                    .addComponent(jComboBox1, 0, 367, Short.MAX_VALUE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel2))
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addGap(2, 2, 2)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setBackground(new java.awt.Color(168, 221, 215));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("ENPH- Cuadernillo 2");

        jLabel6.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel6.setText("Ana Torres Guerrero");

        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Jefe Hogar");

        jLabel8.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        jLabel8.setText("26 de septiembre de 2026");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton2.setText("Volver");
        jButton2.addActionListener(this::jButton2ActionPerformed);

        jScrollPane1.setBorder(null);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Nº", "Alimento", "Cantidad", "Unidad", "Equivalencia (g / cm³)", "Forma de Adquisición", "Lugar", "Frecuencia de compra", "Tipo de gasto", "Valor", "Acción"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        Fecha.setFont(new java.awt.Font("Arial", 1, 28)); // NOI18N
        Fecha.setText("Fecha");

        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("Marque todos los gastos para habilitar la confirmación.");

        jButton1.setBackground(new java.awt.Color(255, 153, 153));
        jButton1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButton1.setText("Confirmar gastos del día");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(83, 83, 83))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 701, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2)
                    .addComponent(Fecha, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addGap(1, 1, 1)
                .addComponent(Fecha)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(65, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        jDialog1.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        HistorialGastosDiarios ventana = new HistorialGastosDiarios();

    ventana.setLocationRelativeTo(null);
    ventana.setVisible(true);

    this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
         // Verificar que exista un gasto seleccionado
    if (gastoEnEdicion == null) {
        javax.swing.JOptionPane.showMessageDialog(this,
                "No se encontró el gasto seleccionado");
        return;
    }

    // Obtener los datos modificados
    String nombre = jTextField1.getText().trim();
    String cantidadTexto = jTextField2.getText().trim();
    String valorTexto = jTextField4.getText().trim();

    // Validar campos obligatorios
    if (nombre.isEmpty() || cantidadTexto.isEmpty()
            || valorTexto.isEmpty()
            || jComboBox1.getSelectedIndex() == 0) {

        javax.swing.JOptionPane.showMessageDialog(this,
                "Debe completar los campos obligatorios");
        return;
    }

    double cantidad;
    double valor;

    try {
        cantidad = Double.parseDouble(
                cantidadTexto.replace(",", "."));

        valor = Double.parseDouble(
                valorTexto.replace(",", "."));

        if (!Double.isFinite(cantidad) || cantidad <= 0
                || !Double.isFinite(valor) || valor < 0) {

            javax.swing.JOptionPane.showMessageDialog(this,
                    "Ingrese una cantidad y un valor válidos");
            return;
        }

    } catch (NumberFormatException e) {
        javax.swing.JOptionPane.showMessageDialog(this,
                "La cantidad y el valor deben ser numéricos");
        return;
    }

    // Obtener unidad seleccionada
    String unidad = jComboBox1.getSelectedItem().toString();

    double contenido = 0;
    String unidadContenido = null;

    // Validar presentación por unidad cuando corresponda
    if (unidad.equals("Unidad")) {

        String contenidoTexto = jTextField3.getText().trim();

        if (!contenidoTexto.isEmpty()) {

            try {
                contenido = Double.parseDouble(
                        contenidoTexto.replace(",", "."));

                if (!Double.isFinite(contenido) || contenido <= 0) {
                    javax.swing.JOptionPane.showMessageDialog(this,
                            "El contenido debe ser mayor que cero");
                    return;
                }

            } catch (NumberFormatException e) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Ingrese un contenido válido");
                return;
            }

            if (jComboBox2.getSelectedIndex() == 0) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Seleccione la unidad del contenido");
                return;
            }

            unidadContenido =
                    jComboBox2.getSelectedItem().toString();

        } else if (jComboBox2.getSelectedIndex() != 0) {

            javax.swing.JOptionPane.showMessageDialog(this,
                    "Ingrese el contenido por unidad");
            return;
        }
    }

    // Actualizar el gasto original
    gastoEnEdicion.nombreAlimento = nombre;
    gastoEnEdicion.cantidad = cantidad;
    gastoEnEdicion.unidadMedida = unidad;
    gastoEnEdicion.valorPagado = valor;
    gastoEnEdicion.contenidoUnidad = contenido;
    gastoEnEdicion.unidadContenido = unidadContenido;

    // Actualizar la tabla
    cargarGastos();

    // Cerrar la ventana de edición
    jDialog1.dispose();

    // Mostrar confirmación
    javax.swing.JOptionPane.showMessageDialog(this,
            "Gasto actualizado correctamente");
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        int filaVista = jTable1.rowAtPoint(evt.getPoint());
int columnaVista = jTable1.columnAtPoint(evt.getPoint());

if (filaVista >= 0 && columnaVista >= 0) {

    int columna = jTable1.convertColumnIndexToModel(columnaVista);

    // Columna Editar
    if (columna == 11) {

        int fila = jTable1.convertRowIndexToModel(filaVista);

        // Reiniciar el gasto seleccionado
        gastoEnEdicion = null;

        // Abrir ventana de edición
        abrirEditarGasto(fila);
    }
}
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        // Verificar que existan gastos
    if (jTable1.getRowCount() == 0) {
        javax.swing.JOptionPane.showMessageDialog(this,
                "No hay gastos registrados para confirmar");
        return;
    }

    // Guardar cualquier cambio pendiente en la tabla
    if (jTable1.isEditing()) {
        jTable1.getCellEditor().stopCellEditing();
    }

    // Verificar que todos estén marcados
    for (int i = 0; i < jTable1.getRowCount(); i++) {

        if (!Boolean.TRUE.equals(jTable1.getValueAt(i, 10))) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Debe revisar y marcar todos los gastos del día");
            return;
        }
    }

    // Solicitar confirmación
    int respuesta = javax.swing.JOptionPane.showConfirmDialog(
        this,
        "¿Está seguro de que desea confirmar todos los gastos del día?",
        "Confirmar gastos",
        javax.swing.JOptionPane.YES_NO_OPTION
    );

    if (respuesta == javax.swing.JOptionPane.YES_OPTION) {

    // Guardar estado confirmado
    RegistroGastos.confirmarDia(fechaSeleccionada);

    javax.swing.JOptionPane.showMessageDialog(this,
            "Gastos del día confirmados correctamente");

    // Regresar al historial
    HistorialGastosDiarios ventana =
            new HistorialGastosDiarios();

    ventana.setLocationRelativeTo(null);
    ventana.setVisible(true);

    this.dispose();
    }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
        String unidad = jComboBox1.getSelectedItem().toString();

    if (unidad.equals("Unidad")) {

        jTextField3.setEnabled(true);
        jComboBox2.setEnabled(true);

    } else {

        jTextField3.setText("");
        jTextField3.setEnabled(false);

        jComboBox2.setSelectedIndex(0);
        jComboBox2.setEnabled(false);
    }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        jDialog1.dispose();
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
        java.awt.EventQueue.invokeLater(() -> new DetalleGastosDiarios().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Fecha;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    // End of variables declaration//GEN-END:variables
}
