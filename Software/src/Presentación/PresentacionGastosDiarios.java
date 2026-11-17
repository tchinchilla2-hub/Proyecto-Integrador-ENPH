package Presentación;

import Modelo.Encuesta;
import Modelo.FormaAdquisicion;
import Modelo.GastosDiarios;
import Modelo.LugarAdquisicion;
import Modelo.UnidadMedida;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.GridLayout;

/**
 * Adaptador de presentación para los incrementos 7, 7.2, 7.3 y 7.4 de Astah.
 * Los formularios de NetBeans (InformacionAlimentosAdquirido,
 * AdquisiciónClasificación, HistorialGastosDiarios y DetalleGastosDiarios)
 * siguen disponibles y no se reemplazan por esta clase.
 */
public class PresentacionGastosDiarios {

    public GastosDiarios capturarGastoDiario(Encuesta pEncuesta,
            FormaAdquisicion[] pFormas, LugarAdquisicion[] pLugares,
            UnidadMedida[] pUnidades) {
        if (pEncuesta == null || pEncuesta.getId() <= 0) {
            throw new IllegalArgumentException("Encuesta inválida.");
        }
        JTextField alimento = new JTextField();
        JTextField cantidad = new JTextField();
        JTextField valor = new JTextField();
        JComboBox<String> unidades = new JComboBox<>(nombresUnidades(pUnidades));
        JComboBox<String> formas = new JComboBox<>(nombresFormas(pFormas));
        JComboBox<String> lugares = new JComboBox<>(nombresLugares(pLugares));
        JComboBox<String> tipos = new JComboBox<>(new String[]{"Gasto del hogar", "Gasto personal"});
        JPanel campos = new JPanel(new GridLayout(0, 2, 8, 5));
        campos.add(new JLabel("Alimento / bebida")); campos.add(alimento);
        campos.add(new JLabel("Cantidad")); campos.add(cantidad);
        campos.add(new JLabel("Unidad")); campos.add(unidades);
        campos.add(new JLabel("Forma de adquisición")); campos.add(formas);
        campos.add(new JLabel("Lugar de adquisición")); campos.add(lugares);
        campos.add(new JLabel("Tipo de gasto")); campos.add(tipos);
        campos.add(new JLabel("Valor pagado o estimado")); campos.add(valor);
        if (JOptionPane.showConfirmDialog(null, campos, "Capturar gasto diario",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return null;
        try {
            double unidadesAdquiridas = Double.parseDouble(cantidad.getText().trim().replace(',', '.'));
            double pago = Double.parseDouble(valor.getText().trim().replace(',', '.'));
            if (alimento.getText().isBlank() || unidadesAdquiridas <= 0 || pago < 0) {
                throw new IllegalArgumentException("Ingrese un alimento, cantidad positiva y valor no negativo.");
            }
            if (unidades.getItemCount() == 0 || formas.getItemCount() == 0 || lugares.getItemCount() == 0) {
                throw new IllegalArgumentException("Faltan catálogos de unidad, forma o lugar de adquisición.");
            }
            GastosDiarios gasto = new GastosDiarios();
            gasto.setEncuesta(pEncuesta);
            gasto.setNombreAlimento(alimento.getText().trim());
            gasto.setCantidad(unidadesAdquiridas);
            gasto.setUnidadMedida((String) unidades.getSelectedItem());
            gasto.setFormaAdquisicion((String) formas.getSelectedItem());
            gasto.setLugarCompra((String) lugares.getSelectedItem());
            gasto.setTipoGasto((String) tipos.getSelectedItem());
            gasto.setValorPagado(pago);
            gasto.setFecha(LocalDate.now());
            if ("Compra".equalsIgnoreCase(gasto.getFormaAdquisicion())) {
                Object frecuencia = JOptionPane.showInputDialog(null, "Frecuencia de compra:",
                        "Frecuencia", JOptionPane.QUESTION_MESSAGE, null,
                        new String[]{"Diario", "Varias veces por semana", "Semanal", "Quincenal",
                                "Mensual", "Bimestral", "Trimestral", "Esporádico"}, "Diario");
                if (frecuencia == null) return null;
                gasto.setFrecuenciaCompra(frecuencia.toString());
            }
            return gasto;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cantidad y valor deben ser números.", e);
        }
    }

    private static String[] nombresFormas(FormaAdquisicion[] a) {
        return a == null ? new String[0] : Arrays.stream(a).filter(x -> x != null)
                .map(FormaAdquisicion::getNombre).toArray(String[]::new);
    }
    private static String[] nombresLugares(LugarAdquisicion[] a) {
        return a == null ? new String[0] : Arrays.stream(a).filter(x -> x != null)
                .map(LugarAdquisicion::getNombre).toArray(String[]::new);
    }
    private static String[] nombresUnidades(UnidadMedida[] a) {
        return a == null ? new String[0] : Arrays.stream(a).filter(x -> x != null)
                .map(UnidadMedida::getNombre).toArray(String[]::new);
    }

    public void presentarGastoDiario(GastosDiarios pGasto) {
        if (pGasto == null || pGasto.getId() <= 0) {
            throw new IllegalArgumentException("No se pudo registrar el gasto.");
        }
        JOptionPane.showMessageDialog(null,
                "Gasto registrado correctamente: " + pGasto.getId());
    }

    public Date capturarFechaGastos(Date[] pFechas) {
        if (pFechas == null || pFechas.length == 0) return null;
        Object seleccion = JOptionPane.showInputDialog(null,
                "Seleccione fecha del historial:", "Consulta de gastos",
                JOptionPane.QUESTION_MESSAGE, null, pFechas, pFechas[0]);
        return seleccion instanceof Date ? (Date) seleccion : null;
    }

    public void presentarDetalleGastosDia(GastosDiarios[] pGastos) {
        if (pGastos == null || pGastos.length == 0) {
            JOptionPane.showMessageDialog(null, "No hay gastos registrados en esa fecha.");
            return;
        }
        Object[][] filas = new Object[pGastos.length][5];
        for (int i = 0; i < pGastos.length; i++) {
            GastosDiarios g = pGastos[i];
            filas[i] = new Object[]{g.getId(), g.getNombreAlimento(), g.getCantidad(),
                    g.getUnidadMedida(), g.getValorPagado()};
        }
        JTable tabla = new JTable(filas,
                new String[]{"ID", "Alimento", "Cantidad", "Unidad", "Valor"});
        JOptionPane.showMessageDialog(null, new JScrollPane(tabla),
                "Detalle de gastos", JOptionPane.PLAIN_MESSAGE);
    }

    public GastosDiarios capturarEdicionGasto(GastosDiarios pGasto,
            FormaAdquisicion[] pFormas, LugarAdquisicion[] pLugares,
            UnidadMedida[] pUnidades) {
        if (pGasto == null || pGasto.getId() <= 0) {
            throw new IllegalArgumentException("Seleccione un gasto registrado.");
        }
        JTextField alimento = new JTextField(pGasto.getNombreAlimento());
        JTextField cantidad = new JTextField(Double.toString(pGasto.getCantidad()));
        JTextField valor = new JTextField(Double.toString(pGasto.getValorPagado()));
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Alimento")); panel.add(alimento);
        panel.add(new JLabel("Cantidad")); panel.add(cantidad);
        panel.add(new JLabel("Valor")); panel.add(valor);
        if (JOptionPane.showConfirmDialog(null, panel, "Editar gasto",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return null;
        try {
            double nuevaCantidad = Double.parseDouble(cantidad.getText().trim().replace(',', '.'));
            double nuevoValor = Double.parseDouble(valor.getText().trim().replace(',', '.'));
            if (alimento.getText().isBlank() || nuevaCantidad <= 0 || nuevoValor < 0) {
                throw new IllegalArgumentException("Revise los datos del gasto.");
            }
            pGasto.setNombreAlimento(alimento.getText().trim());
            pGasto.setCantidad(nuevaCantidad);
            pGasto.setValorPagado(nuevoValor);
            return pGasto;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Cantidad y valor deben ser numéricos.", ex);
        }
    }

    public boolean capturarConfirmacionDia() {
        return JOptionPane.showConfirmDialog(null,
                "¿Confirma que todos los gastos de este día fueron revisados?",
                "Confirmar revisión", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
