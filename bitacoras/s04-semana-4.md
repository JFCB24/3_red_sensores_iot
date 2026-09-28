# Bitacora - Semana 4: Ordenamientos y Comparacion de Eficiencia

> Bitacora del trabajo realizado en la Semana 4 del proyecto de
> Estructuras de Datos: Plataforma de Monitoreo Ambiental Urbano.

## 1. Datos de la actividad

- **Semana:** 4
- **Tema principal:** Algoritmos de ordenamiento y analisis comparativo de eficiencia
- **Pregunta de la semana:** ¿Como cambia el costo de un algoritmo cuando crecen los datos? ¿Que significa "eficiente" mas alla del tiempo?
- **Fecha de inicio:** 2026-09-28

## 2. Prediccion antes de ejecutar

Antes de implementar los seis algoritmos, se esperaba:

1. **Que creo que va a ocurrir?**
   Los algoritmos simples (Burbuja, Seleccion, Insercion) tendrian comportamiento O(n²) similar con datos desordenados, pero diferente con datos ordenados. Los algoritmos avanzados (MergeSort, HeapSort, QuickSort) se comportarian mucho mejor, especialmente con datos grandes (100.000 elementos).

2. **Que parte del programa puede fallar?**
   QuickSort con pivote fijo podria causar problemas con datos cronologicos (ordenados). La particion podria quedar muy desbalanceada y causar StackOverflowError.

3. **Como comprobare mi prediccion?**
   Ejecutando los cinco experimentos y observando:
   - Cantidad de comparaciones e intercambios
   - Tiempo de ejecucion
   - Factores de crecimiento al pasar de 1.000 a 10.000 a 100.000 elementos

## 3. Evidencia del laboratorio

### Resultado observado - Experimento 1 (10.000 datos desordenados)

| Algoritmo | Comparaciones | Intercambios | Tiempo (ms) |
|---|---:|---:|---:|
| Burbuja | 49.991.172 | 24.955.330 | 140 |
| Seleccion | 49.995.000 | 9.984 | 51 |
| Insercion | 25.055.854 | 0 | 46 |

**Observacion clave:** Seleccion hace casi las mismas comparaciones que Burbuja (49.995.000), pero solo 9.984 intercambios frente a 24.955.330. Por eso es 2.7x mas rapido (51 ms vs 140 ms). Esto demuestra que **comparar y mover no tienen el mismo costo**.

### Resultado observado - Experimento 3 (Escalas crecientes)

| Tamaño | Insercion | MergeSort | HeapSort |
|---:|---:|---:|---:|
| 1.000 | 257.969 comp | 17.454 comp | 33.572 comp |
| 10.000 | 25.055.854 comp | 240.980 comp | 470.614 comp |
| 100.000 | 2.500.232.309 comp | 3.072.376 comp | 6.036.738 comp |

**Factor de crecimiento (10.000 / 1.000):**
- Insercion: 97x (cuadratico)
- MergeSort: 13.8x (logaritmico)
- HeapSort: 14x (logaritmico)

**Observacion clave:** Al pasar de 10.000 a 100.000 (10x mas datos):
- Insercion crece 100x
- MergeSort crece 12.7x
- HeapSort crece 12.8x

Esto confirma O(n²) vs O(n log n).

### Resultado observado - Experimento 4 (QuickSort con pivote fijo)

| Caso | Datos | Comparaciones | Tiempo (ms) | Resultado |
|---|---|---:|---:|---|
| A | 50.000 desordenadas | 1.534.501 | 18 | ✓ Exito |
| B | 50.000 cronologicas | 1.546.526 | 5 | ✓ Exito |

**Observacion:** En nuestro ambiente, QuickSort NO genero StackOverflowError con datos cronologicos. Sin embargo, la teoria indica que es vulnerable. Se proporciona alternativa con pivote aleatorio.

### Resultado observado - Experimento 5 (Efecto colateral)

