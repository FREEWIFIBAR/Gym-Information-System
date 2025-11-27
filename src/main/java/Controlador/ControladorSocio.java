/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Socio;
import Modelo.SocioDAO;
import Utilidad.GestionTablasSocio;
import Vista.VistaCRUDActualizarSocio;
import Vista.VistaCRUDSocio;
import Vista.VistaInscripcionAlta;
import Vista.VistaInscripcionBaja;
import Vista.VistaInscripciones;
import Vista.VistaMensajes;
import Vista.VistaSocios;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Cotrolador Socio
 *
 * @author freew
 */
public class ControladorSocio implements ActionListener {

    private SessionFactory sessionFactory = null;

    private SocioDAO socioDAO;
    private VistaSocios vSocio;
    private VistaCRUDSocio vCRUDSocio;
    private VistaCRUDActualizarSocio vCRUDActualizarSocio;

    private final VistaMensajes vMensajes;

    private Session sesion;
    private Transaction tr;

    private Socio Socio;
    private GestionTablasSocio GestionTablasSocio;

    private String numSocio;
    private String nombreSocio;

    private int fila;

    private SimpleDateFormat formatoFecha;
    private Date fechaChooserNac;
    private Date fechaChooser;
    private Date fechaActual;

    private VistaInscripciones vInscripcion;

    private VistaInscripcionAlta vInscripcionAlta;
    private VistaInscripcionBaja vInscripcionBaja;

    private void addListeners() {
        vSocio.jButtonNuevoSocio.addActionListener(this);
        vSocio.jButtonBajaSocio.addActionListener(this);
        vSocio.jButtonActualizarSocio.addActionListener(this);

        vCRUDSocio.jButtonInsertarSocio.addActionListener(this);
        vCRUDSocio.jButtonCancelarSocio.addActionListener(this);

        vCRUDActualizarSocio.jButtonActualizarSocio.addActionListener(this);
        vCRUDActualizarSocio.jButtonCancelarSocio.addActionListener(this);

        vInscripcion.jButtonAlta.addActionListener(this);
        vInscripcion.jButtonBaja.addActionListener(this);

        vInscripcionAlta.jButtonAltaSocio.addActionListener(this);
        vInscripcionAlta.jButtonCancelarAlta.addActionListener(this);

        vInscripcionBaja.jButtonBajaSocio.addActionListener(this);
        vInscripcionBaja.jButtonCancelarBaja.addActionListener(this);

        vSocio.jButtonFiltro.addActionListener(this);
    }

