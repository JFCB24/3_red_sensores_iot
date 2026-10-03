# Bitacora individual - Semana 04

## 1. Datos de la actividad

- **Estudiante:** Juan Felipe Castellanos Bran
- **Equipo:** Plataforma de Monitoreo Ambiental Urbano
- **Semana:** 4
- **Fecha del laboratorio:** 2026-10-01
- **Fecha del taller:** 2026-10-03
- **Tema principal:** Algoritmos de ordenamiento y analisis comparativo de eficiencia
- **Pregunta de la semana:** ¿Como cambia el costo de un algoritmo cuando crecen los datos? ¿Que significa "eficiente" mas alla del tiempo?

## 2. Prediccion antes de ejecutar

1. **Que creo que va a ocurrir?**
   Los algoritmos cuadraticos simples (Burbuja, Seleccion, Insercion) tendran un costo de orden $O(n^2)$ con datos desordenados. Sin embargo, al recibir datos cronologicos (ya ordenados), Burbuja con corte temprano e Insercion caeran a $O(n)$. Por otro lado, los avanzados (MergeSort, HeapSort) mantendran un crecimiento $O(n \log n)$.

2. **Que parte del programa o del algoritmo puede fallar?**
   QuickSort con pivote fijo en el primer elemento puede degradar severamente ante datos ordenados cronologicamente, multiplicando las comparaciones hacia $O(n^2)$ por la generacion de particiones desbalanceadas.

3. **Como comprobare mi prediccion?**
   Ejecutando la suite de cinco experimentos dentro del sistema, contrastando el numero de comparaciones, intercambios y tiempo medido (ms) frente al crecimiento de datos (1.000, 10.000, 50.000 y 100.000 lecturas).

## 3. Evidencia del laboratorio

### Resultado observado

**Experimento 1 (10.000 lecturas desordenadas):**
- Burbuja: 49.990.814 comparaciones | 24.928.244 intercambios | 873 ms
- Seleccion: 49.995.000 comparaciones | 9.994 intercambios | 845 ms
- Insercion: 24.938.233 comparaciones | 0 intercambios | 216 ms

**Experimento 2 (10.000 lecturas ordenadas cronologicamente):**
- Burbuja: 9.999 comparaciones | 0 intercambios | 0 ms
- Seleccion: 49.995.000 comparaciones | 0 intercambios | 266 ms
- Insercion: 9.999 comparaciones | 0 intercambios | 1 ms

**Experimento 3 (Simples vs Avanzados en escala creciente):**
- 1.000 datos: Insercion (242.787 comp, 4 ms) | MergeSort (17.368 comp, 1 ms) | HeapSort (33.572 comp, 1 ms)
- 10.000 datos: Insercion (24.938.233 comp, 216 ms) | MergeSort (240.792 comp, 4 ms) | HeapSort (470.868 comp, 7 ms)
- 100.000 datos: Insercion (2.497.222.762 comp, 37.644 ms) | MergeSort (3.072.650 comp, 62 ms) | HeapSort (6.039.112 comp, 96 ms)

**Experimento 4 (QuickSort con pivote en primer elemento - 50.000 lecturas):**
- Caso A (Desordenadas): 1.530.921 comparaciones | 38 ms
- Caso B (Orden cronologico): 1.250.099.995 comparaciones | 2.948 ms

**Experimento 5 (Ranking por PM2.5 y consulta binaria):**
- Paso 1 (Llegada en orden de timestamp): Busqueda binaria exitosa en posicion 73.412 con 16 comparaciones.
- Paso 2 (Ranking generado): Ordenamiento por PM2.5 (rango 5.0 a 60.0).
- Paso 3 (Consulta posterior): `Ordenado por timestamp: false`. Busqueda binaria arroja posicion -1 (fallo por precondicion rota). Busqueda lineal recupera el elemento en la posicion 87.705.

### Diferencia entre la prediccion y el resultado

La prediccion teorica coincidio en su totalidad:
- Se comprobo el corte temprano en Burbuja e Insercion bajando a 9.999 operaciones ($O(n)$).
- Seleccion no aprovecho el orden en comparaciones (~50 millones), pero no realizo intercambios (0).
- QuickSort con pivote en el primer elemento manifesto una degradacion cuadratica masiva en datos cronologicos, pasando de 1,5 millones a 1.250 millones de comparaciones.

### Error o comportamiento inesperado

