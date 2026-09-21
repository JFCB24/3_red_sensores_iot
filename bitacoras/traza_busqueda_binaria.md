# Bitácora — Semana 3: traza y mediciones de búsqueda binaria

**Espacio académico:** Estructuras de Datos
**Proyecto integrador:** Red de Sensores IoT
**Rama de trabajo:** `feature/semana-3-busqueda`
**Ejecución:** `java -cp bin IngestaSensores` (JDK 26, Windows 11)

---

## 1. Traza previa a la corrección (versión defectuosa)

Antes de tocar el código se razonó sobre el comportamiento del ciclo.

Arreglo de prueba (el valor coincide con la posición):

```text
[0, 1, 2, 3]
```

Objetivo:

```text
3
```

Versión defectuosa, que actualizaba los límites con `inicio = medio`:

| Paso | inicio | fin | medio | valor medio | comparación | acción |
|---:|---:|---:|---:|---:|---|---|
| 1 | 0 | 3 | 1 | 1 | `1 < 3` | `inicio = medio` → `inicio = 1` |
| 2 | 1 | 3 | 2 | 2 | `2 < 3` | `inicio = medio` → `inicio = 2` |
| 3 | 2 | 3 | 2 | 2 | `2 < 3` | `inicio = medio` → `inicio = 2` |
| 4 | 2 | 3 | 2 | 2 | `2 < 3` | `inicio = medio` → `inicio = 2` |
| … | 2 | 3 | 2 | 2 | `2 < 3` | el estado ya no cambia |

### Diagnóstico

A partir del paso 3 el estado se repite:

```text
inicio = 2
fin    = 3
medio  = (2 + 3) / 2 = 2
```

La división entera hace que `medio` vuelva a valer `inicio`. Como la acción es
`inicio = medio`, el intervalo **no se reduce** y la condición `inicio <= fin`
nunca se vuelve falsa. El `while` no termina.

El defecto no está en la comparación ni en el cálculo de `medio`: está en que
la actualización del límite no descarta la posición ya comparada.

---

## 2. Regla de corrección aplicada

`medio` **ya fue comparado** en esta vuelta, así que no debe volver a formar
parte del intervalo:

```java
if (comparacion < 0) {
    inicio = medio + 1;   // descarta la mitad izquierda y a medio
} else {
    fin = medio - 1;      // descarta la mitad derecha y a medio
}
```

Con `+1` y `-1` el intervalo se achica al menos en una posición por vuelta, por
lo que el ciclo siempre termina.

---

## 3. Traza posterior a la corrección

Misma entrada, ahora con la versión corregida. Salida real del programa
(`BancoDePruebas.trazaBusquedaBinaria()`):

```text
=== TRAZA: [0, 1, 2, 3] BUSCANDO 3 ===
  paso   inicio    fin    medio    valor medio accion
     1        0      3        1              1 medio < objetivo, inicio = medio + 1
     2        2      3        2              2 medio < objetivo, inicio = medio + 1
     3        3      3        3              3 encontrado, termina
Total de comparaciones: 3
```

| Paso | inicio | fin | medio | valor medio | comparación | acción |
|---:|---:|---:|---:|---:|---|---|
| 1 | 0 | 3 | 1 | 1 | `1 < 3` | `inicio = 2` |
| 2 | 2 | 3 | 2 | 2 | `2 < 3` | `inicio = 3` |
| 3 | 3 | 3 | 3 | 3 | `3 = 3` | encontrado, retorna 3 |

Tres comparaciones para cuatro elementos: `log₂(4) = 2`, más la comparación que
confirma el hallazgo.

---

## 4. Casos de prueba mínimos

Salida real de `BancoDePruebas.pruebasMinimas()`:

```text
=== CASOS DE PRUEBA MINIMOS (BUSQUEDA BINARIA) ===
arreglo    caso             esperado   obtenido  comparaciones   estado
pequeno    primero                 0          0              4       OK
pequeno    intermedio              8          8              4       OK
pequeno    ultimo                 15         15              5       OK
pequeno    inexistente            -1         -1              5       OK
grande     primero                 0          0             19       OK
grande     intermedio         500000     500000             19       OK
grande     ultimo             999999     999999             20       OK
grande     inexistente            -1         -1             20       OK
```

- Arreglo pequeño: 16 lecturas. Arreglo grande: 1.000.000 de lecturas.
- Los 8 casos pasan.
- El caso **inexistente** termina: es la evidencia de que el ciclo infinito
  quedó corregido. Con la versión defectuosa este caso no habría retornado.
