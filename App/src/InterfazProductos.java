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

public class InterfazProductos extends JFrame {
    private JButton botonProductos;
    private JButton botonClientes;
    private JButton botonVentas;
    private JButton botonReportes;
    private JTable tablaProductos;
    private JButton botonAgregar;
    private JButton botonEliminar;
    private JButton botonEditarStock;
    private JButton botonRegresar;

    public InterfazProductos() {
        // Configuración de la interfaz de Productos
        setTitle("Interfaz de Productos");
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

        // Crear la tabla de productos
        tablaProductos = new JTable();
        actualizarTablaProductos(); // Método para actualizar el contenido de la tabla

        // Crear un panel para los botones debajo de la tabla
        JPanel panelBotones = new JPanel();

        // Botón "Añadir Producto"
        botonAgregar = new JButton("Añadir Producto");
        botonAgregar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirVentanaAñadirProducto();
            }
        });

        // Botón "Eliminar Producto"
        botonEliminar = new JButton("Eliminar Producto");
        botonEliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Lógica para eliminar un producto seleccionado
                int filaSeleccionada = tablaProductos.getSelectedRow();
                if (filaSeleccionada != -1) {
                    // Obtener el ID del producto de la fila seleccionada
                    int idProducto = (int) tablaProductos.getValueAt(filaSeleccionada, 0);

                    // Confirmar la eliminación
                    int opcion = JOptionPane.showConfirmDialog(null, "¿Estás seguro de eliminar este producto?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
                    if (opcion == JOptionPane.YES_OPTION) {
                        // Eliminar el producto de la base de datos y actualizar la tabla
                        if (eliminarProducto(idProducto)) {
                            JOptionPane.showMessageDialog(null, "Producto eliminado correctamente");
                            // Actualizar la tabla de productos en la interfaz principal
                            actualizarTablaProductos();
                        } else {
                            JOptionPane.showMessageDialog(null, "Error al eliminar el producto", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Selecciona un producto para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Botón "Editar Stock"
        botonEditarStock = new JButton("Editar Stock");
        botonEditarStock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Lógica para editar el stock de un producto seleccionado
                int filaSeleccionada = tablaProductos.getSelectedRow();
                if (filaSeleccionada != -1) {
                    // Obtener el ID del producto de la fila seleccionada
                    int idProducto = (int) tablaProductos.getValueAt(filaSeleccionada, 0);
                    // Obtener el stock actual del producto
                    int stockActual = (int) tablaProductos.getValueAt(filaSeleccionada, 2);
                    // Abrir ventana emergente para editar el stock
                    abrirVentanaEditarStock(idProducto, stockActual);
                } else {
                    JOptionPane.showMessageDialog(null, "Selecciona un producto para editar el stock", "Error", JOptionPane.ERROR_MESSAGE);
                }
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
        panelBotones.add(botonAgregar);
        panelBotones.add(botonEliminar);
        panelBotones.add(botonEditarStock);
        panelBotones.add(botonRegresar);

        // Agregar la tabla y los botones al área principal
        panelAreaPrincipal.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);
        panelAreaPrincipal.add(panelBotones, BorderLayout.SOUTH);

        // Agregar el área principal al panel principal
        panelPrincipal.add(panelAreaPrincipal, BorderLayout.CENTER);

        // Agregar el panel principal a la interfaz de Productos
        add(panelPrincipal);

        // Hacer visible la interfaz de Productos
        setVisible(true);
    }

    // Método para abrir la ventana de editar stock
    private void abrirVentanaEditarStock(int idProducto, int stockActual) {
        // Crear una nueva ventana para editar el stock
        JFrame ventanaEditarStock = new JFrame("Editar Stock");
        ventanaEditarStock.setSize(300, 150);
        ventanaEditarStock.setLayout(new BorderLayout());
        ventanaEditarStock.setLocationRelativeTo(null);

        // Crear panel para los campos de entrada
        JPanel panelEntrada = new JPanel();
        panelEntrada.setLayout(new GridLayout(2, 2, 5, 5));

        // Campos de entrada
        JTextField campoStock = new JTextField(String.valueOf(stockActual));

        // Etiquetas
        JLabel etiquetaStock = new JLabel("Nuevo Stock:");

        // Añadir componentes al panel de entrada
        panelEntrada.add(etiquetaStock);
        panelEntrada.add(campoStock);

        // Crear panel para los botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout());

        // Botones
        JButton botonAceptar = new JButton("Aceptar");
        JButton botonCerrar = new JButton("Cerrar");

        // Acción del botón Aceptar
        botonAceptar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Lógica para editar el stock del producto en la base de datos
                try {
                    // Obtener el nuevo stock desde el campo de texto
                    int nuevoStock = Integer.parseInt(campoStock.getText());

                    // Verificar que el nuevo stock no sea negativo
                    if (nuevoStock < 0) {
                        JOptionPane.showMessageDialog(null, "El stock no puede ser negativo", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Actualizar el stock del producto en la base de datos
                    if (editarStockProducto(idProducto, nuevoStock)) {
                        JOptionPane.showMessageDialog(null, "Stock actualizado correctamente");
                        // Actualizar la tabla de productos en la interfaz principal
                        actualizarTablaProductos();

                        // Verificar si el nuevo stock es igual a cero y, en ese caso, eliminar el producto
                        if (nuevoStock == 0) {
                            if (eliminarProducto(idProducto)) {
                                JOptionPane.showMessageDialog(null, "Producto eliminado debido a que el stock es cero");
                                // Actualizar la tabla de productos en la interfaz principal
                                actualizarTablaProductos();
                            } else {
                                JOptionPane.showMessageDialog(null, "Error al eliminar el producto", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al actualizar el stock", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Por favor, ingrese un valor numérico válido para el stock", "Error", JOptionPane.ERROR_MESSAGE);
                }
                // Cierra la ventana emergente después de realizar la actualización
                ventanaEditarStock.dispose();
            }
        });

        // Acción del botón Cerrar
        botonCerrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Cierra la ventana emergente sin realizar ninguna acción
                ventanaEditarStock.dispose();
            }
        });

        // Añadir botones al panel de botones
        panelBotones.add(botonAceptar);
        panelBotones.add(botonCerrar);

        // Añadir paneles a la ventana emergente
        ventanaEditarStock.add(panelEntrada, BorderLayout.CENTER);
        ventanaEditarStock.add(panelBotones, BorderLayout.SOUTH);

        // Hacer visible la ventana emergente
        ventanaEditarStock.setVisible(true);
    }

    // Método para editar el stock de un producto en la base de datos
    private boolean editarStockProducto(int idProducto, int nuevoStock) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = new Conexion().conectar(); // Establecer conexión (ajusta según tu clase de conexión)
            String query = "UPDATE PRODUCTOS SET Stock = ? WHERE Id_producto = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, nuevoStock);
            preparedStatement.setInt(2, idProducto);

            // Ejecutar la actualización
            int filasAfectadas = preparedStatement.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // Cerrar recursos
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
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

    private void actualizarTablaProductos() {
        // Lógica para obtener los datos de los productos de la base de datos
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
    
        try {
            connection = new Conexion().conectar(); // Establecer conexión (ajusta según tu clase de conexión)
            String query = "SELECT * FROM PRODUCTOS";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
    
            // Crear las columnas y los datos para la tabla
            Vector<String> columnas = new Vector<>();
            columnas.add("ID Producto");
            columnas.add("Descripción");
            columnas.add("Stock");
            columnas.add("Precio Unitario");
    
            Vector<Vector<Object>> datos = new Vector<>();
    
            // Llenar los datos desde el ResultSet
            while (resultSet.next()) {
                Vector<Object> fila = new Vector<>();
                fila.add(resultSet.getInt("Id_producto"));
                fila.add(resultSet.getString("Descripción_producto"));
                fila.add(resultSet.getInt("Stock"));
                fila.add(resultSet.getString("Precio_unitario")); // Obtener el precio como String
                datos.add(fila);
            }
    
            // Crear el modelo de la tabla y establecerlo en la tabla
            DefaultTableModel modelo = new DefaultTableModel(datos, columnas);
            tablaProductos.setModel(modelo);
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

    private boolean eliminarProducto(int idProducto) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = new Conexion().conectar(); // Establecer conexión (ajusta según tu clase de conexión)
            String query = "DELETE FROM PRODUCTOS WHERE Id_producto = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, idProducto);

            // Ejecutar la eliminación
            int filasAfectadas = preparedStatement.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // Cerrar recursos
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    // Método para abrir la ventana de añadir producto
    private void abrirVentanaAñadirProducto() {
        // Crear una nueva ventana para añadir un producto
        JFrame ventanaAñadirProducto = new JFrame("Añadir Producto");
        ventanaAñadirProducto.setSize(400, 250);
        ventanaAñadirProducto.setLayout(new BorderLayout());
        ventanaAñadirProducto.setLocationRelativeTo(null);
    
        // Crear panel para los campos de entrada
        JPanel panelEntrada = new JPanel();
        panelEntrada.setLayout(new GridLayout(4, 2, 5, 5));
    
        // Campos de entrada
        JTextField campoIdProducto = new JTextField();
        JTextField campoDescripcion = new JTextField();
        JTextField campoStock = new JTextField();
        JTextField campoPrecio = new JTextField(); // Mantenemos JTextField para el campo de precio
    
        // Etiquetas
        JLabel etiquetaIdProducto = new JLabel("ID Producto:");
        JLabel etiquetaDescripcion = new JLabel("Descripción:");
        JLabel etiquetaStock = new JLabel("Stock:");
        JLabel etiquetaPrecio = new JLabel("Precio:");
    
        // Añadir componentes al panel de entrada
        panelEntrada.add(etiquetaIdProducto);
        panelEntrada.add(campoIdProducto);
        panelEntrada.add(etiquetaDescripcion);
        panelEntrada.add(campoDescripcion);
        panelEntrada.add(etiquetaStock);
        panelEntrada.add(campoStock);
        panelEntrada.add(etiquetaPrecio);
        panelEntrada.add(campoPrecio);
    
        // Crear panel para los botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout());
    
        // Botones
        JButton botonLimpiar = new JButton("Limpiar");
        JButton botonAceptar = new JButton("Aceptar");
        JButton botonCerrar = new JButton("Cerrar");
    
        // Acción del botón Limpiar
        botonLimpiar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Limpiar los campos de entrada
                campoIdProducto.setText("");
                campoDescripcion.setText("");
                campoStock.setText("");
                campoPrecio.setText(null);
            }
        });
    
        // Acción del botón Aceptar
        botonAceptar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Lógica para añadir el producto a la base de datos
                // Puedes implementar la lógica según tus necesidades
                try {
                    String idProducto = campoIdProducto.getText();
                    String descripcion = campoDescripcion.getText();
                    String stock = campoStock.getText();
                    String precio = campoPrecio.getText();
        
                    // Verificar que los campos no estén vacíos
                    if (idProducto.isEmpty() || descripcion.isEmpty() || stock.isEmpty() || precio.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
        
                    // Convertir los valores de texto a los tipos de datos correspondientes
                    int idProductoInt = Integer.parseInt(idProducto);
                    int stockInt = Integer.parseInt(stock);
        
                    // Establecer conexión a la base de datos
                    Connection connection = new Conexion().conectar();
                    String query = "INSERT INTO PRODUCTOS (Id_producto, Descripción_producto, Stock, Precio_unitario) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setInt(1, idProductoInt);
                        preparedStatement.setString(2, descripcion);
                        preparedStatement.setInt(3, stockInt);
                        preparedStatement.setString(4, precio); // Se utiliza el precio como string
        
                        // Ejecutar la inserción
                        preparedStatement.executeUpdate();
        
                        JOptionPane.showMessageDialog(null, "Producto añadido correctamente");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error al añadir el producto", "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        // Cerrar la conexión a la base de datos
                        if (connection != null) {
                            connection.close();
                        }
                    }
        
                    // Cierra la ventana emergente después de realizar la inserción
                    ventanaAñadirProducto.dispose();
        
                    // Actualiza la tabla de productos en la interfaz principal
                    actualizarTablaProductos();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Por favor, ingrese valores numéricos válidos para Stock", "Error", JOptionPane.ERROR_MESSAGE);
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
                ventanaAñadirProducto.dispose();
            }
        });
    
        // Añadir botones al panel de botones
        panelBotones.add(botonLimpiar);
        panelBotones.add(botonAceptar);
        panelBotones.add(botonCerrar);
    
        // Añadir paneles a la ventana emergente
        ventanaAñadirProducto.add(panelEntrada, BorderLayout.CENTER);
        ventanaAñadirProducto.add(panelBotones, BorderLayout.SOUTH);
    
        // Hacer visible la ventana emergente
        ventanaAñadirProducto.setVisible(true);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new InterfazProductos();
            }
        });
    }
}
