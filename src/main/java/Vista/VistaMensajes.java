/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vista;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Vista Mensajes
 *
 * @author freew
 */
public class VistaMensajes {

    public void Mensaje(Component C, String tipoMensaje, String texto) {
        switch (tipoMensaje) {
            case "info":
                JOptionPane.showMessageDialog(C, texto, "Información", JOptionPane.INFORMATION_MESSAGE);
                break;

            case "error":
                JOptionPane.showMessageDialog(C, texto, "Error", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }

    public boolean Confirmacion(String s) {
        int result = JOptionPane.showConfirmDialog(null, s, "Atención", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }

}
