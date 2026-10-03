/* ============================================================
   PLATAFORMA DE MONITOREO AMBIENTAL URBANO
   Ordenador - SEMANA 4

   Implementa seis algoritmos de ordenamiento con medicion
   de comparaciones e intercambios. Los contadores son estaticos
   para poder comparar el costo de cada algoritmo.
   ============================================================ */

public class Ordenador {

    private static long comparaciones = 0;
    private static long intercambios = 0;

    // ============ CONTADORES ============

    private static void registrarComparacion() {
        comparaciones++;
    }

    private static void registrarIntercambio() {
        intercambios++;
    }

    public static void reiniciarContadores() {
        comparaciones = 0;
        intercambios = 0;
    }

    public static long getComparaciones() {
        return comparaciones;
    }

    public static long getIntercambios() {
        return intercambios;
    }

    // ============ UTILIDADES ============

    private static void intercambiar(LecturaSensor[] datos, int i, int j) {
        LecturaSensor temporal = datos[i];
        datos[i] = datos[j];
        datos[j] = temporal;
        registrarIntercambio();
    }

    private static boolean mayorPm25(LecturaSensor a, LecturaSensor b) {
        registrarComparacion();
        return a.getTimestamp().compareTo(b.getTimestamp()) > 0;
    }

    private static boolean menorPm25(LecturaSensor a, LecturaSensor b) {
        registrarComparacion();
        return a.getTimestamp().compareTo(b.getTimestamp()) < 0;
    }

    private static boolean menorOIgualPm25(LecturaSensor a, LecturaSensor b) {
        registrarComparacion();
        return a.getTimestamp().compareTo(b.getTimestamp()) <= 0;
    }

    // ============ ALGORITMOS SIMPLES ============

    /**
     * Bubble Sort con corte temprano.
     *
     * TODO 1: Implementar bandera de corte temprano para detectar
     * cuando el arreglo ya esta ordenado. Esto mejora el comportamiento
     * en datos que ya estan parcialmente ordenados.
     */
    public static void burbuja(LecturaSensor[] datos) {
        reiniciarContadores();
        int n = datos.length;

        for (int pasada = 0; pasada < n - 1; pasada++) {
            boolean huboIntercambio = false;

            for (int j = 0; j < n - 1 - pasada; j++) {
                if (mayorPm25(datos[j], datos[j + 1])) {
                    intercambiar(datos, j, j + 1);
                    huboIntercambio = true;
                }
            }

            if (!huboIntercambio) {
                break;
            }
        }
    }

    /**
     * Selection Sort.
     *
     * Busca el elemento minimo en cada pasada y lo coloca en su
     * posicion correcta. Realiza muchas comparaciones pero pocos
     * intercambios.
     */
    public static void seleccion(LecturaSensor[] datos) {
        reiniciarContadores();
        int n = datos.length;

        for (int i = 0; i < n - 1; i++) {
            int posicionMenor = i;

            for (int j = i + 1; j < n; j++) {
                if (menorPm25(datos[j], datos[posicionMenor])) {
                    posicionMenor = j;
                }
            }

            if (posicionMenor != i) {
                intercambiar(datos, i, posicionMenor);
            }
        }
    }

    /**
     * Insertion Sort.
     *
     * Mantiene una parte del arreglo ordenada e inserta cada nuevo
     * elemento en su posicion correcta. Excelente desempeno si los
     * datos ya estan parcialmente ordenados.
     */
    public static void insercion(LecturaSensor[] datos) {
        reiniciarContadores();

        for (int i = 1; i < datos.length; i++) {
            LecturaSensor actual = datos[i];
            int j = i - 1;

            while (j >= 0 && menorOIgualPm25(actual, datos[j])) {
                datos[j + 1] = datos[j];
                j--;
            }

            datos[j + 1] = actual;
        }
    }

    // ============ ALGORITMOS AVANZADOS ============

    /**
     * Merge Sort.
     *
     * Divide y vencerás: divide el arreglo por la mitad, ordena
     * recursivamente cada mitad y fusiona los resultados.
     * Complejidad garantizada O(n log n).
     */
    public static void mergeSort(LecturaSensor[] datos) {
        reiniciarContadores();
        mergeSortRecursivo(datos, 0, datos.length - 1);
    }

    private static void mergeSortRecursivo(
            LecturaSensor[] datos,
            int izq,
            int der) {

        if (izq >= der) {
            return;
        }

        int medio = (izq + der) / 2;

        mergeSortRecursivo(datos, izq, medio);
        mergeSortRecursivo(datos, medio + 1, der);
        fusionar(datos, izq, medio, der);
    }

    private static void fusionar(
            LecturaSensor[] datos,
            int izq,
            int medio,
            int der) {

        LecturaSensor[] temporal = new LecturaSensor[der - izq + 1];

        int i = izq;
        int j = medio + 1;
        int k = 0;

        while (i <= medio && j <= der) {
            registrarComparacion();

            if (menorOIgualPm25(datos[i], datos[j])) {
                temporal[k++] = datos[i++];
            } else {
                temporal[k++] = datos[j++];
            }
        }

        while (i <= medio) {
            temporal[k++] = datos[i++];
        }

        while (j <= der) {
            temporal[k++] = datos[j++];
        }

        for (int x = 0; x < temporal.length; x++) {
            datos[izq + x] = temporal[x];
        }
    }

