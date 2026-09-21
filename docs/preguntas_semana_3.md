# Preguntas de análisis — Semana 3

**Espacio académico:** Estructuras de Datos
**Proyecto integrador:** Red de Sensores IoT

---

# Parte A — Pensamiento crítico sobre búsqueda y eficiencia

## Pregunta 1

> Una empresa tiene un millón de registros y realiza únicamente cinco búsquedas
> durante todo el día. ¿Tiene sentido diseñar toda la estrategia de
> almacenamiento alrededor de una búsqueda binaria?

No, y el propio experimento lo sugiere.

La búsqueda binaria ahorró, en nuestras mediciones, unos 11,9 milisegundos por
consulta sobre un millón de registros. Cinco consultas al día son unos **60
milisegundos ahorrados en 24 horas**. Ese no es un problema que justifique
rediseñar nada.

Lo que sí cuesta es sostener la precondición. Para poder usar binaria hay que
mantener los datos ordenados por el campo de búsqueda, y eso tiene un precio que
no aparece en la tabla de comparaciones:

- **Ordenar cuesta.** El orden es `O(n log n)`, muy por encima del `O(n)` de una
  lineal. Si ordenamos un millón de registros para hacer cinco búsquedas, pagamos
  mucho más de lo que ahorramos.
- **Mantener el orden cuesta en cada inserción.** Una red de sensores ingiere
  datos de forma continua. Insertar en un arreglo ordenado obliga a desplazar
  elementos; agregar al final no.
- **Solo se puede privilegiar un campo.** Ordenar por timestamp no ayuda en nada
  a buscar por PM2.5.
- **Cuesta en complejidad de código.** Más invariantes que sostener, más
  supuestos que se pueden romper en silencio, más código que mantener y más
  formas de equivocarse.

Los factores que yo consideraría antes de decidir:

1. **Relación entre escrituras y lecturas.** Cinco búsquedas contra una ingesta
   continua es un perfil dominado por escritura. El diseño debe optimizar la
   ingesta.
2. **Latencia exigida.** Si esas cinco consultas son reportes internos, 12 ms no
   le importan a nadie. Si alimentan una alerta de calidad del aire en tiempo
   real, la conversación cambia.
3. **Volatilidad de los datos.** Datos que cambian constantemente hacen que
   mantener el orden sea un costo permanente, no uno inicial.
4. **Crecimiento esperado.** Cinco búsquedas hoy pueden ser cinco mil el próximo
   semestre. La decisión debe poder revisarse, no quedar incrustada.

La conclusión general es que **la eficiencia asintótica describe cómo crece un
costo, no si ese costo importa**. `O(n)` con `n` pequeño o con pocas ejecuciones
puede ser perfectamente aceptable. Optimizar lo que no es un cuello de botella
agrega riesgo sin entregar beneficio.

---

## Pregunta 2

> Un algoritmo puede ser mucho más rápido que otro y, sin embargo, producir una
> respuesta incorrecta. ¿Por qué la corrección debe analizarse antes que la
> eficiencia?

Porque **la velocidad de una respuesta equivocada no vale nada**. Un algoritmo
incorrecto que tarda 1 ms no es mejor que uno incorrecto que tarda 1 segundo: los
dos entregan lo mismo, que es nada útil.

Nuestro experimento 4 lo muestra literalmente. La búsqueda binaria por PM2.5 es
dramáticamente más rápida que la lineal —17 comparaciones contra 10.000— y
encontró **0 de 20** valores que sí existían. La lineal, lenta, encontró los 20.
Aquí el algoritmo "eficiente" es directamente inservible.

Hay tres razones de fondo:

**Primera: la eficiencia se mide sobre lo correcto.** Preguntar "¿cuánto cuesta?"
solo tiene sentido si ya sabemos que hace lo que debe. Si no, estamos midiendo
con precisión la velocidad de un error.

