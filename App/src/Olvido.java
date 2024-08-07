import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
public class Olvido extends JFrame {
    private JTextField usuarioTextField;
    private JTextField dependenciaTextField;
    private JPasswordField nuevaContraseñaField;
    private JCheckBox mostrarContraseñaNueva;
    private JPasswordField confirmarContraseñaField;
    private JCheckBox mostrarContraseñaConfirmar;

    private Conexion conexion;  // Asegúrate de tener la clase Conexion que proporcionaste

    public Olvido() {
        conexion = new Conexion();

        // Configuración de la interfaz de olvido de contraseña
        setTitle("Olvido de Contraseña");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Crear un panel principal
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));

        // Etiqueta de bienvenida
        JLabel bienvenidaLabel = new JLabel("Bienvenido al sistema de recuperación de contraseña");
        bienvenidaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Componentes de olvido de contraseña
        JPanel panelOlvido = new JPanel();
        panelOlvido.setLayout(new BoxLayout(panelOlvido, BoxLayout.Y_AXIS));

        JLabel usuarioLabel = new JLabel("Usuario:");
        usuarioLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usuarioTextField = new JTextField();
        usuarioTextField.setMaximumSize(new Dimension(300, 30));
        usuarioTextField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel dependenciaLabel = new JLabel("Dependencia:");
        dependenciaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dependenciaTextField = new JTextField();
        dependenciaTextField.setMaximumSize(new Dimension(300, 30));
        dependenciaTextField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nuevaContraseñaLabel = new JLabel("Nueva Contraseña:");
        nuevaContraseñaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nuevaContraseñaField = new JPasswordField();
        nuevaContraseñaField.setMaximumSize(new Dimension(300, 30));
        nuevaContraseñaField.setAlignmentX(Component.CENTER_ALIGNMENT);

        mostrarContraseñaNueva = new JCheckBox("Mostrar Contraseña");
        mostrarContraseñaNueva.setAlignmentX(Component.CENTER_ALIGNMENT); // Alineación a la izquierda

        JLabel confirmarContraseñaLabel = new JLabel("Confirmar Contraseña:");
        confirmarContraseñaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmarContraseñaField = new JPasswordField();
        confirmarContraseñaField.setMaximumSize(new Dimension(300, 30));
        confirmarContraseñaField.setAlignmentX(Component.CENTER_ALIGNMENT);

        mostrarContraseñaConfirmar = new JCheckBox("Mostrar Contraseña");
        mostrarContraseñaConfirmar.setAlignmentX(Component.CENTER_ALIGNMENT); // Alineación a la izquierda

        // Agregar evento para mostrar/ocultar la contraseña nueva
        mostrarContraseñaNueva.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                int estado = e.getStateChange();
                if (estado == ItemEvent.SELECTED) {
                    nuevaContraseñaField.setEchoChar((char) 0); // Mostrar contraseña
                } else {
                    nuevaContraseñaField.setEchoChar('*'); // Ocultar contraseña
                }
            }
        });

        // Agregar evento para mostrar/ocultar la contraseña de confirmación
        mostrarContraseñaConfirmar.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                int estado = e.getStateChange();
                if (estado == ItemEvent.SELECTED) {
                    confirmarContraseñaField.setEchoChar((char) 0); // Mostrar contraseña
                } else {
                    confirmarContraseñaField.setEchoChar('*'); // Ocultar contraseña
                }
            }
        });

        // Botones de olvido de contraseña
        JPanel panelBotonesOlvido = new JPanel();
        panelBotonesOlvido.setLayout(new BoxLayout(panelBotonesOlvido, BoxLayout.X_AXIS));
        JButton botonAceptar = new JButton("Aceptar");
        JButton botonLimpiar = new JButton("Limpiar");
        JButton botonRegresar = new JButton("Regresar al Login");
        panelBotonesOlvido.add(botonAceptar);
        panelBotonesOlvido.add(Box.createRigidArea(new Dimension(10, 0)));
        panelBotonesOlvido.add(botonLimpiar);
        panelBotonesOlvido.add(Box.createRigidArea(new Dimension(10, 0)));
        panelBotonesOlvido.add(botonRegresar);

        // Acciones de los botones de olvido de contraseña
        botonAceptar.addActionListener(e -> {
            // Obtener los valores de los campos
            String usuario = usuarioTextField.getText().trim();
            String dependencia = dependenciaTextField.getText().trim();
            char[] nuevaContraseña = nuevaContraseñaField.getPassword();
            char[] confirmarContraseña = confirmarContraseñaField.getPassword();

            // Verificar si los campos están vacíos
            if (usuario.isEmpty() || dependencia.isEmpty() || nuevaContraseña.length == 0 || confirmarContraseña.length == 0) {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar si las contraseñas coinciden
            if (!Arrays.equals(nuevaContraseña, confirmarContraseña)) {
                JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar si el usuario y la dependencia coinciden con algún usuario en la base de datos
            if (verificarUsuarioDependencia(usuario, dependencia)) {
                // Aquí debes agregar la lógica para cambiar la contraseña y mostrar el mensaje de éxito
                cambiarContraseña(usuario, dependencia, new String(nuevaContraseña));

                JOptionPane.showMessageDialog(null, "Contraseña cambiada con éxito", "Cambio Exitoso", JOptionPane.INFORMATION_MESSAGE);

                // Limpiar los campos de texto después de cambiar la contraseña
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(null, "Usuario o dependencia incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        botonLimpiar.addActionListener(e -> {
            // Limpiar los campos de texto
            limpiarCampos();
        });

        botonRegresar.addActionListener(e -> {
            // Regresar a la interfaz de login
            dispose();  // Cerrar la ventana actual
            new Interfaz();  // Abrir la interfaz de login
        });

        // Agregar componentes al panel de olvido de contraseña
        panelOlvido.add(bienvenidaLabel);
        panelOlvido.add(usuarioLabel);
        panelOlvido.add(usuarioTextField);
        panelOlvido.add(dependenciaLabel);
        panelOlvido.add(dependenciaTextField);
        panelOlvido.add(nuevaContraseñaLabel);
        panelOlvido.add(nuevaContraseñaField);
        panelOlvido.add(mostrarContraseñaNueva);
        panelOlvido.add(confirmarContraseñaLabel);
        panelOlvido.add(confirmarContraseñaField);
        panelOlvido.add(mostrarContraseñaConfirmar);

        // Agregar componentes al panel principal
        panelPrincipal.add(panelOlvido);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));
        panelPrincipal.add(panelBotonesOlvido);

        // Agregar el panel principal a la interfaz de olvido de contraseña
        add(panelPrincipal);

        // Hacer visible la interfaz de olvido de contraseña
        setVisible(true);
    }

    // Método para verificar si el usuario y la dependencia coinciden con algún usuario en la base de datos
    private boolean verificarUsuarioDependencia(String usuario, String dependencia) {
        try (Connection connection = conexion.conectar()) {
            String query = "SELECT 1 FROM USUARIOS WHERE Id_usuario = ? AND Dependencia = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, usuario);
                preparedStatement.setString(2, dependencia);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next();  // Retorna true si hay una coincidencia
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Método para cambiar la contraseña en la base de datos
    private void cambiarContraseña(String usuario, String dependencia, String nuevaContraseña) {
        try (Connection connection = conexion.conectar()) {
            String query = "UPDATE USUARIOS SET Contraseña = ? WHERE Id_usuario = ? AND Dependencia = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, nuevaContraseña);
                preparedStatement.setString(2, usuario);
                preparedStatement.setString(3, dependencia);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para limpiar los campos de texto
    private void limpiarCampos() {
        usuarioTextField.setText("");
        dependenciaTextField.setText("");
        nuevaContraseñaField.setText("");
        confirmarContraseñaField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Olvido());
    }
}
