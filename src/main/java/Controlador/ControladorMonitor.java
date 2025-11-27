/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Monitor;
import Modelo.MonitorDAO;
import Utilidad.GestionTablasMonitor;
import Vista.VistaCRUDActualizarMonitor;
import Vista.VistaCRUDMonitor;
import Vista.VistaMensajes;
import Vista.VistaMonitores;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Controlador Monitor
 *
 * @author freew
 */
public class ControladorMonitor implements ActionListener {

    private SessionFactory sessionFactory = null;

    private MonitorDAO monitorDAO;
    private VistaMonitores vMonitor;
    private VistaCRUDMonitor vCRUDMonitor;
    private VistaCRUDActualizarMonitor vCRUDActualizarMonitor;

    private final VistaMensajes vMensajes;

    private Session sesion;
    private Transaction tr;

    private Monitor Monitor;
    private GestionTablasMonitor GestionTablasMonitor;

    private String codMonitor;
    private String nombreMonitor;

    private int fila;

    private SimpleDateFormat formatoFecha;
    private Date fechaChooser;
    private Date fechaActual;

    private void addListeners() {
        vMonitor.jButtonNuevoMonitor.addActionListener(this);
        vMonitor.jButtonBajaMonitor.addActionListener(this);
        vMonitor.jButtonActualizarMonitor.addActionListener(this);

        vCRUDMonitor.jButtonInsertarMonitor.addActionListener(this);
        vCRUDMonitor.jButtonCancelarMonitor.addActionListener(this);

        vCRUDActualizarMonitor.jButtonActualizarMonitor.addActionListener(this);
        vCRUDActualizarMonitor.jButtonCancelarMonitor.addActionListener(this);
    }

