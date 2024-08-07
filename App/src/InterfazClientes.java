import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InterfazClientes extends JFrame {
    private JButton botonProductos;
    private JButton botonClientes;
    private JButton botonVentas;
    private JButton botonReportes;
    private JButton botonAgregarCliente;
    private JButton botonRegresar;
    private JTable tablaClientes;

    public InterfazClientes() {
        // Configuración de la interfaz de Clientes
        setTitle("Interfaz de Clientes");
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

        // Crear un área principal para mostrar las acciones
        JPanel panelAreaPrincipal = new JPanel();
        panelAreaPrincipal.setLayout(new BorderLayout());

        // Crear la tabla de clientes
        tablaClientes = new JTable();
        actualizarTablaClientes(); // Método para actualizar el contenido de la tabla

        // Crear un panel para los botones debajo de la tabla
        JPanel panelBotones = new JPanel();

        // Botón "Agregar Cliente"
        botonAgregarCliente = new JButton("Agregar Cliente");
        botonAgregarCliente.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirVentanaAgregarCliente();
            }
        });
        // Botón "Regresar al Login"
        botonRegresar = new JButton("Regresar al Login");
        botonRegresar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Cerrar la interfaz actual y abrir la interfaz de login
                dispose();
                new Interfaz();
            }
        });
        
        // Agregar botones al panel de botones
        panelBotones.add(botonAgregarCliente);
        panelBotones.add(botonRegresar);

        // Agregar la tabla al área principal
        panelAreaPrincipal.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);
        panelAreaPrincipal.add(panelBotones, BorderLayout.SOUTH);

        // Agregar el área principal al panel principal
        panelPrincipal.add(panelAreaPrincipal, BorderLayout.CENTER);

        // Agregar el panel principal a la interfaz de Clientes
        add(panelPrincipal);

        // Hacer visible la interfaz de Clientes
        setVisible(true);
    }
    private void abrirVentanaAgregarCliente() {
        // Crear una nueva ventana para agregar un cliente
        JFrame ventanaAgregarCliente = new JFrame("Agregar Cliente");
        ventanaAgregarCliente.setSize(400, 250);
        ventanaAgregarCliente.setLayout(new BorderLayout());
        ventanaAgregarCliente.setLocationRelativeTo(null);

        // Crear panel para los campos de entrada
        JPanel panelEntrada = new JPanel();
        panelEntrada.setLayout(new GridLayout(5, 2, 5, 5));

        // Campos de entrada
        JTextField campoIdCliente = new JTextField();
        JTextField campoNombre = new JTextField();
        JTextField campoApellidos = new JTextField();
        JTextField campoCelular = new JTextField();
        JTextField campoDireccion = new JTextField();

        // Etiquetas
        JLabel etiquetaIdCliente = new JLabel("ID Cliente:");
        JLabel etiquetaNombre = new JLabel("Nombre:");
        JLabel etiquetaApellidos = new JLabel("Apellidos:");
        JLabel etiquetaCelular = new JLabel("Celular:");
        JLabel etiquetaDireccion = new JLabel("Dirección:");

        // Añadir componentes al panel de entrada
        panelEntrada.add(etiquetaIdCliente);
        panelEntrada.add(campoIdCliente);
        panelEntrada.add(etiquetaNombre);
        panelEntrada.add(campoNombre);
        panelEntrada.add(etiquetaApellidos);
        panelEntrada.add(campoApellidos);
        panelEntrada.add(etiquetaCelular);
        panelEntrada.add(campoCelular);
        panelEntrada.add(etiquetaDireccion);
        panelEntrada.add(campoDireccion);

        // Crear panel para los botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout());

        // Botones
        JButton botonAceptar = new JButton("Aceptar");
        JButton botonCerrar = new JButton("Cerrar");

        // Acción del botón Aceptar
        botonAceptar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Lógica para agregar el cliente a la base de datos
                // Puedes implementar la lógica según tus necesidades
                try {
                    String idCliente = campoIdCliente.getText();
                    String nombre = campoNombre.getText();
                    String apellidos = campoApellidos.getText();
                    String celular = campoCelular.getText();
                    String direccion = campoDireccion.getText();

                    // Verificar que los campos no estén vacíos
                    if (idCliente.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || celular.isEmpty() || direccion.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Convertir los valores de texto a los tipos de datos correspondientes
                    int idClienteInt = Integer.parseInt(idCliente);

                    // Establecer conexión a la base de datos
                    Connection connection = new Conexion().conectar();
                    String query = "INSERT INTO CLIENTES (Id_cliente, Nombre_cliente, Apellidos_cliente, Celular_cliente, Dirección_cliente) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setInt(1, idClienteInt);
                        preparedStatement.setString(2, nombre);
                        preparedStatement.setString(3, apellidos);
                        preparedStatement.setString(4, celular);
                        preparedStatement.setString(5, direccion);

                        // Ejecutar la inserción
                        preparedStatement.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Cliente añadido correctamente");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error al añadir el cliente", "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        // Cerrar la conexión a la base de datos
                        if (connection != null) {
                            connection.close();
                        }
                    }

                    // Cierra la ventana emergente después de realizar la inserción
                    ventanaAgregarCliente.dispose();

                    // Actualiza la tabla de clientes en la interfaz principal
                    actualizarTablaClientes();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Por favor, ingrese valores numéricos válidos para ID Cliente", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Acción del botón Cerrar
        botonCerrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Cierra la ventana emergente sin realizar ninguna acción
                ventanaAgregarCliente.dispose();
            }
        });

        // Añadir botones al panel de botones
        panelBotones.add(botonAceptar);
        panelBotones.add(botonCerrar);

        // Añadir paneles a la ventana emergente
        ventanaAgregarCliente.add(panelEntrada, BorderLayout.CENTER);
        ventanaAgregarCliente.add(panelBotones, BorderLayout.SOUTH);

        // Hacer visible la ventana emergente
        ventanaAgregarCliente.setVisible(true);
    }

    // Método para actualizar la tabla de clientes
    private void actualizarTablaClientes() {
        // Lógica para obtener los datos de los clientes de la base de datos
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = new Conexion().conectar(); // Establecer conexión (ajusta según tu clase de conexión)
            String query = "SELECT * FROM CLIENTES";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            // Crear las columnas y los datos para la tabla
            Vector<String> columnas = new Vector<>();
            columnas.add("ID Cliente");
            columnas.add("Nombre");
            columnas.add("Apellido");
            columnas.add("Celular");
            columnas.add("Dirección");

            Vector<Vector<Object>> datos = new Vector<>();

            // Llenar los datos desde el ResultSet
            while (resultSet.next()) {
                Vector<Object> fila = new Vector<>();
                fila.add(resultSet.getInt("Id_cliente"));
                fila.add(resultSet.getString("Nombre_cliente"));
                fila.add(resultSet.getString("Apellidos_cliente"));
                fila.add(resultSet.getString("Celular_cliente"));
                fila.add(resultSet.getString("Dirección_cliente"));
                datos.add(fila);
            }

            // Crear el modelo de la tabla y establecerlo en la tabla
            DefaultTableModel modelo = new DefaultTableModel(datos, columnas);
            tablaClientes.setModel(modelo);
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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new InterfazClientes();
            }
        });
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
}
