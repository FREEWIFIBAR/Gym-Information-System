/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Actividad;
import Modelo.ActividadDAO;
import Modelo.Monitor;
import Modelo.MonitorDAO;
import Modelo.Socio;
import Modelo.SocioDAO;
import Utilidad.GestionTablasActividad;
import Utilidad.GestionTablasMonitor;
import Utilidad.GestionTablasSocio;
import Vista.VistaMensajes;
import Vista.VistaPrincipal;
import Vista.VistaActividades;
import Vista.VistaCRUDActividad;
import Vista.VistaCRUDActualizarActividad;
import Vista.VistaCRUDActualizarMonitor;
import Vista.VistaCRUDActualizarSocio;
import Vista.VistaCRUDMonitor;
import Vista.VistaCRUDSocio;
import Vista.VistaInicio;
import Vista.VistaInscripcionAlta;
import Vista.VistaInscripcionBaja;
import Vista.VistaInscripciones;
import Vista.VistaMonitores;
import Vista.VistaSocios;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Controlador Principal
 *
 * @author freew
 */
public class ControladorPrincipal implements ActionListener {

    private SessionFactory sessionFactory = null;

    private Session sesion;
    private Transaction tr;

    private final ControladorMonitor cm;
    private final ControladorSocio cs;
    private final ControladorActividad ca;

    private VistaPrincipal vPrincipal;
    private VistaInicio vInicio;
    private VistaMonitores vMonitor;
    private VistaSocios vSocio;
    private VistaActividades vActividad;
    private VistaInscripciones vInscripcion;

    private MonitorDAO monitorDAO;
    private SocioDAO socioDAO;
    private ActividadDAO actividadDAO;

    private GestionTablasMonitor GestionTablasMonitor;
    private GestionTablasSocio GestionTablasSocio;
    private GestionTablasActividad GestionTablasActividad;

    private VistaCRUDMonitor vCRUDMonitor;
    private VistaCRUDSocio vCRUDSocio;
    private VistaCRUDActividad vCRUDActividad;

    private VistaCRUDActualizarMonitor vCRUDActualizarMonitor;
    private VistaCRUDActualizarSocio vCRUDActualizarSocio;
    private VistaCRUDActualizarActividad vCRUDActualizarActividad;

    private VistaInscripcionAlta vInscripcionAlta;
    private VistaInscripcionBaja vInscripcionBaja;

    private final VistaMensajes vMensajes;

    private void addListeners() {
        vPrincipal.jMenuItemInicio.addActionListener(this);
        vPrincipal.jMenuItemMonitor.addActionListener(this);
        vPrincipal.jMenuItemSocio.addActionListener(this);
        vPrincipal.jMenuItemActividad.addActionListener(this);
        vPrincipal.jMenuItemInscripcion.addActionListener(this);
        vPrincipal.jMenuItemSalir.addActionListener(this);
    }

    public ControladorPrincipal(SessionFactory sessionFactory) {

        this.sessionFactory = sessionFactory;

        this.vMensajes = new VistaMensajes();

        if (this.sessionFactory != null) {

            this.vPrincipal = new VistaPrincipal();
            this.vInicio = new VistaInicio();
            this.vMonitor = new VistaMonitores();
            this.vSocio = new VistaSocios();
            this.vActividad = new VistaActividades();
            this.vInscripcion = new VistaInscripciones();

            this.monitorDAO = new MonitorDAO();
            this.socioDAO = new SocioDAO();
            this.actividadDAO = new ActividadDAO();

            this.GestionTablasMonitor = new GestionTablasMonitor();
            this.GestionTablasSocio = new GestionTablasSocio();
            this.GestionTablasActividad = new GestionTablasActividad();

            this.vCRUDMonitor = new VistaCRUDMonitor();
            this.vCRUDSocio = new VistaCRUDSocio();
            this.vCRUDActividad = new VistaCRUDActividad();

            this.vCRUDActualizarMonitor = new VistaCRUDActualizarMonitor();
            this.vCRUDActualizarSocio = new VistaCRUDActualizarSocio();
            this.vCRUDActualizarActividad = new VistaCRUDActualizarActividad();

            this.vInscripcionAlta = new VistaInscripcionAlta();
            this.vInscripcionBaja = new VistaInscripcionBaja();

            GestionTablasMonitor.inicializarTablaMonitores(vMonitor);
            GestionTablasSocio.inicializarTablaSocios(vSocio);
            GestionTablasActividad.inicializarTablaActividades(vActividad);

            vPrincipal.getContentPane().setLayout(new CardLayout());
            vPrincipal.add(vInicio);
            vPrincipal.add(vMonitor);
            vPrincipal.add(vSocio);
            vPrincipal.add(vActividad);
            vPrincipal.add(vInscripcion);

            addListeners();

            vPrincipal.setLocationRelativeTo(null);
            vPrincipal.setVisible(true);

            vInicio.setVisible(true);
            vMonitor.setVisible(false);
            vSocio.setVisible(false);
            vActividad.setVisible(false);
            vInscripcion.setVisible(false);

        } else {
            System.out.println("No se pudo establecer la conexión a la base de datos. Saliendo...");
            System.exit(1);
        }

        cm = new ControladorMonitor(sessionFactory, vMonitor, monitorDAO, vCRUDMonitor, vCRUDActualizarMonitor);
        cs = new ControladorSocio(sessionFactory, vSocio, socioDAO, vCRUDSocio, vCRUDActualizarSocio, vInscripcion, vInscripcionAlta, vInscripcionBaja);
        ca = new ControladorActividad(sessionFactory, vActividad, actividadDAO, vCRUDActividad, vCRUDActualizarActividad);
    }