    /**
     * Heap Sort.
     *
     * Construye un heap maximo y extrae elementos uno a uno.
     * Complejidad garantizada O(n log n).
     */
    public static void heapSort(LecturaSensor[] datos) {
        reiniciarContadores();
        int n = datos.length;

        // Construir el heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(datos, n, i);
        }

        // Extraer elementos del heap
        for (int fin = n - 1; fin > 0; fin--) {
            intercambiar(datos, 0, fin);
            heapify(datos, fin, 0);
        }
    }

    private static void heapify(LecturaSensor[] datos, int n, int raiz) {
        int mayor = raiz;
        int izquierda = 2 * raiz + 1;
        int derecha = 2 * raiz + 2;

        if (izquierda < n) {
            registrarComparacion();
            if (mayorPm25(datos[izquierda], datos[mayor])) {
                mayor = izquierda;
            }
        }

        if (derecha < n) {
            registrarComparacion();
            if (mayorPm25(datos[derecha], datos[mayor])) {
                mayor = derecha;
            }
        }

        if (mayor != raiz) {
            intercambiar(datos, raiz, mayor);
            heapify(datos, n, mayor);
        }
    }

    // ============ QUICKSORT VERSIONES ============

    /**
     * QuickSort con pivote en el primer elemento.
     *
     * Esta version tiene problemas con datos ordenados cronologicamente,
     * ya que el pivote siempre resulta ser el minimo, produciendo
     * particiones muy desbalanceadas.
     *
     * Se utiliza en el Experimento 4 para demostrar el problema.
     */
    public static void quickSortPivotePrimero(LecturaSensor[] datos) {
        reiniciarContadores();
        quickSortPivotePrimeroRecursivo(datos, 0, datos.length - 1);
    }

    private static void quickSortPivotePrimeroRecursivo(
            LecturaSensor[] datos,
            int inicio,
            int fin) {

        if (inicio >= fin) {
            return;
        }

        int posicionPivote = particionarPivotePrimero(datos, inicio, fin);

        quickSortPivotePrimeroRecursivo(datos, inicio, posicionPivote - 1);
        quickSortPivotePrimeroRecursivo(datos, posicionPivote + 1, fin);
    }

    private static int particionarPivotePrimero(
            LecturaSensor[] datos,
            int inicio,
            int fin) {

        LecturaSensor pivote = datos[inicio];
        int izq = inicio;
        int der = fin;

        while (izq < der) {
            registrarComparacion();
            while (izq < der && mayorPm25(pivote, datos[der])) {
                registrarComparacion();
                der--;
            }

            if (izq < der) {
                datos[izq] = datos[der];
                izq++;
            }

            registrarComparacion();
            while (izq < der && menorOIgualPm25(datos[izq], pivote)) {
                registrarComparacion();
                izq++;
            }

            if (izq < der) {
                datos[der] = datos[izq];
                der--;
            }
        }

        datos[izq] = pivote;
        return izq;
    }

    /**
     * QuickSort con pivote aleatorio.
     *
     * TODO 2: Cambia el comportamiento comparado con pivote fijo,
     * mejorando el desempeno en datos ordenados.
     */
    public static void quickSortPivoteAleatorio(LecturaSensor[] datos) {
        reiniciarContadores();
        quickSortPivoteAleatorioRecursivo(datos, 0, datos.length - 1);
    }

    private static void quickSortPivoteAleatorioRecursivo(
            LecturaSensor[] datos,
            int inicio,
            int fin) {

        if (inicio >= fin) {
            return;
        }

        // Elegir pivote aleatorio
        int posicion = inicio + (int) (Math.random() * (fin - inicio + 1));
        intercambiar(datos, inicio, posicion);

        int posicionPivote = particionarPivotePrimero(datos, inicio, fin);

        quickSortPivoteAleatorioRecursivo(datos, inicio, posicionPivote - 1);
        quickSortPivoteAleatorioRecursivo(datos, posicionPivote + 1, fin);
    }

    // ============ METODOS AUXILIARES PARA EXPERIMENTOS ============

    /**
     * Verifica si el arreglo esta ordenado por timestamp de manera
     * ascendente. Se utiliza en el Experimento 5.
     */
    public static boolean estaOrdenadoPorTimestamp(LecturaSensor[] datos) {
        for (int i = 0; i < datos.length - 1; i++) {
            if (datos[i].getTimestamp().compareTo(datos[i + 1].getTimestamp()) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Ordena el arreglo por PM2.5 de manera ascendente.
     * Se utiliza en el Experimento 5 para generar el ranking.
     */
        private static int compararPorPm25(LecturaSensor a, LecturaSensor b) {
        return Double.compare(a.getPm25(), b.getPm25());
    }

    public static void ordenarPorPm25(LecturaSensor[] datos) {
        reiniciarContadores();
        for (int i = 1; i < datos.length; i++) {
            LecturaSensor actual = datos[i];
            int j = i - 1;
            while (j >= 0 && compararPorPm25(datos[j], actual) > 0) {
                datos[j + 1] = datos[j];
                j--;
            }
            datos[j + 1] = actual;
        }
    }
}
