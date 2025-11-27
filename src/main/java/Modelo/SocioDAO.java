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
 * Socio DAO
 *
 * @author freew
 */
public class SocioDAO {

    /**
     * Muestra una lista con los socios que hay en la base de datos.
     *
     * @param sesion
     * @return
     * @throws Exception
     */
    public ArrayList<Socio> listaSocios(Session sesion) throws Exception {
        Query consulta = sesion.createNamedQuery("Socio.findAll", Socio.class);
        ArrayList<Socio> socio = (ArrayList<Socio>) consulta.getResultList();
        return socio;
    }

    /**
     * Devuelve el nombre del último socio insertado en la base de datos.
     *
     * @param sesion
     * @return
     */
    public String ultimoSocio(Session sesion) {
        Query consulta = sesion.createQuery("FROM Socio ORDER BY numeroSocio DESC", Socio.class);
        List<Socio> socio = consulta.getResultList();

        if (socio.isEmpty()) {
            return "S001";
        }

        Socio ultimoSocio = socio.get(0);
        String numSocio = ultimoSocio.getNumeroSocio();

        int num = Integer.parseInt(numSocio.substring(1));
        num++;

        return String.format("S%03d", num);
    }

    /**
     * Inserta el socio pasado por parametro en la base de datos.
     *
     * @param sesion
     * @param nuevoSocio
     * @throws Exception
     */
    public void insertarSocio(Session sesion, Socio nuevoSocio) throws Exception {
        sesion.saveOrUpdate(nuevoSocio);
    }

    /**
     * Elimina el socio cuyo numero es pasado por parametro en la base de datos.
     *
     * @param sesion
     * @param numeroSocio
     * @throws Exception
     */
    public void eliminarSocio(Session sesion, String numeroSocio) throws Exception {
        Transaction tr = null;

        try {
            tr = sesion.beginTransaction();

            Query consulta = sesion.createQuery("FROM Socio WHERE numeroSocio = :numeroSocio", Socio.class);
            consulta.setParameter("numeroSocio", numeroSocio);
            List<Socio> socio = consulta.getResultList();

            if (!socio.isEmpty()) {
                Socio socioSelec = socio.get(0);
                sesion.delete(socioSelec);
            }

            tr.commit();
        } catch (Exception ex) {
            if (tr != null) {
                tr.rollback();
            }
        }
    }

    /**
     * Actualiza el socio pasado por parametro en la base de datos.
     *
     * @param sesion
     * @param nuevoSocio
     * @throws Exception
     */
    public void actualizarSocio(Session sesion, Socio nuevoSocio) throws Exception {
        sesion.merge(nuevoSocio);
    }

    /**
     * Devuelve un listado con los socios de la base de datos.
     *
     * @param sesion
     * @return
     * @throws Exception
     */
    public List<Socio> SociosDisponibles(Session sesion) throws Exception {
        Transaction tr = null;

        try {
            tr = sesion.beginTransaction();

            Query consulta = sesion.createNativeQuery("SELECT * FROM SOCIO s", Socio.class);
            List<Socio> sociosDisponibles = consulta.getResultList();

            tr.commit();
            return sociosDisponibles;

        } catch (Exception ex) {
            if (tr != null) {
                tr.rollback();
            }
            throw new RuntimeException("Error al obtener los socios disponibles: " + ex.getMessage(), ex);
        }
    }

    /**
     * Devuelve un listado con los socios de la base de datos que tengan el
     * mismo nombre que el que indica el parametro pasado.
     *
     * @param sesion
     * @param nombre
     * @return
     */
    public List<Socio> FiltroSocio(Session sesion, String nombre) {

        try {
            Query consulta = sesion.createQuery("FROM Socio s WHERE s.nombre = :nombre", Socio.class);
            consulta.setParameter("nombre", nombre);
            consulta.setMaxResults(1);

            return consulta.getResultList();

        } catch (Exception ex) {
            throw new RuntimeException("Error al obtener los socios filtrados: " + ex.getMessage());
        }
    }

}
