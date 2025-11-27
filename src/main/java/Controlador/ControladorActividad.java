/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Actividad;
import Modelo.ActividadDAO;
import Modelo.Monitor;
import Utilidad.GestionTablasActividad;
import Vista.VistaActividades;
import Vista.VistaCRUDActividad;
import Vista.VistaCRUDActualizarActividad;
import Vista.VistaMensajes;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Controlador Actividad
 *
 * @author freew
 */
public class ControladorActividad implements ActionListener {

    private SessionFactory sessionFactory = null;

    private ActividadDAO actividadDAO;
    private VistaActividades vActividad;
    private VistaCRUDActividad vCRUDActividad;
    private VistaCRUDActualizarActividad vCRUDActualizarActividad;

    private final VistaMensajes vMensajes;

    private Session sesion;
    private Transaction tr;

    private Actividad Actividad;
    private GestionTablasActividad GestionTablasActividad;

    private String idActividad;
    private String nombreActividad;
    private String nombreMonitor;

    private int fila;

    private void addListeners() {
        vActividad.jButtonNuevaActividad.addActionListener(this);
        vActividad.jButtonBajaActividad.addActionListener(this);
        vActividad.jButtonActualizarActividad.addActionListener(this);

        vCRUDActividad.jButtonInsertarActividad.addActionListener(this);
        vCRUDActividad.jButtonCancelarActividad.addActionListener(this);

        vCRUDActualizarActividad.jButtonActualizarActividad.addActionListener(this);
        vCRUDActualizarActividad.jButtonCancelarActividad.addActionListener(this);
    }

