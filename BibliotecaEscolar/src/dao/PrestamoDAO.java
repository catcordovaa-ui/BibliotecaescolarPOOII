
package dao;

import conexion.DatabaseConnection;
import modelo.Prestamo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

public class PrestamoDAO {

    public boolean insertar(Prestamo prestamo) {
        String sql = "INSERT INTO prestamos " +
                "(id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto) " +
                "VALUES (?, ?, ?, ?, ?)";

        Connection conexion = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, prestamo.getIdEstudiante());
            ps.setInt(2, prestamo.getIdLibro());
            ps.setString(3, prestamo.getFechaPrestamo());
            ps.setString(4, prestamo.getFechaDevolucion());
            ps.setBoolean(5, prestamo.isDevuelto());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al registrar préstamo: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Prestamo> listarTodos() {
        ArrayList<Prestamo> lista = new ArrayList<>();

        String sql = "SELECT * FROM prestamos";

        Connection conexion = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Prestamo prestamo = new Prestamo(
                        rs.getInt("id"),
                        rs.getInt("id_estudiante"),
                        rs.getInt("id_libro"),
                        rs.getString("fecha_prestamo"),
                        rs.getString("fecha_devolucion"),
                        rs.getBoolean("devuelto")
                );

                lista.add(prestamo);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar préstamos: " + e.getMessage());
        }

        return lista;
    }

    public Prestamo buscarPorId(int id) {
        String sql = "SELECT * FROM prestamos WHERE id = ?";

        Connection conexion = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Prestamo(
                            rs.getInt("id"),
                            rs.getInt("id_estudiante"),
                            rs.getInt("id_libro"),
                            rs.getString("fecha_prestamo"),
                            rs.getString("fecha_devolucion"),
                            rs.getBoolean("devuelto")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar préstamo: " + e.getMessage());
        }

        return null;
    }

    public boolean actualizar(Prestamo prestamo) {
        String sql = "UPDATE prestamos SET id_estudiante = ?, id_libro = ?, " +
                "fecha_prestamo = ?, fecha_devolucion = ?, devuelto = ? WHERE id = ?";

        Connection conexion = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, prestamo.getIdEstudiante());
            ps.setInt(2, prestamo.getIdLibro());
            ps.setString(3, prestamo.getFechaPrestamo());
            ps.setString(4, prestamo.getFechaDevolucion());
            ps.setBoolean(5, prestamo.isDevuelto());
            ps.setInt(6, prestamo.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar préstamo: " + e.getMessage());
            return false;
        }
    }

    public boolean registrarDevolucion(int idPrestamo) {
        Connection conexion = DatabaseConnection.getInstance().getConnection();

        synchronized (DatabaseConnection.getInstance()) {
            try {
                conexion.setAutoCommit(false);

                String sqlBuscar =
                        "SELECT id_libro, devuelto FROM prestamos WHERE id = ? FOR UPDATE";

                int idLibro;
                boolean devuelto;

                try (PreparedStatement ps = conexion.prepareStatement(sqlBuscar)) {
                    ps.setInt(1, idPrestamo);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conexion.rollback();
                            return false;
                        }

                        idLibro = rs.getInt("id_libro");
                        devuelto = rs.getBoolean("devuelto");
                    }
                }

                if (devuelto) {
                    conexion.rollback();
                    return false;
                }

                String sqlDevolucion =
                        "UPDATE prestamos SET devuelto = TRUE WHERE id = ? AND devuelto = FALSE";

                try (PreparedStatement ps = conexion.prepareStatement(sqlDevolucion)) {
                    ps.setInt(1, idPrestamo);

                    if (ps.executeUpdate() == 0) {
                        conexion.rollback();
                        return false;
                    }
                }

                String sqlStock =
                        "UPDATE libros SET stock = stock + 1 WHERE id = ?";

                try (PreparedStatement ps = conexion.prepareStatement(sqlStock)) {
                    ps.setInt(1, idLibro);

                    if (ps.executeUpdate() == 0) {
                        conexion.rollback();
                        return false;
                    }
                }

                conexion.commit();
                return true;

            } catch (SQLException e) {
                try {
                    conexion.rollback();
                } catch (SQLException error) {
                    System.out.println("Error al revertir la devolución.");
                }

                System.out.println("Error al registrar devolución: " + e.getMessage());
                return false;

            } finally {
                try {
                    conexion.setAutoCommit(true);
                } catch (SQLException e) {
                    System.out.println("Error al restaurar la conexión.");
                }
            }
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM prestamos WHERE id = ?";

        Connection conexion = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar préstamo: " + e.getMessage());
            return false;
        }
    }
}
