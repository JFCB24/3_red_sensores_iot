/* ============================================================
   PLATAFORMA DE MONITOREO AMBIENTAL URBANO
   BancoDePruebas - SEMANA 3

   Contiene los experimentos de eficiencia de la Semana 3.

   ESTA CLASE NO TIENE main().
   El unico main del proyecto sigue estando en IngestaSensores.
   ============================================================ */

public class BancoDePruebas {

    private static final int[] TAMANOS = {
        1_000,
        100_000,
        1_000_000
    };

    /**
     * Experimento 1: busqueda lineal en el PEOR CASO.
     *
     * Se busca siempre la ultima lectura del arreglo, que es la
     * posicion mas cara de alcanzar para un recorrido secuencial.
     */
    public static void experimentoUno() {

        System.out.println(
                "=== EXPERIMENTO 1: BUSQUEDA LINEAL (PEOR CASO) ==="
        );

        System.out.printf(
                "%12s %16s %14s%n",
                "lecturas",
                "comparaciones",
                "tiempo (ms)"
        );

        for (int n : TAMANOS) {

            LecturaSensor[] datos =
                    GeneradorDatos.generar(n);

            String objetivo =
                    GeneradorDatos.timestampEnPosicion(n - 1);

            long inicio = System.nanoTime();

            int posicion =
                    BuscadorLecturas
                            .busquedaLinealPorTimestamp(
                                    datos,
                                    objetivo
                            );

            long fin = System.nanoTime();

            System.out.printf(
                    "%12d %16d %14.3f%n",
                    n,
                    BuscadorLecturas.getComparaciones(),
                    (fin - inicio) / 1_000_000.0
            );

            if (posicion < 0) {
                System.out.println(
                        "ADVERTENCIA: no encontro una lectura existente."
                );
            }
        }

        System.out.println();
    }

    /**
     * Experimento 2: compara busqueda lineal y busqueda binaria
     * sobre el mismo arreglo y el mismo objetivo.
     */
    public static void experimentoDos() {

        System.out.println(
                "=== EXPERIMENTO 2: LINEAL vs BINARIA ==="
        );

        System.out.printf(
                "%12s %14s %14s %12s%n",
                "lecturas",
                "lineal",
                "binaria",
                "relacion"
        );

        for (int n : TAMANOS) {

            LecturaSensor[] datos =
                    GeneradorDatos.generar(n);

            String objetivo =
                    GeneradorDatos.timestampEnPosicion(n - 1);

            BuscadorLecturas.busquedaLinealPorTimestamp(
                    datos,
                    objetivo
            );

            int lineal =
                    BuscadorLecturas.getComparaciones();

            BuscadorLecturas.busquedaBinariaPorTimestamp(
                    datos,
                    objetivo
            );

            int binaria =
                    BuscadorLecturas.getComparaciones();

            System.out.printf(
                    "%12d %14d %14d %12.1f%n",
                    n,
                    lineal,
                    binaria,
                    (double) lineal / binaria
            );
        }

        System.out.println();
    }

    /**
     * Experimento 3: buscar un timestamp que NO existe.
     *
     * Para la busqueda lineal este es el caso mas caro posible:
     * hay que recorrer todo el arreglo para poder afirmar que el
     * dato no esta.
     */
    public static void experimentoTres() {

        System.out.println(
                "=== EXPERIMENTO 3: DATO INEXISTENTE ==="
        );

        LecturaSensor[] datos =
                GeneradorDatos.generar(100_000);

        String objetivo =
                GeneradorDatos.timestampInexistente();

        BuscadorLecturas.busquedaLinealPorTimestamp(
                datos,
                objetivo
        );

        int lineal =
                BuscadorLecturas.getComparaciones();

        BuscadorLecturas.busquedaBinariaPorTimestamp(
                datos,
                objetivo
        );

        int binaria =
                BuscadorLecturas.getComparaciones();

        System.out.println(
                "Lecturas: 100000   timestamp buscado: 9999999999"
        );

        System.out.println(
                "Lineal  -> comparaciones: " + lineal
        );

        System.out.println(
                "Binaria -> comparaciones: " + binaria
        );

        System.out.println();
    }

    /**
     * Experimento 4: que ocurre cuando la busqueda binaria se aplica
     * sobre un campo que NO esta ordenado.
     *
     * Se buscan 20 valores de PM2.5 que con certeza existen, porque
     * se toman del propio arreglo. La busqueda lineal deberia
     * encontrarlos todos. La binaria, no.
     */
    public static void experimentoCuatro() {

        System.out.println(
                "=== EXPERIMENTO 4: BINARIA POR PM2.5 (PRECONDICION) ==="
        );

        LecturaSensor[] datos =
                GeneradorDatos.generar(10_000);

        int aciertosLineal = 0;
        int aciertosBinaria = 0;

        for (int i = 0; i < 20; i++) {

            double valor =
                    datos[i * 137].getPm25();

            int posLineal = -1;

            for (int j = 0; j < datos.length; j++) {

                if (datos[j].getPm25() == valor) {
                    posLineal = j;
                    break;
                }
            }

            int posBinaria =
                    BuscadorLecturas
                            .busquedaBinariaPorPm25(
                                    datos,
                                    valor
                            );

            if (posLineal >= 0) {
                aciertosLineal++;
            }

            if (posBinaria >= 0) {
                aciertosBinaria++;
            }
        }

        System.out.println(
                "Valores buscados que SI existen:   20"
        );

        System.out.println(
                "Encontrados por busqueda lineal:   "
                        + aciertosLineal
        );

        System.out.println(
                "Encontrados por busqueda binaria:  "
                        + aciertosBinaria
        );

        System.out.println(
                "Conclusion: algoritmo correcto + precondicion falsa"
                        + " = resultado incorrecto."
        );

        System.out.println();
    }

    /**
     * Demuestra por que un String se compara con equals() y no con ==.
     *
     * Se construye a proposito un String con el mismo contenido pero
     * distinta referencia (new String(...)), que es exactamente lo que
     * ocurre cuando el texto llega leido desde un archivo o una red.
     */
    public static void demostracionComparacionStrings() {

        System.out.println(
                "=== COMPARACION DE STRING: == vs equals() ==="
        );

        LecturaSensor[] datos = GeneradorDatos.generar(1_000);

        // Mismo contenido, referencia diferente.
        String buscado = new String("EST-005");

        int conIgualdadReferencia =
                BuscadorLecturas.buscarPorEstacionDefectuoso(
                        datos,
                        buscado
                );

        int conEquals =
                BuscadorLecturas.buscarPorEstacion(
                        datos,
                        buscado
                );

        System.out.println(
                "Estacion buscada: \"EST-005\" (existe en los datos)"
        );

        System.out.println(
                "Con ==      -> posicion: " + conIgualdadReferencia
        );

        System.out.println(
                "Con equals()-> posicion: " + conEquals
        );

        System.out.println();
    }
}
