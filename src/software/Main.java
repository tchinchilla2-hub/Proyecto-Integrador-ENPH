/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package software;
import Presentación.AccesoOficina;
import Presentación.PresentacionEncuestador;
import Modelo.Encuestador;
import javax.swing.SwingUtilities;
import Presentación.PresentacionVivienda;
import Modelo.Vivienda;
import Modelo.Barrio;
import Modelo.Hogar;
import Modelo.JefeHogar;
import Modelo.Encuesta;
import Modelo.Visita;
import Modelo.GastosDiarios;
import Datos.Persistencia;



/**
 *
 * @author thain
 */
public class Main {
    private PresentacionEncuestador presentacion;

    public Main() {
    }
    // Constructor que recibe el formulario abierto
    public Main(PresentacionEncuestador presentacion) {
        this.presentacion = presentacion;
    }

    // Punto de entrada del sistema
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            AccesoOficina ventana = new AccesoOficina();

            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);

        });
    }

    // Incremento: Registrar encuestador
    public void crearEncuestador() {

        Persistencia persistencia = new Persistencia();

        // Capturar los datos del formulario
        Encuestador encuestador =
                presentacion.capturarEncuestador();

        // Guardar el encuestador en Oracle
        encuestador =
                persistencia.grabarEncuestador(encuestador);

        // Presentar el resultado
        presentacion.presentarEncuestador(encuestador);
    }
    
    private PresentacionVivienda presentacionVivienda;

    // Constructor para registrar viviendas
    public Main(PresentacionVivienda presentacionVivienda) {
        this.presentacionVivienda = presentacionVivienda;
    }

    // Incremento 02: Crear vivienda
    public void crearVivienda() {

        // Capturar el barrio seleccionado
        Barrio barrio =
                presentacionVivienda.capturarBarrio();

        // Capturar los datos de la vivienda
        Vivienda vivienda =
                presentacionVivienda.capturarVivienda(barrio);

        // Registrar la vivienda en Oracle
        Persistencia persistencia = new Persistencia();

        vivienda = persistencia.grabarVivienda(vivienda);

        // Mostrar el resultado
        presentacionVivienda.presentarVivienda(vivienda);
    }
    
    public Hogar crearHogar(Hogar hogar, JefeHogar jefeHogar) {

    // Crear el objeto de persistencia
    Persistencia persistencia = new Persistencia();

    // Registrar hogar y jefe del hogar en Oracle
    Hogar hogarRegistrado =
            persistencia.grabarHogarYJefe(hogar, jefeHogar);

    // Devolver el hogar registrado
    return hogarRegistrado;
}
    
    public int asignarHogares() {

    Persistencia persistencia = new Persistencia();

    Hogar[] hogares =
            persistencia.recuperarHogaresDisponibles();

    Encuestador[] encuestadores =
            persistencia.recuperarEncuestadores();

    if (hogares.length == 0) {
        return 0;
    }

    if (encuestadores.length == 0) {
        throw new IllegalStateException(
                "No hay encuestadores registrados.");
    }

    // Consultar cuántos hogares tiene cada encuestador
    int[] cargas = new int[encuestadores.length];

    for (int i = 0; i < encuestadores.length; i++) {

        Hogar[] asignados =
                persistencia.recuperarHogaresAsignados(
                        encuestadores[i].getIdentificacion()
                );

        cargas[i] = asignados.length;
    }

    // Distribuir entre los encuestadores con menor carga
    for (Hogar hogar : hogares) {

        int indiceMenor = 0;

        for (int i = 1; i < cargas.length; i++) {

            if (cargas[i] < cargas[indiceMenor]) {
                indiceMenor = i;
            }
        }

        hogar.setEncuestador(
                encuestadores[indiceMenor]);

        cargas[indiceMenor]++;
    }

    // Método definido en el diagrama de Astah
    persistencia.actualizarAsignacionHogar(hogares);

    return hogares.length;
}



