
package controlador;

import dao.UsuarioDAO;
import modelo.Usuario;
import vista.VentanaLogin;

public class LoginControlador {

    private VentanaLogin ventana;
    private UsuarioDAO usuarioDAO;

    public LoginControlador(VentanaLogin ventana) {
        this.ventana = ventana;
        this.usuarioDAO = new UsuarioDAO();
    }

    public void iniciarSesion() {

        String rut = ventana.getRut();
        String contrasena = ventana.getContrasena();

        if (rut.isEmpty() || contrasena.isEmpty()) {
            ventana.mostrarMensaje(
                    "Debes ingresar el RUT y la contraseña."
            );
            return;
        }

        Usuario usuario = usuarioDAO.buscarPorRut(rut);

        if (usuario != null
                && usuarioDAO.validarLogin(rut, contrasena)) {

            ventana.mostrarMensaje(
                    "Bienvenido/a, " + usuario.getNombre()
            );

            ventana.abrirVentanaPrincipal();

        } else {
            ventana.mostrarMensaje(
                    "RUT o contraseña incorrectos."
            );
        }
    }
}

