/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 * Actividad DAO
 *
 * @author freew
 */
public class ActividadDAO {

    /**
     * Muestra una lista con las actividades que hay en la base de datos.
     *
     * @param sesion
     * @return
     * @throws Exception
     */
    public ArrayList<Actividad> listaActividades(Session sesion) throws Exception {
        Query consulta = sesion.createNamedQuery("Actividad.findAll", Actividad.class);
        ArrayList<Actividad> actividad = (ArrayList<Actividad>) consulta.getResultList();
        return actividad;
    }

    /**
     * Devuelve el nombre de la última actividad insertada en la base de datos.
     *
     * @param sesion
     * @return
     */
    public String ultimaActividad(Session sesion) {
        Query consulta = sesion.createQuery("FROM Actividad ORDER BY idActividad DESC", Actividad.class);
        List<Actividad> actividad = consulta.getResultList();

        if (actividad.isEmpty()) {
            return "AC01";
        }

        Actividad ultimaActividad = actividad.get(0);
        String idActividad = ultimaActividad.getIdActividad();

        int num = Integer.parseInt(idActividad.substring(2));
        num++;

        return String.format("AC%02d", num);
    }

    /**
     * Inserta la actividad pasada por parametro en la base de datos.
     *
     * @param sesion
     * @param nuevaActividad
     * @throws Exception
     */
    public void insertarActividad(Session sesion, Actividad nuevaActividad) throws Exception {
        sesion.saveOrUpdate(nuevaActividad);
    }

    /**
     * Elimina la actividad cuya id es pasada por parametro en la base de datos.
     *
     * @param sesion
     * @param idActividad
     * @throws Exception
     */
    public void eliminarActividad(Session sesion, String idActividad) throws Exception {
        Transaction tr = null;

        try {
            tr = sesion.beginTransaction();

            Query consulta = sesion.createQuery("FROM Actividad WHERE idActividad = :idActividad", Actividad.class);
            consulta.setParameter("idActividad", idActividad);
            List<Actividad> actividad = consulta.getResultList();

            if (!actividad.isEmpty()) {
                Actividad actividadSelec = actividad.get(0);
                sesion.delete(actividadSelec);
            }

            tr.commit();
        } catch (Exception ex) {
            if (tr != null) {
                tr.rollback();
            }
        }
    }

    /**
     * Actualiza la actividad pasada por parametro en la base de datos.
     *
     * @param sesion
     * @param nuevaActividad
     * @throws Exception
     */
    public void actualizarActividad(Session sesion, Actividad nuevaActividad) throws Exception {
        sesion.merge(nuevaActividad);
    }

    /**
     * Devuelve un listado con los monitores de la base de datos.
     *
     * @param sesion
     * @return
     * @throws Exception
     */
    public List<Monitor> MonitoresDisponibles(Session sesion) throws Exception {
        Transaction tr = null;

        try {
            tr = sesion.beginTransaction();

            Query consulta = sesion.createNativeQuery("SELECT * FROM MONITOR m", Monitor.class);
            List<Monitor> monitoresDisponibles = consulta.getResultList();

            tr.commit();
            return monitoresDisponibles;

        } catch (Exception ex) {
            if (tr != null) {
                tr.rollback();
            }
            throw new RuntimeException("Error al obtener los monitores disponibles: " + ex.getMessage(), ex);
        }
    }

    /**
     * Devuelva un monitor cuyo nombre es indicado en los parametros.
     *
     * @param sesion
     * @param nombre
     * @return
     * @throws Exception
     */
    public Monitor MonitorResponsable(Session sesion, String nombre) throws Exception {
        Query consulta = sesion.createQuery("FROM Monitor WHERE nombre = :nombre", Monitor.class);
        consulta.setParameter("nombre", nombre);
        Monitor monitor = (Monitor) consulta.getSingleResult();
        return monitor;
    }

    /**
     * Devuelve un listado con los monitores que estan ocupados en un dia y a
     * una hora concretos indicados en los parametros.
     *
     * @param sesion
     * @param dia
     * @param hora
     * @return
     */
    public List<Monitor> MonitoresOcupados(Session sesion, String dia, int hora) {
        Query consulta = sesion.createQuery("SELECT a.monitorResponsable FROM Actividad a WHERE a.dia = :dia AND a.hora = :hora");
        consulta.setParameter("dia", dia);
        consulta.setParameter("hora", hora);
        List<Monitor> monitoresOcupados = consulta.getResultList();
        return monitoresOcupados;
    }

}
