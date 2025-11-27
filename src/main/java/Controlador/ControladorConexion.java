/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Config.HibernateUtil;
import Vista.VistaConexion;
import Vista.VistaMensajes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.hibernate.SessionFactory;

/**
 * Controlador Conexion
 *
 * @author freew
 */
public class ControladorConexion implements ActionListener {

    private SessionFactory sessionFactory = null;
    private ControladorPrincipal cp;

    public static String user;
    public static String pass;

    private final VistaMensajes vMensajes;
    private final VistaConexion vConexion;

    private void addListeners() {
        vConexion.jButtonEntrarAplicacion.addActionListener(this);
        vConexion.jButtonSalirDialogoConexion.addActionListener(this);
    }

    public ControladorConexion() {
        vMensajes = new VistaMensajes();
        vConexion = new VistaConexion();

        addListeners();

        vConexion.setLocationRelativeTo(null);
        vConexion.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "EntrarAplicacion":
                user = vConexion.jTextFieldUsuario.getText();
                pass = new String(vConexion.jPasswordField.getPassword());
                sessionFactory = HibernateUtil.buildSessionFactory();

                if (sessionFactory == null) {
                    vMensajes.Mensaje(null, "error", "Error al introducir las credenciales");
                } else {
                    vMensajes.Mensaje(null, "info", "Conexión correcta con Hibernate");
                    vConexion.dispose();

                    cp = new ControladorPrincipal(sessionFactory);
                }

                break;

            case "SalirDialogoConexion":
                vMensajes.Mensaje(null, "info", "Salida correcta de la aplicación");
                vConexion.dispose();
                System.exit(0);

                break;
        }
    }

}
