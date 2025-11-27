/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utilidad;

import Modelo.Socio;
import Vista.VistaSocios;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Gestion Tablas Socio
 *
 * @author freew
 */
public class GestionTablasSocio {

    private static DefaultTableModel modeloTablaSocios;

    // Se sobreescribe el método isCellEditable para hacer que las filas
    // no se puedan editar al hacer doble click
    public void inicializarTablaSocios(VistaSocios vSocio) {
        modeloTablaSocios = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vSocio.jTableSocios.setModel(modeloTablaSocios);
    }

    public static void dibujarTablaSocios(VistaSocios vSocio) {
        String[] columnasTabla = {"Número", "Nombre", "DNI",
            "Fecha de Nacimiento", "Telefono", "Correo", "Fecha de Alta", "Categoría"};
        modeloTablaSocios.setColumnIdentifiers(columnasTabla);

        // Para no permitir el redimensionamiento de las columnas con el ratón
        vSocio.jTableSocios.getTableHeader().setResizingAllowed(false);
        vSocio.jTableSocios.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        // Así se fija el ancho de las columnas
        vSocio.jTableSocios.getColumnModel().getColumn(0).setPreferredWidth(70);
        vSocio.jTableSocios.getColumnModel().getColumn(1).setPreferredWidth(200);
        vSocio.jTableSocios.getColumnModel().getColumn(2).setPreferredWidth(90);
        vSocio.jTableSocios.getColumnModel().getColumn(3).setPreferredWidth(150);
        vSocio.jTableSocios.getColumnModel().getColumn(4).setPreferredWidth(100);
        vSocio.jTableSocios.getColumnModel().getColumn(5).setPreferredWidth(250);
        vSocio.jTableSocios.getColumnModel().getColumn(6).setPreferredWidth(100);
        vSocio.jTableSocios.getColumnModel().getColumn(7).setPreferredWidth(80);
    }

    public static void rellenarTablaSocios(ArrayList<Socio> socios) {
        Object[] fila = new Object[8];
        for (Socio socio : socios) {
            fila[0] = socio.getNumeroSocio();
            fila[1] = socio.getNombre();
            fila[2] = socio.getDni();
            fila[3] = socio.getFechaNacimiento();
            fila[4] = socio.getTelefono();
            fila[5] = socio.getCorreo();
            fila[6] = socio.getFechaEntrada();
            fila[7] = socio.getCategoria();
            modeloTablaSocios.addRow(fila);
        }
    }

    public void vaciarTablaSocios() {
        while (modeloTablaSocios.getRowCount() > 0) {
            modeloTablaSocios.removeRow(0);
        }
    }

}