- **Que ocurrio?** Inicialmente, Burbuja no cortaba en el Experimento 2 (realizaba 49 millones de comparaciones) e Insercion no alcanzaba el orden lineal.
- **Por que ocurrio?** Los metodos auxiliares de comparacion en `Ordenador.java` estaban evaluando `getPm25()` (cuyos valores son aleatorios) en lugar de evaluar el `timestamp` (que era el campo que venia ordenado cronologicamente desde el generador).
- **Como lo corregimos o que falta corregir?** Se modificaron las funciones comparadoras para utilizar `a.getTimestamp().compareTo(b.getTimestamp())` en todos los algoritmos base, y se implemento un metodo exclusivo `compararPorPm25` para la generacion del ranking en el Experimento 5.

## 4. Explicacion en lenguaje llano

Ordenar no es simplemente hacer que una lista se vea bonita rapido, sino decidir cuantas veces vas a mirar los datos y cuantas veces vas a moverlos de puesto. Imagina ordenar una fila de cartas de juego:
- **Burbuja:** Compara pares contiguos. Si en toda una pasada ninguna carta cambia de lugar, la bandera avisa que ya terminaste y paras de inmediato.
- **Seleccion:** Revisa toda la mesa para buscar la mas pequena y ponerla al inicio; mira muchisimo, pero casi no mueve cartas.
- **MergeSort:** Reparte la baraja en mitades hasta tener montones diminutos, los organiza por separado y luego los junta ordenadamente.

> Ordenar eficientemente significa reducir el trabajo elemental. Un buen algoritmo no se mide solo por los segundos que tarda en el computador, sino por evitar trabajo innecesario a medida que la cantidad de informacion se multiplica.

### Ejemplo o analogia

**Organizar una biblioteca escolar:**
Si recibes 100 libros nuevos y usas Seleccion, tendras que revisar los titulos de los 100 libros para ubicar el primero en el estante, luego revisar 99 para el segundo, etc. Leiste muchisimas portadas (comparaciones), pero cada libro solo fue movido una vez de la mesa al estante (intercambios minimos). En cambio, Burbuja moveria libros pesados de una mano a otra constantemente con cada comparacion fallida.
*Donde deja de ser exacta:* En una computadora no hay cansancio fisico al levantar objetos, pero mover bloques de memoria grandes consume ciclos de procesador y accesos al bus de datos.

## 5. El vacio que encontre

- **Mi duda concreta es:** ¿Por que Seleccion tardo un tiempo similar a Burbuja en el Experimento 1 desordenado a pesar de hacer solo 9.994 intercambios frente a casi 25 millones de Burbuja?
- **Lo que ya puedo explicar es:** Ambos algoritmos tienen que recorrer y comparar exactamente $n(n-1)/2 \approx 50.000.000$ de pares de datos.
- **Para resolver la duda consulte:** El analisis de la arquitectura JVM, el costo de las comparaciones de Strings (`compareTo` de timestamps) y las trazas de ejecucion.
- **Ahora lo entiendo asi:** En Java, la operacion de comparar dos cadenas de texto (`timestamp`) requiere evaluar caracter por caracter en memoria. Ese costo por comparacion fue tan representativo dentro del ciclo interior que absorbio gran parte del tiempo total de ejecucion, mitigando la ventaja de haber ahorrado intercambios de referencias en el arreglo.

## 6. Trazado de la solucion

**Insercion paso a paso sobre el arreglo cronologico [3, 1, 4, 1, 5]:**

| Paso | Estado de los datos o estructura | Decision o resultado |
|---|---|---|
| 1 | `[3]` \| `1, 4, 1, 5` | El primer elemento ya forma el subarreglo ordenado. |
| 2 | `[1, 3]` \| `4, 1, 5` | Se toma el 1. Se compara con 3 ($1 < 3$), se corre el 3 a la derecha e inserta el 1. |
| 3 | `[1, 3, 4]` \| `1, 5` | Se toma el 4. Se compara con el 3 ($4 > 3$), se queda en su posicion. |
| 4 | `[1, 1, 3, 4]` \| `5` | Se toma el segundo 1. Se desplazan 4 y 3; se respeta la posicion del primer 1 (orden estable). |
| 5 | `[1, 1, 3, 4, 5]` | Se toma el 5. Se compara con 4 ($5 > 4$), permanece en su posicion final. |

## 7. Decision de diseño

