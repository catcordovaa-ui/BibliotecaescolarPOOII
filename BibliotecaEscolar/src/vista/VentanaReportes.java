
package vista;

import conexion.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VentanaReportes extends JFrame {

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public VentanaReportes() {

        setTitle("Reportes de biblioteca");
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);

        JButton botonMasPrestados = new JButton("Libros más prestados");
        JButton botonHistorial = new JButton("Historial por estudiante");
        JButton botonPrestamosActivos = new JButton("Préstamos activos");
        JButton botonVolver = new JButton("Volver");

        JPanel panelBotones = new JPanel();

        panelBotones.add(botonMasPrestados);
        panelBotones.add(botonHistorial);
        panelBotones.add(botonPrestamosActivos);
        panelBotones.add(botonVolver);

        add(panelBotones, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        botonMasPrestados.addActionListener(e -> cargarMasPrestados());
        botonHistorial.addActionListener(e -> cargarHistorial());
        botonPrestamosActivos.addActionListener(e -> cargarPrestamosActivos());
        botonVolver.addActionListener(e -> dispose());
    }

    private void cargarMasPrestados() {

        String sql =
                "SELECT l.titulo, l.autor, COUNT(p.id) AS cantidad " +
                        "FROM libros l INNER JOIN prestamos p ON l.id = p.id_libro " +
                        "GROUP BY l.id, l.titulo, l.autor " +
                        "ORDER BY cantidad DESC";

        ejecutarConsulta(
                sql,
                new String[]{"Libro", "Autor", "Cantidad de préstamos"}
        );
    }

    private void cargarHistorial() {

        String idTexto = JOptionPane.showInputDialog(
                this, "Ingresa el ID del estudiante:"
        );

        if (idTexto == null || idTexto.trim().isEmpty()) {
            return;
        }

        try {
            int idEstudiante = Integer.parseInt(idTexto.trim());

            String sql =
                    "SELECT e.nombre, l.titulo, p.fecha_prestamo, " +
                            "p.fecha_devolucion, p.devuelto " +
                            "FROM prestamos p " +
                            "INNER JOIN estudiantes e ON p.id_estudiante = e.id " +
                            "INNER JOIN libros l ON p.id_libro = l.id " +
                            "WHERE e.id = ? ORDER BY p.fecha_prestamo DESC";

            modeloTabla.setRowCount(0);
            modeloTabla.setColumnIdentifiers(
                    new String[]{"Estudiante", "Libro", "Fecha préstamo",
                            "Fecha devolución", "Devuelto"}
            );

            Connection conexion =
                    DatabaseConnection.getInstance().getConnection();

            if (conexion == null) {
                JOptionPane.showMessageDialog(
                        this, "No hay conexión con la base de datos."
                );
                return;
            }

            synchronized (DatabaseConnection.getInstance()) {

                try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                    ps.setInt(1, idEstudiante);

                    try (ResultSet rs = ps.executeQuery()) {

                        while (rs.next()) {
                            modeloTabla.addRow(new Object[]{
                                    rs.getString("nombre"),
                                    rs.getString("titulo"),
                                    rs.getString("fecha_prestamo"),
                                    rs.getString("fecha_devolucion"),
                                    rs.getBoolean("devuelto") ? "Sí" : "No"
                            });
                        }
                    }
                }
            }

            if (modeloTabla.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "No se encontraron préstamos para ese estudiante."
                );
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this, "El ID debe ser un número."
            );

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this, "Error al consultar historial: " + e.getMessage()
            );
        }
    }

    private void cargarPrestamosActivos() {

        String sql =
                "SELECT p.id, e.nombre, l.titulo, " +
                        "p.fecha_prestamo, p.fecha_devolucion " +
                        "FROM prestamos p " +
                        "INNER JOIN estudiantes e ON p.id_estudiante = e.id " +
                        "INNER JOIN libros l ON p.id_libro = l.id " +
                        "WHERE p.devuelto = FALSE";

        ejecutarConsulta(
                sql,
                new String[]{"ID préstamo", "Estudiante", "Libro",
                        "Fecha préstamo", "Fecha devolución"}
        );
    }

    private void ejecutarConsulta(String sql, String[] columnas) {

        modeloTabla.setRowCount(0);
        modeloTabla.setColumnIdentifiers(columnas);

        Connection conexion =
                DatabaseConnection.getInstance().getConnection();

        if (conexion == null) {
            JOptionPane.showMessageDialog(
                    this, "No hay conexión con la base de datos."
            );
            return;
        }

        synchronized (DatabaseConnection.getInstance()) {

            try (PreparedStatement ps = conexion.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    int cantidadColumnas = rs.getMetaData().getColumnCount();
                    Object[] fila = new Object[cantidadColumnas];

                    for (int i = 0; i < cantidadColumnas; i++) {
                        fila[i] = rs.getObject(i + 1);
                    }

                    modeloTabla.addRow(fila);
                }

                if (modeloTabla.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(
                            this, "No hay resultados para mostrar."
                    );
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(
                        this, "Error al generar reporte: " + e.getMessage()
                );
            }
        }
    }
}