**Segunda: los errores de corrección son silenciosos; los de eficiencia,
ruidosos.** Un programa lento se nota: alguien se queja, hay una métrica, hay un
tiempo de espera visible. Un programa que responde `-1` cuando el dato existe no
se queja de nada. Devuelve un valor perfectamente válido en tipo y forma, y el
sistema sigue funcionando sobre una mentira. En el experimento 4 no hubo
excepción, ni advertencia, ni bloqueo: solo respuestas falsas.

**Tercera: corregir después cuesta más.** Si se optimiza primero y se construye
encima, el defecto queda en los cimientos. Cuando aparezca, habrá que rehacer
todo lo que se apoyó en él. Al revés es barato: optimizar algo que ya funciona es
una mejora acotada y medible.

En la práctica el orden es: **primero que funcione, después medir dónde duele, y
solo entonces optimizar ese punto concreto**. Optimizar sin medir es adivinar.

---

## Pregunta 3

> Una plataforma consulta constantemente por `timestamp`, pero ocasionalmente
> necesita consultar por `PM2.5`. ¿Qué consecuencias tendría organizar los datos
> pensando principalmente en uno de estos campos?

Un arreglo solo puede estar ordenado por un criterio a la vez. Elegir uno es
renunciar al otro, así que la pregunta real es **qué se optimiza y qué se
sacrifica**.

### Si se organiza por timestamp

- Las consultas frecuentes pasan a `O(log n)`: 20 comparaciones sobre un millón.
- Es el orden **natural** de una red de sensores. Los datos llegan cronológicos,
  así que el orden se mantiene solo con agregar al final. No hay costo de
  reordenamiento continuo.
- Habilita gratis las consultas por rango de fechas, que en una plataforma de
  monitoreo son la operación más común: "PM2.5 de la última semana".
- **Pero** las consultas por PM2.5 quedan en `O(n)`. Como son ocasionales, ese
  costo se paga pocas veces.

Para el perfil descrito, esta es la opción correcta: se optimiza lo frecuente y
se acepta el costo de lo raro, y además coincide con el orden en que los datos
llegan de forma natural.

### Si se organiza por PM2.5

- Se optimiza lo que casi nunca se hace y se castiga lo que se hace todo el
  tiempo.
- Cada lectura nueva rompería el orden y habría que insertarla en su lugar,
  desplazando elementos. En una red de sensores que ingiere continuamente, eso es
  un costo permanente sobre la operación más frecuente del sistema.
- Se pierden las consultas por rango de fechas eficientes.

### Consecuencias más allá del código

- **Sobre la operación de la plataforma:** el orden por timestamp permite
  descartar datos viejos por el extremo del arreglo y consultar ventanas
  temporales. Son operaciones cotidianas de monitoreo ambiental.
- **Sobre el costo de ingesta:** cualquier orden que no sea el cronológico
  convierte la inserción, que es constante, en una operación cara.
- **Sobre la evolución del sistema:** si mañana las consultas por PM2.5 se
  vuelven frecuentes, la salida no es reordenar todo, sino **agregar una
  estructura auxiliar** —un índice secundario— que permita buscar por PM2.5 sin
  alterar el orden principal. Se paga memoria extra y el trabajo de mantener el
  índice sincronizado, y a cambio ambas consultas quedan rápidas.

La idea general: la organización de los datos no es una decisión técnica aislada,
es una **apuesta sobre cómo se van a usar**. Y conviene poder revisarla cuando el
uso cambie.

---

## Pregunta 4

> Un conjunto de datos perfectamente ordenado y alguien modifica registros sin
> conservar el orden. ¿Qué riesgos aparecen si el sistema sigue usando búsqueda
> binaria sin verificar?

El riesgo central es que **el sistema falla sin avisar**. No se cae, no lanza
excepciones, no deja rastro en un log. Simplemente empieza a responder mal.

### Riesgos concretos