public Encuesta crearEncuesta(Hogar hogar) {

    if (hogar == null || hogar.getId() <= 0) {
        throw new IllegalArgumentException(
                "Debe seleccionar un hogar válido.");
    }

    Encuesta encuesta = new Encuesta(hogar);

    Persistencia persistencia = new Persistencia();

    return persistencia.grabarEncuesta(encuesta);
}

/** Variante interactiva definida por PresentacionEncuesta en Astah.
 * La creación automática del formulario de hogar conserva crearEncuesta(Hogar).
 */
public Encuesta crearEncuesta(Presentación.PresentacionEncuesta pantalla,
        Hogar[] hogaresDisponibles) {
    if (pantalla == null) {
        throw new IllegalArgumentException("Debe proporcionar una presentación de encuesta.");
    }
    Encuesta capturada = pantalla.capturarEncuesta(hogaresDisponibles);
    if (capturada == null) return null; // selección cancelada
    Encuesta registrada = crearEncuesta(capturada.getHogar());
    pantalla.presentarEncuesta(registrada);
    return registrada;
}

public void consultarCronograma(String identificacionEncuestador) {
    if (identificacionEncuestador == null || identificacionEncuestador.trim().isEmpty()) {
        throw new IllegalArgumentException("Debe identificar al encuestador.");
    }
    Persistencia persistencia = new Persistencia();
    Hogar[] hogares = persistencia.recuperarHogaresAsignados(identificacionEncuestador);
    Encuesta[] encuestas = new Encuesta[hogares.length];
    java.util.List<Visita> visitas = new java.util.ArrayList<>();
    for (int i = 0; i < hogares.length; i++) {
        Encuesta encuesta = persistencia.recuperarEncuestaHogar(hogares[i].getId());
        encuestas[i] = encuesta;
        if (encuesta != null) {
            for (Visita visita : persistencia.recuperarVisitasEncuesta(encuesta.getId())) {
                visitas.add(visita);
            }
        }
    }
    String nombre = persistencia.recuperarNombreEncuestador(identificacionEncuestador);
    Presentación.PresentacionCronograma ventana =
            new Presentación.PresentacionCronograma(identificacionEncuestador, false);
    ventana.presentarCronograma(hogares, encuestas, visitas.toArray(new Visita[0]));
    ventana.presentarNombreEncuestador(nombre);
    ventana.setLocationRelativeTo(null);
    ventana.setVisible(true);
}

// Incremento 06: confirmar únicamente visitas iniciadas y seleccionadas.
public void confirmarVisitasAtendidas(Visita[] visitasAtendidas) {
    if (visitasAtendidas == null || visitasAtendidas.length == 0) {
        throw new IllegalArgumentException("Seleccione visitas para confirmar.");
    }
    Persistencia persistencia = new Persistencia();
    for (Visita visita : visitasAtendidas) {
        if (visita == null || visita.getId() <= 0) {
            throw new IllegalArgumentException("Existe una visita sin identificar.");
        }
        persistencia.actualizarVisita(visita);
    }
}

public Visita iniciarVisita(Visita visita) {
 
    if (visita == null) {
        throw new IllegalArgumentException(
                "Debe proporcionar una visita válida.");
    }

    Persistencia persistencia = new Persistencia();

    Visita visitaGuardada = persistencia.grabarVisita(visita);
    return visitaGuardada;
}

public void finalizarVisita(Visita visita) {

    if (visita == null || visita.getId() <= 0) {
        throw new IllegalArgumentException(
                "Debe seleccionar una visita registrada.");
    }

    Persistencia persistencia = new Persistencia();

    persistencia.actualizarVisita(visita);
}


