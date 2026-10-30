package episs.arbolbinario;

import java.util.ArrayList;
import java.util.List;

/*
 * Regla del ABB: los menores van a la izquierda y los mayores a la derecha.
 * Un subárbol es un nodo junto con todos sus descendientes. La misma regla
 * de menores y mayores se cumple en cada uno de los subárboles del ABB.
 */
public class ArbolBinarioBusqueda {

    /** Un nodo guarda un número y las referencias a sus dos posibles hijos. */
    // public: otras clases pueden conocer este tipo, por ejemplo para dibujarlo.
    // static: un Nodo no necesita guardar una referencia a un objeto árbol externo.
    // No significa que todos los nodos compartan sus datos: cada nodo es distinto.
    public static final class Nodo {
        // private protege los campos: la GUI no puede cambiarlos directamente.
        // int guarda un entero, por ejemplo 50. No guarda otro nodo ni admite null.
        private int dato;
        // Estas variables guardan referencias a otros objetos Nodo, no copias.
        // null significa que no existe un hijo en ese lado. Al crear un Nodo,
        // Java inicializa automáticamente ambas referencias con null.
        private Nodo izquierdo;
        private Nodo derecho;

        // Constructor: se ejecuta al escribir new Nodo(...).
        // Es privado para que los nodos se creen mediante la lógica del árbol.
        private Nodo(int dato) {
            // this.dato es el campo del objeto; dato es el parámetro recibido.
            this.dato = dato;
        }

        // La interfaz puede leer los nodos para dibujarlos, pero no modificarlos.
        // Estos métodos se llaman getters: devuelven el valor de un campo.
        public int getDato() {
            return dato;
        }

        // Devuelve la referencia al hijo izquierdo, o null si no existe.
        public Nodo getIzquierdo() {
            return izquierdo;
        }

        // Devuelve la referencia al hijo derecho, o null si no existe.
        public Nodo getDerecho() {
            return derecho;
        }
    }

    // La raíz es la entrada al árbol. Si vale null, el árbol está vacío.
    // No necesitamos una lista adicional: desde la raíz seguimos los enlaces
    // izquierdo y derecho para llegar a todos los demás nodos.
    private Nodo raiz;

    // Permite que la GUI empiece a leer el árbol desde su primer nodo.
    public Nodo getRaiz() {
        return raiz;
    }

    public boolean estaVacio() {
        // == compara; = asigna. Esta comparación devuelve true o false.
        return raiz == null;
    }

    /** Devuelve true si agregó el número o false si ya estaba en el árbol. */
    // Método público: quien lo usa solo indica el número, sin pasar ningún nodo.
    // insertar(int) e insertar(Nodo, int) son métodos sobrecargados: tienen el
    // mismo nombre, pero Java los distingue por sus diferentes parámetros.
    public boolean insertar(int dato) {
        // Primero buscamos para impedir duplicados y comunicar el resultado
        // mediante un boolean. Esta búsqueda no modifica el árbol.
        if (buscar(dato)) {
            return false;
        }
        // El ayudante devuelve la raíz del subárbol resultante. La guardamos
        // porque, si antes estaba vacío, ahora hay un nuevo primer nodo.
        raiz = insertar(raiz, dato);
        return true;
    }

    // Método privado recursivo: actual es el nodo que estamos revisando.
    // Recursión significa llamar al mismo método con un problema más pequeño:
    // aquí avanzamos hacia uno de los hijos hasta encontrar un espacio libre.
    // Este método devuelve un Nodo, mientras que el público devuelve boolean.
    private Nodo insertar(Nodo actual, int dato) {
        // Caso base: encontramos un espacio libre y creamos el nuevo nodo.
        // El caso base detiene las llamadas; sin él la recursión no terminaría.
        if (actual == null) {
            return new Nodo(dato);
        }
        if (dato < actual.dato) {
            // Guardamos el resultado para enlazar el hijo nuevo con su padre.
            // Ejemplo: insertar 30 debajo de 50 coloca el nuevo nodo a su izquierda.
            actual.izquierdo = insertar(actual.izquierdo, dato);
        } else if (dato > actual.dato) {
            actual.derecho = insertar(actual.derecho, dato);
        }
        // La igualdad no entra en ninguna rama: nunca se crea un duplicado.
        // Al terminar cada llamada, el retorno permite regresar a su padre.
        return actual; // Devolvemos la raíz del subárbol después de modificarlo.
    }

