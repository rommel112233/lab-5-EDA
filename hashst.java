// HashST.java

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class HashST<Key, Value> {

    private static final int DEFAULT_CAPACITY = 10; // Capacidad inicial por defecto
    private int M; // Número de cadenas (buckets)
    private int N; // Número de pares clave-valor
    private LinkedList<Entry>[] st; // Array de listas enlazadas (cadenas)

    // Clase interna para representar un par clave-valor
    private static class Entry<Key, Value> {
        Key key;
        Value value;

        public Entry(Key key, Value value) {
            this.key = key;
            this.value = value;
        }
    }

    public HashST() {
        this(DEFAULT_CAPACITY);
    }

    public HashST(int capacity) {
        this.M = capacity;
        this.N = 0;
        st = (LinkedList<Entry>[]) new LinkedList[M];
        for (int i = 0; i < M; i++) {
            st[i] = new LinkedList<>();
        }
    }

    // Función hash para convertir la clave en un índice
    private int hash(Key key) {
        return (key.hashCode() & 0x7fffffff) % M;
    }

    public int size() {
        return N;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Inserta un par clave-valor en la tabla hash.
     * Si la clave ya existe, actualiza su valor.
     */
    public void put(Key key, Value val) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        if (val == null) {
            // Si el valor es null, podemos considerar eliminar la clave.
            delete(key);
            return;
        }

        int i = hash(key);
        for (Entry<Key, Value> entry : st[i]) {
            if (entry.key.equals(key)) {
                entry.value = val; // Clave existente, actualizar valor
                return;
            }
        }
        st[i].add(new Entry<>(key, val)); // Nueva clave, añadir al final de la lista
        N++; // Incrementar el número de pares

        // Opcional: Redimensionar si la tabla se vuelve demasiado densa
        // if (N >= 10 * M) resize(2 * M); // Ejemplo: redimensionar si la carga es muy alta
    }

    /**
     * Retorna el valor asociado a la clave.
     * @param key La clave a buscar.
     * @return El valor asociado a la clave, o null si la clave no se encuentra.
     */
    public Value get(Key key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        int i = hash(key);
        for (Entry<Key, Value> entry : st[i]) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
        }
        return null; // Clave no encontrada
    }

    /**
     * Verifica si la tabla hash contiene la clave especificada.
     * @param key La clave a buscar.
     * @return true si la clave existe, false en caso contrario.
     */
    public boolean contains(Key key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        return get(key) != null;
    }

    /**
     * Elimina la clave y su valor asociado de la tabla hash.
     * @param key La clave a eliminar.
     */
    public void delete(Key key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        int i = hash(key);
        
        Entry<Key, Value> toRemove = null;
        for (Entry<Key, Value> entry : st[i]) {
            if (entry.key.equals(key)) {
                toRemove = entry;
                break;
            }
        }
        if (toRemove != null) {
            st[i].remove(toRemove);
            N--; // Decrementar el número de pares
            // Opcional: Redimensionar si la tabla se vuelve demasiado escasa
            // if (M > DEFAULT_CAPACITY && N <= 2 * M) resize(M / 2); // Ejemplo: redimensionar si la carga es muy baja
        }
    }

    // Opcional: Implementación de redimensionamiento
    /*
    private void resize(int capacity) {
        HashST<Key, Value> temp = new HashST<>(capacity);
        for (int i = 0; i < M; i++) {
            for (Entry<Key, Value> entry : st[i]) {
                temp.put(entry.key, entry.value);
            }
        }
        this.M = temp.M;
        this.N = temp.N;
        this.st = temp.st;
    }
    */
}
