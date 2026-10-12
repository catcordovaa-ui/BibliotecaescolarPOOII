
package vista;

import dao.LibroDAO;
import modelo.Libro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaLibros extends JFrame {

    private JTextField campoTitulo;
    private JTextField campoAutor;
    private JTextField campoIsbn;
    private JTextField campoEditorial;
    private JTextField campoStock;
    private JTextField campoCategoria;
    private JTextField campoId;

    private JTable tablaLibros;
    private DefaultTableModel modeloTabla;

    private LibroDAO libroDAO;

    public VentanaLibros() {

        libroDAO = new LibroDAO();

        setTitle("Gestión de libros");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));

        JPanel panelFormulario = new JPanel(new GridLayout(7, 2, 5, 5));

        campoId = new JTextField();
        campoTitulo = new JTextField();
        campoAutor = new JTextField();
        campoIsbn = new JTextField();
        campoEditorial = new JTextField();
        campoStock = new JTextField();
        campoCategoria = new JTextField();

        campoId.setEditable(false);

        panelFormulario.add(new JLabel("ID (selecciona una fila):"));
        panelFormulario.add(campoId);

        panelFormulario.add(new JLabel("Título:"));
        panelFormulario.add(campoTitulo);

        panelFormulario.add(new JLabel("Autor:"));
        panelFormulario.add(campoAutor);

        panelFormulario.add(new JLabel("ISBN:"));
        panelFormulario.add(campoIsbn);

        panelFormulario.add(new JLabel("Editorial:"));
        panelFormulario.add(campoEditorial);

        panelFormulario.add(new JLabel("Stock:"));
        panelFormulario.add(campoStock);

        panelFormulario.add(new JLabel("ID categoría:"));
        panelFormulario.add(campoCategoria);

        JPanel panelBotones = new JPanel();

        JButton botonRegistrar = new JButton("Registrar");
        JButton botonActualizar = new JButton("Actualizar");
        JButton botonEliminar = new JButton("Eliminar");
        JButton botonLimpiar = new JButton("Limpiar");
        JButton botonVolver = new JButton("Volver");

        panelBotones.add(botonRegistrar);
        panelBotones.add(botonActualizar);
        panelBotones.add(botonEliminar);
        panelBotones.add(botonLimpiar);
        panelBotones.add(botonVolver);

        modeloTabla = new DefaultTableModel(
                new String[]{"ID", "Título", "Autor", "ISBN",
                        "Editorial", "Stock", "ID categoría"}, 0
        );

        tablaLibros = new JTable(modeloTabla);
        JScrollPane desplazamiento = new JScrollPane(tablaLibros);

        panelPrincipal.add(panelFormulario, BorderLayout.NORTH);
        panelPrincipal.add(desplazamiento, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);

        cargarLibros();

        botonRegistrar.addActionListener(e -> registrarLibro());
        botonActualizar.addActionListener(e -> actualizarLibro());
        botonEliminar.addActionListener(e -> eliminarLibro());
        botonLimpiar.addActionListener(e -> limpiarCampos());

        botonVolver.addActionListener(e -> dispose());

        tablaLibros.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarDatosSeleccionados();
            }
        });
    }

    private void cargarLibros() {

        modeloTabla.setRowCount(0);

        List<Libro> libros = libroDAO.listarTodos();

        for (Libro libro : libros) {
            modeloTabla.addRow(new Object[]{
                    libro.getId(),
                    libro.getTitulo(),
                    libro.getAutor(),
                    libro.getIsbn(),
                    libro.getEditorial(),
                    libro.getStock(),
                    libro.getIdCategoria()
            });
        }
    }

    private void registrarLibro() {

        if (campoTitulo.getText().trim().isEmpty()
                || campoAutor.getText().trim().isEmpty()
                || campoIsbn.getText().trim().isEmpty()
                || campoEditorial.getText().trim().isEmpty()
                || campoStock.getText().trim().isEmpty()
                || campoCategoria.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this, "Completa todos los campos."
            );
            return;
        }

        try {
            int stock = Integer.parseInt(campoStock.getText());
            int idCategoria = Integer.parseInt(campoCategoria.getText());

            if (stock < 0 || idCategoria <= 0) {
                JOptionPane.showMessageDialog(
                        this, "El stock no puede ser negativo y la categoría debe ser válida."
                );
                return;
            }

            Libro libro = new Libro(
                    0,
                    campoTitulo.getText().trim(),
                    campoAutor.getText().trim(),
                    campoIsbn.getText().trim(),
                    campoEditorial.getText().trim(),
                    stock,
                    idCategoria
            );

            if (libroDAO.insertar(libro)) {
                JOptionPane.showMessageDialog(this, "Libro registrado correctamente.");
                cargarLibros();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar el libro.");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this, "Stock e ID categoría deben ser números."
            );
        }
    }

    private void actualizarLibro() {

        if (campoId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Selecciona un libro de la tabla."
            );
            return;
        }

        try {
            int stock = Integer.parseInt(campoStock.getText());
            int idCategoria = Integer.parseInt(campoCategoria.getText());

            if (stock < 0 || idCategoria <= 0) {
                JOptionPane.showMessageDialog(
                        this, "El stock no puede ser negativo y la categoría debe ser válida."
                );
                return;
            }

            Libro libro = new Libro(
                    Integer.parseInt(campoId.getText()),
                    campoTitulo.getText().trim(),
                    campoAutor.getText().trim(),
                    campoIsbn.getText().trim(),
                    campoEditorial.getText().trim(),
                    stock,
                    idCategoria
            );

            if (libroDAO.actualizar(libro)) {
                JOptionPane.showMessageDialog(this, "Libro actualizado correctamente.");
                cargarLibros();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar el libro.");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this, "Revisa los campos numéricos."
            );
        }
    }

    private void eliminarLibro() {

        if (campoId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Selecciona un libro de la tabla."
            );
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que deseas eliminar este libro?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(campoId.getText());

            if (libroDAO.eliminar(id)) {
                JOptionPane.showMessageDialog(this, "Libro eliminado correctamente.");
                cargarLibros();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo eliminar. Puede tener préstamos asociados."
                );
            }
        }
    }

    private void cargarDatosSeleccionados() {

        int fila = tablaLibros.getSelectedRow();

        if (fila >= 0) {
            campoId.setText(modeloTabla.getValueAt(fila, 0).toString());
            campoTitulo.setText(modeloTabla.getValueAt(fila, 1).toString());
            campoAutor.setText(modeloTabla.getValueAt(fila, 2).toString());
            campoIsbn.setText(modeloTabla.getValueAt(fila, 3).toString());
            campoEditorial.setText(modeloTabla.getValueAt(fila, 4).toString());
            campoStock.setText(modeloTabla.getValueAt(fila, 5).toString());
            campoCategoria.setText(modeloTabla.getValueAt(fila, 6).toString());
        }
    }

    private void limpiarCampos() {
        campoId.setText("");
        campoTitulo.setText("");
        campoAutor.setText("");
        campoIsbn.setText("");
        campoEditorial.setText("");
        campoStock.setText("");
        campoCategoria.setText("");
        tablaLibros.clearSelection();
    }
}

