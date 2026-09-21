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
}
