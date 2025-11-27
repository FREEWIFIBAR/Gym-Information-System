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
 * Monitor DAO
 *
 * @author freew
 */
public class MonitorDAO {

    /**
     * Muestra una lista con los monitores que hay en la base de datos.
     *
     * @param sesion
     * @return
     * @throws Exception
     */
    public ArrayList<Monitor> listaMonitores(Session sesion) throws Exception {
        Query consulta = sesion.createNamedQuery("Monitor.findAll", Monitor.class);
        ArrayList<Monitor> monitor = (ArrayList<Monitor>) consulta.getResultList();
        return monitor;
    }

    /**
     * Devuelve el nombre del último monitor insertado en la base de datos.
     *
     * @param sesion
     * @return
     */
    public String ultimoMonitor(Session sesion) {
        Query consulta = sesion.createQuery("FROM Monitor ORDER BY codMonitor DESC", Monitor.class);
        List<Monitor> monitor = consulta.getResultList();

        if (monitor.isEmpty()) {
            return "M001";
        }

        Monitor ultimoMonitor = monitor.get(0);
        String codMontior = ultimoMonitor.getCodMonitor();

        int num = Integer.parseInt(codMontior.substring(1));
        num++;

        return String.format("M%03d", num);
    }

    /**
     * Inserta el monitor pasado por parametro en la base de datos.
     *
     * @param sesion
     * @param nuevoMonitor
     * @throws Exception
     */
    public void insertarMonitor(Session sesion, Monitor nuevoMonitor) throws Exception {
        sesion.saveOrUpdate(nuevoMonitor);
    }

    /**
     * Elimina el monitor cuyo código es pasado por parametro en la base de
     * datos.
     *
     * @param sesion
     * @param codMonitor
     * @throws Exception
     */
    public void eliminarMonitor(Session sesion, String codMonitor) throws Exception {
        Transaction tr = null;

        try {
            tr = sesion.beginTransaction();

            Query consulta = sesion.createQuery("FROM Monitor WHERE codMonitor = :codMonitor", Monitor.class);
            consulta.setParameter("codMonitor", codMonitor);
            List<Monitor> monitor = consulta.getResultList();

            if (!monitor.isEmpty()) {
                Monitor monitorSelec = monitor.get(0);
                sesion.delete(monitorSelec);
            }

            tr.commit();
        } catch (Exception ex) {
            if (tr != null) {
                tr.rollback();
            }
        }
    }

    /**
     * Actualiza el monitor pasado por parametro en la base de datos.
     *
     * @param sesion
     * @param nuevoMonitor
     * @throws Exception
     */
    public void actualizarMonitor(Session sesion, Monitor nuevoMonitor) throws Exception {
        sesion.merge(nuevoMonitor);
    }

}
