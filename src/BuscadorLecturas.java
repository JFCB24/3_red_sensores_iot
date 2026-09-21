/* ============================================================
   PLATAFORMA DE MONITOREO AMBIENTAL URBANO
   BuscadorLecturas - SEMANA 3

   Contiene los algoritmos de busqueda utilizados por el
   proyecto. No es una aplicacion aparte: es una capacidad
   nueva de la misma plataforma.
   ============================================================ */

public class BuscadorLecturas {

    /**
     * Cantidad de comparaciones realizadas por la ultima busqueda.
     *
     * Medir comparaciones permite estudiar el costo del algoritmo
     * sin depender del reloj de la maquina.
     */
    private static int comparaciones = 0;

    public static int getComparaciones() {
        return comparaciones;
    }

    /**
     * Busqueda lineal por timestamp.
     *
     * No necesita que los datos esten ordenados.
     * Costo: O(n) en el peor caso.
     *
     * @param datos arreglo de lecturas
     * @param timestamp timestamp que se desea encontrar
     * @return posicion de la lectura o -1 si no existe
     */
    public static int busquedaLinealPorTimestamp(
            LecturaSensor[] datos,
            String timestamp) {

        comparaciones = 0;

        for (int i = 0; i < datos.length; i++) {
            comparaciones++;

            // .equals() compara CONTENIDO. El operador == compararia
            // referencias, que no es lo que necesitamos con String.
            if (datos[i].getTimestamp().equals(timestamp)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Busca la primera lectura de una estacion.
     *
     * ESTA VERSION CONTIENE UN ERROR INTENCIONAL.
     *
     * Usa == sobre String, que compara REFERENCIAS y no contenido.
     * Se conserva para poder demostrar el defecto antes de corregirlo.
     *
     * @return posicion de la primera coincidencia o -1
     */
    public static int buscarPorEstacionDefectuoso(
            LecturaSensor[] datos,
            String idSensor) {

        comparaciones = 0;

        for (int i = 0; i < datos.length; i++) {

            comparaciones++;

            if (datos[i].getIdSensor() == idSensor) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Busqueda binaria por timestamp.
     *
     * PRIMERA VERSION: CONTIENE UN CICLO INFINITO.
     *
     * Actualiza el limite con "inicio = medio" en lugar de
     * "inicio = medio + 1". Cuando el intervalo se reduce a dos
     * elementos, medio vuelve a valer inicio en cada vuelta, el
     * intervalo deja de achicarse y el while no termina nunca.
     *
     * NO se invoca desde ningun experimento. Queda registrada para
     * poder razonar sobre el defecto antes de corregirlo.
     */
    public static int busquedaBinariaPorTimestampDefectuosa(
            LecturaSensor[] datos,
            String timestamp) {

        comparaciones = 0;

        int inicio = 0;
        int fin = datos.length - 1;

        while (inicio <= fin) {

            int medio = (inicio + fin) / 2;

            comparaciones++;

            int comparacion =
                    datos[medio]
                            .getTimestamp()
                            .compareTo(timestamp);

            if (comparacion == 0) {
                return medio;
            }

            if (comparacion < 0) {
                inicio = medio;   // DEFECTO: el intervalo no avanza
            } else {
                fin = medio;      // DEFECTO: el intervalo no avanza
            }
        }

        return -1;
    }

    /**
     * Busqueda binaria por timestamp. VERSION CORREGIDA.
     *
     * PRECONDICION: las lecturas deben estar ordenadas
     * ascendentemente por timestamp. Si no se cumple, el algoritmo
     * sigue terminando, pero su respuesta deja de ser confiable.
     *
     * Costo: O(log2 n).
     *
     * Cada comparacion descarta la mitad del espacio de busqueda.
     * Los limites usan medio + 1 y medio - 1 porque la posicion
     * medio YA fue comparada: no hay que volver a revisarla, y asi
     * el intervalo siempre se reduce.
     *
     * @return posicion de la lectura o -1 si no existe
     */
    public static int busquedaBinariaPorTimestamp(
            LecturaSensor[] datos,
            String timestamp) {

        comparaciones = 0;

        int inicio = 0;
        int fin = datos.length - 1;

        while (inicio <= fin) {

            int medio = (inicio + fin) / 2;

            comparaciones++;

            int comparacion =
                    datos[medio]
                            .getTimestamp()
                            .compareTo(timestamp);

            if (comparacion == 0) {
                return medio;
            }

            if (comparacion < 0) {
                inicio = medio + 1;   // descarta la mitad izquierda
            } else {
                fin = medio - 1;      // descarta la mitad derecha
            }
        }

        return -1;
    }

    /**
     * Busca la primera lectura de una estacion. VERSION CORREGIDA.
     *
     * equals() compara el CONTENIDO de los String, que es lo que
     * realmente necesitamos. Dos objetos String distintos pueden
     * contener el mismo texto.
     *
     * @return posicion de la primera coincidencia o -1
     */
    public static int buscarPorEstacion(
            LecturaSensor[] datos,
            String idSensor) {

        comparaciones = 0;

        for (int i = 0; i < datos.length; i++) {

            comparaciones++;

            if (datos[i].getIdSensor().equals(idSensor)) {
                return i;
            }
        }

        return -1;
    }
}
