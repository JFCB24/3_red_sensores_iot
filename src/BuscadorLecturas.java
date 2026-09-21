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
