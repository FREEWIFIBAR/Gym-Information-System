/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utilidad;

import Modelo.Actividad;
import Vista.VistaActividades;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Gestion Tablas Actividad
 *
 * @author freew
 */
public class GestionTablasActividad {

    private static DefaultTableModel modeloTablaActividades;

    // Se sobreescribe el método isCellEditable para hacer que las filas
    // no se puedan editar al hacer doble click
    public void inicializarTablaActividades(VistaActividades vActividad) {
        modeloTablaActividades = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vActividad.jTableActividades.setModel(modeloTablaActividades);
    }

    public static void dibujarTablaActividades(VistaActividades vActividad) {
        String[] columnasTabla = {"Id", "Nombre", "Día",
            "Hora", "Descripción", "Precio", "Monitor Responsable"};
        modeloTablaActividades.setColumnIdentifiers(columnasTabla);

        // Para no permitir el redimensionamiento de las columnas con el ratón
        vActividad.jTableActividades.getTableHeader().setResizingAllowed(false);
        vActividad.jTableActividades.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        // Así se fija el ancho de las columnas
        vActividad.jTableActividades.getColumnModel().getColumn(0).setPreferredWidth(10);
        vActividad.jTableActividades.getColumnModel().getColumn(1).setPreferredWidth(60);
        vActividad.jTableActividades.getColumnModel().getColumn(2).setPreferredWidth(40);
        vActividad.jTableActividades.getColumnModel().getColumn(3).setPreferredWidth(20);
        vActividad.jTableActividades.getColumnModel().getColumn(4).setPreferredWidth(300);
        vActividad.jTableActividades.getColumnModel().getColumn(5).setPreferredWidth(20);
        vActividad.jTableActividades.getColumnModel().getColumn(6).setPreferredWidth(150);
    }

    public static void rellenarTablaActividades(ArrayList<Actividad> actividades) {
        Object[] fila = new Object[7];
        for (Actividad actividad : actividades) {
            fila[0] = actividad.getIdActividad();
            fila[1] = actividad.getNombre();
            fila[2] = actividad.getDia();
            fila[3] = actividad.getHora();
            fila[4] = actividad.getDescripcion();
            fila[5] = actividad.getPrecioBaseMes();
            fila[6] = actividad.getMonitorResponsable().getNombre();
            modeloTablaActividades.addRow(fila);
        }
    }

    public void vaciarTablaActividades() {
        while (modeloTablaActividades.getRowCount() > 0) {
            modeloTablaActividades.removeRow(0);
        }
    }

}