    private ArrayList<Monitor> pideMonitores() throws Exception {
        ArrayList<Monitor> lMonitores = monitorDAO.listaMonitores(sesion);
        return lMonitores;
    }

    private ArrayList<Socio> pideSocios() throws Exception {
        ArrayList<Socio> lSocios = socioDAO.listaSocios(sesion);
        return lSocios;
    }

    private ArrayList<Actividad> pideActividades() throws Exception {
        ArrayList<Actividad> lActividades = actividadDAO.listaActividades(sesion);
        return lActividades;
    }

    private void SociosDisponibles(Session sesion) throws Exception {
        List<Socio> sociosDisponibles = socioDAO.SociosDisponibles(sesion);
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>();

        for (Socio socio : sociosDisponibles) {
            modelo.addElement(socio.getNombre());
        }

        vInscripcion.jComboBoxSocio.setModel(modelo);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {

            case "Inicio":
                vInicio.setVisible(true);
                vMonitor.setVisible(false);
                vSocio.setVisible(false);
                vActividad.setVisible(false);
                vInscripcion.setVisible(false);

                break;

            case "GestionMonitores":
                vInicio.setVisible(false);
                vMonitor.setVisible(true);
                vSocio.setVisible(false);
                vActividad.setVisible(false);
                vInscripcion.setVisible(false);

                GestionTablasMonitor.dibujarTablaMonitores(vMonitor);
                sesion = sessionFactory.openSession();
                tr = sesion.beginTransaction();
                try {
                    ArrayList<Monitor> lMonitores = pideMonitores();
                    GestionTablasMonitor.vaciarTablaMonitores();
                    GestionTablasMonitor.rellenarTablaMonitores(lMonitores);
                    tr.commit();
                } catch (Exception ex) {
                    tr.rollback();
                    vMensajes.Mensaje(null, "error", "Error en la petición de Monitores\n" + ex.getMessage());
                } finally {
                    if (sesion != null && sesion.isOpen()) {
                        sesion.close();
                    }
                }

                break;

            case "GestionSocios":
                vInicio.setVisible(false);
                vMonitor.setVisible(false);
                vSocio.setVisible(true);
                vActividad.setVisible(false);
                vInscripcion.setVisible(false);

                GestionTablasSocio.dibujarTablaSocios(vSocio);
                sesion = sessionFactory.openSession();
                tr = sesion.beginTransaction();
                try {
                    ArrayList<Socio> lSocios = pideSocios();
                    GestionTablasSocio.vaciarTablaSocios();
                    GestionTablasSocio.rellenarTablaSocios(lSocios);
                    tr.commit();
                } catch (Exception ex) {
                    tr.rollback();
                    vMensajes.Mensaje(null, "error", "Error en la petición de Socios\n" + ex.getMessage());
                } finally {
                    if (sesion != null && sesion.isOpen()) {
                        sesion.close();
                    }
                }

                break;

            case "GestionActividades":
                vInicio.setVisible(false);
                vMonitor.setVisible(false);
                vSocio.setVisible(false);
                vActividad.setVisible(true);
                vInscripcion.setVisible(false);

                GestionTablasActividad.dibujarTablaActividades(vActividad);
                sesion = sessionFactory.openSession();
                tr = sesion.beginTransaction();
                try {
                    ArrayList<Actividad> lActividades = pideActividades();
                    GestionTablasActividad.vaciarTablaActividades();
                    GestionTablasActividad.rellenarTablaActividades(lActividades);
                    tr.commit();
                } catch (Exception ex) {
                    tr.rollback();
                    vMensajes.Mensaje(null, "error", "Error en la petición de Actividades\n" + ex.getMessage());
                } finally {
                    if (sesion != null && sesion.isOpen()) {
                        sesion.close();
                    }
                }

                break;

            case "GestionInscripciones":
                sesion = sessionFactory.openSession();

                try {
                    SociosDisponibles(sesion);
                } catch (Exception ex) {
                    vMensajes.Mensaje(null, "error", "Error en la petición de Socios\n" + ex.getMessage());
                }

                vInicio.setVisible(false);
                vMonitor.setVisible(false);
                vSocio.setVisible(false);
                vActividad.setVisible(false);
                vInscripcion.setVisible(true);

                break;

            case "Salir":
                vMensajes.Mensaje(null, "info", "Salida correcta de la aplicación");
                vPrincipal.dispose();
                System.exit(0);

                break;
        }
    }

}
