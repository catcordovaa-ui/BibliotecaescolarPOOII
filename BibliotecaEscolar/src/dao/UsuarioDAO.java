
package dao;

import conexion.DatabaseConnection;
import modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // Validar inicio de sesión
    public boolean validarLogin(String rut, String contrasena) {
        String sql = "SELECT * FROM usuarios WHERE rut = ? AND contraseña = ?";

        Connection conexion = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, rut);
            ps.setString(2, contrasena);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("Error al validar usuario: " + e.getMessage());
        }

        return false;
    }

    // Buscar usuario por RUT
    public Usuario buscarPorRut(String rut) {
        String sql = "SELECT * FROM usuarios WHERE rut = ?";

        Connection conexion = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, rut);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("rut"),
                            rs.getString("correo"),
                            rs.getString("contraseña"),
                            rs.getString("rol")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        }

        return null;
    }

    // Listar todos los usuarios
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";

        Connection conexion = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("rut"),
                        rs.getString("correo"),
                        rs.getString("contraseña"),
                        rs.getString("rol")
                );

                usuarios.add(usuario);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }

        return usuarios;
    }
}