    public ControladorMonitor(SessionFactory sessionFactory, VistaMonitores vMonitor, MonitorDAO monitorDAO, VistaCRUDMonitor vCRUDMonitor, VistaCRUDActualizarMonitor vCRUDActualizarMonitor) {

        this.sessionFactory = sessionFactory;

        this.vMensajes = new VistaMensajes();

        if (this.sessionFactory != null) {

            this.vMonitor = vMonitor;
            this.monitorDAO = monitorDAO;
            this.vCRUDMonitor = vCRUDMonitor;
            this.vCRUDActualizarMonitor = vCRUDActualizarMonitor;

            addListeners();

            this.Monitor = new Monitor();
            this.GestionTablasMonitor = new GestionTablasMonitor();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {

            case "NuevoMonitor":
                sesion = sessionFactory.openSession();

                codMonitor = monitorDAO.ultimoMonitor(sesion);
                vCRUDMonitor.jTextFieldCodigoMonitor.setText(codMonitor);

                vCRUDMonitor.setLocationRelativeTo(null);
                vCRUDMonitor.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                vCRUDMonitor.setResizable(false);
                vCRUDMonitor.setVisible(true);

                break;

            case "InsertarMonitor":
                sesion = sessionFactory.openSession();
                tr = sesion.beginTransaction();

                Monitor.setCodMonitor(vCRUDMonitor.jTextFieldCodigoMonitor.getText());
                Monitor.setNombre(vCRUDMonitor.jTextFieldNombreMonitor.getText());
                Monitor.setDni(vCRUDMonitor.jTextFieldDniMonitor.getText());
                Monitor.setTelefono(vCRUDMonitor.jTextFieldTelefonoMonitor.getText());
                Monitor.setCorreo(vCRUDMonitor.jTextFieldCorreoMonitor.getText());
                Monitor.setNick(vCRUDMonitor.jTextFieldNickMonitor.getText());

                formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
                fechaChooser = vCRUDMonitor.jDateChooserEntradaMonitor.getDate();

                if (fechaChooser != null) {
                    String fechaString = formatoFecha.format(fechaChooser);
                    Monitor.setFechaEntrada(fechaString);
                }

                if (Monitor.getNombre().isEmpty() || Monitor.getDni().isEmpty() || Monitor.getTelefono().isEmpty() || Monitor.getCorreo().isEmpty() || Monitor.getFechaEntrada() == null || Monitor.getNick().isEmpty()) {
                    vMensajes.Mensaje(null, "error", "Faltan datos por rellenar");
                    return;
                }

                if (!Monitor.getDni().matches("\\d{8}[A-Z]")) {
                    vMensajes.Mensaje(null, "error", "El DNI es incorrecto");
                    return;
                }

                if (!Monitor.getCorreo().matches(".+@.+\\..+")) {
                    vMensajes.Mensaje(null, "error", "El correo es incorrecto");
                    return;
                }

                if (!Monitor.getTelefono().matches("\\d{9}")) {
                    vMensajes.Mensaje(null, "error", "El telefono es incorrecto");
                    return;
                }

                fechaActual = new Date();
                if (fechaChooser != null && fechaChooser.after(fechaActual)) {
                    vMensajes.Mensaje(null, "error", "La fecha es incorrecta");
                    return;
                }

                try {
                    monitorDAO.insertarMonitor(sesion, Monitor);
                    tr.commit();
                    vMensajes.Mensaje(null, "info", "Monitor insertado correctamente");

                    ArrayList<Monitor> lMonitores = pideMonitores();
                    GestionTablasMonitor.vaciarTablaMonitores();
                    GestionTablasMonitor.rellenarTablaMonitores(lMonitores);

                    vCRUDMonitor.dispose();
                } catch (Exception ex) {
                    if (tr != null) {
                        tr.rollback();
                    }
                    vMensajes.Mensaje(null, "error", "Error al insertar el monitor");
                } finally {
                    if (sesion != null && sesion.isOpen()) {
                        sesion.close();
                    }
                }

                break;

            case "CancelarInsertar":
                vCRUDMonitor.dispose();

                break;

            case "BajaMonitor":
                sesion = sessionFactory.openSession();
                fila = vMonitor.jTableMonitores.getSelectedRow();

                if (fila == -1) {
                    vMensajes.Mensaje(null, "error", "Tienes que seleccionar un monitor");
                    return;
                }

                codMonitor = vMonitor.jTableMonitores.getValueAt(fila, 0).toString();
                nombreMonitor = vMonitor.jTableMonitores.getValueAt(fila, 1).toString();

                if (vMensajes.Confirmacion("¿Seguro que quieres dar de baja a " + nombreMonitor + "?")) {
                    try {
                        monitorDAO.eliminarMonitor(sesion, codMonitor);
                        vMensajes.Mensaje(null, "info", "Monitor eliminado correctamente");

                        ArrayList<Monitor> lMonitores = pideMonitores();
                        GestionTablasMonitor.vaciarTablaMonitores();
                        GestionTablasMonitor.rellenarTablaMonitores(lMonitores);
                    } catch (Exception ex) {
                        vMensajes.Mensaje(null, "error", "Error al eliminar el monitor");
                    }
                }

                break;

            case "ActualizarMonitor":
                sesion = sessionFactory.openSession();
                fila = vMonitor.jTableMonitores.getSelectedRow();

                if (fila == -1) {
                    vMensajes.Mensaje(null, "error", "Tienes que seleccionar un monitor");
                    return;
                }

                codMonitor = vMonitor.jTableMonitores.getValueAt(fila, 0).toString();
                Monitor = sesion.get(Monitor.class, codMonitor);

                if (Monitor == null) {
                    vMensajes.Mensaje(null, "error", "Monitor no encontrado");
                    return;
                }

                vCRUDActualizarMonitor.jTextFieldCodigoMonitor.setText(Monitor.getCodMonitor());
                vCRUDActualizarMonitor.jTextFieldNombreMonitor.setText(Monitor.getNombre());
                vCRUDActualizarMonitor.jTextFieldDniMonitor.setText(Monitor.getDni());
                vCRUDActualizarMonitor.jTextFieldTelefonoMonitor.setText(Monitor.getTelefono());
                vCRUDActualizarMonitor.jTextFieldCorreoMonitor.setText(Monitor.getCorreo());
                vCRUDActualizarMonitor.jTextFieldNickMonitor.setText(Monitor.getNick());

                try {
                    formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
                    fechaChooser = formatoFecha.parse(Monitor.getFechaEntrada());
                    vCRUDActualizarMonitor.jDateChooserEntradaMonitor.setDate(fechaChooser);
                } catch (ParseException ex) {
                    vMensajes.Mensaje(null, "error", "Error con la fecha");
                }

                vCRUDActualizarMonitor.setLocationRelativeTo(null);
                vCRUDActualizarMonitor.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                vCRUDActualizarMonitor.setResizable(false);
                vCRUDActualizarMonitor.setVisible(true);

                sesion.close();

                break;

            case "Actualizar":
                sesion = sessionFactory.openSession();
                tr = sesion.beginTransaction();

                Monitor.setNombre(vCRUDActualizarMonitor.jTextFieldNombreMonitor.getText());
                Monitor.setDni(vCRUDActualizarMonitor.jTextFieldDniMonitor.getText());
                Monitor.setTelefono(vCRUDActualizarMonitor.jTextFieldTelefonoMonitor.getText());
                Monitor.setCorreo(vCRUDActualizarMonitor.jTextFieldCorreoMonitor.getText());
                Monitor.setNick(vCRUDActualizarMonitor.jTextFieldNickMonitor.getText());

                formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
                fechaChooser = vCRUDActualizarMonitor.jDateChooserEntradaMonitor.getDate();

                if (fechaChooser != null) {
                    String fechaString = formatoFecha.format(fechaChooser);
                    Monitor.setFechaEntrada(fechaString);
                }

                if (Monitor.getNombre().isEmpty() || Monitor.getDni().isEmpty() || Monitor.getTelefono().isEmpty() || Monitor.getCorreo().isEmpty() || Monitor.getFechaEntrada() == null || Monitor.getNick().isEmpty()) {
                    vMensajes.Mensaje(null, "error", "Faltan datos por rellenar");
                    return;
                }

                if (!Monitor.getDni().matches("\\d{8}[A-Z]")) {
                    vMensajes.Mensaje(null, "error", "El DNI es incorrecto");
                    return;
                }

                if (!Monitor.getCorreo().matches(".+@.+\\..+")) {
                    vMensajes.Mensaje(null, "error", "El correo es incorrecto");
                    return;
                }

                if (!Monitor.getTelefono().matches("\\d{9}")) {
                    vMensajes.Mensaje(null, "error", "El telefono es incorrecto");
                    return;
                }

                fechaActual = new Date();
                if (fechaChooser != null && fechaChooser.after(fechaActual)) {
                    vMensajes.Mensaje(null, "error", "La fecha es incorrecta");
                    return;
                }

                try {
                    monitorDAO.actualizarMonitor(sesion, Monitor);
                    tr.commit();
                    vMensajes.Mensaje(null, "info", "Monitor actualizado correctamente");

                    ArrayList<Monitor> lMonitores = pideMonitores();
                    GestionTablasMonitor.vaciarTablaMonitores();
                    GestionTablasMonitor.rellenarTablaMonitores(lMonitores);

                    vCRUDActualizarMonitor.dispose();
                } catch (Exception ex) {
                    if (tr != null) {
                        tr.rollback();
                    }
                    vMensajes.Mensaje(null, "error", "Error al actualizar el monitor");
                } finally {
                    if (sesion != null && sesion.isOpen()) {
                        sesion.close();
                    }
                }

                break;

            case "CancelarActualizar":
                vCRUDActualizarMonitor.dispose();

                break;
        }
    }

    private ArrayList<Monitor> pideMonitores() throws Exception {
        ArrayList<Monitor> lMonitores = monitorDAO.listaMonitores(sesion);
        return lMonitores;
    }

}