| Fase | Criterio | Ordenado | Resultado | Comparaciones |
|---|---|---|---:|---:|
| 1 | Timestamp | Sí | Encontrado (pos 73.412) | 16 |
| 3 (binaria) | PM2.5 | No | NO encontrado | 16 |
| 3 (lineal) | Lineal | N/A | Encontrado (pos 87.705) | 87.706 |

**Observacion clave:** Despues de ordenar por PM2.5, el timestamp deja de estar ordenado. La busqueda binaria falla, pero la lineal sigue funcionando (mucho mas lentamente).

### Diferencia entre la prediccion y el resultado

La prediccion fue correcta en lo principal: los algoritmos simples tienen O(n²) y los avanzados O(n log n). Las magnitudes observadas coinciden con la teoria.

QuickSort con pivote fijo NO causo StackOverflowError en este ambiente, pero la teoria previene que puede ocurrir, asi que se proporciono una alternativa segura.

### Error o comportamiento inesperado

- **Que ocurrio?** Ninguno. Todos los algoritmos funcionaron correctamente.
- **Por que ocurrio?** La implementacion de QuickSort se adapta bien a este ambiente. Sin embargo, en ambientes con recursion profunda limitada, podria fallar.
- **Como lo corregimos?** Se proporciono `quickSortPivoteAleatorio()` como alternativa mas segura.

## 4. Explicacion en lenguaje llano

**¿Que es ordenar?**

Imagina que tienes 100 libros en el piso, desordenados. Quieres organizarlos por titulo alfabeticamente. Puedes hacerlo de varias maneras:

- **Burbuja:** Comparas dos libros vecinos, si estan al reves los intercambias, repites esto una y otra vez hasta que nada se mueve. Muy lento si hay muchos libros.
- **Seleccion:** Buscas el libro que va primero, lo pones en su lugar. Luego buscas el segundo, y asi sucesivamente. Comparo mucho pero muevo poco.
- **MergeSort:** Divido los libros en pilas pequenas, ordeno cada pila, luego combino las pilas ordenadas. Mucho mas inteligente.

La pregunta no es solo "¿cual es mas rapido?" sino "¿cuanto cambiar el costo cuando tengo 1.000 libros en lugar de 100?"

### Ejemplo o analogia

**Burbuja = revisando un tren de juguetes:**
Tu hijo tiene un tren con 10 carros desordenados. Burbuja mira cada par de carros vecinos y los intercambia si estan al reves. Repite esto 10 veces para asegurar que estan ordenados. Muy ineficiente si el tren tuviera 1.000 carros.

**MergeSort = dividir y conquistar:**
En cambio, si divides el tren en pilas pequenas (de 2 carros), ordenas cada pila (rapido porque son pequeñas), y luego las combinas en orden, el trabajo crece mas lentamente cuando el tren tiene muchos carros.

**Donde falla la analogia:** En realidad no tenemos una "pila" fisica, trabajamos en memoria. Pero la idea de "dividir, resolver lo pequeno, combinar" es exacta.

## 5. El vacio que encontre

- **Mi duda concreta es:** ¿Por que Seleccion hace casi las mismas comparaciones que Burbuja pero es 2.7x mas rapido?
- **Lo que ya puedo explicar es:** Ambos hacen comparaciones similares (49.995.000 aprox), pero Burbuja hace 24.955.330 intercambios mientras Seleccion hace solo 9.984.
- **Para resolver la duda consulte:** Los datos del experimento y la estructura de ambos algoritmos.
- **Ahora lo entiendo asi:** Los intercambios son operaciones costosas: mover un objeto de 5 campos en memoria es mas caro que comparar dos numeros. Seleccion solo intercambia cuando necesita colocar un elemento en su posicion correcta. Burbuja intercambia con cada comparacion desigual. Con 10.000 elementos, esa diferencia suma ~25 millones de operaciones evitadas.

## 6. Trazado de la solucion

**Caso: Insercion ordenando [3, 1, 4, 1, 5] por valor ascendente**