    public ControladorActividad(SessionFactory sessionFactory, VistaActividades vActividad, ActividadDAO actividadDAO, VistaCRUDActividad vCRUDActividad, VistaCRUDActualizarActividad vCRUDActualizarActividad) {

        this.sessionFactory = sessionFactory;

        this.vMensajes = new VistaMensajes();

        if (this.sessionFactory != null) {

            this.vActividad = vActividad;
            this.actividadDAO = actividadDAO;
            this.vCRUDActividad = vCRUDActividad;
            this.vCRUDActualizarActividad = vCRUDActualizarActividad;

            addListeners();

            this.Actividad = new Actividad();
            this.GestionTablasActividad = new GestionTablasActividad();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {

            case "NuevaActividad":
                sesion = sessionFactory.openSession();

                idActividad = actividadDAO.ultimaActividad(sesion);
                vCRUDActividad.jTextIdActividad.setText(idActividad);

                try {
                    MonitoresResponsables(sesion);
                } catch (Exception ex) {
                    vMensajes.Mensaje(null, "error", "Error con los monitores responsables");
                }

                vCRUDActividad.setLocationRelativeTo(null);
                vCRUDActividad.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                vCRUDActividad.setResizable(false);
                vCRUDActividad.setVisible(true);

                break;

            case "InsertarActividad":
                sesion = sessionFactory.openSession();
                tr = sesion.beginTransaction();

                Actividad.setIdActividad(vCRUDActividad.jTextIdActividad.getText());
                Actividad.setNombre(vCRUDActividad.jTextFieldNombreActividad.getText());
                Actividad.setDescripcion(vCRUDActividad.jTextFieldDescripcionActividad.getText());

                if (vCRUDActividad.jTextFieldPrecioActividad.getText().isEmpty()) {
                    vMensajes.Mensaje(null, "error", "Tienes que rellenar el precio");
                    return;
                }

                Actividad.setPrecioBaseMes(Integer.parseInt(vCRUDActividad.jTextFieldPrecioActividad.getText()));

                Actividad.setDia(vCRUDActividad.jComboBoxDia.getSelectedItem().toString());
                Actividad.setHora(Integer.parseInt(vCRUDActividad.jComboBoxHora.getSelectedItem().toString()));

                nombreMonitor = vCRUDActividad.jComboBoxMonitor.getSelectedItem().toString();

                if (Actividad.getNombre().isEmpty() || Actividad.getDescripcion().isEmpty()) {
                    vMensajes.Mensaje(null, "error", "Faltan datos por rellenar");
                    return;
                }

                if (Actividad.getPrecioBaseMes() < 0) {
                    vMensajes.Mensaje(null, "error", "El precio tiene que ser positivo");
                    return;
                }

                try {
                    Actividad.setMonitorResponsable(actividadDAO.MonitorResponsable(sesion, nombreMonitor));

                    List<Monitor> monitoresOcupados = actividadDAO.MonitoresOcupados(sesion, Actividad.getDia(), Actividad.getHora());

                    if (monitoresOcupados.contains(Actividad.getMonitorResponsable())) {
                        vMensajes.Mensaje(null, "error", "El monitor esta ocupado ese día a esa hora");
                        return;
                    }

                    actividadDAO.insertarActividad(sesion, Actividad);
                    tr.commit();
                    vMensajes.Mensaje(null, "info", "Actividad insertada correctamente");

                    ArrayList<Actividad> lActividades = pideActividades();
                    GestionTablasActividad.vaciarTablaActividades();
                    GestionTablasActividad.rellenarTablaActividades(lActividades);

                    vCRUDActividad.dispose();
                } catch (Exception ex) {
                    if (tr != null) {
                        tr.rollback();
                    }
                    vMensajes.Mensaje(null, "error", "Error al insertar la actividad");
                } finally {
                    if (sesion != null && sesion.isOpen()) {
                        sesion.close();
                    }
                }

                break;

            case "CancelarInsertar":
                vCRUDActividad.dispose();

                break;

            case "BajaActividad":
                sesion = sessionFactory.openSession();
                fila = vActividad.jTableActividades.getSelectedRow();

                if (fila == -1) {
                    vMensajes.Mensaje(null, "error", "Tienes que seleccionar una actividad");
                    return;
                }

                idActividad = vActividad.jTableActividades.getValueAt(fila, 0).toString();
                nombreActividad = vActividad.jTableActividades.getValueAt(fila, 1).toString();

                if (vMensajes.Confirmacion("¿Seguro que quieres dar de baja " + nombreActividad + "?")) {
                    try {
                        actividadDAO.eliminarActividad(sesion, idActividad);
                        vMensajes.Mensaje(null, "info", "Actividad eliminada correctamente");

                        ArrayList<Actividad> lActividad = pideActividades();
                        GestionTablasActividad.vaciarTablaActividades();
                        GestionTablasActividad.rellenarTablaActividades(lActividad);
                    } catch (Exception ex) {
                        vMensajes.Mensaje(null, "error", "Error al eliminar la actividad");
                    }
                }

                break;

            case "ActualizarActividad":
                sesion = sessionFactory.openSession();
                fila = vActividad.jTableActividades.getSelectedRow();

                if (fila == -1) {
                    vMensajes.Mensaje(null, "error", "Tienes que seleccionar una actividad");
                    return;
                }

                idActividad = vActividad.jTableActividades.getValueAt(fila, 0).toString();
                Actividad = sesion.get(Actividad.class, idActividad);

                if (Actividad == null) {
                    vMensajes.Mensaje(null, "error", "Actividad no encontrada");
                    return;
                }

                vCRUDActualizarActividad.jTextIdActividad.setText(Actividad.getIdActividad());
                vCRUDActualizarActividad.jTextFieldNombreActividad.setText(Actividad.getNombre());
                vCRUDActualizarActividad.jTextFieldDescripcionActividad.setText(Actividad.getDescripcion());
                vCRUDActualizarActividad.jTextFieldPrecioActividad.setText(String.valueOf(Actividad.getPrecioBaseMes()));

                vCRUDActualizarActividad.jComboBoxDia.setSelectedItem(Actividad.getDia());
                vCRUDActualizarActividad.jComboBoxHora.setSelectedItem(String.valueOf(Actividad.getHora()));

                try {
                    MonitoresDisponibles(sesion, Actividad);
                } catch (Exception ex) {
                    vMensajes.Mensaje(null, "error", "Error con los monitores disponibles");
                }

                vCRUDActualizarActividad.setLocationRelativeTo(null);
                vCRUDActualizarActividad.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                vCRUDActualizarActividad.setResizable(false);
                vCRUDActualizarActividad.setVisible(true);

                sesion.close();

                break;

            case "Actualizar":
                sesion = sessionFactory.openSession();
                tr = sesion.beginTransaction();

                Actividad.setNombre(vCRUDActualizarActividad.jTextFieldNombreActividad.getText());
                Actividad.setDescripcion(vCRUDActualizarActividad.jTextFieldDescripcionActividad.getText());

                if (vCRUDActualizarActividad.jTextFieldPrecioActividad.getText().isEmpty()) {
                    vMensajes.Mensaje(null, "error", "Tienes que rellenar el precio");
                    return;
                }

                Actividad.setPrecioBaseMes(Integer.parseInt(vCRUDActualizarActividad.jTextFieldPrecioActividad.getText()));

                Actividad.setDia(vCRUDActualizarActividad.jComboBoxDia.getSelectedItem().toString());
                Actividad.setHora(Integer.parseInt(vCRUDActualizarActividad.jComboBoxHora.getSelectedItem().toString()));

                nombreMonitor = vCRUDActualizarActividad.jComboBoxMonitor.getSelectedItem().toString();

                if (Actividad.getNombre().isEmpty() || Actividad.getDescripcion().isEmpty()) {
                    vMensajes.Mensaje(null, "error", "Faltan datos por rellenar");
                    return;
                }

                if (Actividad.getPrecioBaseMes() < 0) {
                    vMensajes.Mensaje(null, "error", "El precio tiene que ser positivo");
                    return;
                }

                try {
                    Actividad.setMonitorResponsable(actividadDAO.MonitorResponsable(sesion, nombreMonitor));

                    List<Monitor> monitoresOcupados = actividadDAO.MonitoresOcupados(sesion, Actividad.getDia(), Actividad.getHora());

                    if (monitoresOcupados.contains(Actividad.getMonitorResponsable())) {
                        vMensajes.Mensaje(null, "error", "El monitor esta ocupado ese día a esa hora");
                        return;
                    }

                    actividadDAO.actualizarActividad(sesion, Actividad);
                    tr.commit();
                    vMensajes.Mensaje(null, "info", "Actividad actualizada correctamente");

                    ArrayList<Actividad> lActividades = pideActividades();
                    GestionTablasActividad.vaciarTablaActividades();
                    GestionTablasActividad.rellenarTablaActividades(lActividades);

                    vCRUDActualizarActividad.dispose();
                } catch (Exception ex) {
                    if (tr != null) {
                        tr.rollback();
                    }
                    vMensajes.Mensaje(null, "error", "Error al actualizar la actividad");
                } finally {
                    if (sesion != null && sesion.isOpen()) {
                        sesion.close();
                    }
                }

                break;

            case "CancelarActualizar":
                vCRUDActualizarActividad.dispose();

                break;
        }
    }

    private ArrayList<Actividad> pideActividades() throws Exception {
        ArrayList<Actividad> lActividades = actividadDAO.listaActividades(sesion);
        return lActividades;
    }

    private void MonitoresResponsables(Session sesion) throws Exception {
        List<Monitor> monitoresResponsables = actividadDAO.MonitoresDisponibles(sesion);
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>();

        for (Monitor monitor : monitoresResponsables) {
            modelo.addElement(monitor.getNombre());
        }

        vCRUDActividad.jComboBoxMonitor.setModel(modelo);
    }

    private void MonitoresDisponibles(Session sesion, Actividad actividad) throws Exception {
        List<Monitor> monitoresDisponibles = actividadDAO.MonitoresDisponibles(sesion);
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>();

        for (Monitor monitor : monitoresDisponibles) {
            modelo.addElement(monitor.getNombre());
        }

        vCRUDActualizarActividad.jComboBoxMonitor.setModel(modelo);

        Monitor monitorResponsable = actividad.getMonitorResponsable();

        if (monitorResponsable != null) {
            modelo.setSelectedItem(monitorResponsable.getNombre());
        }
    }

}
