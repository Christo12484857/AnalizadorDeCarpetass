// Las tres clases del programa comparten este paquete para organizar el proyecto.
package episs.arbolbinario;

// Application proporciona el método que inicia el entorno gráfico de JavaFX.
import javafx.application.Application;

/** Clase de inicio: ejecuta este archivo con el triángulo verde de IntelliJ. */
// Esta clase solo arranca la aplicación; la ventana y la lógica del árbol están
// en las otras dos clases para poder explicarlas por separado.
public class Main {
    // main es el punto de entrada que Java ejecuta al iniciar el programa.
    // public permite que Java lo invoque; static permite llamarlo sin crear Main.
    // void significa que no devuelve un resultado.
    // args contiene los argumentos de ejecución; aquí no se necesitan argumentos.
    public static void main(String[] args) {
        // JavaFX abre la ventana y llama al método start de Arbol_Binario.
        // Arbol_Binario.class indica cuál es la clase Application que se debe abrir.
        // launch inicia el entorno gráfico y mantiene su ciclo de eventos activo
        // para atender los botones y el teclado mientras la aplicación se ejecuta.
        Application.launch(Arbol_Binario.class, args);
    }
}