    public ControladorSocio(SessionFactory sessionFactory, VistaSocios vSocio, SocioDAO socioDAO, VistaCRUDSocio vCRUDSocio, VistaCRUDActualizarSocio vCRUDActualizarSocio, VistaInscripciones vInscripcion, VistaInscripcionAlta vInscripcionAlta, VistaInscripcionBaja vInscripcionBaja) {

        this.sessionFactory = sessionFactory;

        this.vMensajes = new VistaMensajes();

        if (this.sessionFactory != null) {

            this.vSocio = vSocio;
            this.socioDAO = socioDAO;
            this.vCRUDSocio = vCRUDSocio;
            this.vCRUDActualizarSocio = vCRUDActualizarSocio;
            this.vInscripcion = vInscripcion;
            this.vInscripcionAlta = vInscripcionAlta;
            this.vInscripcionBaja = vInscripcionBaja;

            addListeners();

            this.Socio = new Socio();
            this.GestionTablasSocio = new GestionTablasSocio();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {

            case "NuevoSocio":
                sesion = sessionFactory.openSession();

                numSocio = socioDAO.ultimoSocio(sesion);
                vCRUDSocio.jTextFieldNumeroSocio.setText(numSocio);

                vCRUDSocio.setLocationRelativeTo(null);
                vCRUDSocio.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                vCRUDSocio.setResizable(false);
                vCRUDSocio.setVisible(true);

                break;

            case "InsertarSocio":
                sesion = sessionFactory.openSession();
                tr = sesion.beginTransaction();

                Socio.setNumeroSocio(vCRUDSocio.jTextFieldNumeroSocio.getText());
                Socio.setNombre(vCRUDSocio.jTextFieldNombreSocio.getText());
                Socio.setDni(vCRUDSocio.jTextFieldDniSocio.getText());
                Socio.setTelefono(vCRUDSocio.jTextFieldTelefonoSocio.getText());
                Socio.setCorreo(vCRUDSocio.jTextFieldCorreoSocio.getText());

                formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
                fechaChooserNac = vCRUDSocio.jDateChooserNacimientoSocio.getDate();

                if (fechaChooserNac != null) {
                    String fechaString = formatoFecha.format(fechaChooserNac);
                    Socio.setFechaNacimiento(fechaString);
                }

                fechaChooser = vCRUDSocio.jDateChooserAltaSocio.getDate();

                if (fechaChooser != null) {
                    String fechaString = formatoFecha.format(fechaChooser);
                    Socio.setFechaEntrada(fechaString);
                }

                Socio.setCategoria(vCRUDSocio.jComboBoxCategoria.getSelectedItem().toString().charAt(0));

                if (Socio.getNombre().isEmpty() || Socio.getDni().isEmpty() || Socio.getFechaNacimiento() == null || Socio.getTelefono().isEmpty() || Socio.getCorreo().isEmpty() || Socio.getFechaEntrada() == null) {
                    vMensajes.Mensaje(null, "error", "Faltan datos por rellenar");
                    return;
                }

                if (!Socio.getDni().matches("\\d{8}[A-Z]")) {
                    vMensajes.Mensaje(null, "error", "El DNI es incorrecto");
                    return;
                }

                if (!Socio.getCorreo().matches(".+@.+\\..+")) {
                    vMensajes.Mensaje(null, "error", "El correo es incorrecto");
                    return;
                }

                if (!Socio.getTelefono().matches("\\d{9}")) {
                    vMensajes.Mensaje(null, "error", "El telefono es incorrecto");
                    return;
                }

                fechaActual = new Date();
                if (fechaChooser != null && fechaChooser.after(fechaActual)) {
                    vMensajes.Mensaje(null, "error", "La fecha de alta es incorrecta");
                    return;
                }

                if (fechaChooserNac != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(fechaChooserNac);
                    cal.add(Calendar.YEAR, 18);
                    Date mayorEdad = cal.getTime();
                    if (fechaActual.before(mayorEdad)) {
                        vMensajes.Mensaje(null, "error", "El socio tiene que ser mayor de 18 años");
                        return;
                    }
                }

                try {
                    socioDAO.insertarSocio(sesion, Socio);
                    tr.commit();
                    vMensajes.Mensaje(null, "info", "Socio insertado correctamente");

                    ArrayList<Socio> lSocios = pideSocios();
                    GestionTablasSocio.vaciarTablaSocios();
                    GestionTablasSocio.rellenarTablaSocios(lSocios);

                    vCRUDSocio.dispose();
                } catch (Exception ex) {
                    if (tr != null) {
                        tr.rollback();
                    }
                    vMensajes.Mensaje(null, "error", "Error al insertar el socio");
                } finally {
                    if (sesion != null && sesion.isOpen()) {
                        sesion.close();
                    }
                }

                break;

            case "CancelarInsertar":
                vCRUDSocio.dispose();

                break;

            case "BajaSocio":
                sesion = sessionFactory.openSession();
                fila = vSocio.jTableSocios.getSelectedRow();

                if (fila == -1) {
                    vMensajes.Mensaje(null, "error", "Tienes que seleccionar un socio");
                    return;
                }

                numSocio = vSocio.jTableSocios.getValueAt(fila, 0).toString();
                nombreSocio = vSocio.jTableSocios.getValueAt(fila, 1).toString();

                if (vMensajes.Confirmacion("¿Seguro que quieres dar de baja a " + nombreSocio + "?")) {
                    try {
                        socioDAO.eliminarSocio(sesion, numSocio);
                        vMensajes.Mensaje(null, "info", "Socio eliminado correctamente");

                        ArrayList<Socio> lSocio = pideSocios();
                        GestionTablasSocio.vaciarTablaSocios();
                        GestionTablasSocio.rellenarTablaSocios(lSocio);
                    } catch (Exception ex) {
                        vMensajes.Mensaje(null, "error", "Error al eliminar el socio");
                    }
                }

                break;

            case "ActualizarSocio":
                sesion = sessionFactory.openSession();
                fila = vSocio.jTableSocios.getSelectedRow();

                if (fila == -1) {
                    vMensajes.Mensaje(null, "error", "Tienes que seleccionar un socio");
                    return;
                }

                numSocio = vSocio.jTableSocios.getValueAt(fila, 0).toString();
                Socio = sesion.get(Socio.class, numSocio);

                if (Socio == null) {
                    vMensajes.Mensaje(null, "error", "Socio no encontrado");
                    return;
                }

                vCRUDActualizarSocio.jTextFieldNumeroSocio.setText(Socio.getNumeroSocio());
                vCRUDActualizarSocio.jTextFieldNombreSocio.setText(Socio.getNombre());
                vCRUDActualizarSocio.jTextFieldDniSocio.setText(Socio.getDni());
                vCRUDActualizarSocio.jTextFieldTelefonoSocio.setText(Socio.getTelefono());
                vCRUDActualizarSocio.jTextFieldCorreoSocio.setText(Socio.getCorreo());

                try {
                    formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
                    fechaChooserNac = formatoFecha.parse(Socio.getFechaNacimiento());
                    vCRUDActualizarSocio.jDateChooserNacimientoSocio.setDate(fechaChooserNac);

                    fechaChooser = formatoFecha.parse(Socio.getFechaEntrada());
                    vCRUDActualizarSocio.jDateChooserAltaSocio.setDate(fechaChooser);
                } catch (ParseException ex) {
                    vMensajes.Mensaje(null, "error", "Error con la fecha");
                }

                vCRUDActualizarSocio.jComboBoxCategoria.setSelectedItem(Socio.getCategoria().toString());

                vCRUDActualizarSocio.setLocationRelativeTo(null);
                vCRUDActualizarSocio.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                vCRUDActualizarSocio.setResizable(false);
                vCRUDActualizarSocio.setVisible(true);

                sesion.close();

                break;

            case "Actualizar":
                sesion = sessionFactory.openSession();
                tr = sesion.beginTransaction();

                Socio.setNombre(vCRUDActualizarSocio.jTextFieldNombreSocio.getText());
                Socio.setDni(vCRUDActualizarSocio.jTextFieldDniSocio.getText());
                Socio.setTelefono(vCRUDActualizarSocio.jTextFieldTelefonoSocio.getText());
                Socio.setCorreo(vCRUDActualizarSocio.jTextFieldCorreoSocio.getText());

                formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
                fechaChooserNac = vCRUDActualizarSocio.jDateChooserNacimientoSocio.getDate();

                if (fechaChooserNac != null) {
                    String fechaString = formatoFecha.format(fechaChooserNac);
                    Socio.setFechaNacimiento(fechaString);
                }

                fechaChooser = vCRUDActualizarSocio.jDateChooserAltaSocio.getDate();

                if (fechaChooser != null) {
                    String fechaString = formatoFecha.format(fechaChooser);
                    Socio.setFechaEntrada(fechaString);
                }

                Socio.setCategoria(vCRUDActualizarSocio.jComboBoxCategoria.getSelectedItem().toString().charAt(0));

                if (Socio.getNombre().isEmpty() || Socio.getDni().isEmpty() || Socio.getFechaNacimiento() == null || Socio.getTelefono().isEmpty() || Socio.getCorreo().isEmpty() || Socio.getFechaEntrada() == null) {
                    vMensajes.Mensaje(null, "error", "Faltan datos por rellenar");
                    return;
                }

                if (!Socio.getDni().matches("\\d{8}[A-Z]")) {
                    vMensajes.Mensaje(null, "error", "El DNI es incorrecto");
                    return;
                }

                if (!Socio.getCorreo().matches(".+@.+\\..+")) {
                    vMensajes.Mensaje(null, "error", "El correo es incorrecto");
                    return;
                }

                if (!Socio.getTelefono().matches("\\d{9}")) {
                    vMensajes.Mensaje(null, "error", "El telefono es incorrecto");
                    return;
                }

                fechaActual = new Date();
                if (fechaChooser != null && fechaChooser.after(fechaActual)) {
                    vMensajes.Mensaje(null, "error", "La fecha es incorrecta");
                    return;
                }

                if (fechaChooserNac != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(fechaChooserNac);
                    cal.add(Calendar.YEAR, 18);
                    Date mayorEdad = cal.getTime();
                    if (fechaActual.before(mayorEdad)) {
                        vMensajes.Mensaje(null, "error", "El socio tiene que ser mayor de 18 años");
                        return;
                    }
                }

                try {
                    socioDAO.actualizarSocio(sesion, Socio);
                    tr.commit();
                    vMensajes.Mensaje(null, "info", "Socio actualizado correctamente");

                    ArrayList<Socio> lSocios = pideSocios();
                    GestionTablasSocio.vaciarTablaSocios();
                    GestionTablasSocio.rellenarTablaSocios(lSocios);

                    vCRUDActualizarSocio.dispose();
                } catch (Exception ex) {
                    if (tr != null) {
                        tr.rollback();
                    }
                    vMensajes.Mensaje(null, "error", "Error al actualizar el socio");
                } finally {
                    if (sesion != null && sesion.isOpen()) {
                        sesion.close();
                    }
                }

                break;

            case "CancelarActualizar":
                vCRUDActualizarSocio.dispose();

                break;

            case "Alta":
                sesion = sessionFactory.openSession();

                vInscripcionAlta.setLocationRelativeTo(null);
                vInscripcionAlta.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                vInscripcionAlta.setResizable(false);
                vInscripcionAlta.setVisible(true);

                break;

            case "Baja":
                sesion = sessionFactory.openSession();

                vInscripcionBaja.setLocationRelativeTo(null);
                vInscripcionBaja.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                vInscripcionBaja.setResizable(false);
                vInscripcionBaja.setVisible(true);

                break;

            case "AltaDeSocio":

                break;

            case "CancelarAlta":
                vInscripcionAlta.dispose();

                break;

            case "BajaDeSocio":

                break;

            case "CancelarBaja":
                vInscripcionBaja.dispose();

                break;

            case "Buscar":
                sesion = sessionFactory.openSession();
                tr = sesion.beginTransaction();

                nombreSocio = vSocio.jTextFieldFiltro.getText();

                ArrayList<Socio> SociosFiltrados = (ArrayList<Socio>) socioDAO.FiltroSocio(sesion, nombreSocio);
                GestionTablasSocio.vaciarTablaSocios();
                GestionTablasSocio.rellenarTablaSocios(SociosFiltrados);
                vSocio.jTextFieldFiltro.setText("");
                tr.commit();

                break;
        }
    }

    private ArrayList<Socio> pideSocios() throws Exception {
        ArrayList<Socio> lSocios = socioDAO.listaSocios(sesion);
        return lSocios;
    }

}
