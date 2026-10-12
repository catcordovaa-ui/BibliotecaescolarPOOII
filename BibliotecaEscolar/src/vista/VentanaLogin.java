
package vista;

import controlador.LoginControlador;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridLayout;

public class VentanaLogin extends JFrame {

    private JTextField campoRut;
    private JPasswordField campoContrasena;
    private JButton botonIngresar;

    public VentanaLogin() {

        setTitle("Inicio de sesión - Biblioteca Escolar");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel etiquetaRut = new JLabel("RUT:");
        campoRut = new JTextField();

        JLabel etiquetaContrasena = new JLabel("Contraseña:");
        campoContrasena = new JPasswordField();

        botonIngresar = new JButton("Ingresar");

        panel.add(etiquetaRut);
        panel.add(campoRut);
        panel.add(etiquetaContrasena);
        panel.add(campoContrasena);
        panel.add(new JLabel(""));
        panel.add(botonIngresar);

        add(panel);

        LoginControlador controlador = new LoginControlador(this);
        botonIngresar.addActionListener(e -> controlador.iniciarSesion());
    }

    public String getRut() {
        return campoRut.getText();
    }

    public String getContrasena() {
        return new String(campoContrasena.getPassword());
    }

    public void mostrarMensaje(String mensaje) {
        javax.swing.JOptionPane.showMessageDialog(this, mensaje);
    }

    public void abrirVentanaPrincipal() {
        VentanaPrincipal ventana = new VentanaPrincipal();
        ventana.setVisible(true);
        dispose();
    }
}
