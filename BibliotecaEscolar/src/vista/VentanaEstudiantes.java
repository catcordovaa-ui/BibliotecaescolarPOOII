
package vista;

import dao.EstudianteDAO;
import modelo.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaEstudiantes extends JFrame {

    private JTextField campoId;
    private JTextField campoNombre;
    private JTextField campoRut;
    private JTextField campoCorreo;
    private JTextField campoCurso;

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private EstudianteDAO estudianteDAO;

    public VentanaEstudiantes() {

        estudianteDAO = new EstudianteDAO();

        setTitle("Gestión de estudiantes");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel formulario = new JPanel(new GridLayout(5, 2, 5, 5));

        campoId = new JTextField();
        campoId.setEditable(false);
        campoNombre = new JTextField();
        campoRut = new JTextField();
        campoCorreo = new JTextField();
        campoCurso = new JTextField();

        formulario.add(new JLabel("ID:"));
        formulario.add(campoId);
        formulario.add(new JLabel("Nombre:"));
        formulario.add(campoNombre);
        formulario.add(new JLabel("RUT:"));
        formulario.add(campoRut);
        formulario.add(new JLabel("Correo:"));
        formulario.add(campoCorreo);
        formulario.add(new JLabel("Curso:"));
        formulario.add(campoCurso);

        JPanel botones = new JPanel();

        JButton registrar = new JButton("Registrar");
        JButton actualizar = new JButton("Actualizar");
        JButton eliminar = new JButton("Eliminar");
        JButton limpiar = new JButton("Limpiar");
        JButton volver = new JButton("Volver");

        botones.add(registrar);
        botones.add(actualizar);
        botones.add(eliminar);
        botones.add(limpiar);
        botones.add(volver);

        modeloTabla = new DefaultTableModel(
                new String[]{"ID", "Nombre", "RUT", "Correo", "Curso"}, 0
        );

        tabla = new JTable(modeloTabla);

        panel.add(formulario, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);

        add(panel);

        cargarEstudiantes();

        registrar.addActionListener(e -> registrarEstudiante());
        actualizar.addActionListener(e -> actualizarEstudiante());
        eliminar.addActionListener(e -> eliminarEstudiante());
        limpiar.addActionListener(e -> limpiarCampos());
        volver.addActionListener(e -> dispose());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccionado();
            }
        });
    }

    private void cargarEstudiantes() {
        modeloTabla.setRowCount(0);

        List<Estudiante> estudiantes = estudianteDAO.listarTodos();

        for (Estudiante estudiante : estudiantes) {
            modeloTabla.addRow(new Object[]{
                    estudiante.getId(),
                    estudiante.getNombre(),
                    estudiante.getRut(),
                    estudiante.getCorreo(),
                    estudiante.getCurso()
            });
        }
    }

    private void registrarEstudiante() {
        if (campoNombre.getText().trim().isEmpty()
                || campoRut.getText().trim().isEmpty()
                || campoCorreo.getText().trim().isEmpty()
                || campoCurso.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos.");
            return;
        }

        Estudiante estudiante = new Estudiante(
                0,
                campoNombre.getText().trim(),
                campoRut.getText().trim(),
                campoCorreo.getText().trim(),
                campoCurso.getText().trim()
        );

        if (estudianteDAO.insertar(estudiante)) {
            JOptionPane.showMessageDialog(this, "Estudiante registrado.");
            cargarEstudiantes();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo registrar.");
        }
    }

    private void actualizarEstudiante() {
        if (campoId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un estudiante.");
            return;
        }

        Estudiante estudiante = new Estudiante(
                Integer.parseInt(campoId.getText()),
                campoNombre.getText().trim(),
                campoRut.getText().trim(),
                campoCorreo.getText().trim(),
                campoCurso.getText().trim()
        );

        if (estudianteDAO.actualizar(estudiante)) {
            JOptionPane.showMessageDialog(this, "Estudiante actualizado.");
            cargarEstudiantes();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar.");
        }
    }

    private void eliminarEstudiante() {
        if (campoId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un estudiante.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this, "¿Eliminar este estudiante?",
                "Confirmar", JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(campoId.getText());

            if (estudianteDAO.eliminar(id)) {
                JOptionPane.showMessageDialog(this, "Estudiante eliminado.");
                cargarEstudiantes();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(
                        this, "No se pudo eliminar. Puede tener préstamos asociados."
                );
            }
        }
    }

    private void cargarSeleccionado() {
        int fila = tabla.getSelectedRow();

        if (fila >= 0) {
            campoId.setText(modeloTabla.getValueAt(fila, 0).toString());
            campoNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
            campoRut.setText(modeloTabla.getValueAt(fila, 2).toString());
            campoCorreo.setText(modeloTabla.getValueAt(fila, 3).toString());
            campoCurso.setText(modeloTabla.getValueAt(fila, 4).toString());
        }
    }

    private void limpiarCampos() {
        campoId.setText("");
        campoNombre.setText("");
        campoRut.setText("");
        campoCorreo.setText("");
        campoCurso.setText("");
        tabla.clearSelection();
    }
}
