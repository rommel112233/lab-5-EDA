// BST.java

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

// La clave (Key) debe ser Comparable para poder ordenarla en el árbol
public class BST<Key extends Comparable<Key>, Value> {

    private Node root; // La raíz del árbol

    private class Node {
        private Key key;
        private Value val;
        private Node left, right;
        private int size; // Número de nodos en este subárbol

        public Node(Key key, Value val, int size) {
            this.key = key;
            this.val = val;
            this.size = size;
        }
    }

    public BST() {
        // Constructor vacío
    }

    public int size() {
        return size(root);
    }

    private int size(Node x) {
        if (x == null) return 0;
        else return x.size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Inserta un par clave-valor en el BST.
     * Si la clave ya existe, actualiza su valor.
     * Si el BST es para List<String>, entonces este put podría agregar a la lista existente.
     * Para el Scoreboard, esto es crucial: si ya existe una lista para esa clave (victorias),
     * querríamos añadir a la lista existente o reemplazarla si la lista está completa.
     * Por ahora, este 'put' básico sobrescribe el valor si la clave ya existe.
     * Tendremos que adaptar esto para List<String> específicamente en Scoreboard.
     */
    public void put(Key key, Value val) {
        root = put(root, key, val);
    }

    private Node put(Node x, Key key, Value val) {
        if (x == null) return new Node(key, val, 1);
        int cmp = key.compareTo(x.key);
        if (cmp < 0) x.left = put(x.left, key, val);
        else if (cmp > 0) x.right = put(x.right, key, val);
        else x.val = val; // Si la clave es igual, actualiza el valor
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }

    /**
     * Retorna el valor asociado a la clave.
     */
    public Value get(Key key) {
        return get(root, key);
    }

    private Value get(Node x, Key key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) return get(x.left, key);
        else if (cmp > 0) return get(x.right, key);
        else return x.val;
    }
    
    /**
     * Elimina la clave y su valor asociado.
     * Esto es fundamental para el winTree en Scoreboard.
     */
    public void delete(Key key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        root = delete(root, key);
    }

    private Node delete(Node x, Key key) {
        if (x == null) return null; // Clave no encontrada

        int cmp = key.compareTo(x.key);
        if (cmp < 0) x.left = delete(x.left, key);
        else if (cmp > 0) x.right = delete(x.right, key);
        else {
            // Caso 1: No tiene hijo izquierdo (o es una hoja)
            if (x.left == null) return x.right;
            // Caso 2: No tiene hijo derecho
            if (x.right == null) return x.left;
            // Caso 3: Tiene ambos hijos
            // Encontrar el sucesor (el nodo más pequeño en el subárbol derecho)
            Node temp = x;
            x = min(temp.right); // El sucesor reemplaza a x
            x.right = deleteMin(temp.right); // Elimina el sucesor del subárbol derecho original
            x.left = temp.left; // Conecta el hijo izquierdo original de x al nuevo x
        }
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }

    private Node min(Node x) {
        if (x.left == null) return x;
        return min(x.left);
    }

    private Node deleteMin(Node x) {
        if (x.left == null) return x.right;
        x.left = deleteMin(x.left);
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }

    /**
     * Retorna la clave más pequeña mayor o igual que 'key'.
     * Necesario para winSuccessor.
     */
    public Key ceiling(Key key) {
        Node x = ceiling(root, key);
        if (x == null) return null;
        return x.key;
    }

    private Node ceiling(Node x, Key key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp == 0) return x; // La clave es igual, este es el techo
        if (cmp < 0) { // La clave es menor, el techo puede estar en el subárbol izquierdo o en el propio x
            Node t = ceiling(x.left, key);
            if (t != null) return t;
            else return x;
        } else { // La clave es mayor, el techo debe estar en el subárbol derecho
            return ceiling(x.right, key);
        }
    }

    /**
     * Retorna un Iterable de valores (List<String> en nuestro caso)
     * cuyas claves están en el rango [lo, hi].
     * Esto es lo que Scoreboard necesita para winRange.
     */
    public Iterable<Value> rangeSearch(Key lo, Key hi) {
        List<Value> values = new ArrayList<>();
        rangeSearch(root, lo, hi, values);
        return values;
    }

    private void rangeSearch(Node x, Key lo, Key hi, List<Value> values) {
        if (x == null) return;
        int cmplo = lo.compareTo(x.key);
        int cmphi = hi.compareTo(x.key);

        if (cmplo < 0) rangeSearch(x.left, lo, hi, values); // Buscar en subárbol izquierdo si 'lo' es menor
        if (cmplo <= 0 && cmphi >= 0) { // Si la clave actual está en el rango
            values.add(x.val);
        }
        if (cmphi > 0) rangeSearch(x.right, lo, hi, values); // Buscar en subárbol derecho si 'hi' es mayor
    }
    
    // Método para obtener todas las claves (útil para depuración o si se necesita iterar todo)
    public Iterable<Key> keys() {
        List<Key> keyList = new ArrayList<>();
        inorder(root, keyList);
        return keyList;
    }

    private void inorder(Node x, List<Key> keyList) {
        if (x == null) return;
        inorder(x.left, keyList);
        keyList.add(x.key);
        inorder(x.right, keyList);
    }
    
    // Método para obtener todos los valores asociados a una clave (si Value es una lista)
    // Este método es una adaptación para que 'get' devuelva una lista que se pueda modificar
    // o para cuando la Key tiene asociada una List<String> y queremos manipular esa lista directamente.
    // Si tu BST es `BST<Integer, List<String>>`, entonces `get(key)` ya devolvería la `List<String>`.
    // Este método podría ser redundante si el `Value` ya es `List<String>`.
    public Value getValues(Key key) {
        return get(key); // Asumiendo que 'get' ya devuelve la Value que es una lista.
    }
}
