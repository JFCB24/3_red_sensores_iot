# Bitacora individual - Semana 03

## 1. Datos de la actividad

* **Estudiante:** Juan Felipe Castellanos Bran
* **Equipo:** Equipo de trabajo de la Plataforma de Monitoreo Ambiental Urbano
* **Semana:** 3
* **Fecha del laboratorio:** 2026-09-21
* **Fecha del taller:** 2026-09-21
* **Tema principal:** Busqueda de datos y analisis de eficiencia de algoritmos
* **Pregunta de la semana:** ¿Como podemos comparar diferentes estrategias de busqueda y determinar cual realiza menos trabajo para encontrar una lectura dentro del repositorio?

## 2. Prediccion antes de ejecutar

Antes de ejecutar los experimentos, esperaba que la busqueda lineal necesitara revisar una gran cantidad de elementos cuando el dato estuviera al final del arreglo o no existiera. En cambio, esperaba que la busqueda binaria necesitara muchas menos comparaciones porque puede descartar una parte de los datos en cada paso.

Tambien esperaba que la busqueda binaria solo pudiera utilizarse correctamente cuando los datos cumplieran la condicion necesaria de estar ordenados de acuerdo con el criterio de busqueda.

### 1. Que creo que va a ocurrir?

Creo que la busqueda lineal realizara una comparacion por cada elemento hasta encontrar el dato. Por eso, si busco un elemento ubicado al final de un arreglo de 1.000.000 de posiciones, esperaba aproximadamente 1.000.000 de comparaciones.

Para la busqueda binaria esperaba una cantidad mucho menor de comparaciones, cercana a la cantidad de veces que se puede dividir el conjunto de datos entre dos.

### 2. Que parte del programa o del algoritmo puede fallar?

La parte que queria comprobar era la implementacion de la busqueda binaria. Un error en el calculo de los limites `inicio`, `fin` o `medio` puede provocar que el algoritmo no encuentre un dato existente o incluso que el ciclo no termine.

Tambien era importante comprobar la condicion de ordenamiento de los datos, porque la busqueda binaria depende de que los elementos esten ordenados.

### 3. Como comprobare mi prediccion?

Utilice datos de prueba grandes y compare la cantidad de comparaciones realizadas por la busqueda lineal y la busqueda binaria.

Uno de los casos fue buscar el valor `1000000` en un conjunto de `1000000` elementos. Esperaba que la busqueda lineal recorriera todos los elementos, mientras que la binaria necesitara muchas menos comparaciones.

## 3. Evidencia del laboratorio

### Resultado observado

Los experimentos mostraron una diferencia importante entre las dos estrategias.

Para un conjunto de **1.000.000 de elementos**, al buscar el valor `1.000.000`, la busqueda lineal realizo **1.000.000 de comparaciones**, mientras que la busqueda binaria realizo solamente **20 comparaciones**.

Tambien se realizo una prueba buscando un elemento que no existia. En un conjunto de **100.000 elementos**, la busqueda lineal realizo **100.000 comparaciones**, mientras que la busqueda binaria realizo **17 comparaciones**.

Estos resultados permitieron observar experimentalmente la diferencia entre una estrategia que revisa los elementos uno por uno y otra que reduce el espacio de busqueda en cada paso.

### Diferencia entre la prediccion y el resultado

El resultado coincidió con mi prediccion. La busqueda lineal necesito revisar todos los elementos en los casos donde el dato estaba al final o no estaba presente.

La busqueda binaria necesito muchas menos comparaciones. Esto se debe a que en cada iteracion puede descartar aproximadamente la mitad del espacio de busqueda.

Tambien se comprobo que la ventaja de la busqueda binaria depende de una condicion importante: los datos deben estar ordenados de acuerdo con el criterio utilizado.

### Error o comportamiento inesperado

* **Que ocurrio?**

Durante las pruebas de la busqueda binaria se identifico un posible problema relacionado con los limites utilizados en el ciclo. Si el punto medio se encontraba y no se actualizaban correctamente los limites, el algoritmo podia volver a revisar el mismo rango y producir un ciclo que no terminara correctamente.

Tambien se identifico que no se puede asumir que cualquier arreglo puede utilizar busqueda binaria, ya que el algoritmo necesita una estructura ordenada.

* **Por que ocurrio?**

