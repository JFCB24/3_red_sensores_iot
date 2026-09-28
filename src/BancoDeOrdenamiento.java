/* ============================================================
   PLATAFORMA DE MONITOREO AMBIENTAL URBANO
   BancoDeOrdenamiento - SEMANA 4

   Cinco experimentos para comparar el comportamiento de los
   seis algoritmos de ordenamiento bajo diferentes condiciones.
   ============================================================ */

import java.util.Random;

public class BancoDeOrdenamiento {

    // ============ UTILIDADES ============

    /**
     * Copia el arreglo para que cada algoritmo empiece en igualdad
     * de condiciones.
     */
    private static LecturaSensor[] copiar(LecturaSensor[] original) {
        LecturaSensor[] copia = new LecturaSensor[original.length];
        System.arraycopy(original, 0, copia, 0, original.length);
        return copia;
    }

    /**
     * Desordena un arreglo con una semilla fija, para que el
     * experimento sea repetible.
     */
    private static LecturaSensor[] desordenar(LecturaSensor[] original) {
        LecturaSensor[] copia = copiar(original);
        Random azar = new Random(777L);
        for (int i = copia.length - 1; i > 0; i--) {
            int j = azar.nextInt(i + 1);
            LecturaSensor t = copia[i];
            copia[i] = copia[j];
            copia[j] = t;
        }
        return copia;
    }

    /**
     * Imprime un reporte del algoritmo con sus metricas.
     */
    private static void reportar(String nombre, long milis) {
        System.out.printf(
                "%-14s comparaciones: %,14d   intercambios: %,14d   %6d ms%n",
                nombre,
                Ordenador.getComparaciones(),
                Ordenador.getIntercambios(),
                milis
        );
    }

    // ============ EXPERIMENTO 1 ============

    /**
     * Compara los tres algoritmos simples sobre 10.000 lecturas
     * DESORDENADAS.
     *
     * Objetivo: observar el comportamiento basico con datos
     * completamente desordenados.
     */
    public void experimentoUno() {
        System.out.println("=== EXPERIMENTO 1: ALGORITMOS SIMPLES, 10.000 LECTURAS DESORDENADAS ===");
        LecturaSensor[] base = desordenar(GeneradorDatos.generar(10_000));

        LecturaSensor[] a = copiar(base);
        long t = System.currentTimeMillis();
        Ordenador.burbuja(a);
        reportar("Burbuja", System.currentTimeMillis() - t);

        LecturaSensor[] b = copiar(base);
        t = System.currentTimeMillis();
        Ordenador.seleccion(b);
        reportar("Seleccion", System.currentTimeMillis() - t);

        LecturaSensor[] c = copiar(base);
        t = System.currentTimeMillis();
        Ordenador.insercion(c);
        reportar("Insercion", System.currentTimeMillis() - t);

        System.out.println();
    }

    // ============ EXPERIMENTO 2 ============

    /**
     * Prueba los mismos tres algoritmos, pero con datos que YA VIENEN
     * ORDENADOS por timestamp (como llegan de la red de sensores).
     *
     * Objetivo: observar como cada algoritmo se comporta cuando los
     * datos ya estan parcialmente ordenados. Burbuja con bandera
     * debe terminar muy rapido.
     */
    public void experimentoDos() {
        System.out.println("=== EXPERIMENTO 2: LOS MISMOS TRES, PERO CON DATOS YA ORDENADOS ===");
        System.out.println("(asi es como llegan de la red de sensores: en orden cronologico)");
        LecturaSensor[] base = GeneradorDatos.generar(10_000);

        LecturaSensor[] a = copiar(base);
        long t = System.currentTimeMillis();
        Ordenador.burbuja(a);
        reportar("Burbuja", System.currentTimeMillis() - t);

        LecturaSensor[] b = copiar(base);
        t = System.currentTimeMillis();
        Ordenador.seleccion(b);
        reportar("Seleccion", System.currentTimeMillis() - t);

        LecturaSensor[] c = copiar(base);
        t = System.currentTimeMillis();
        Ordenador.insercion(c);
        reportar("Insercion", System.currentTimeMillis() - t);

        System.out.println();
    }

    // ============ EXPERIMENTO 3 ============

    /**
     * Compara insercion (simple) contra mergeSort y heapSort (avanzados)
     * a escala creciente: 1.000, 10.000 y 100.000 lecturas.
     *
     * Objetivo: observar como crecen las comparaciones cuando crece n.
     * Calcular los factores de crecimiento permite entender el O(n log n)
     * versus O(n^2).
     */
    public void experimentoTres() {
        System.out.println("=== EXPERIMENTO 3: SIMPLES CONTRA AVANZADOS A ESCALA CRECIENTE ===");
        int[] tamanos = {1_000, 10_000, 100_000};

        for (int n : tamanos) {
            System.out.println("-- " + String.format("%,d", n) + " lecturas desordenadas --");
            LecturaSensor[] base = desordenar(GeneradorDatos.generar(n));

            LecturaSensor[] a = copiar(base);
            long t = System.currentTimeMillis();
            Ordenador.insercion(a);
            reportar("Insercion", System.currentTimeMillis() - t);

            LecturaSensor[] b = copiar(base);
            t = System.currentTimeMillis();
            Ordenador.mergeSort(b);
            reportar("MergeSort", System.currentTimeMillis() - t);

            LecturaSensor[] c = copiar(base);
            t = System.currentTimeMillis();
            Ordenador.heapSort(c);
            reportar("HeapSort", System.currentTimeMillis() - t);

            System.out.println();
        }
    }

