/* ============================================================
   PLATAFORMA DE MONITOREO AMBIENTAL URBANO
   GeneradorDatos - SEMANA 3

   Genera lecturas sinteticas en memoria para los experimentos
   de eficiencia. No necesitamos un CSV de un millon de filas:
   necesitamos un millon de lecturas durante unos segundos.
   ============================================================ */

import java.util.Random;

public class GeneradorDatos {

    private static final int NUM_ESTACIONES = 9;

    /** Semilla fija: los experimentos deben poder repetirse. */
    private static final long SEMILLA = 20262L;

    /**
     * Genera n lecturas en orden cronologico ascendente.
     *
     * El timestamp se construye con la posicion del arreglo, por lo
     * que el arreglo queda ORDENADO por timestamp. Esa es justamente
     * la precondicion que necesita la busqueda binaria.
     */
    public static LecturaSensor[] generar(int n) {

        Random azar = new Random(SEMILLA);
        LecturaSensor[] datos = new LecturaSensor[n];

        for (int i = 0; i < n; i++) {

            String id = String.format(
                    "EST-%03d",
                    (i % NUM_ESTACIONES) + 1
            );

            String timestamp = String.format(
                    "%010d",
                    i
            );

            double temperatura =
                    11 + azar.nextDouble() * 18;

            double humedad =
                    55 + azar.nextDouble() * 35;

            // PM2.5 aleatorio: NO queda ordenado. Esto es intencional.
            double pm25 =
                    5 + azar.nextDouble() * 55;

            datos[i] = new LecturaSensor(
                    id,
                    timestamp,
                    redondear(temperatura),
                    redondear(humedad),
                    redondear(pm25)
            );
        }

        return datos;
    }

    private static double redondear(double valor) {
        return Math.round(valor * 10.0) / 10.0;
    }

    /**
     * Devuelve un timestamp que SI existe en el arreglo generado.
     */
    public static String timestampEnPosicion(int posicion) {
        return String.format("%010d", posicion);
    }

    /**
     * Devuelve un timestamp que NO existe en los arreglos generados,
     * porque es mayor que cualquier posicion usada.
     */
    public static String timestampInexistente() {
        return "9999999999";
    }
}
