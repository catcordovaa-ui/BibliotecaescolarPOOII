
package servicio;

import conexion.DatabaseConnection;
import modelo.Prestamo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PrestamoServicio {

    public boolean registrarPrestamo(Prestamo prestamo) {

        synchronized (DatabaseConnection.getInstance()) {

            Connection conexion =
                    DatabaseConnection.getInstance().getConnection();

            if (conexion == null) {
                System.out.println("No hay conexión con la base de datos.");
                return false;
            }

            try {
                conexion.setAutoCommit(false);

                String consulta =
                        "SELECT stock FROM libros WHERE id = ? FOR UPDATE";

                int stock;

                try (PreparedStatement ps = conexion.prepareStatement(consulta)) {
                    ps.setInt(1, prestamo.getIdLibro());

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conexion.rollback();
                            return false;
                        }

                        stock = rs.getInt("stock");
                    }
                }

                if (stock <= 0) {
                    conexion.rollback();
                    System.out.println("No hay libros disponibles.");
                    return false;
                }

                String actualizarStock =
                        "UPDATE libros SET stock = stock - 1 WHERE id = ?";

                try (PreparedStatement ps =
                             conexion.prepareStatement(actualizarStock)) {
                    ps.setInt(1, prestamo.getIdLibro());

                    if (ps.executeUpdate() == 0) {
                        conexion.rollback();
                        return false;
                    }
                }

                String insertarPrestamo =
                        "INSERT INTO prestamos " +
                                "(id_estudiante, id_libro, fecha_prestamo, " +
                                "fecha_devolucion, devuelto) VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement ps =
                             conexion.prepareStatement(insertarPrestamo)) {
                    ps.setInt(1, prestamo.getIdEstudiante());
                    ps.setInt(2, prestamo.getIdLibro());
                    ps.setString(3, prestamo.getFechaPrestamo());
                    ps.setString(4, prestamo.getFechaDevolucion());
                    ps.setBoolean(5, false);

                    ps.executeUpdate();
                }

                conexion.commit();

                System.out.println("Préstamo registrado correctamente.");
                return true;

            } catch (SQLException e) {

                try {
                    conexion.rollback();
                } catch (SQLException error) {
                    System.out.println(
                            "Error al deshacer cambios: " + error.getMessage());
                }

                System.out.println(
                        "Error al registrar préstamo: " + e.getMessage());

                return false;

            } finally {

                try {
                    conexion.setAutoCommit(true);
                } catch (SQLException e) {
                    System.out.println(
                            "Error al restablecer la conexión: " + e.getMessage());
                }
            }
        }
    }
}
