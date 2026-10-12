
package vista;

import concurrencia.HiloPrestamo;
import dao.EstudianteDAO;
import dao.LibroDAO;
import dao.PrestamoDAO;
import modelo.Estudiante;
import modelo.Libro;
import modelo.Prestamo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class VentanaPrestamos extends JFrame {

    private JComboBox<Estudiante> comboEstudiantes;
    private JComboBox<Libro> comboLibros;
    private JTable tablaPrestamos;
    private DefaultTableModel modeloTabla;

    private JButton botonPrestar;
    private JButton botonDevolver;
    private JButton botonActualizar;

    private EstudianteDAO estudianteDAO;
    private LibroDAO libroDAO;
    private PrestamoDAO prestamoDAO;

    public VentanaPrestamos() {

        estudianteDAO = new EstudianteDAO();
        libroDAO = new LibroDAO();
        prestamoDAO = new PrestamoDAO();

        setTitle("Préstamos y devoluciones");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        JPanel panelSuperior = new JPanel(new GridLayout(3, 2, 5, 5));

        comboEstudiantes = new JComboBox<>();
        comboLibros = new JComboBox<>();

        botonPrestar = new JButton("Registrar préstamo");
        botonDevolver = new JButton("Registrar devolución");
        botonActualizar = new JButton("Actualizar lista");
        JButton botonVolver = new JButton("Volver");

        panelSuperior.add(new JLabel("Estudiante:"));
        panelSuperior.add(comboEstudiantes);
        panelSuperior.add(new JLabel("Libro:"));
        panelSuperior.add(comboLibros);
        panelSuperior.add(botonPrestar);
        panelSuperior.add(botonDevolver);

        modeloTabla = new DefaultTableModel(
                new String[]{"ID préstamo", "ID estudiante", "ID libro",
                        "Fecha préstamo", "Fecha devolución", "Devuelto"}, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaPrestamos = new JTable(modeloTabla);

        JPanel panelBotones = new JPanel();
        panelBotones.add(botonActualizar);
        panelBotones.add(botonVolver);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(new JScrollPane(tablaPrestamos), BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);

        cargarEstudiantes();
        cargarLibros();
        cargarPrestamos();

        botonPrestar.addActionListener(e -> registrarPrestamo());
        botonDevolver.addActionListener(e -> registrarDevolucion());

        botonActualizar.addActionListener(e -> {
            cargarEstudiantes();
            cargarLibros();
            cargarPrestamos();
        });

        botonVolver.addActionListener(e -> dispose());
    }

    private void cargarEstudiantes() {
        comboEstudiantes.removeAllItems();

        List<Estudiante> estudiantes = estudianteDAO.listarTodos();

        for (Estudiante estudiante : estudiantes) {
            comboEstudiantes.addItem(estudiante);
        }
    }

    private void cargarLibros() {
        comboLibros.removeAllItems();

        List<Libro> libros = libroDAO.listarTodos();

        for (Libro libro : libros) {
            comboLibros.addItem(libro);
        }
    }

    private void cargarPrestamos() {
        modeloTabla.setRowCount(0);

        List<Prestamo> prestamos = prestamoDAO.listarTodos();

        for (Prestamo prestamo : prestamos) {
            modeloTabla.addRow(new Object[]{
                    prestamo.getId(),
                    prestamo.getIdEstudiante(),
                    prestamo.getIdLibro(),
                    prestamo.getFechaPrestamo(),
                    prestamo.getFechaDevolucion(),
                    prestamo.isDevuelto() ? "Sí" : "No"
            });
        }
    }

    private void registrarPrestamo() {

        Estudiante estudiante =
                (Estudiante) comboEstudiantes.getSelectedItem();

        Libro libro = (Libro) comboLibros.getSelectedItem();

        if (estudiante == null || libro == null) {
            JOptionPane.showMessageDialog(
                    this, "Debes seleccionar un estudiante y un libro."
            );
            return;
        }

        if (libro.getStock() <= 0) {
            JOptionPane.showMessageDialog(
                    this, "No hay ejemplares disponibles de este libro."
            );
            return;
        }

        LocalDate hoy = LocalDate.now();
        LocalDate fechaDevolucion = hoy.plusDays(7);

        Prestamo prestamo = new Prestamo(
                0,
                estudiante.getId(),
                libro.getId(),
                hoy.toString(),
                fechaDevolucion.toString(),
                false
        );

        botonDesactivar(true);

        HiloPrestamo hilo = new HiloPrestamo(prestamo);

        hilo.start();

        new Thread(() -> {
            try {
                hilo.join();

                boolean resultado = hilo.isResultado();

                SwingUtilities.invokeLater(() -> {
                    botonDesactivar(false);

                    cargarLibros();
                    cargarPrestamos();

                    if (resultado) {
                        JOptionPane.showMessageDialog(
                                this, "Préstamo registrado correctamente."
                        );
                    } else {
                        JOptionPane.showMessageDialog(
                                this,
                                "No se pudo registrar el préstamo. " +
                                        "Revisa el stock y la conexión con la base de datos."
                        );
                    }
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                SwingUtilities.invokeLater(() -> {
                    botonDesactivar(false);
                    JOptionPane.showMessageDialog(
                            this, "Se interrumpió el proceso del préstamo."
                    );
                });
            }
        }).start();
    }

    private void botonDesactivar(boolean estado) {
        botonPrestar.setEnabled(!estado);
        botonDevolver.setEnabled(!estado);
        botonActualizar.setEnabled(!estado);
        comboEstudiantes.setEnabled(!estado);
        comboLibros.setEnabled(!estado);
    }

    private void registrarDevolucion() {

        int fila = tablaPrestamos.getSelectedRow();

        if (fila < 0) {
            JOptionPane.showMessageDialog(
                    this, "Selecciona un préstamo en la tabla."
            );
            return;
        }

        int idPrestamo = Integer.parseInt(
                modeloTabla.getValueAt(fila, 0).toString()
        );

        boolean resultado = prestamoDAO.registrarDevolucion(idPrestamo);

        if (resultado) {
            JOptionPane.showMessageDialog(
                    this, "Devolución registrada correctamente."
            );

            cargarLibros();
            cargarPrestamos();

        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo registrar la devolución. " +
                            "Comprueba que el préstamo exista y no esté devuelto."
            );
        }
    }
}