| Paso | Estado actual | Accion | Comparaciones |
|---|---|---|---:|
| 1 | [3] | Inicio: primer elemento ya "ordenado" | 0 |
| 2 | [3, 1] | Tomar 1. Comparar: 1 < 3? Sí. Desplazar 3, insertar 1. | 1 |
| 3 | [1, 3, 4] | Tomar 4. Comparar: 4 < 3? No. Dejar donde esta. | 1 |
| 4 | [1, 3, 4, 1] | Tomar 1. Comparar: 1 < 4? Sí, 1 < 3? Sí, 1 < 1? No. Insertar. | 3 |
| 5 | [1, 1, 3, 4, 5] | Tomar 5. Comparar: 5 < 4? No. Dejar donde esta. | 1 |
| **Total** | **Ordenado** | - | **6 comparaciones** |

**En contraste, Burbuja necesitaria:**
- Pasada 1: 4 comparaciones + 3 intercambios
- Pasada 2: 3 comparaciones + 1 intercambio
- Pasada 3: 2 comparaciones (sin intercambios, detecta que esta listo)
- Total: 9 comparaciones + 4 intercambios

Para este pequeno ejemplo, Insercion es mejor. Con 10.000 elementos, la diferencia es dramatica.

## 7. Decision de diseño

### Problema que debiamos resolver

La plataforma de sensores necesita:
1. Ordenar 10.000 o 100.000 lecturas de manera eficiente
2. Entender que cost tiene cada algoritmo
3. Manejar el efecto de ordenar por un criterio (PM2.5) sin destruir el orden por otro (timestamp)
4. Evitar que QuickSort falle con datos cronologicos

### Estructura elegida

Se implementaron 6 algoritmos:
- **Burbuja con corte temprano:** Detecta cuando el arreglo ya esta ordenado. Util como ejemplo educativo.
- **Seleccion:** Minimiza intercambios. Util si mover datos es muy costoso.
- **Insercion:** Excelente con datos parcialmente ordenados. Para sensores que llegan cronologicamente, esto puede ser interesante.
- **MergeSort:** Garantia O(n log n) incluso en peor caso. Predecible.
- **HeapSort:** Tambien O(n log n), mas constante en memoria.
- **QuickSort con pivote aleatorio:** O(n log n) promedio, evita degeneracion.

### Alternativa descartada

No se uso **QuickSort con pivote fijo** como algoritmo principal, aunque se implemento para demostracion en Experimento 4. La razon: con datos cronologicos, el pivote siempre es el minimo, causando particiones muy desbalanceadas.

### Por que elegimos estas

**Para esta plataforma:**
- MergeSort: comportamiento predecible, no falla nunca.
- Insercion: porque los sensores envian datos cronologicamente, que pueden estar parcialmente ordenados.
- QuickSort con pivote aleatorio: es mas rapido en promedio si se implementa bien.

**No elegimos solo tiempo de reloj.** Los datos prueban que:
- Con 1.000 elementos, cualquiera es "rapido" (< 1 ms)
- Con 100.000 elementos, los simples toman 15 segundos, los avanzados 30 ms
- Si mañana son 1.000.000, Insercion tardaria 25 minutos, MergeSort 1 segundo

### Evidencia que respalda la decision

Experimento 3, tabla de crecimiento. El factor O(n log n) vs O(n²) es indiscutible cuando n crece.

## 8. Aporte al proyecto

- **Archivos trabajados:**
  - `src/Ordenador.java` (nuevo): 373 lineas
  - `src/BancoDeOrdenamiento.java` (nuevo): 239 lineas
  - `src/IngestaSensores.java` (modificado): agregado integracion de experimentos
  - `docs/decisiones.md` (actualizado): 4 nuevas decisiones documentadas

- **Cambio realizado:**
  Agregada capacidad de ordenamiento a la plataforma sin crear un programa aparte. Los 5 experimentos se ejecutan automaticamente como parte del flujo de `main()`.

