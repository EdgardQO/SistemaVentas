import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class Interfaz extends JFrame {
    private JTextField usuarioTextField;
    private JPasswordField contraseñaPasswordField;

    public Interfaz() {
        // Configuración de la interfaz
        setTitle("Login");
        setSize(800, 800); // Tamaño más grande
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Crear un panel principal
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));

        // Agregar imagen
        ImageIcon logo = new ImageIcon("C:\\Users\\User\\Downloads\\sistema-e1519327286834.jpg"); // Reemplaza con la ruta de tu imagen
        JLabel imagenLabel = new JLabel(logo);
        imagenLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Agregar texto debajo de la imagen
        JLabel textoLabel = new JLabel("Bienvenido al sistema de login");
        textoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Componentes
        JPanel panelUsuario = new JPanel();
        panelUsuario.setLayout(new BoxLayout(panelUsuario, BoxLayout.Y_AXIS));
        JLabel usuarioLabel = new JLabel("Usuario:");
        usuarioLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usuarioTextField = new JTextField();
        usuarioTextField.setMaximumSize(new Dimension(300, 30)); // Establecer tamaño máximo
        usuarioTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelUsuario.add(usuarioLabel);
        panelUsuario.add(usuarioTextField);

        JPanel panelContraseña = new JPanel();
        panelContraseña.setLayout(new BoxLayout(panelContraseña, BoxLayout.Y_AXIS));
        JLabel contraseñaLabel = new JLabel("Contraseña:");
        contraseñaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contraseñaPasswordField = new JPasswordField();
        contraseñaPasswordField.setMaximumSize(new Dimension(300, 30)); // Establecer tamaño máximo
        contraseñaPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JCheckBox mostrarContraseña = new JCheckBox("Mostrar Contraseña");
        mostrarContraseña.setAlignmentX(Component.CENTER_ALIGNMENT);
        mostrarContraseña.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int estado = e.getStateChange();
                if (estado == ItemEvent.SELECTED) {
                    contraseñaPasswordField.setEchoChar((char) 0); // Mostrar contraseña
                } else {
                    contraseñaPasswordField.setEchoChar('*'); // Ocultar contraseña
                }
            }
        });

        panelContraseña.add(contraseñaLabel);
        panelContraseña.add(contraseñaPasswordField);
        panelContraseña.add(mostrarContraseña);

        // Botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.X_AXIS));
        JButton boton1 = new JButton("Iniciar sesión");
        JButton boton2 = new JButton("Registrar nuevo usuario");
        panelBotones.add(boton1);
        panelBotones.add(Box.createRigidArea(new Dimension(10, 0))); // Espacio entre botones

        // Acción del botón "Iniciar sesión"
        boton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Aquí debes agregar la lógica para verificar las credenciales de inicio de sesión
                // Si la verificación es exitosa, cierra esta interfaz y abre la InterfazPrincipal
                if (verificarCredenciales()) {
                    dispose(); // Cerrar la interfaz actual
                    new InterfazPrincipal(); // Abrir la interfaz principal
                } else {
                    JOptionPane.showMessageDialog(null, "Inicio de sesión fallido", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        // Acción del botón Registrar nuevo usuario
        boton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Cerrar la interfaz actual
                dispose();
                // Abrir la interfaz de registro
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new NuevoRegistro();
                    }
                });
            }
        });

        panelBotones.add(boton2);

        // Texto "¿Olvidaste tu contraseña?" debajo y al medio del botón "Iniciar sesión"
        JLabel olvidoLabel = new JLabel("¿Olvidaste tu contraseña?");
        olvidoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Agregar evento de clic al texto "¿Olvidaste tu contraseña?"
        olvidoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirOlvido();
            }
        });

        // Agregar componentes al panel principal
        panelPrincipal.add(imagenLabel);
        panelPrincipal.add(textoLabel);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 20))); // Espacio entre imagen/texto y campos de entrada
        panelPrincipal.add(panelUsuario);
        panelPrincipal.add(panelContraseña);
        panelPrincipal.add(panelBotones);
        panelPrincipal.add(olvidoLabel);

        // Agregar el panel principal a la interfaz
        add(panelPrincipal);

        // Hacer visible la interfaz
        setVisible(true);
    }

    // Método para abrir la interfaz de olvido de contraseña
    private void abrirOlvido() {
        dispose();  // Cerrar la ventana actual
        new Olvido();  // Abrir la interfaz de olvido de contraseña
    }

    private boolean verificarCredenciales() {
        // Implementa aquí la lógica para verificar las credenciales de inicio de sesión
        // Devuelve true si las credenciales son válidas, de lo contrario, false
        // Puedes agregar la lógica para verificar las credenciales con tu base de datos aquí

        // Realizar la consulta en la base de datos
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = new Conexion().conectar(); // Establecer conexión (ajusta según tu clase de conexión)
            String query = "SELECT * FROM USUARIOS WHERE Id_usuario = ? AND Contraseña = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, usuarioTextField.getText());
            preparedStatement.setString(2, new String(contraseñaPasswordField.getPassword()));
            resultSet = preparedStatement.executeQuery();

            // Si hay un resultado, el inicio de sesión es exitoso
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar recursos
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Si algo sale mal, consideramos el inicio de sesión como fallido
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Interfaz();
            }
        });
    }
}