**Falsos negativos.** El síntoma que medimos: datos que existen y el sistema
reporta que no. En un contexto de monitoreo ambiental, una lectura crítica de
PM2.5 que "no existe" es una alerta que nunca se dispara.

**Fallo intermitente e irreproducible.** La binaria no falla siempre: depende de
qué mitad descarte y de dónde quedó el dato desordenado. Algunas consultas
aciertan y otras no, sin patrón visible. Es la peor clase de error: el que no se
puede reproducir a voluntad y por lo tanto es dificilísimo de diagnosticar.

**Pérdida de confianza en los datos, no en el código.** Cuando alguien reporta
que "el sistema no encuentra un registro", la sospecha natural cae sobre la
ingesta o sobre el propio dato. Nadie revisa la búsqueda, porque la búsqueda
"está bien probada". El defecto real está en un supuesto que ya no se cumple, y
puede tardar meses en encontrarse.

**Contaminación en cascada.** Si otros módulos —reportes, promedios, alertas—
consumen el resultado de la búsqueda, el error se propaga. Cada consumidor
produce su propia conclusión equivocada y el rastro hacia el origen se pierde.

**Daño acumulado y silencioso.** El sistema lleva funcionando mal desde el
momento de la modificación. Cuando se detecte, hay que auditar hacia atrás todo
lo que se decidió sobre esas respuestas.

### La raíz del problema

La precondición era conocimiento de quien escribió el algoritmo, pero **no era
parte del código**. Vivía en un comentario, y un comentario no se ejecuta ni
impide nada.

### Qué haría para mitigarlo

1. **Volver la precondición explícita.** Una verificación `estaOrdenado()` en
   modo de desarrollo o pruebas: `O(n)`, no siempre aceptable en producción, pero
   valiosa como red de seguridad.
2. **Proteger la invariante en la estructura, no en el usuario.** Si el
   repositorio garantiza el orden en cada inserción y actualización, nadie puede
   romperlo desde afuera. Es preferible hacer imposible el error a documentarlo.
3. **Marcar el orden como sucio.** Una bandera que las operaciones de
   modificación activen y que la búsqueda consulte, cayendo a lineal si el orden
   ya no es confiable. Responde más lento, pero responde bien.
4. **Probar el caso desordenado.** Nuestro experimento 4 es exactamente eso: una
   prueba que detecta la violación de precondición. Debe formar parte de la
   batería de pruebas, no ser una curiosidad de una semana.

La lección general: **un supuesto no verificado es un defecto esperando el
momento**. Mientras se cumpla, nadie lo nota; cuando deje de cumplirse, nadie
sabrá por qué el sistema miente.

---

## Pregunta 5

> "Que funcione no significa que sea una buena solución." Relaciona esta
> afirmación con las semanas 1, 2 y 3. ¿Qué ha cambiado en tu manera de analizar
> una solución?

Cada semana del proyecto agregó una pregunta nueva, y ninguna reemplazó a la
anterior: se fueron acumulando.

### Semana 1 — ¿Los datos son confiables?

El problema no era escribir el lector de CSV, era darse cuenta de que **un
programa que no valida igual produce un resultado**. Un promedio calculado sobre
temperaturas imposibles se ve exactamente igual que uno correcto: es un número,
tiene decimales, se imprime bien. La diferencia es que no significa nada.

Ahí apareció la primera grieta entre "funciona" y "sirve": un programa puede
correr de principio a fin sin errores y estar entregando basura.

### Semana 2 — ¿La estructura sostiene lo que promete?

El `RepositorioLecturas` venía con la advertencia de que "corre sin caerse y
entrega resultados incorrectos". Eso fue lo importante: **no caerse no es
evidencia de nada**. Un `eliminar()` que deja un duplicado fantasma no lanza
excepción; un arreglo con techo fijo no avisa que dejó de aceptar datos.