- **Problema que debiamos resolver:** El sistema de sensores ingesta flujos masivos de datos continuos que requieren busquedas binarias instantaneas por fecha y generacion de rankings por nivel de contaminacion.
- **Estructura, algoritmo o estrategia elegida:** MergeSort como algoritmo base de ordenamiento para la plataforma y uso de banderas de deteccion cronologica.
- **Alternativa descartada:** QuickSort clasico con pivote fijo en el primer elemento.
- **Por que elegimos la primera:** MergeSort garantiza de manera estricta una cota temporal de $O(n \log n)$ en todos los escenarios (peor, mejor y promedio). QuickSort con pivote en el extremo inicial degenera catastroficamente a $O(n^2)$ cuando los datos llegan cronologicamente.
- **Que evidencia respalda la decision:** En el Experimento 4, ante 50.000 lecturas cronologicas, QuickSort realizo 1.250.099.995 comparaciones (tardando casi 3 segundos), mientras que MergeSort resolvio 100.000 lecturas en apenas 62 ms con 3 millones de comparaciones.

## 8. Aporte al proyecto

- **Archivo(s) o modulo(s) trabajado(s):**
    - `src/Ordenador.java`
    - `src/BancoDeOrdenamiento.java`
    - `src/IngestaSensores.java`
- **Cambio realizado:** Implementacion del corte temprano en Burbuja (TODO 1), estandarizacion del criterio de comparacion cronologico (`timestamp`), y desarrollo del metodo `ordenarPorPm25` para la evaluacion del efecto colateral sobre busquedas binarias (TODO 3).
- **Como se conecta con la capa anterior:** Se enlaza directamente con la Semana 3: la busqueda binaria requiere como precondicion estricta que los datos esten ordenados por el criterio de consulta.
- **Que queda pendiente para la siguiente semana:** Implementar indices secundarios o mantener arreglos referenciales separados para no destruir el orden cronologico al generar rankings por contaminacion.

## 9. Commits realizados

| Commit | Mensaje | Que demuestra |
|---|---|---|
| `8901f03` | `fix: corregir criterio de ordenamiento y corte temprano en algoritmos de Semana 4` | Correccion del criterio de comparacion a timestamp, bandera de Burbuja y ordenamiento estable por PM2.5. |

## 10. Reexplicacion final

> El costo algoritmico se dispara exponencialmente segun su clase de complejidad; ante un aumento de 10x en el tamano de datos, un algoritmo $O(n^2)$ multiplica su costo por 100x, mientras que un algoritmo $O(n \log n)$ solo lo incrementa en ~13x. Eficiencia no es medir milisegundos en una maquina particular, sino seleccionar la estructura o algoritmo cuyo patron de comparaciones y movimientos de memoria preserve cotas escalables e invariantes matematicas segun las caracteristicas de la entrada.

## 11. Reflexion individual

1. **Lo que ahora puedo hacer y antes no podia:**
   Medir, auditar y diagnosticar con precision matematica la cantidad de comparaciones e intercambios que ejecuta un algoritmo, identificando cuando degenera a su peor caso.

2. **El error o supuesto que mas me enseno:**
   Asumir que los datos estaban ordenados cuando los algoritmos evaluaban un atributo diferente (`pm25` vs `timestamp`). Me enseno que la precondicion de orden depende estrictamente del campo por el cual se compara.

3. **La pregunta que llevaria a la proxima clase:**
   ¿Cual es el costo en memoria y procesamiento de mantener copias de arreglos ordenadas por distintos indices frente a usar estructuras indexadas como arboles binarios o tablas hash?

4. **Que parte del trabajo fue realmente mia:**
   La depuracion y correccion de la logica en `Ordenador.java`, la adaptacion del metodo `ordenarPorPm25`, el analisis de las causas de degradacion en QuickSort y la interpretacion de las metricas experimentales registradas en esta bitacora.

## Lista de verificacion antes de entregar

- [x] Escribi la prediccion antes de consultar el resultado.
- [x] Inclui evidencia concreta del laboratorio.
- [x] Explique un concepto sin depender de jerga.
- [x] Registre un vacio, una duda o un error real.
- [x] Trace al menos un caso paso a paso.
- [x] Justifique una decision del proyecto y una alternativa descartada.
- [x] Registre mis commits y mi aporte individual.
- [x] Deje claro que queda pendiente.
- [x] Renombre el archivo con el formato `sXX-nombre.md`.
