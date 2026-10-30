import episs.arbolbinario.Arbol_Binario;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class VerificarInterfaz {
    private static Scene escena;

    public static void main(String[] args) throws Exception {
        CountDownLatch fin = new CountDownLatch(1);
        AtomicReference<Throwable> fallo = new AtomicReference<>();
        Platform.startup(() -> {
            Stage ventana = new Stage();
            try {
                new Arbol_Binario().start(ventana);
                escena = ventana.getScene();
                comprobar(texto("inorden").contains("Árbol vacío"), "Inicio vacío");
                pulsar("ejemplo");
                actualizar();
                comprobar(nodos() == 7, "Dibujo del ejemplo");
                comprobar(texto("inorden").endsWith("20 → 30 → 40 → 50 → 60 → 70 → 80"), "Inorden visible");
                comprobar(texto("preorden").endsWith("50 → 30 → 20 → 40 → 70 → 60 → 80"), "Preorden visible");
                comprobar(texto("postorden").endsWith("20 → 40 → 30 → 60 → 80 → 70 → 50"), "Postorden visible");
                foto("target/qa/interfaz-ejemplo.png");
                poner("60");
                pulsar("buscar");
                StackPane encontrado = (StackPane) escena.lookup("#nodo-60");
                comprobar(((Circle) encontrado.getChildren().getFirst()).getFill().equals(Color.web("#15803d")), "Búsqueda verde");
                poner("65");
                pulsar("buscar");
                comprobar(texto("mensaje").contains("no está"), "Búsqueda ausente");
                pulsar("insertar");
                comprobar(nodos() == 8, "Inserción visible");
                pulsar("insertar");
                comprobar(nodos() == 8 && texto("mensaje").contains("ya existe"), "Duplicado rechazado");
                poner("30");
                pulsar("eliminar");
                comprobar(nodos() == 7 && escena.lookup("#nodo-30") == null, "Eliminar dos hijos desde GUI");
                comprobar(escena.lookup("#nodo-40") != null, "Sucesor dibujado");
                for (String invalido : new String[]{"", "hola", "3.5", "2147483648"}) {
                    poner(invalido);
                    pulsar("insertar");
                    comprobar(nodos() == 7 && texto("mensaje").contains("Introduce un entero"), "Entrada inválida: " + invalido);
                }
                for (String valor : new String[]{"-2147483648", "2147483647", "0", "-1"}) {
                    poner(valor);
                    pulsar("insertar");
                    comprobar(escena.lookup("#nodo-" + valor) != null, "Nodo visible: " + valor);
                    pulsar("buscar");
                    comprobar(texto("mensaje").contains("Se encontró"), "Buscar extremo: " + valor);
                }
                ventana.setWidth(900);
                ventana.setHeight(700);
                actualizar();
                foto("target/qa/interfaz-extremos.png");
                pulsar("vaciar");
                comprobar(nodos() == 0 && texto("inorden").contains("Árbol vacío"), "Vaciar desde GUI");
                for (int i = 1; i <= 20; i++) {
                    poner(String.valueOf(i));
                    pulsar("insertar");
                }
                actualizar();
                ScrollPane panel = (ScrollPane) escena.lookup("#desplazamiento");
                Pane lienzo = (Pane) escena.lookup("#lienzo");
                comprobar(lienzo.getPrefWidth() > panel.getViewportBounds().getWidth(), "Scroll horizontal");
                comprobar(lienzo.getPrefHeight() > panel.getViewportBounds().getHeight(), "Scroll vertical");
                poner("20");
                pulsar("buscar");
                comprobar(panel.getVvalue() > 0.8 && panel.getHvalue() > 0.8, "Enfoque del nodo profundo");
                System.out.println("GUI OK: ejemplo, recorridos, buscar, insertar, duplicados, eliminar, validación, extremos, vaciar y scroll.");
            } catch (Throwable error) {
                fallo.set(error);
                error.printStackTrace();
            } finally {
                ventana.close();
                fin.countDown();
            }
        });
        if (!fin.await(30, TimeUnit.SECONDS)) throw new AssertionError("La GUI excedió el tiempo de verificación");
        Platform.exit();
        if (fallo.get() != null) throw new AssertionError("Error al verificar GUI", fallo.get());
    }

    private static void poner(String valor) { ((TextField) escena.lookup("#entrada")).setText(valor); }
    private static void pulsar(String id) { ((Button) escena.lookup("#" + id)).fire(); }
    private static String texto(String id) { return ((Label) escena.lookup("#" + id)).getText(); }
    private static long nodos() {
        return ((Pane) escena.lookup("#lienzo")).getChildren().stream().filter(n -> n instanceof StackPane).count();
    }
    private static void comprobar(boolean condicion, String mensaje) { if (!condicion) throw new AssertionError(mensaje); }
    private static void actualizar() { escena.getRoot().applyCss(); escena.getRoot().layout(); }
    private static void foto(String ruta) throws Exception {
        actualizar();
        WritableImage captura = escena.snapshot(null);
        BufferedImage imagen = new BufferedImage((int) captura.getWidth(), (int) captura.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < imagen.getHeight(); y++) {
            for (int x = 0; x < imagen.getWidth(); x++) imagen.setRGB(x, y, captura.getPixelReader().getArgb(x, y));
        }
        ImageIO.write(imagen, "png", new File(ruta));
    }
}
