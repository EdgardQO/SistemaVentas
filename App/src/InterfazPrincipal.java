import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InterfazPrincipal extends JFrame {
    private JButton botonProductos;
    private JButton botonClientes;
    private JButton botonVentas;
    private JButton botonReportes;

    public InterfazPrincipal() {
        // Configuración de la interfaz principal
        setTitle("Sistema de Abarrotes");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Crear un panel principal
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());

        // Crear un panel de navegación (nav)
        JPanel panelNav = new JPanel();
        panelNav.setLayout(new BoxLayout(panelNav, BoxLayout.X_AXIS));

        // Crear botones de navegación
        botonProductos = createButton(panelNav, "Productos");
        botonClientes = createButton(panelNav, "Clientes");
        botonVentas = createButton(panelNav, "Ventas");
        botonReportes = createButton(panelNav, "Reportes");

        // Agregar botones al panel de navegación
        panelNav.add(botonProductos);
        panelNav.add(botonClientes);
        panelNav.add(botonVentas);
        panelNav.add(botonReportes);

        // Agregar el panel de navegación al panel principal
        panelPrincipal.add(panelNav, BorderLayout.NORTH);

        // Agregar un panel central con una imagen y dos textos
        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));

        // Agregar imagen
        ImageIcon imagen = new ImageIcon("C:\\Users\\User\\Downloads\\sistema-e1519327286834.jpg");
        Image img = imagen.getImage();
        Image nuevaImg = img.getScaledInstance(400, 300, Image.SCALE_SMOOTH);
        imagen = new ImageIcon(nuevaImg);

        JLabel imagenLabel = new JLabel(imagen);
        imagenLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Agregar texto 1
        JLabel texto1Label = new JLabel("Texto 1");
        texto1Label.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Agregar texto 2
        JLabel texto2Label = new JLabel("Texto 2");
        texto2Label.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Agregar componentes al panel central
        panelCentro.add(Box.createRigidArea(new Dimension(0, 20))); // Espacio en blanco
        panelCentro.add(imagenLabel);
        panelCentro.add(Box.createRigidArea(new Dimension(0, 20))); // Espacio entre imagen y texto 1
        panelCentro.add(texto1Label);
        panelCentro.add(texto2Label);

        // Agregar el panel central al panel principal
        panelPrincipal.add(panelCentro, BorderLayout.CENTER);

        // Agregar el panel principal a la interfaz principal
        add(panelPrincipal);

        // Hacer visible la interfaz principal
        setVisible(true);
    }

    private JButton createButton(JPanel panel, String text) {
        JButton button = new JButton(text);

        // Establecer el tamaño preferido del botón
        Dimension buttonSize = new Dimension(800 / 4, 40);
        button.setPreferredSize(buttonSize);

        // Agregar efecto de sombreado al hacer clic
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Cierra la interfaz actual antes de abrir la nueva
                dispose();

                // Abre la interfaz correspondiente
                switch (text) {
                    case "Productos":
                        new InterfazProductos();
                        break;
                    case "Clientes":
                        new InterfazClientes();
                        break;
                    case "Ventas":
                        new InterfazVentas();
                        break;
                    case "Reportes":
                        new InterfazReportes();
                        break;
                }
            }
        });

        panel.add(button); // Agregar botón al panel

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new InterfazPrincipal();
            }
        });
    }
}