Ahí entendí que un Tipo Abstracto de Dato es un **contrato**, y que el valor está
en que el contrato se cumpla siempre, no en que el código compile.

### Semana 3 — ¿Cuánto cuesta y bajo qué condiciones vale?

Esta semana la pregunta dejó de ser "¿funciona?" y pasó a ser "¿cuánto cuesta?" y
sobre todo "¿cuándo deja de ser válido?".

Las dos cosas que más me cambiaron la forma de mirar el código:

- **El ciclo infinito.** Un error de un solo carácter —`medio` en vez de
  `medio + 1`— que no es un error de lógica visible sino de una propiedad que hay
  que razonar: *el intervalo debe reducirse en cada vuelta*. Eso no se ve leyendo
  el código rápido, se ve trazándolo.
- **El experimento de PM2.5.** Un algoritmo correcto, rápido, bien escrito, que
  responde mal porque el dato no cumple un supuesto. La calidad de la solución no
  está solo en el algoritmo: está en la relación entre el algoritmo y las
  condiciones en que se usa.

### Lo que cambió en mi forma de analizar

Al empezar, mi criterio era básicamente **"compila y no se cae"**. Hoy, antes de
dar algo por terminado, me pregunto:

1. ¿Es **correcto**, o solamente no falla? (Semana 1)
2. ¿Cumple su **contrato** en todos los casos, incluidos los bordes? (Semana 2)
3. ¿**Cuánto cuesta** y cómo crece ese costo cuando crecen los datos? (Semana 3)
4. ¿Qué **precondiciones** asume y qué pasa el día que dejen de cumplirse?
5. ¿Tengo **evidencia medida**, o solo una intuición de que está bien?
6. ¿Puedo **explicar por qué** elegí esto en lugar de la alternativa?

El cambio más grande es haber pasado de **confiar en que el programa funciona** a
**exigirle que me lo demuestre**. Por eso esta semana el conteo de comparaciones
importó más que el reloj: el reloj dice qué tan rápido fue hoy en esta máquina;
las comparaciones explican cómo se va a comportar cuando los datos se multipliquen
por mil.

Y el aprendizaje que resume todo: **un programa puede estar equivocado sin dar
ninguna señal de estarlo**. Por eso no alcanza con ejecutarlo; hay que probarlo,
medirlo y dejar escrito por qué se decidió así.

---

# Parte B — Reto de comprensión sobre Git y ramas

## Pregunta 1

> ¿Por qué `main` debe mantenerse estable mientras una funcionalidad está en
> desarrollo?

Porque `main` es el **punto de referencia** del proyecto, y una referencia que
cambia constantemente deja de servir como referencia.

Mientras `main` funciona, siempre existe una versión ejecutable a la que volver.
Si algo se rompe en una rama, la comparación es inmediata: funciona allá y no
acá, luego el problema está en mis cambios. Si `main` también está rota, se
pierde esa capacidad de aislar el problema.

Además, `main` es la base desde la que nacen las ramas siguientes. Ramificar
desde una `main` rota propaga el defecto a todas las ramas nuevas, y multiplica
un problema que era uno solo.

Y hay una razón práctica de equipo: `main` es lo que otra persona clona o
entrega. Si no compila, el trabajo de todos queda bloqueado por el estado
intermedio de una sola persona.

## Pregunta 2

> Diferencia conceptual entre `feature/` y `bugfix/`.

La diferencia es **qué le pasa al comportamiento esperado del sistema**.

`feature/` agrega una capacidad que antes no existía. El sistema no estaba mal:
simplemente no hacía eso. Antes de esta semana el proyecto no sabía buscar; no
era un defecto, era un alcance. Por eso la rama se llama
`feature/semana-3-busqueda`.

`bugfix/` corrige algo que ya existía y se comportaba mal. No se agrega alcance:
se cierra la distancia entre lo que el sistema promete y lo que realmente hace.
Comparar `idSensor` con `==` era una promesa incumplida, no una funcionalidad
faltante.

