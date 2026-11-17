package Presentación;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author thain
 */
import Presentación.MenuPrincipalOficina;
import Datos.ConexionOracle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import Datos.Persistencia;
import Modelo.Hogar;
import Modelo.JefeHogar;
import Modelo.Vivienda;
//Para mostrar Fecha Actual que abre el administrador
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PresentacionHogar extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PresentacionHogar.class.getName());

    /**
     * Creates new form RegistrarHogar
     */
    public PresentacionHogar() {
    initComponents();
    actualizarFechaActual();

    txtTotalPersonas1.setText("");

    cmbVivienda.removeAllItems();
    cmbVivienda.addItem("Seleccione la dirección");
    cmbVivienda.setSelectedIndex(0);
    cmbVivienda.setEnabled(false);

    txtTotalPersonas.setText("");

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
private void presentarHogar(Modelo.Encuesta encuesta) {

    if (encuesta == null || encuesta.getHogar() == null) {
        JOptionPane.showMessageDialog(
                this,
                "No se recibió la información del registro.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        return;
    }

    int idHogar = encuesta.getHogar().getId();
    int idEncuesta = encuesta.getId();

    JOptionPane.showMessageDialog(
            this,
            "Registro realizado correctamente en Oracle.\n\n"
            + "Hogar: HG-"
            + String.format("%03d", idHogar)
            + "\nEncuesta: " + idEncuesta
            + "\nJefe del hogar registrado correctamente.",
            "Registro exitoso",
            JOptionPane.INFORMATION_MESSAGE);
}

// Métodos públicos requeridos por el diagrama de clases Astah.
public String capturarNombreBarrio() {
    return txtTotalPersonas1.getText().trim();
}

public Hogar capturarHogar(Vivienda[] pViviendas) {
    if (pViviendas == null || pViviendas.length == 0) {
        throw new IllegalArgumentException("No hay viviendas disponibles.");
    }
    if (cmbVivienda.getSelectedIndex() <= 0) {
        throw new IllegalArgumentException("Seleccione una vivienda.");
    }
    Object seleccion = cmbVivienda.getSelectedItem();
    int id = Integer.parseInt(seleccion.toString().split(" - ", 2)[0]);
    Vivienda elegida = null;
    for (Vivienda vivienda : pViviendas) {
        if (vivienda != null && vivienda.getId() == id) {
            elegida = vivienda;
            break;
        }
    }
    if (elegida == null) {
        throw new IllegalArgumentException("La vivienda seleccionada no pertenece al listado disponible.");
    }
    int personas = Integer.parseInt(txtTotalPersonas.getText().trim());
    if (personas <= 0) {
        throw new IllegalArgumentException("La cantidad de personas debe ser mayor que cero.");
    }
    return new Hogar(personas, elegida);
}

public JefeHogar capturarJefeHogar() {
    return new JefeHogar(
            txtIdentificacion.getText().trim(),
            txtNombres.getText().trim(),
            txtApellidos.getText().trim(),
            txtTelefono.getText().trim(),
            txtCorreo.getText().trim(), null);
}

public void presentarHogar(Hogar pHogar) {
    if (pHogar == null || pHogar.getId() <= 0) {
        throw new IllegalArgumentException("No se ha registrado un hogar válido.");
    }
    JOptionPane.showMessageDialog(this,
            "Hogar registrado correctamente: HG-" +
            String.format("%03d", pHogar.getId()),
            "Registro de hogar", JOptionPane.INFORMATION_MESSAGE);
}

private Hogar capturarHogar() {

    int cantidadPersonas = Integer.parseInt(
            txtTotalPersonas.getText().trim());

    String viviendaSeleccionada =
            cmbVivienda.getSelectedItem().toString();

    int idVivienda = Integer.parseInt(
            viviendaSeleccionada.split(" - ", 2)[0]);

    Vivienda vivienda = new Vivienda("", null);
    vivienda.setId(idVivienda);

    return new Hogar(cantidadPersonas, vivienda);
}


private JefeHogar capturarJefeHogar(Hogar hogar) {

    String identificacion =
            txtIdentificacion.getText().trim();

    String nombres =
            txtNombres.getText().trim();

    String apellidos =
            txtApellidos.getText().trim();

    String telefono =
            txtTelefono.getText().trim();

    String correo =
            txtCorreo.getText().trim();

    return new JefeHogar(
            identificacion,
            nombres,
            apellidos,
            telefono,
            correo,
            hogar
    );
}

private void cargarViviendas(String ubicacionBuscada) {

    cmbVivienda.removeAllItems();
    cmbVivienda.addItem("Seleccione la dirección");
    cmbVivienda.setEnabled(false);

    if (ubicacionBuscada == null ||
            ubicacionBuscada.trim().isEmpty()) {
        return;
    }

    String sql =
        "SELECT v.ID, v.DIRECCION, "
        + "b.NOMBRE AS BARRIO, "
        + "m.NOMBRE AS MUNICIPIO "
        + "FROM VIVIENDA v "
        + "JOIN BARRIO b ON v.ID_BARRIO = b.ID "
        + "JOIN MUNICIPIO m ON b.ID_MUNICIPIO = m.ID "
        + "WHERE UPPER(TRANSLATE(m.NOMBRE, "
        + "'ÁÉÍÓÚÜÑ', 'AEIOUUN')) LIKE ? "
        + "OR UPPER(TRANSLATE(b.NOMBRE, "
        + "'ÁÉÍÓÚÜÑ', 'AEIOUUN')) LIKE ? "
        + "ORDER BY m.NOMBRE, b.NOMBRE, v.DIRECCION";

    try (Connection conexion =
            ConexionOracle.obtenerConexion()) {

        if (conexion == null) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo conectar con Oracle.");
            return;
        }

        try (PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            String busqueda = ubicacionBuscada.trim()
                    .toUpperCase(java.util.Locale.ROOT)
                    .replace('Á', 'A')
                    .replace('É', 'E')
                    .replace('Í', 'I')
                    .replace('Ó', 'O')
                    .replace('Ú', 'U')
                    .replace('Ü', 'U')
                    .replace('Ñ', 'N');

            ps.setString(1, "%" + busqueda + "%");
            ps.setString(2, "%" + busqueda + "%");

            try (ResultSet rs = ps.executeQuery()) {

                int cantidad = 0;

                while (rs.next()) {

                    int id = rs.getInt("ID");
                    String direccion = rs.getString("DIRECCION");
                    String barrio = rs.getString("BARRIO");
                    String municipio = rs.getString("MUNICIPIO");

                    cmbVivienda.addItem(
                            id + " - " + direccion
                            + " - " + barrio
                            + " - " + municipio
                    );

                    cantidad++;
                }

                cmbVivienda.setEnabled(cantidad > 0);

                if (cantidad == 0) {
                    JOptionPane.showMessageDialog(this,
                            "No se encontraron viviendas "
                            + "en la ubicación indicada.");
                }
            }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Error al buscar viviendas:\n"
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
        jLabel9 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtIdentificacion = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtNombres = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtApellidos = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        txtCorreo = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        btnLimpiar = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtTotalPersonas = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        cmbVivienda = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        txtTotalPersonas1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        Volver = new javax.swing.JButton();
        lblFecha = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setBackground(new java.awt.Color(168, 221, 215));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("ENPH- Cuadernillo 2");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setText("Administrador");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addGap(16, 16, 16))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel9))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Registrar hogar y jefe del hogar");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 204, 255), 3));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("Información del jefe del hogar");

        jLabel13.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel13.setText("Número de identificación:");

        txtIdentificacion.addActionListener(this::txtIdentificacionActionPerformed);

        jLabel14.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel14.setText("Nombres:");

        jLabel15.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel15.setText("Apellidos:");

        jLabel16.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel16.setText("Teléfono:");

        jLabel19.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel19.setText("Coreo electrónico (opcional):");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtIdentificacion, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNombres, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(jLabel19))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIdentificacion, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombres, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        btnGuardar.setBackground(new java.awt.Color(102, 153, 255));
        btnGuardar.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnGuardar.setText("Guardar hogar");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 204, 255), 3));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setText("Registrar hogar");

        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel11.setText("Cantidad de personas");

        txtTotalPersonas.addActionListener(this::txtTotalPersonasActionPerformed);

        jLabel21.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel21.setText("Dirección de la vivienda");

        cmbVivienda.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        cmbVivienda.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione una vivienda" }));
        cmbVivienda.addActionListener(this::cmbViviendaActionPerformed);

        jLabel12.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel12.setText("Nombre del barrio:");

        jButton1.setText("Buscar");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)
                            .addComponent(txtTotalPersonas1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                            .addComponent(jLabel11)
                            .addComponent(txtTotalPersonas))
                        .addGap(89, 89, 89)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbVivienda, javax.swing.GroupLayout.PREFERRED_SIZE, 573, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addGap(18, 18, 18)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTotalPersonas1, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbVivienda, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTotalPersonas, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        Volver.setText("Volver");
        Volver.addActionListener(this::VolverActionPerformed);

        lblFecha.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Volver)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Volver)
                    .addComponent(lblFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addGap(20, 20, 20)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLimpiar)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        txtTotalPersonas1.setText("");

    cmbVivienda.removeAllItems();
    cmbVivienda.addItem("Seleccione la dirección");
    cmbVivienda.setEnabled(false);

    txtTotalPersonas.setText("");

    txtIdentificacion.setText("");
    txtNombres.setText("");
    txtApellidos.setText("");
    txtTelefono.setText("");
    txtCorreo.setText("");

    txtTotalPersonas1.requestFocusInWindow();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // TODO add your handling code here:
        
        String cantidadTexto = txtTotalPersonas.getText().trim();
        String identificacion = txtIdentificacion.getText().trim();
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();

        // Validar campos obligatorios
        if (cantidadTexto.isEmpty()
                || identificacion.isEmpty()
                || nombres.isEmpty()
                || apellidos.isEmpty()
                || telefono.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Complete todos los campos obligatorios.");
            return;
        }

        // Validar vivienda
        if (cmbVivienda.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una vivienda.");
            return;
        }

        int cantidadPersonas;

        try {
            cantidadPersonas = Integer.parseInt(cantidadTexto);

            if (cantidadPersonas <= 0) {
                JOptionPane.showMessageDialog(this,
                        "La cantidad de personas debe ser mayor que cero.");
                return;
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese un número válido de personas.");
            return;
        }

        // Validar longitudes
        if (identificacion.length() > 20
                || nombres.length() > 80
                || apellidos.length() > 80
                || telefono.length() > 20
                || correo.length() > 100) {

            JOptionPane.showMessageDialog(this,
                    "Uno de los campos supera la longitud permitida.");
            return;
        }

    try {

    // Capturar datos antes de iniciar el SwingWorker
       final Hogar hogarCapturado = capturarHogar();

       final JefeHogar jefeCapturado =
               capturarJefeHogar(hogarCapturado);

       btnGuardar.setEnabled(false);
       btnGuardar.setText("Guardando...");

        javax.swing.SwingWorker<Modelo.Encuesta, Void> trabajador =
                new javax.swing.SwingWorker<Modelo.Encuesta, Void>() {

            
        @Override
        protected Modelo.Encuesta doInBackground()
                throws Exception {

            software.Main controlador = new software.Main();

            System.out.println("ENPH: Registrando hogar...");

            Hogar hogarRegistrado =
                    controlador.crearHogar(
                            hogarCapturado,
                            jefeCapturado);

            System.out.println(
                    "ENPH: Hogar registrado: "
                    + hogarRegistrado.getId());

            Modelo.Encuesta encuesta =
                    controlador.crearEncuesta(hogarRegistrado);

            System.out.println(
                    "ENPH: Encuesta registrada: "
                    + encuesta.getId());

            return encuesta;
        }


            @Override
            protected void done() {

                btnGuardar.setEnabled(true);
                btnGuardar.setText("Guardar hogar");

                try {

                    Modelo.Encuesta encuesta = get();

                    // Método que agregamos en el paso anterior
                    presentarHogar(encuesta);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                    JOptionPane.showMessageDialog(PresentacionHogar.this,
                            "Se interrumpió el registro.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);

                } catch (java.util.concurrent.ExecutionException e) {

                    Throwable causa = e.getCause();

                    JOptionPane.showMessageDialog(PresentacionHogar.this,
                            "Error al registrar el hogar:\n"
                            + (causa != null
                                    ? causa.getMessage()
                                    : e.getMessage()),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        // Ejecutar sin bloquear la interfaz
        trabajador.execute();

    } catch (RuntimeException e) {

        btnGuardar.setEnabled(true);
        btnGuardar.setText("Guardar hogar");

        JOptionPane.showMessageDialog(
                this,
                "Error al seleccionar la vivienda:\n"
                + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    }//GEN-LAST:event_btnGuardarActionPerformed

    private void txtIdentificacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdentificacionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdentificacionActionPerformed

    private void VolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VolverActionPerformed
        // TODO add your handling code here:
        MenuPrincipalOficina menu = new MenuPrincipalOficina();
        menu.setLocationRelativeTo(null);
        menu.setVisible(true);

        this.dispose();
    }//GEN-LAST:event_VolverActionPerformed

    private void cmbViviendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbViviendaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbViviendaActionPerformed

    private void txtTotalPersonasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalPersonasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalPersonasActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        String barrio = capturarNombreBarrio();

    if (barrio.isEmpty()) {
        cmbVivienda.removeAllItems();
        cmbVivienda.addItem("Seleccione la dirección");
        cmbVivienda.setEnabled(false);

        JOptionPane.showMessageDialog(this,
            "Primero escriba el nombre del barrio.");
        return;
    }

    cargarViviendas(barrio);
    }//GEN-LAST:event_jButton1ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new PresentacionHogar().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Volver;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JComboBox<String> cmbVivienda;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JTextField txtApellidos;
    private javax.swing.JTextField txtCorreo;
    private javax.swing.JTextField txtIdentificacion;
    private javax.swing.JTextField txtNombres;
    private javax.swing.JTextField txtTelefono;
    private javax.swing.JTextField txtTotalPersonas;
    private javax.swing.JTextField txtTotalPersonas1;
    // End of variables declaration//GEN-END:variables
}
