public class App {
    public static void main(String[] args) {
        // Llamamos a la clase Conexion
        Conexion conexion = new Conexion();
        conexion.conectar();

        // Luego, puedes llamar a la clase Interfaz
        new Interfaz();
    }
} 