En la práctica la distinción también comunica **urgencia y riesgo**. Un `bugfix`
suele significar que algo está mal *ahora mismo* en la línea estable y compite
por prioridad. Un `feature` es trabajo planificado. Y leer el historial permite
responder preguntas distintas: "¿qué capacidades ganamos?" contra "¿qué venía
fallando?".

## Pregunta 3

> ¿Por qué `feature/semana-3-busqueda` es mejor nombre que `rama3`?

Porque `rama3` solo dice que es la tercera, que es exactamente la información que
Git ya tiene y que a nadie le sirve.

`feature/semana-3-busqueda` responde tres preguntas de un vistazo:

- `feature` → es trabajo nuevo, no una corrección urgente;
- `semana-3` → a qué momento y contexto del proyecto pertenece;
- `busqueda` → qué parte del sistema toca.

El valor se nota con el tiempo. Dentro de dos meses, con ocho ramas en el
repositorio, `rama3` obliga a abrir los commits para recordar qué era. El nombre
descriptivo se entiende sin abrir nada.

También evita colisiones de significado: dos personas pueden crear `rama3` para
cosas distintas, pero `feature/semana-3-busqueda` y `bugfix/error-promedio-pm25`
nunca se confunden. Y el prefijo agrupa: se pueden listar todas las `feature/` o
todas las `bugfix/`.

Un nombre de rama es **comunicación con quien lea el repositorio después**, y ese
alguien suele ser uno mismo, sin recordar nada.

## Pregunta 4

> ¿Qué ventaja tiene desarrollar en una rama antes de fusionar?

La ventaja de fondo es que **separa el lugar donde se experimenta del lugar donde
se garantiza que algo funciona**.

Esta semana quedó muy claro. En la rama pude:

- **Cometer errores a propósito.** Se registraron dos defectos intencionales —la
  comparación con `==` y el ciclo infinito— para poder estudiarlos. Eso es
  material pedagógico valioso en una rama, y contaminación inaceptable en `main`.
- **Guardar estados intermedios.** Hubo commits donde solo existía la búsqueda
  lineal y el resto faltaba. Es trabajo legítimo en curso, pero no es una versión
  entregable.
- **Trabajar sin presión de tiempo.** La rama puede estar rota una hora o un día
  sin afectar a nadie.
- **Descartar sin consecuencias.** Si el enfoque hubiera sido malo, se borra la
  rama y no queda rastro en la línea estable.

Y una ventaja que solo se ve después: el merge queda como **una unidad revisable**
en la historia. En lugar de doce commits sueltos mezclados con todo lo demás, hay
un bloque identificable que dice "esto fue la Semana 3", con su punto de entrada y
su punto de integración. Si hubiera que revertir la semana completa, se puede.

## Pregunta 5

> ¿Por qué es importante probar `main` después del merge?

Porque **un merge sin conflictos no significa un merge correcto**.

Git resuelve texto, no significado. Puede combinar dos cambios sin quejarse y
producir código que no compila o que se comporta mal, simplemente porque cada
cambio era válido por separado pero no juntos. El caso típico: una rama renombra
un método y otra agrega una llamada al nombre viejo, en líneas distintas. Git no
ve conflicto; el compilador sí.

Además, cada rama se probó contra la `main` del momento en que nació. Si `main`
avanzó mientras tanto, la combinación resultante **nunca fue ejecutada por
nadie** hasta después del merge. Es un estado literalmente nuevo.

Y si hubo conflictos resueltos a mano, con más razón: ahí hay una decisión humana
sin verificar.

Probar después del merge es lo que convierte a `main` otra vez en línea base
confiable. Si no se prueba, `main` deja de ser "la versión que funciona" y pasa a
ser "la versión que esperamos que funcione", que es justamente lo que esta forma
de trabajar quiere evitar.
