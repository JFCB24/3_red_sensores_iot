# Decisiones de diseño

## 1. Punto de entrada

El proyecto mantiene un único punto de entrada:

```text
IngestaSensores.main()
```

No se crean aplicaciones independientes por semana.

`BancoDePruebas` es una clase auxiliar y no contiene `main`.

## 2. Búsqueda por timestamp

Se utilizan dos estrategias:

- Búsqueda lineal: no requiere ordenamiento.
- Búsqueda binaria: requiere que el arreglo esté ordenado por timestamp.

Los datos sintéticos de `GeneradorDatos` se generan en orden cronológico, por lo que la búsqueda binaria por timestamp cumple su precondición.

### Evidencia medida

Buscando la última lectura (peor caso) sobre datos generados:

| Tamaño | Lineal | Binaria |
|---:|---:|---:|
| 1.000 | 1.000 | 10 |
| 100.000 | 100.000 | 17 |
| 1.000.000 | 1.000.000 | 20 |

Multiplicar los datos por mil agrega diez comparaciones a la binaria. Esa es la
razón concreta de la decisión, no una preferencia de estilo.

### Corrección registrada: el ciclo infinito

La primera versión actualizaba los límites con `inicio = medio`. Cuando el
intervalo se reduce a dos posiciones, la división entera hace que `medio` vuelva
a valer `inicio`, el intervalo deja de achicarse y el `while` no termina.

La versión vigente usa `medio + 1` y `medio - 1`, porque la posición `medio` ya
fue comparada en esa vuelta. Así el intervalo se reduce siempre.

La traza completa está en `bitacoras/traza_busqueda_binaria.md`.

## 3. Búsqueda por PM2.5

No se asume que los datos estén ordenados por PM2.5.

Por tanto, la búsqueda binaria por PM2.5 se conserva como experimento para demostrar el efecto de una precondición incumplida.

### Evidencia medida

Sobre 20 valores de PM2.5 que con certeza existen en el arreglo (se tomaron del
propio arreglo), la búsqueda lineal encontró 20 y la binaria encontró 0.

El algoritmo está bien implementado: usa el mismo esquema de intervalos que la
versión por timestamp, que sí funciona. Lo que falla es la condición del dato.

```text
algoritmo correcto + precondición falsa = resultado incorrecto
```

### Decisión

Las consultas por PM2.5 se resuelven con búsqueda lineal mientras el arreglo no
esté ordenado por ese campo.

## 4. Comparación de String

Los identificadores de estación se comparan mediante:

```java
equals()
```

y no mediante:

```java
==
```

porque se necesita comparar contenido.

### Evidencia medida

Buscando `new String("EST-005")`, que existe en la posición 4:

| Comparación | Resultado |
|---|---:|
| `==` | -1 |
| `equals()` | 4 |

`==` pregunta si dos referencias apuntan al mismo objeto. Los identificadores
que llegan leídos desde el CSV no son literales internados, así que `==` falla
en silencio: no lanza excepción, simplemente responde que el dato no existe.

El método `buscarPorEstacionDefectuoso` se conserva únicamente para poder
ejecutar esa demostración; el código de producción usa `buscarPorEstacion`.

## 5. Medición

La comparación principal entre algoritmos utiliza el número de comparaciones.

El tiempo en milisegundos se conserva como evidencia experimental, pero no es la única medida utilizada.

### Por qué no se decide solo con el reloj

Con 1.000 lecturas la búsqueda binaria resultó **más lenta** en tiempo de reloj
(0,0287 ms) que la lineal (0,0216 ms), pese a hacer 100 veces menos
comparaciones. A esa escala domina el calentamiento de la JVM.

Con 1.000.000 de lecturas la relación se invierte con claridad: 11,9855 ms
contra 0,0513 ms.

El tiempo depende de la máquina, del sistema operativo, de la caché y de la
carga del equipo. El número de comparaciones, no.

## 6. Evolución del proyecto

La Semana 3 agrega una nueva capacidad a la misma plataforma:

```text
Sensores
   ↓
Ingesta
   ↓
Repositorio
   ↓
Búsqueda
   ↓
Medición de eficiencia
```

La Semana 4 podrá extender esta misma arquitectura para estudiar ordenamiento.

## 7. Pregunta pendiente para la Semana 4

¿Conviene ordenar los datos antes de realizar las búsquedas?

La binaria es enormemente más barata, pero exige un arreglo ordenado y ordenar
también cuesta. La respuesta depende de cuántas búsquedas se hagan por cada
ordenamiento, y de si los datos se modifican después de ordenarlos.

Esta pregunta se retoma en la Semana 4.

---

# Decisiones de diseño — Semana 4

