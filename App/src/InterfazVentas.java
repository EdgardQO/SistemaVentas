import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class InterfazVentas extends JFrame {
    private JButton botonProductos;
    private JButton botonClientes;
    private JButton botonVentas;
    private JButton botonReportes;
    private JButton botonAgregar;
    private JButton botonEliminar;
    private JButton botonLogin;

    private JTable tablaCombinada;
    private DefaultTableModel modeloCombinado;

    public InterfazVentas() {
        setTitle("Interfaz de Ventas");
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

        // Crear la tabla combinada
        modeloCombinado = new DefaultTableModel();
        tablaCombinada = new JTable(modeloCombinado);
        JScrollPane scrollPaneCombinada = new JScrollPane(tablaCombinada);
        panelAreaPrincipal.add(scrollPaneCombinada, BorderLayout.CENTER);

        // Crear un panel para los botones debajo de la tabla
        JPanel panelBotones = new JPanel();

        // Botón "Agregar Venta"
        botonAgregar = new JButton("Agregar Venta");
        botonAgregar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                agregarNuevaVenta();
            }
        });

        // Botón "Eliminar Venta"
        botonEliminar = new JButton("Eliminar Venta");
        botonEliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int filaSeleccionada = tablaCombinada.getSelectedRow();
                if (filaSeleccionada != -1) {
                    int idVenta = (int) tablaCombinada.getValueAt(filaSeleccionada, 0);
                    eliminarVenta(idVenta);
                } else {
                    JOptionPane.showMessageDialog(null, "Selecciona una fila para eliminar la venta", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Botón "Regresar al Login"
        botonLogin = new JButton("Regresar al Login");
        botonLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Interfaz();
            }
        });

        // Agregar botones al panel de botones
        panelBotones.add(botonAgregar);
        panelBotones.add(botonEliminar);
        panelBotones.add(botonLogin);

        // Agregar la tabla y los botones al área principal
        panelAreaPrincipal.add(new JScrollPane(tablaCombinada), BorderLayout.CENTER);
        panelAreaPrincipal.add(panelBotones, BorderLayout.SOUTH);

        // Agregar el área principal al panel principal
        panelPrincipal.add(panelAreaPrincipal, BorderLayout.CENTER);

        // Agregar el panel principal a la interfaz de Ventas
        add(panelPrincipal);

        // Configurar la tabla combinada
        String[] columnasCombinado = {"ID VENTA", "ID USUARIO", "ID CLIENTE", "ID PRODUCTO", "FECHA", "CANTIDAD VENDIDA", "NOMBRES", "APELLIDOS"};
        modeloCombinado.setColumnIdentifiers(columnasCombinado);

        // Cargar datos en la tabla combinada desde la base de datos
        cargarDatosTablaCombinada();

        // Hacer visible la interfaz de Ventas
        setVisible(true);
    }
    private void agregarNuevaVenta() {
        Vector<Object> nuevaVenta = obtenerInformacionNuevaVenta();
    
        if (nuevaVenta != null) {
            Conexion conexion = new Conexion();
            Connection cx = conexion.conectar();
    
            if (cx != null) {
                try {
                    // Validar que el ID de usuario exista
                    if (!existeIdUsuario((int) nuevaVenta.get(1), cx)) {
                        JOptionPane.showMessageDialog(null, "El ID de usuario no existe", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
    
                    // Validar que el ID de cliente exista
                    if (!existeIdCliente((int) nuevaVenta.get(2), cx)) {
                        JOptionPane.showMessageDialog(null, "El ID de cliente no existe", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
    
                    // Validar que el ID de producto exista
                    if (!existeIdProducto((int) nuevaVenta.get(3), cx)) {
                        JOptionPane.showMessageDialog(null, "El ID de producto no existe", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
    
                    // Insertar nueva venta
                    String insertQuery = "INSERT INTO VENTAS (Id_venta, Id_usuario, Id_cliente, Id_producto, Fecha_venta, Cantidad_venta) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement insertStatement = cx.prepareStatement(insertQuery);
    
                    // Agregar valores a la consulta
                    for (int i = 0; i < 6; i++) { // Utilizar solo 6 valores (los primeros 6)
                        insertStatement.setObject(i + 1, nuevaVenta.get(i));
                    }
    
                    int rowsInserted = insertStatement.executeUpdate();
    
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "Venta agregada correctamente");
                        cargarDatosTablaCombinada();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al agregar la venta", "Error", JOptionPane.ERROR_MESSAGE);
                    }
    
                    insertStatement.close();
                    cx.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al agregar la venta: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    

    private boolean existeIdUsuario(int idUsuario, Connection cx) throws SQLException {
        String query = "SELECT * FROM USUARIOS WHERE Id_usuario = ?";
        PreparedStatement ps = cx.prepareStatement(query);
        ps.setInt(1, idUsuario);
        ResultSet rs = ps.executeQuery();
        boolean existe = rs.next();
        rs.close();
        ps.close();
        return existe;
    }

    private boolean existeIdCliente(int idCliente, Connection cx) throws SQLException {
        String query = "SELECT * FROM CLIENTES WHERE Id_cliente = ?";
        PreparedStatement ps = cx.prepareStatement(query);
        ps.setInt(1, idCliente);
        ResultSet rs = ps.executeQuery();
        boolean existe = rs.next();
        rs.close();
        ps.close();
        return existe;
    }

    private boolean existeIdProducto(int idProducto, Connection cx) throws SQLException {
        String query = "SELECT * FROM PRODUCTOS WHERE Id_producto = ?";
        PreparedStatement ps = cx.prepareStatement(query);
        ps.setInt(1, idProducto);
        ResultSet rs = ps.executeQuery();
        boolean existe = rs.next();
        rs.close();
        ps.close();
        return existe;
    }

    private Vector<Object> obtenerInformacionNuevaVenta() {
        Vector<Object> nuevaVenta = new Vector<>();

        try {
            int idVenta = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID de venta"));
            int idUsuario = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID de usuario"));
            int idCliente = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID de cliente"));
            int idProducto = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID de producto"));
            String fecha = JOptionPane.showInputDialog("Ingrese la fecha");
            int cantidadVendida = Integer.parseInt(JOptionPane.showInputDialog("Ingrese la cantidad vendida"));

            // Validar que la fecha no esté vacía
            if (fecha.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "La fecha no puede estar vacía", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            // Agregar la información al vector
            nuevaVenta.add(idVenta);
            nuevaVenta.add(idUsuario);
            nuevaVenta.add(idCliente);
            nuevaVenta.add(idProducto);
            nuevaVenta.add(fecha);
            nuevaVenta.add(cantidadVendida);

            // Obtener nombres y apellidos del cliente
            Conexion conexion = new Conexion();
            Connection cx = conexion.conectar();

            if (cx != null) {
                try {
                    String obtenerNombresQuery = "SELECT Nombre_cliente, Apellidos_cliente FROM CLIENTES WHERE Id_cliente = ?";
                    PreparedStatement obtenerNombresStatement = cx.prepareStatement(obtenerNombresQuery);
                    obtenerNombresStatement.setInt(1, idCliente);

                    ResultSet nombresResultSet = obtenerNombresStatement.executeQuery();

                    if (nombresResultSet.next()) {
                        String nombres = nombresResultSet.getString("Nombre_cliente");
                        String apellidos = nombresResultSet.getString("Apellidos_cliente");

                        nuevaVenta.add(nombres);
                        nuevaVenta.add(apellidos);
                    } else {
                        JOptionPane.showMessageDialog(null, "No se encontraron nombres y apellidos para el ID de cliente proporcionado", "Error", JOptionPane.ERROR_MESSAGE);
                        return null;
                    }

                    nombresResultSet.close();
                    obtenerNombresStatement.close();
                    cx.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al obtener nombres y apellidos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error: Ingrese datos válidos", "Error", JOptionPane.ERROR_MESSAGE);
            nuevaVenta = null;
        }

        return nuevaVenta;
    }

    private void eliminarVenta(int idVenta) {
        Conexion conexion = new Conexion();
        Connection cx = conexion.conectar();

        if (cx != null) {
            try {
                String deleteQuery = "DELETE FROM VENTAS WHERE Id_venta = ?";
                PreparedStatement deleteStatement = cx.prepareStatement(deleteQuery);
                deleteStatement.setInt(1, idVenta);

                int rowsDeleted = deleteStatement.executeUpdate();

                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(null, "Venta eliminada correctamente");
                    cargarDatosTablaCombinada();
                } else {
                    JOptionPane.showMessageDialog(null, "Error al eliminar la venta", "Error", JOptionPane.ERROR_MESSAGE);
                }

                deleteStatement.close();
                cx.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al eliminar la venta: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarDatosTablaCombinada() {
        Conexion conexion = new Conexion();
        Connection cx = conexion.conectar();

        if (cx != null) {
            try {
                String consulta = "SELECT V.*, C.Nombre_cliente, C.Apellidos_cliente FROM VENTAS V " +
                        "INNER JOIN CLIENTES C ON V.Id_cliente = C.Id_cliente";
                PreparedStatement ps = cx.prepareStatement(consulta);
                ResultSet rs = ps.executeQuery();

                modeloCombinado.setRowCount(0);

                while (rs.next()) {
                    Vector<Object> fila = new Vector<>();
                    fila.add(rs.getInt("Id_venta"));
                    fila.add(rs.getLong("Id_usuario"));
                    fila.add(rs.getInt("Id_cliente"));
                    fila.add(rs.getInt("Id_producto"));
                    fila.add(rs.getString("Fecha_venta"));
                    fila.add(rs.getInt("Cantidad_venta"));
                    fila.add(rs.getString("Nombre_cliente"));
                    fila.add(rs.getString("Apellidos_cliente"));

                    // Agregar nombres y apellidos a la tabla (aunque no se almacenen en la base de datos)
                    modeloCombinado.addRow(fila);
                }

                rs.close();
                ps.close();
                cx.close();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al cargar datos en la tabla combinada: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton createButton(JPanel panel, String text) {
        JButton button = new JButton(text);

        Dimension buttonSize = new Dimension(800 / 4, 40);
        button.setPreferredSize(buttonSize);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();

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

        panel.add(button);

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new InterfazVentas();
            }
        });
    }
}