// Variante coordinada con la presentación del diagrama de Astah, sin reemplazar
// los formularios GUI ya existentes de registro de gastos.
public GastosDiarios registrarGastoDiario(Modelo.Encuesta encuesta,
        Presentación.PresentacionGastosDiarios pantalla) {
    if (pantalla == null) throw new IllegalArgumentException("Falta la pantalla de gastos.");
    Persistencia persistencia = new Persistencia();
    GastosDiarios gasto = pantalla.capturarGastoDiario(encuesta,
            persistencia.recuperarFormasAdquisicion(),
            persistencia.recuperarLugaresAdquisicion(),
            persistencia.recuperarUnidadesMedida());
    
    if (gasto == null) return null;
    GastosDiarios registrado = registrarGastoDiario(gasto);
    pantalla.presentarGastoDiario(registrado);
    return registrado;
}

public void consultarGastosDiarios(int idEncuesta,
        Presentación.PresentacionGastosDiarios pantalla) {
    if (pantalla == null) throw new IllegalArgumentException("Falta la pantalla de gastos.");
    java.sql.Date fecha = pantalla.capturarFechaGastos(consultarFechasGastos(idEncuesta));
    if (fecha != null) {
        pantalla.presentarDetalleGastosDia(
                consultarGastosDiarios(idEncuesta, fecha.toLocalDate()));
    }
}

// INCREMENTO 05: REGISTRAR GASTOS DIARIOS

public GastosDiarios registrarGastoDiario(GastosDiarios gasto) {

  if (gasto == null) {
        throw new IllegalArgumentException(
                "Debe proporcionar los datos del gasto."
        );
    }

    if (gasto.getEncuesta() == null
            || gasto.getEncuesta().getId() <= 0) {
        throw new IllegalArgumentException(
                "El gasto debe estar asociado a una encuesta válida."
        );
    }

    Persistencia persistencia = new Persistencia();

    return persistencia.grabarGastoDiario(gasto);
}



public GastosDiarios[] consultarGastosDiarios(int idEncuesta) {

    if (idEncuesta <= 0) {
        throw new IllegalArgumentException(
                "Debe proporcionar una encuesta válida.");
    }

    Persistencia persistencia = new Persistencia();

    GastosDiarios[] gastos =
            persistencia.recuperarGastosEncuesta(idEncuesta);

    return gastos;
}

public void confirmarGastosDia(int idEncuesta, java.time.LocalDate fecha) {
    if (idEncuesta <= 0 || fecha == null) {
        throw new IllegalArgumentException("Seleccione encuesta y fecha válidas.");
    }
    new Persistencia().actualizarRevisionGastosDia(
            idEncuesta, java.sql.Date.valueOf(fecha));
}

public GastosDiarios[] consultarGastosDiarios(int idEncuesta, java.time.LocalDate fecha) {
    if (idEncuesta <= 0 || fecha == null) {
        throw new IllegalArgumentException("Seleccione encuesta y fecha válidas.");
    }
    return new Persistencia().recuperarGastosPorFecha(
            idEncuesta, java.sql.Date.valueOf(fecha));
}

public java.sql.Date[] consultarFechasGastos(int idEncuesta) {
    return new Persistencia().recuperarHistorialGastos(idEncuesta);
}

public void finalizarRecoleccion(int idEncuesta) {
    Encuesta encuesta = new Persistencia().recuperarEncuesta(idEncuesta);
    if (encuesta == null) throw new IllegalArgumentException("Encuesta no encontrada.");
    new Persistencia().actualizarEncuesta(encuesta);
}

// Firma sin parámetros conservada por compatibilidad con el diagrama.
public void finalizarRecoleccion() {
    String codigo = software.DatosRecoleccion.codigoHogar;
    if (codigo == null || !codigo.matches("HG-\\d+")) {
        throw new IllegalStateException("Primero debe seleccionar un hogar válido.");
    }
    int idHogar = Integer.parseInt(codigo.substring(3));
    Encuesta encuesta = new Persistencia().recuperarEncuestaHogar(idHogar);
    if (encuesta == null) throw new IllegalStateException("El hogar no tiene encuesta.");
    finalizarRecoleccion(encuesta.getId());
}

public void generarScripts() {

	}
}