    // Punto de entrada de la búsqueda: siempre comienza en la raíz.
    public boolean buscar(int dato) {
        return buscar(raiz, dato);
    }

    // Esta otra sobrecarga recibe la posición actual y busca solo donde puede
    // estar el dato. No necesita revisar los dos hijos en cada comparación.
    private boolean buscar(Nodo actual, int dato) {
        if (actual == null) {
            return false; // Caso base: llegamos a un espacio vacío sin encontrarlo.
        }
        if (dato == actual.dato) {
            // Encontrarlo en cualquier nivel es suficiente para terminar.
            return true;
        }
        if (dato < actual.dato) {
            return buscar(actual.izquierdo, dato);
        }
        // Si no era igual ni menor, es mayor: continuamos por la derecha.
        // Cada return lleva el mismo resultado hasta la primera llamada.
        return buscar(actual.derecho, dato);
    }

    /** Devuelve true si eliminó el número o false si no existía. */
    public boolean eliminar(int dato) {
        // ! significa negación. Si buscar devuelve false, !false es true
        // y entramos al if para informar que no hay nada que eliminar.
        if (!buscar(dato)) {
            return false;
        }
        // La raíz también puede cambiar, por ejemplo al borrar el único nodo.
        raiz = eliminar(raiz, dato);
        return true;
    }

    // Como al insertar, devolvemos cómo queda el subárbol y reasignamos sus
    // enlaces. Así no se pierde el hijo que deba sustituir al nodo eliminado.
    private Nodo eliminar(Nodo actual, int dato) {
        if (actual == null) {
            return null;
        }
        if (dato < actual.dato) {
            // Todavía no lo encontramos: primero buscamos en el lado correcto.
            actual.izquierdo = eliminar(actual.izquierdo, dato);
        } else if (dato > actual.dato) {
            actual.derecho = eliminar(actual.derecho, dato);
        } else {
            // Llegamos aquí cuando dato == actual.dato: este nodo debe salir.
            // Caso 1: si es una hoja, se devuelve null y se desconecta del padre.
            // Caso 2: si solo tiene un hijo, ese hijo ocupa su lugar.
            // La primera condición cubre una hoja y también un nodo que tiene
            // únicamente hijo derecho: en una hoja, derecho ya vale null.
            if (actual.izquierdo == null) {
                return actual.derecho;
            }
            // Si llegamos aquí, sabemos que sí hay hijo izquierdo. Si no hay
            // derecho, devolvemos el izquierdo para que el padre lo adopte.
            if (actual.derecho == null) {
                return actual.izquierdo;
            }

            // Caso 3: tiene dos hijos. El sucesor es el menor del lado derecho.
            // Ejemplo: al quitar 50 de un árbol con 30 a la izquierda y con
            // 70, 60 y 80 en su lado derecho, el sucesor de 50 es 60.
            // Reemplazar 50 por 60 conserva el orden de menores y mayores.
            Nodo sucesor = nodoMinimo(actual.derecho);
            // Copiamos el número, no el nodo completo ni sus referencias.
            actual.dato = sucesor.dato;
            // Quitamos el sucesor original para que su valor no quede repetido.
            actual.derecho = eliminar(actual.derecho, sucesor.dato);
        }
        return actual; // El padre recibe el subárbol con sus enlaces actualizados.
    }

    // En un ABB, seguir siempre a la izquierda lleva al menor de ese subárbol.
    // Aquí recibimos un nodo existente; los métodos que llaman a este ayudante
    // ya comprobaron que ese subárbol no está vacío.
    private Nodo nodoMinimo(Nodo actual) {
        while (actual.izquierdo != null) {
            // Cambia la referencia local actual; no modifica los enlaces.
            actual = actual.izquierdo;
        }
        return actual;
    }

    // Integer es la clase que representa un entero y también admite null.
    // Usamos Integer en mínimo y máximo para expresar "no hay resultado"
    // cuando está vacío; el tipo primitivo int no permite devolver null.
    public Integer minimo() {
        // Operador ternario: condición ? resultadoSiEsTrue : resultadoSiEsFalse.
        return estaVacio() ? null : nodoMinimo(raiz).dato;
    }

