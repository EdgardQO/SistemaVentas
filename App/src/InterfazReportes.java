import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class InterfazReportes extends JFrame {
    private JButton botonProductos;
    private JButton botonClientes;
    private JButton botonVentas;
    private JButton botonReportes;
    private JButton botonEmitirBoleta;
    private JButton botonEmitirFactura;
    private JButton botonRealizarBusquedas;
    private JButton botonRegresar;

    private JComboBox<String> clientesComboBox;
    private JComboBox<String> fechasComboBox;

    private Conexion conexion;

    public InterfazReportes() {
        conexion = new Conexion();

        setTitle("Interfaz de Reportes");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());

        JPanel panelNav = new JPanel();
        panelNav.setLayout(new BoxLayout(panelNav, BoxLayout.X_AXIS));

        botonProductos = createButton(panelNav, "Productos");
        botonClientes = createButton(panelNav, "Clientes");
        botonVentas = createButton(panelNav, "Ventas");
        botonReportes = createButton(panelNav, "Reportes");

        panelNav.add(botonProductos);
        panelNav.add(botonClientes);
        panelNav.add(botonVentas);
        panelNav.add(botonReportes);

        panelPrincipal.add(panelNav, BorderLayout.NORTH);

        JPanel panelAreaPrincipal = new JPanel();

        botonEmitirBoleta = createButton(panelAreaPrincipal, "Emitir Boleta");
        botonEmitirFactura = createButton(panelAreaPrincipal, "Emitir Factura");
        botonRealizarBusquedas = createButton(panelAreaPrincipal, "Realizar Búsquedas");

        panelPrincipal.add(panelAreaPrincipal, BorderLayout.CENTER);

        JPanel panelRegresar = new JPanel();
        botonRegresar = new JButton("Regresar al Login");
        panelRegresar.add(botonRegresar);
        panelPrincipal.add(panelRegresar, BorderLayout.SOUTH);

        botonRegresar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Interfaz();
            }
        });

        botonEmitirBoleta.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                seleccionarCliente();
            }
        });

        botonRealizarBusquedas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                seleccionarFecha();
            }
        });

        add(panelPrincipal);
        setVisible(true);
    }

    private JButton createButton(JPanel panel, String text) {
        JButton button = new JButton(text);
        Dimension buttonSize = new Dimension(800 / 4, 40);
        button.setPreferredSize(buttonSize);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
                        // No cierres la ventana principal al abrir Reportes
                        break;
                }
            }
        });

        panel.add(button);
        return button;
    }

    private void seleccionarCliente() {
        clientesComboBox = new JComboBox<>();
        try (Connection connection = conexion.conectar()) {
            String query = "SELECT Id_cliente, Nombre_cliente FROM CLIENTES";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String idCliente = resultSet.getString("Id_cliente");
                    String nombreCliente = resultSet.getString("Nombre_cliente");
                    clientesComboBox.addItem(idCliente + " - " + nombreCliente);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al obtener la lista de clientes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Seleccione un cliente:"));
        panel.add(clientesComboBox);

        int result = JOptionPane.showOptionDialog(null, panel, "Emitir Boleta",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{"Aceptar", "Cerrar"}, null);

        if (result == JOptionPane.OK_OPTION) {
            String selectedClient = (String) clientesComboBox.getSelectedItem();
            if (selectedClient != null && !selectedClient.isEmpty()) {
                String clientId = selectedClient.split(" ")[0]; // Obtener el ID del cliente
                mostrarGastosPorCliente(clientId);
            }
        }
    }

    private void mostrarGastosPorCliente(String clientId) {
        JDialog dialog = new JDialog(this, "Gastos por Cliente", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        try (Connection connection = conexion.conectar()) {
            String query = "SELECT CLIENTES.Nombre_cliente, Fecha_venta, " +
                    "SUM(PRODUCTOS.Precio_unitario * VENTAS.Cantidad_venta) AS MontoTotal " +
                    "FROM VENTAS " +
                    "INNER JOIN PRODUCTOS ON VENTAS.Id_producto = PRODUCTOS.Id_producto " +
                    "INNER JOIN CLIENTES ON VENTAS.Id_cliente = CLIENTES.Id_cliente " +
                    "WHERE VENTAS.Id_cliente = ? " +
                    "GROUP BY VENTAS.Fecha_venta";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, clientId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String nombreCliente = resultSet.getString("Nombre_cliente");
                        String fechaVenta = resultSet.getString("Fecha_venta");
                        double montoTotal = resultSet.getDouble("MontoTotal");

                        double igv = montoTotal * 0.18; // Calcular IGV (18%)
                        double totalPagar = montoTotal + igv;

                        textArea.append("Cliente: " + nombreCliente + "\n");
                        textArea.append("Fecha: " + fechaVenta + "\n");
                        textArea.append("Total Monto: " + montoTotal + "\n");
                        textArea.append("IGV (18%): " + igv + "\n");
                        textArea.append("Total a Pagar: " + totalPagar + "\n\n");
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al obtener gastos por cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        dialog.add(new JScrollPane(textArea));
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }

    private void seleccionarFecha() {
        fechasComboBox = new JComboBox<>();
        try (Connection connection = conexion.conectar()) {
            String query = "SELECT DISTINCT Fecha_venta FROM VENTAS";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String fechaVenta = resultSet.getString("Fecha_venta");
                    fechasComboBox.addItem(fechaVenta);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al obtener la lista de fechas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Seleccione una fecha:"));
        panel.add(fechasComboBox);

        int result = JOptionPane.showOptionDialog(null, panel, "Realizar Búsquedas",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{"Aceptar", "Cerrar"}, null);

        if (result == JOptionPane.OK_OPTION) {
            String selectedDate = (String) fechasComboBox.getSelectedItem();
            if (selectedDate != null && !selectedDate.isEmpty()) {
                mostrarVentasPorFecha(selectedDate);
            }
        }
    }

    private void mostrarVentasPorFecha(String selectedDate) {
        JDialog dialog = new JDialog(this, "Ventas por Fecha", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        try (Connection connection = conexion.conectar()) {
            String query = "SELECT VENTAS.Id_venta, CLIENTES.Nombre_cliente, VENTAS.Fecha_venta, " +
                    "VENTAS.Id_producto, VENTAS.Id_cliente, PRODUCTOS.Precio_unitario, VENTAS.Cantidad_venta " +
                    "FROM VENTAS INNER JOIN PRODUCTOS ON VENTAS.Id_producto = PRODUCTOS.Id_producto " +
                    "INNER JOIN CLIENTES ON VENTAS.Id_cliente = CLIENTES.Id_cliente " +
                    "WHERE VENTAS.Fecha_venta = ?";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, selectedDate);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int idVenta = resultSet.getInt("Id_venta");
                        String nombreCliente = resultSet.getString("Nombre_cliente");
                        String fechaVenta = resultSet.getString("Fecha_venta");
                        int idProducto = resultSet.getInt("Id_producto");
                        int idCliente = resultSet.getInt("Id_cliente");
                        double precioUnitario = resultSet.getDouble("Precio_unitario");
                        int cantidadVenta = resultSet.getInt("Cantidad_venta");

                        textArea.append("ID Venta: " + idVenta + "\n");
                        textArea.append("Cliente: " + nombreCliente + "\n");
                        textArea.append("Fecha: " + fechaVenta + "\n");
                        textArea.append("ID Producto: " + idProducto + "\n");
                        textArea.append("ID Cliente: " + idCliente + "\n");
                        textArea.append("Precio Unitario: " + precioUnitario + "\n");
                        textArea.append("Cantidad Venta: " + cantidadVenta + "\n\n");
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al obtener ventas por fecha: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        dialog.add(new JScrollPane(textArea));
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InterfazReportes());
    }
}