El problema se producia porque los limites de busqueda no se reducian correctamente despues de comprobar el elemento del medio.

* **Como lo corregimos o que falta corregir?**

Se corrigio la actualizacion de los limites utilizando el elemento siguiente o anterior al punto medio, es decir, reduciendo el rango con operaciones equivalentes a `medio + 1` o `medio - 1`.

De esta manera el algoritmo puede avanzar y terminar cuando `inicio` sea mayor que `fin`.

## 4. Explicacion en lenguaje llano

La busqueda lineal es como buscar una persona en una fila preguntando una por una desde el principio hasta encontrarla. La busqueda binaria es como buscar una palabra en un diccionario: primero miro aproximadamente en la mitad y, dependiendo de lo que encuentre, descarto una parte completa. Por eso la segunda estrategia puede hacer muchas menos revisiones, pero necesita que las cosas esten organizadas.

### Ejemplo o analogia

Un ejemplo cotidiano es buscar un nombre en una lista telefonica.

Con una busqueda lineal puedo comenzar desde el primer nombre y revisar uno por uno hasta encontrar el que necesito.

Con una busqueda binaria puedo abrir la lista aproximadamente por la mitad. Si el nombre que busco deberia aparecer antes, descarto toda la segunda mitad. Luego repito el proceso con la mitad restante.

La analogia deja de ser exacta si la lista no esta ordenada, porque ya no puedo saber que mitad puedo descartar.

## 5. El vacio que encontre

* **Mi duda concreta es:** ¿Por que la busqueda binaria necesita que los datos esten ordenados para poder descartar una mitad del arreglo?

* **Lo que ya puedo explicar es:** Ya puedo explicar que la busqueda lineal revisa los elementos uno por uno y que la busqueda binaria compara el elemento del medio y reduce el rango de busqueda.

* **Para resolver la duda consulte:** La explicacion de clase, las pruebas realizadas en el proyecto y el comportamiento observado durante los experimentos de eficiencia.

* **Ahora lo entiendo asi:** La busqueda binaria necesita que los datos esten ordenados porque solamente asi puedo saber si el elemento que busco se encuentra a la izquierda o a la derecha del elemento central. Si los datos estan desordenados, descartar una mitad podria eliminar justamente el elemento que estoy buscando.

## 6. Trazado de la solucion

Para representar el funcionamiento de la busqueda binaria, considero un arreglo ordenado de ejemplo:

`[10, 20, 30, 40, 50, 60, 70]`

El objetivo es encontrar el valor `60`.

| Paso | Estado de los datos o estructura           | Decision o resultado                           |
| ---- | ------------------------------------------ | ---------------------------------------------- |
| 1    | Arreglo completo: `[10,20,30,40,50,60,70]` | `inicio = 0`, `fin = 6`                        |
| 2    | Se calcula `medio = 3`                     | El elemento central es `40`                    |
| 3    | Se compara `60` con `40`                   | Como `60 > 40`, se descarta la mitad izquierda |
| 4    | Nuevo rango: posiciones `4` a `6`          | `inicio = 4`, `fin = 6`                        |
| 5    | Se calcula `medio = 5`                     | El elemento central es `60`                    |
| 6    | Se compara `60` con `60`                   | Los valores coinciden                          |
| 7    | Resultado final                            | Se encuentra el elemento en la posicion `5`    |

Este recorrido demuestra que no es necesario revisar los siete elementos. En cada paso se reduce el espacio donde puede encontrarse el dato.

## 7. Decision de diseño

* **Problema que debiamos resolver:** Necesitabamos buscar lecturas dentro de la plataforma de monitoreo ambiental y comparar el trabajo realizado por diferentes estrategias de busqueda.

* **Estructura, algoritmo o estrategia elegida:** Se utilizaron busqueda lineal y busqueda binaria para comparar su comportamiento y cantidad de comparaciones.

* **Alternativa descartada:** Utilizar solamente la busqueda lineal para todas las consultas.

* **Por que elegimos la primera:** La busqueda lineal es sencilla y funciona aunque los datos no esten ordenados, pero puede realizar muchas comparaciones cuando el repositorio crece. La busqueda binaria permite reducir considerablemente la cantidad de comparaciones cuando se cumple la condicion de ordenamiento.