    public Integer maximo() {
        if (estaVacio()) {
            return null;
        }
        // El mayor se encuentra de forma simétrica: siempre hacia la derecha.
        Nodo actual = raiz;
        while (actual.derecho != null) {
            actual = actual.derecho;
        }
        return actual.dato;
    }

    // Otra vez separamos la entrada pública del ayudante que recibe un nodo.
    public int contarNodos() {
        return contarNodos(raiz);
    }

    private int contarNodos(Nodo actual) {
        if (actual == null) {
            // Un espacio vacío aporta cero nodos al conteo.
            return 0;
        }
        // 1 cuenta el nodo actual; después sumamos sus dos subárboles.
        return 1 + contarNodos(actual.izquierdo) + contarNodos(actual.derecho);
    }

    /** Altura medida en aristas: árbol vacío = -1; raíz sola = 0. */
    public int altura() {
        return altura(raiz);
    }

    private int altura(Nodo actual) {
        if (actual == null) {
            // La convención -1 hace que una hoja tenga altura 1 + (-1) = 0.
            return -1;
        }
        // Una arista es una conexión entre dos nodos. Math.max elige el lado
        // más profundo y sumamos la conexión que baja desde el nodo actual.
        // Por ejemplo, raíz -> hijo -> nieto tiene altura 2, aunque tenga 3 niveles.
        return 1 + Math.max(altura(actual.izquierdo), altura(actual.derecho));
    }

    // void indica que este método no devuelve un resultado.
    public void vaciar() {
        // Al soltar la raíz, el árbol deja de acceder a todos sus nodos.
        // Java administra la memoria; aquí no necesitamos borrar uno por uno.
        raiz = null;
    }

    /** Izquierda, raíz, derecha: en un ABB produce los números ordenados. */
    public List<Integer> inorden() {
        // List<Integer> es una lista de enteros. Cada llamada pública crea una
        // lista nueva y vacía, para no mezclar este recorrido con uno anterior.
        List<Integer> recorrido = new ArrayList<>();
        // Todos los pasos recursivos reciben la misma lista y agregan datos a ella.
        inorden(raiz, recorrido);
        // Devolvemos la lista terminada para que la GUI decida cómo mostrarla.
        return recorrido;
    }

    private void inorden(Nodo actual, List<Integer> recorrido) {
        if (actual == null) {
            return; // Todos los recorridos terminan al llegar a un hijo vacío.
        }
        // 1. Visitamos los menores. 2. Agregamos el nodo. 3. Visitamos los mayores.
        // add agrega un dato al final de la lista, conservando el orden de visita.
        inorden(actual.izquierdo, recorrido);
        recorrido.add(actual.dato);
        inorden(actual.derecho, recorrido);
    }

    /** Raíz, izquierda, derecha. */
    public List<Integer> preorden() {
        // Se usa el mismo esquema de lista nueva + ayudante recursivo.
        List<Integer> recorrido = new ArrayList<>();
        preorden(raiz, recorrido);
        return recorrido;
    }

    private void preorden(Nodo actual, List<Integer> recorrido) {
        if (actual == null) {
            return;
        }
        // Agregamos ANTES de visitar los hijos: por eso se llama preorden.
        recorrido.add(actual.dato);
        preorden(actual.izquierdo, recorrido);
        preorden(actual.derecho, recorrido);
    }

    /** Izquierda, derecha, raíz. */
    public List<Integer> postorden() {
        List<Integer> recorrido = new ArrayList<>();
        postorden(raiz, recorrido);
        return recorrido;
    }

    private void postorden(Nodo actual, List<Integer> recorrido) {
        if (actual == null) {
            return;
        }
        // Agregamos DESPUÉS de visitar los dos hijos: por eso se llama postorden.
        // Con raíz 50 e hijos 30 y 70: inorden [30, 50, 70],
        // preorden [50, 30, 70] y postorden [30, 70, 50].
        postorden(actual.izquierdo, recorrido);
        postorden(actual.derecho, recorrido);
        recorrido.add(actual.dato);
    }
}