## 8. Selección de algoritmos de ordenamiento

Se implementan seis algoritmos:

**Simples:** Burbuja (con corte temprano), Selección, Inserción.
**Avanzados:** MergeSort, HeapSort, QuickSort.

La implementación mide:
- Número de comparaciones
- Número de intercambios
- Tiempo de ejecución

### Evidencia medida

Con 10.000 datos desordenados:

| Algoritmo | Comparaciones | Intercambios | Tiempo (ms) |
|---|---:|---:|---:|
| Burbuja | 49.991.172 | 24.955.330 | 140 |
| Selección | 49.995.000 | 9.984 | 51 |
| Inserción | 25.055.854 | 0 | 46 |
| MergeSort | 240.980 | 0 | 3 |
| HeapSort | 470.614 | 124.118 | 3 |
| QuickSort | 1.534.501 | 0 | 18 |

La conclusión principal: **comparar y mover no tienen el mismo costo**.

Selección realiza prácticamente las mismas comparaciones que Burbuja, pero
mucho menos intercambios. Por eso puede ser más rápido a pesar de hacer
comparaciones similares.

## 9. Inserción y datos parcialmente ordenados

Inserción tiene un comportamiento especial cuando los datos ya están parcialmente
ordenados.

Con datos ordenados por timestamp (como llegan de la red):

| Algoritmo | Comparaciones | Tiempo (ms) |
|---|---:|---:|
| Burbuja | 49.958.144 | 101 |
| Selección | 49.995.000 | 53 |
| Inserción | 24.832.832 | 44 |

Inserción mantiene su desempeño porque el arreglo no está ordenado por PM2.5
(el criterio de comparación). Sin embargo, en escenarios donde **los datos
llegan casi ordenados**, Inserción puede alcanzar O(n), lo que la hace
competitiva incluso contra algoritmos O(n log n).

### Decisión

Se mantiene Inserción como candidato viable para la plataforma de sensores,
ya que las lecturas lleguen cronológicamente y pueden estar parcialmente
ordenadas por otras variables.

## 10. Pivote de QuickSort

QuickSort con pivote fijo en el primer elemento es vulnerable con datos
ordenados cronológicamente.

### Evidencia medida

Con 50.000 datos:

| Caso | Datos | Comparaciones | Tiempo (ms) |
|---|---|---:|---:|
| A | Desordenados | 1.534.501 | 18 |
| B | Cronológicos | 1.546.526 | 5 |

En nuestro experimento, el caso B no causó StackOverflowError. Sin embargo,
la teoría indica que con particiones muy desbalanceadas, la profundidad de
recursión puede exceder la pila disponible.

### Decisión

Para evitar el riesgo de StackOverflowError en producción, se proporciona una
alternativa: `quickSortPivoteAleatorio`, que elige el pivote al azar. Esto
garantiza un comportamiento promedio O(n log n) incluso con datos ordenados.

Para la plataforma de sensores, se recomienda usar QuickSort con pivote
aleatorio o MergeSort (que tiene garantía O(n log n) incluso en el peor caso).

## 11. Efecto colateral del ordenamiento

Ordenar por un criterio puede destruir el orden por otro criterio.

### Escenario en el Experimento 5

1. Datos llegan ordenados por timestamp → búsqueda binaria por timestamp funciona.
2. Se solicita un ranking por PM2.5 → se ordena el arreglo.
3. Timestamp ya no está ordenado → búsqueda binaria por timestamp devuelve `-1`.
4. Búsqueda lineal aún encuentra el dato, pero es mucho más lenta.

### Evidencia medida

Buscando timestamp `000073412`:

| Fase | Criterio | Ordenado | Resultado | Comparaciones |
|---|---|---|---:|---:|
| 1 | Por timestamp | Sí | Encontrado (pos 73412) | 16 |
| 3 | Por PM2.5 | No | No encontrado | 16 |
| 3 (lineal) | Lineal | N/A | Encontrado (pos 87705) | 87706 |

### Decisión

Cuando se necesiten ordenamientos múltiples, la solución depende del caso de uso:

**A. Trabajar con copias:** Mantener una copia original ordenada por timestamp
y una copia ordenada por PM2.5. Costo: memoria adicional.

**B. Restaurar el orden:** Ordenar, generar el ranking y luego restaurar el
orden original. Costo: tiempo adicional de ordenamiento.

**C. Mantener índices separados:** Crear estructuras que permitan acceder
a los datos según diferentes criterios sin modificar el arreglo original.
Costo: complejidad adicional.

Para esta plataforma, se elige la opción **A (copias)** por simplicidad:
la memoria es abundante comparada con el costo de re-ordenar constantemente.

---