- Multiplicar el tamaño por 62.500 (de 16 a 1.000.000) sube las comparaciones
  de 5 a 20. Eso es crecimiento logarítmico, no proporcional.

---

## 5. Tabla de mediciones (resultados propios)

Salida real de los experimentos 1 y 2:

| Tamaño | Lineal (comparaciones) | Binaria (comparaciones) | Relación | Tiempo lineal (ms) | Tiempo binaria (ms) |
|---:|---:|---:|---:|---:|---:|
| 1.000 | 1.000 | 10 | 100,0 | 0,0216 | 0,0287 |
| 100.000 | 100.000 | 17 | 5.882,4 | 2,6121 | 0,1261 |
| 1.000.000 | 1.000.000 | 20 | 50.000,0 | 11,9855 | 0,0513 |

Búsqueda de un timestamp **inexistente** sobre 100.000 lecturas:

| Algoritmo | Comparaciones |
|---|---:|
| Lineal | 100.000 |
| Binaria | 17 |

### Lectura de los resultados

1. **La lineal es exactamente `n`.** Buscar la última lectura obliga a recorrer
   todo el arreglo. Es el peor caso y se cumple sin desviación: 1.000, 100.000 y
   1.000.000 de comparaciones.

2. **La binaria coincide con `log₂(n)`.** Los valores teóricos son 9,97 / 16,61 /
   19,93 y lo medido fue 10 / 17 / 20. Multiplicar los datos por 1.000 solo
   agregó 10 comparaciones.

3. **El tiempo es ruidoso; las comparaciones no.** Con 1.000 lecturas la binaria
   fue *más lenta* en reloj (0,0287 ms contra 0,0216 ms) aunque hizo 100 veces
   menos comparaciones. A esa escala domina el calentamiento de la JVM, no el
   algoritmo. Por eso la evidencia principal del comportamiento algorítmico es el
   conteo de operaciones, y el tiempo es solo respaldo experimental.

4. **El dato inexistente es el peor caso de la lineal.** Para afirmar que algo
   *no está* hay que revisarlo todo: 100.000 comparaciones. La binaria descarta
   mitades hasta agotar el intervalo y responde en 17.

---

## 6. Experimento de precondición (PM2.5)

Salida real de `BancoDePruebas.experimentoCuatro()`:

```text
=== EXPERIMENTO 4: BINARIA POR PM2.5 (PRECONDICION) ===
Valores buscados que SI existen:   20
Encontrados por busqueda lineal:   20
Encontrados por busqueda binaria:  0
```

Se buscaron 20 valores de PM2.5 tomados del propio arreglo, así que los 20
existen con certeza. La búsqueda lineal los encontró todos. La binaria no
encontró ninguno.

El código de `busquedaBinariaPorPm25` es correcto: mismo esquema de intervalos
que la versión por timestamp, que sí funciona. Lo que falla es el dato: PM2.5 se
genera con `5 + azar.nextDouble() * 55`, de modo que el arreglo no está ordenado
por ese campo. Al descartar una mitad, la binaria descarta valores que sí podían
contener el objetivo.

```text
algoritmo correcto + precondición falsa = resultado incorrecto
```

---

## 7. Comparación de String: `==` contra `equals()`

Salida real de `BancoDePruebas.demostracionComparacionStrings()`:

```text
=== COMPARACION DE STRING: == vs equals() ===
Estacion buscada: "EST-005" (existe en los datos)
Con ==      -> posicion: -1
Con equals()-> posicion: 4
```

La estación `EST-005` existe en la posición 4. La versión con `==` devolvió
`-1`, es decir, afirmó que no existe.

El texto buscado se construyó con `new String("EST-005")`: mismo contenido,
objeto distinto. `==` compara si dos referencias apuntan al mismo objeto en
memoria; `equals()` compara el contenido. Cuando los String llegan leídos desde
un archivo CSV o desde la red no son literales internados por el compilador, así
que `==` falla en silencio: no lanza excepción, simplemente no encuentra nada.

---

## 8. Conclusión de la bitácora

Lo que se verificó esta semana:

- La búsqueda lineal cuesta `O(n)` y se midió exactamente `n` en el peor caso.
- La búsqueda binaria cuesta `O(log₂ n)` y se midió exactamente `log₂ n`.
- La binaria solo es válida si se cumple su precondición de orden.
- Un algoritmo puede terminar, no fallar y aun así responder mal.
- El conteo de operaciones explica el comportamiento; el reloj solo lo respalda.
