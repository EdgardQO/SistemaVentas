import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

public class NuevoRegistro extends JFrame {
    private JTextField nombresTextField;
    private JTextField apellidosTextField;
    private JTextField idUsuarioTextField;
    private JTextField dependenciaTextField;
    private JTextField celularTextField;
    private JPasswordField contraseñaField;
    private JPasswordField confirmarContraseñaField;

    public NuevoRegistro() {
        // Configuración de la interfaz de registro
        setTitle("Registro de Usuario");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Crear un panel principal
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));

        // Etiqueta de bienvenida
        JLabel bienvenidaLabel = new JLabel("Bienvenido al sistema de registro");
        bienvenidaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Componentes de registro
        JPanel panelRegistro = new JPanel();
        panelRegistro.setLayout(new BoxLayout(panelRegistro, BoxLayout.Y_AXIS));

        // Crear un panel para cada par de etiqueta y campo de texto
        crearPanelCampo("Nombres:", nombresTextField = new JTextField(), panelRegistro);
        crearPanelCampo("Apellidos:", apellidosTextField = new JTextField(), panelRegistro);
        crearPanelCampo("Id_usuario:", idUsuarioTextField = new JTextField(), panelRegistro);
        crearPanelCampo("Dependencia:", dependenciaTextField = new JTextField(), panelRegistro);
        crearPanelCampo("Celular:", celularTextField = new JTextField(), panelRegistro);
        crearPanelCampoContraseña("Contraseña:", contraseñaField = new JPasswordField(), panelRegistro);
        crearPanelCampoContraseña("Confirmar Contraseña:", confirmarContraseñaField = new JPasswordField(), panelRegistro);

        // Botones de registro
        JPanel panelBotonesRegistro = new JPanel();
        panelBotonesRegistro.setLayout(new BoxLayout(panelBotonesRegistro, BoxLayout.X_AXIS));
        JButton botonRegistrar = new JButton("Registrar");
        JButton botonLimpiar = new JButton("Limpiar");
        JButton botonRegresar = new JButton("Regresar al Login");
        panelBotonesRegistro.add(botonRegistrar);
        panelBotonesRegistro.add(Box.createRigidArea(new Dimension(10, 0)));
        panelBotonesRegistro.add(botonLimpiar);
        panelBotonesRegistro.add(Box.createRigidArea(new Dimension(10, 0)));
        panelBotonesRegistro.add(botonRegresar);

        // Acciones de los botones de registro
        agregarAccionesBotonesRegistro(botonRegistrar, botonLimpiar, botonRegresar);

        // Agregar componentes al panel principal
        panelPrincipal.add(bienvenidaLabel);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));
        panelPrincipal.add(panelRegistro);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));
        panelPrincipal.add(panelBotonesRegistro);

        // Agregar el panel principal a la interfaz de registro
        add(panelPrincipal);

        // Hacer visible la interfaz de registro
        setVisible(true);
    }

    private void crearPanelCampo(String labelText, JTextField textField, JPanel containerPanel) {
        JLabel label = new JLabel(labelText);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        textField.setMaximumSize(new Dimension(300, 30));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panelCampo = new JPanel();
        panelCampo.setLayout(new BoxLayout(panelCampo, BoxLayout.Y_AXIS));
        panelCampo.add(label);
        panelCampo.add(Box.createRigidArea(new Dimension(0, 5))); // Añadir espacio vertical
        panelCampo.add(textField);

        containerPanel.add(panelCampo);
    }

    private void crearPanelCampoContraseña(String labelText, JPasswordField passwordField, JPanel containerPanel) {
        JLabel label = new JLabel(labelText);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField.setMaximumSize(new Dimension(300, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JCheckBox mostrarContraseña = new JCheckBox("Mostrar Contraseña");
        mostrarContraseña.setAlignmentX(Component.CENTER_ALIGNMENT);

        mostrarContraseña.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                int estado = e.getStateChange();
                if (estado == ItemEvent.SELECTED) {
                    passwordField.setEchoChar((char) 0); // Mostrar contraseña
                } else {
                    passwordField.setEchoChar('*'); // Ocultar contraseña
                }
            }
        });

        JPanel panelCampo = new JPanel();
        panelCampo.setLayout(new BoxLayout(panelCampo, BoxLayout.Y_AXIS));
        panelCampo.add(label);
        panelCampo.add(Box.createRigidArea(new Dimension(0, 5))); // Añadir espacio vertical
        panelCampo.add(passwordField);
        panelCampo.add(mostrarContraseña);

        containerPanel.add(panelCampo);
    }

    private void agregarAccionesBotonesRegistro(JButton botonRegistrar, JButton botonLimpiar, JButton botonRegresar) {
        botonRegistrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Obtener los valores de los campos
                String nombres = nombresTextField.getText().trim();
                String apellidos = apellidosTextField.getText().trim();
                String idUsuario = idUsuarioTextField.getText().trim();
                String dependencia = dependenciaTextField.getText().trim();
                String celular = celularTextField.getText().trim();
                char[] contraseña = contraseñaField.getPassword();
                char[] confirmarContraseña = confirmarContraseñaField.getPassword();

                // Verificar si los campos están vacíos
                if (nombres.isEmpty() || apellidos.isEmpty() || idUsuario.isEmpty() || dependencia.isEmpty() || celular.isEmpty() || contraseña.length == 0 || confirmarContraseña.length == 0) {
                    JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Verificar si las contraseñas coinciden
                if (!Arrays.equals(contraseña, confirmarContraseña)) {
                    JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Aquí debes agregar la lógica para registrar el usuario y mostrar el mensaje de éxito
                if (agregarUsuario(nombres, apellidos, idUsuario, dependencia, celular, new String(contraseña))) {
                    JOptionPane.showMessageDialog(null, "El usuario se registró con éxito", "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
                    // Limpiar los campos de texto después de registrar
                    limpiarCampos();
                } else {
                    JOptionPane.showMessageDialog(null, "Error al registrar el usuario", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        botonLimpiar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Limpiar los campos de texto
                limpiarCampos();
            }
        });

        botonRegresar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Regresar a la interfaz de login
                dispose();  // Cerrar la ventana actual
                new Interfaz();  // Abrir la interfaz de login
            }
        });
    }

    private void limpiarCampos() {
        nombresTextField.setText("");
        apellidosTextField.setText("");
        idUsuarioTextField.setText("");
        dependenciaTextField.setText("");
        celularTextField.setText("");
        contraseñaField.setText("");
        confirmarContraseñaField.setText("");
    }

    private boolean agregarUsuario(String nombres, String apellidos, String idUsuario, String dependencia, String celular, String contraseña) {
        try (Connection connection = new Conexion().conectar()) {
            String query = "INSERT INTO USUARIOS (Nombres, Apellidos, Id_usuario, Dependencia, Celular, Contraseña) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, nombres);
                preparedStatement.setString(2, apellidos);
                preparedStatement.setString(3, idUsuario);
                preparedStatement.setString(4, dependencia);
                preparedStatement.setString(5, celular);
                preparedStatement.setString(6, contraseña);

                int filasAfectadas = preparedStatement.executeUpdate();
                return filasAfectadas > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NuevoRegistro());
    }
}
