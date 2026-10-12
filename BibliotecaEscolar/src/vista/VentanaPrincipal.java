
package vista;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {

        setTitle("Sistema de Gestión de Biblioteca Escolar");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10));

        JLabel titulo = new JLabel(
                "BIBLIOTECA ESCOLAR",
                JLabel.CENTER
        );

        JButton botonLibros = new JButton("Gestión de libros");
        JButton botonEstudiantes = new JButton("Gestión de estudiantes");
        JButton botonPrestamos = new JButton("Préstamos y devoluciones");
        JButton botonReportes = new JButton("Reportes");
        JButton botonSalir = new JButton("Cerrar sesión");

        botonLibros.addActionListener(e -> {
            VentanaLibros ventana = new VentanaLibros();
            ventana.setVisible(true);
        });

        botonEstudiantes.addActionListener(e -> {
            VentanaEstudiantes ventana = new VentanaEstudiantes();
            ventana.setVisible(true);
        });

        botonPrestamos.addActionListener(e -> {
            VentanaPrestamos ventana = new VentanaPrestamos();
            ventana.setVisible(true);
        });

        botonReportes.addActionListener(e -> {
            VentanaReportes ventana = new VentanaReportes();
            ventana.setVisible(true);
        });

        botonSalir.addActionListener(e -> {
            dispose();
            VentanaLogin login = new VentanaLogin();
            login.setVisible(true);
        });

        panel.add(titulo);
        panel.add(botonLibros);
        panel.add(botonEstudiantes);
        panel.add(botonPrestamos);
        panel.add(botonReportes);
        panel.add(botonSalir);

        add(panel);
    }
}