    // ============ EXPERIMENTO 4 ============

    /**
     * Prueba QuickSort con pivote fijo en el primer elemento.
     *
     * Caso A: 50.000 lecturas DESORDENADAS -> debe funcionar bien
     * Caso B: 50.000 lecturas EN ORDEN CRONOLOGICO -> puede causar
     *         StackOverflowError porque las particiones quedan muy
     *         desbalanceadas
     *
     * Objetivo: demostrar el problema de pivote fijo y la importancia
     * de seleccionar el pivote inteligentemente.
     */
    public void experimentoCuatro() {
        System.out.println("=== EXPERIMENTO 4: QUICKSORT CON PIVOTE = PRIMER ELEMENTO ===");

        System.out.println("-- Caso A: 50.000 lecturas DESORDENADAS --");
        LecturaSensor[] revueltas = desordenar(GeneradorDatos.generar(50_000));
        long t = System.currentTimeMillis();
        Ordenador.quickSortPivotePrimero(revueltas);
        reportar("QuickSort", System.currentTimeMillis() - t);

        System.out.println();
        System.out.println("-- Caso B: 50.000 lecturas EN ORDEN CRONOLOGICO (como llegan de la red) --");
        LecturaSensor[] enOrden = GeneradorDatos.generar(50_000);
        try {
            t = System.currentTimeMillis();
            Ordenador.quickSortPivotePrimero(enOrden);
            reportar("QuickSort", System.currentTimeMillis() - t);
        } catch (StackOverflowError e) {
            System.out.println("QuickSort      -> StackOverflowError: el programa se quedo sin pila.");
            System.out.println("                  Comparaciones alcanzadas antes de morir: "
                    + String.format("%,d", Ordenador.getComparaciones()));
        }

        System.out.println();
    }

    // ============ EXPERIMENTO 5 ============

    /**
     * Demuestra el efecto colateral del ordenamiento.
     *
     * Paso 1: datos ordenados por timestamp -> busqueda binaria funciona
     * Paso 2: se ordenan por PM2.5
     * Paso 3: timestamp deja de estar ordenado -> busqueda binaria falla
     *         pero busqueda lineal sigue encontrando el dato
     *
     * Objetivo: aprender que un cambio en un modulo puede romper las
     * precondiciones de otro, sin que ninguno este "mal".
     */
    public void experimentoCinco() {
        System.out.println("=== EXPERIMENTO 5: EL RANKING Y LA CONSULTA ===");

        LecturaSensor[] datos = GeneradorDatos.generar(100_000);
        String objetivo = GeneradorDatos.timestampEnPosicion(73_412);

        System.out.println("Paso 1. Los datos llegan de la red en orden cronologico.");
        System.out.println("        Ordenado por timestamp: "
                + Ordenador.estaOrdenadoPorTimestamp(datos));
        int pos = BuscadorLecturas.busquedaBinariaPorTimestamp(datos, objetivo);
        System.out.println("        Consulta binaria por timestamp -> posicion: " + pos
                + "  (comparaciones: " + BuscadorLecturas.getComparaciones() + ")");

        System.out.println();
        System.out.println("Paso 2. El area de comunicaciones pide el ranking de estaciones");
        System.out.println("        mas contaminadas. Ordenamos por PM2.5.");
        Ordenador.ordenarPorPm25(datos);
        System.out.println("        Ranking listo. PM2.5 mas bajo: " + datos[0].getPm25()
                + " | mas alto: " + datos[datos.length - 1].getPm25());

        System.out.println();
        System.out.println("Paso 3. Otro usuario vuelve a consultar la misma lectura de siempre.");
        System.out.println("        Ordenado por timestamp: "
                + Ordenador.estaOrdenadoPorTimestamp(datos));
        pos = BuscadorLecturas.busquedaBinariaPorTimestamp(datos, objetivo);
        System.out.println("        Consulta binaria por timestamp -> posicion: " + pos
                + "  (comparaciones: " + BuscadorLecturas.getComparaciones() + ")");

        System.out.println();
        System.out.println("        Verificacion con busqueda lineal -> posicion: "
                + BuscadorLecturas.busquedaLinealPorTimestamp(datos, objetivo));
        System.out.println();
    }
}