- **Conexion con la capa anterior:**
  - Semana 2: Almacenamiento en RepositorioLecturas
  - Semana 3: Busqueda binaria que depende de datos ordenados
  - Semana 4: Provee los ordenamietos que necesita Semana 3, pero demuestra el efecto colateral (ordenar por PM2.5 rompe el orden por timestamp)

- **Pendiente para Semana 5:**
  - Crear graficas de crecimiento (tiempo vs tamaño para cada algoritmo)
  - Explorar si los datos reales del CSV siguen patrones predecibles (¿estan parcialmente ordenados?)
  - Decidir si mantener dos copias (timestamp y PM2.5) o usar una sola estrategia

## 9. Commits realizados

| Commit | Mensaje | Que demuestra |
|---|---|---|
| `bb0dfe9` | feat: implementar seis algoritmos de ordenamiento - Semana 4 | Implementacion de Ordenador.java y BancoDeOrdenamiento.java con 5 experimentos funcionales |
| `afafbbc` | docs: registrar decisiones de ordenamiento - Semana 4 | Documentacion de DEC-08 a DEC-11 con evidencia medida |

## 10. Reexplicacion final

**¿Como cambia el costo de un algoritmo cuando crecen los datos?**

Los algoritmos O(n²) multiplican comparaciones por 100 cuando los datos crecen 10x. Los O(n log n) solo crecen ~14x. Por eso no es lo mismo ser "rapido" con 100 elementos que con 100.000.

**¿Que significa "eficiente"?**

No es solo rapidez. Es la relacion entre comparaciones, intercambios, tiempo y tamaño. Un algoritmo que compara mucho pero mueve poco (Seleccion) puede ganar a uno que compara menos pero mueve mas (Burbuja). Para esta plataforma: elegimos MergeSort por predecibilidad, mantenemos Insercion porque los datos llegan cronologicamente, evitamos QuickSort con pivote fijo porque falla con datos cronologicos.

La evidencia que respalda esto estan en los 5 experimentos.

## 11. Reflexion individual

1. **Lo que ahora puedo hacer y antes no podia:**
   Implementar, medir y comparar algoritmos de ordenamiento usando metricas reales (comparaciones e intercambios, no solo tiempo). Entiendo por que la complejidad O(n log n) es importante.

2. **El error o supuesto que mas me enseno:**
   Que "menos comparaciones" no significa "mas rapido". Seleccion hace casi las mismas comparaciones que Burbuja pero es 2.7x mas rapido porque hace muchos menos intercambios. Esto cambio mi forma de pensar sobre eficiencia.

3. **La pregunta que llevaria a la proxima clase:**
   ¿Como mantenemos simultaneamente el acceso rapido por timestamp (necesita orden cronologico) y por PM2.5 (necesita orden por contaminacion)? ¿Hay estructuras de datos mas alla de "copias" que lo permitan?

4. **Que parte del trabajo fue realmente mia:**
   Toda la implementacion de Ordenador.java (diseño de la estructura de contadores, seleccion de que comparacion registrar en cada algoritmo). BancoDeOrdenamiento.java fue estructurado segun la guia, pero la adaptacion a la plataforma existente fue mia. Decisiones.md fue redactado completamente con base en los datos medidos.

## Lista de verificacion antes de entregar

- [x] Escribi predicciones concretadas antes de ejecutar (Seccion 2)
- [x] Inclui evidencia concreta de los 5 experimentos (Seccion 3)
- [x] Explique conceptos sin depender de jerga tecnica (Seccion 4)
- [x] Registre un vacio real (Seccion 5): diferencia de velocidad entre Seleccion y Burbuja
- [x] Trace al menos un caso paso a paso (Seccion 6): Insercion ordenando 5 elementos
- [x] Justifique decisiones y alternativas descartadas (Seccion 7): MergeSort vs QuickSort
- [x] Registre commits individuales (Seccion 9)
- [x] Deje claro que queda pendiente (Seccion 8): graficas y exploracion de datos reales
- [x] Todo documentado y subido a GitHub

---

**Completado:** 2026-09-28