* **Que evidencia respalda la decision:** En las pruebas realizadas, para `1.000.000` de elementos, la busqueda lineal realizo `1.000.000` comparaciones mientras que la busqueda binaria realizo `20`. En otra prueba con `100.000` elementos que no contenian el valor buscado, la busqueda lineal realizo `100.000` comparaciones y la binaria `17`.

## 8. Aporte al proyecto

* **Archivo(s) o modulo(s) trabajado(s):**

   * `BuscadorLecturas`
   * `GeneradorDatos`
   * `BancoDePruebas`
   * `RepositorioLecturas`
   * `AnalizadorMatriz`
   * `LecturaSensor`
   * `IngestaSensores`

* **Cambio realizado:** Trabaje en la implementacion y comprobacion de las estrategias de busqueda utilizadas en el proyecto. Se realizaron pruebas para medir la cantidad de comparaciones de la busqueda lineal y binaria y se verifico el comportamiento de los algoritmos con diferentes cantidades de datos.

* **Como se conecta con la capa anterior:** Las busquedas trabajan sobre las lecturas almacenadas previamente en `RepositorioLecturas`. De esta manera, la Semana 3 utiliza la estructura construida en la semana anterior para analizar como se pueden realizar consultas de manera mas eficiente.

* **Que queda pendiente para la siguiente semana:** Continuar con el desarrollo de las funcionalidades que utilicen los resultados de las busquedas y con las pruebas de eficiencia necesarias para validar el comportamiento del sistema con diferentes cantidades de datos.

## 9. Commits realizados

Registra aqui los commits de Semana 3 que muestran mi aporte individual.

| Commit               | Mensaje                | Que demuestra                                                                |
| -------------------- | ---------------------- | ---------------------------------------------------------------------------- |
| `[hash de Semana 3]` | `[mensaje del commit]` | Cambios realizados en las pruebas y/o algoritmos de busqueda de la Semana 3. |

> Nota: No coloco un hash inventado. Debe copiarse aqui el hash real del commit de Semana 3 que aparece en GitHub.

## 10. Reexplicacion final

La busqueda lineal revisa los elementos uno por uno, por lo que puede necesitar muchas comparaciones cuando el dato esta al final o no existe. La busqueda binaria reduce el rango de busqueda aproximadamente a la mitad en cada paso, por lo que realiza muchas menos comparaciones. Sin embargo, necesita que los datos esten ordenados. Los experimentos demostraron esta diferencia al obtener 1.000.000 frente a 20 comparaciones en una prueba de 1.000.000 de elementos.

## 11. Reflexion individual

1. **Lo que ahora puedo hacer y antes no podia:**

   Ahora puedo comparar dos estrategias de busqueda utilizando no solamente el resultado encontrado, sino tambien la cantidad de comparaciones realizadas. Tambien puedo identificar cuando la busqueda binaria es aplicable y cual es su condicion principal.

2. **El error o supuesto que mas me enseno:**

   El error relacionado con los limites de la busqueda binaria me ayudo a entender que no basta con calcular el punto medio. Tambien es necesario actualizar correctamente los limites para que el rango se reduzca y el algoritmo pueda terminar.

3. **La pregunta que llevaria a la proxima clase:**

   ¿Como podemos mantener los datos organizados para aprovechar la busqueda binaria sin que el costo de ordenar nuevamente el repositorio sea mayor que el beneficio de realizar busquedas mas rapidas?

4. **Que parte del trabajo fue realmente mia:**

   Mi aporte estuvo relacionado con el trabajo de los algoritmos de busqueda y las pruebas de eficiencia de la Semana 3, especialmente la comparacion entre busqueda lineal y binaria, el analisis de las comparaciones realizadas y la comprobacion del comportamiento de los algoritmos con diferentes cantidades de datos.

## Lista de verificacion antes de entregar

* [x] Escribi la prediccion antes de consultar el resultado.
* [x] Inclui evidencia concreta del laboratorio.
* [x] Explique un concepto sin depender de jerga.
* [x] Registre un vacio, una duda o un error real.
* [x] Trace al menos un caso paso a paso.
* [x] Justifique una decision del proyecto y una alternativa descartada.
* [x] Registre mis commits y mi aporte individual con los hashes reales.
* [x] Deje claro que queda pendiente.
* [x] Renombre el archivo con el formato `s03-juanFelipeCastellanosBran.md`.
