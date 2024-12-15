![](/static/Aspose.Words.a46354b6-fcbc-4498-b260-ca2c234afca9.001.jpeg)

**INGENIERÍA EN TECNOLOGÍAS DE LA INFORMACIÓN**

**“Arquitectura de Software”**

**Procesador de Pagos**

ING. HENRY RAMIRO CORAL <a name="_int_w35z2yfd"></a>CORAL 

**NRC:** 3897

**Integrantes:**

- Karla Criollo
- Emilio Ñacato
- Andrés Ortiz
- Anthony Quishpe

` `**Periodo Académico**

S-I NOVIEMBRE 24 – MAYO 25




# Índice
[Procesador de Pagos	2](#_toc1112495252)

[Requisitos funcionales	2](#_toc744626550)

[1. Recepción de Transacciones con Información Encriptada	2](#_toc1431548182)

[2. Registro y Actualización del Estado de las Transacciones	2](#_toc1332021340)

[3. Gestión de Tarifas y Comisiones	3](#_toc1603560538)

[4. Claves de Conexión con Bancos y Marcas	3](#_toc1626717001)

[5. Manejo de Fraude	3](#_toc1659679531)

[6. Registro de Conexiones con Bancos y Marcas	4](#_toc1720108136)

[Requisitos No Funcionales	4](#_toc839382365)

[Épicas	5](#_toc2099076550)

[Épica 1: Seguridad y Cumplimiento Normativo	5](#_toc556240461)

[Épica 2: Prevención y Gestión de Fraudes	5](#_toc1742206974)

[Épica 3: Rendimiento y Escalabilidad	5](#_toc340126974)

[Épica 4: Gestión Financiera	5](#_toc963716686)

[Épica 5: Cumplimiento Regulatorio	6](#_toc1257153761)

[Dominios	6](#_toc228894691)

[Componentes Lógicos	7](#_toc279521635)

[Asignación de componentes	9](#_toc1054121192)

[Flujo Procesador de Pagos	9](#_toc1360180899)

#
#
#
#
#
#




# <a name="_toc183176444"></a><a name="_toc183641569"></a><a name="_toc1112495252"></a>**Procesador de Pagos**
## <a name="_toc183176445"></a><a name="_toc183641570"></a><a name="_toc744626550"></a>**Requisitos funcionales**
### <a name="_toc1431548182"></a>**1. Recepción de Transacciones con Información Encriptada**
**Requisito:**

- Toda la información de la tarjeta, incluyendo datos del titular (CardHolder), debe estar encriptada al ser recibida.
- **Explicación:**
- Para cumplir con los estándares de seguridad como PCI DSS (Payment Card Industry Data Security Standard), es esencial que los datos sensibles de las tarjetas de crédito se transmitan y almacenen de forma segura.
- Se recomienda utilizar técnicas como **encriptación AES-256** para los datos en tránsito y almacenamiento.
### <a name="_toc1332021340"></a>**2. Registro y Actualización del Estado de las Transacciones**
**Requisito:**

- Cada transacción debe ser registrada inicialmente y su estado actualizado conforme avance en el flujo (pendiente, aprobada, rechazada).

**Explicación:**

- Un historial detallado asegura trazabilidad y auditoría.
- Estados clave:
  - **Pendiente:** La transacción fue recibida pero no procesada.
  - **Aprobada:** Autorizada por el banco.
  - **Rechazada:** No aprobada por fondos insuficientes, límites o motivos de seguridad.
  - **En revisión:** Bajo análisis de fraude.
- Utilizar bases de datos relacionales o no relacionales para gestionar este historial, con timestamps claros para cada cambio.
### <a name="_toc1603560538"></a>**3. Gestión de Tarifas y Comisiones**
**Requisito:**

- Mantener un registro actualizado de las tarifas y comisiones aplicadas por el procesador de pagos.

**Explicación:**

- Estas tarifas pueden variar según el banco, la marca (Visa, Mastercard, etc.), el tipo de tarjeta (crédito o débito)
- Crear una tabla dinámica en la base de datos con reglas claras para calcular las tarifas según parámetros.
### <a name="_toc1626717001"></a>**4. Claves de Conexión con Bancos y Marcas**
**Requisito:**

- Mantener claves de autenticación seguras para las conexiones con las redes bancarias y marcas de tarjetas.

**Explicación:**

- Estas claves, generalmente API keys o certificados digitales, deben almacenarse de manera segura.
- Renovar periódicamente las claves para evitar riesgos de vulneración.
- Implementar protocolos seguros como **TLS 1.3** para la comunicación.
### <a name="_toc1659679531"></a>**5. Manejo de Fraude**
#### **Sub-requisitos:**
1. **Límite de transacciones por día y por hora:**
   1. Implementar un sistema que limite las transacciones por tarjeta a un máximo definido por día y hora para prevenir abusos.
   1. Ejemplo: No más de 5 transacciones en una hora o 20 al día.
1. **Límite de monto por día:**
   1. Establecer un monto máximo diario permitido por tarjeta.
   1. Ejemplo: No más de $1,000 por día.
1. **Restricción geográfica por tiempo:**
   1. Bloquear transacciones realizadas con la misma tarjeta desde países diferentes si no ha transcurrido un tiempo razonable (mínimo 3 horas).
   1. Requiere integración con sistemas de geolocalización (IP o ubicación del dispositivo).

**Explicación:**

- Estas reglas ayudan a mitigar riesgos de fraude, asegurando que el comportamiento transaccional sea consistente con el uso legítimo de la tarjeta.
### <a name="_toc1720108136"></a>**6. Registro de Conexiones con Bancos y Marcas**
**Requisito:**

- Mantener un registro (log) detallado de todas las conexiones realizadas con bancos y marcas de tarjetas.

**Explicación:**

- Cada conexión debe registrar:
  - Fecha y hora.
  - Tipo de solicitud (autorización, reversión, consulta de estado).
  - Identificador único de transacción.
  - Respuesta recibida.
- Estos logs son esenciales para auditorías y resolución de conflictos.
## <a name="_toc183176446"></a><a name="_toc183641571"></a><a name="_toc839382365"></a>**Requisitos No Funcionales**
1. #### **Modularidad**
   Un procesador de pagos debe estar compuesto por componentes modulares para facilitar la integración con otros sistemas (Post / Gateway, Marca y Core Bancario). Esto permite actualizaciones y cambios sin afectar a todo el sistema. Cada componente del procesador (como validación, autorización) debe estar claramente separado y ser independiente para facilitar su mantenimiento y evolución.
1. #### **Seguridad**
   La seguridad es crucial para un procesador de pagos, ya que maneja datos financieros sensibles. Es necesario cifrar la información, garantizar comunicaciones seguras y aplicar medidas de autenticación robustas.
1. #### **Disponibilidad**
   Un procesador de pagos debe estar disponible las 24 horas del día, los 7 días de la semana, para que los comercios y clientes puedan realizar transacciones en cualquier momento. El sistema debe garantizar un tiempo de actividad muy alto (idealmente 99.99% o más), con medidas de recuperación ante fallas.
1. #### **Escalabilidad**
   A medida que el volumen de transacciones crece, el procesador de pagos debe ser capaz de manejar un mayor número de solicitudes sin perder rendimiento.
1. #### **Capacidad de Recuperación**
   En caso de un fallo o desastre, el procesador de pagos debe ser capaz de recuperar rápidamente la operatividad, garantizando la continuidad de las transacciones y la seguridad de los datos. El sistema debe tener mecanismos de respaldo y recuperación ante desastres
## <a name="_toc1923442756"></a><a name="_toc183176447"></a><a name="_toc183641572"></a><a name="_toc2099076550"></a>**Épicas**
### <a name="_toc183176448"></a><a name="_toc183641573"></a><a name="_toc556240461"></a>**Épica 1: Seguridad y Cumplimiento Normativo**
Esta épica abarca todos los aspectos relacionados con la protección de datos sensibles, la seguridad en las comunicaciones y la integridad del sistema.

- **Requerimientos:**
  - Asegurar la recepción de información encriptada desde el Payment Gateway utilizando protocolos seguros.	
  - Comunicación segura con redes de tarjetas.
  - Usar sistemas de tokenización.
  - Congelar las cuentas en caso de actividad sospechosa
### <a name="_toc183176449"></a><a name="_toc183641574"></a><a name="_toc1742206974"></a>**Épica 2: Prevención y Gestión de Fraudes**
<a name="_toc183176450"></a>Se enfoca en identificar, prevenir y responder a posibles actividades fraudulentas dentro del sistema.

- **Requerimientos:**
1. Realizar la prevención y monitoreo de fraudes.
### <a name="_toc183641575"></a><a name="_toc340126974"></a>**Épica 3: Rendimiento y Escalabilidad**
Se centra en garantizar que el sistema pueda manejar altos volúmenes de transacciones y usuarios simultáneamente, manteniendo un rendimiento óptimo.

- **Requerimientos**:
  - <a name="_toc183176451"></a>Manejo de alta concurrencia para soportar múltiples transacciones simultáneamente.
### <a name="_toc183641576"></a><a name="_toc963716686"></a>**Épica 4: Gestión Financiera**
Aborda la capacidad del sistema para realizar cálculos precisos y automatizados de las comisiones asociadas con las transacciones.

- **Requerimientos**:
  - <a name="_toc183176452"></a>Implementar el cálculo y cobro de comisiones.Épica 5: Monitoreo y Prevención de Riesgos
### <a name="_toc183641577"></a><a name="_toc1257153761"></a>**Épica 5: Cumplimiento Regulatorio**
Garantiza que el sistema opere dentro del marco legal y regulatorio del país o región donde está implementado.

- **Requerimientos**:
  - Cumplir con las normas y estándares del país.
## <a name="_toc228894691"></a>**Dominios**
1. **Dominio de comisiones**

   ![](/static/Aspose.Words.a46354b6-fcbc-4498-b260-ca2c234afca9.002.png)

1. **Dominio de transacciones**

   ![](/static/Aspose.Words.a46354b6-fcbc-4498-b260-ca2c234afca9.003.png)




1. **Dominio de fraude**

   ![](/static/Aspose.Words.a46354b6-fcbc-4498-b260-ca2c234afca9.004.png)

1. **Dominio de conexiones y registro**

![](/static/Aspose.Words.a46354b6-fcbc-4498-b260-ca2c234afca9.005.png)
## <a name="_toc183176453"></a><a name="_toc183641578"></a><a name="_toc279521635"></a>**Componentes Lógicos**

|**Componente**|**Funcionalidad**|**Conexión con Servicios/Componentes**|
| :- | :- | :- |
|**Recepción y Encriptación de Datos**|- Encriptar la información sensible de las transacciones (tarjetas, datos del titular) al momento de la recepción.|- Base de datos para almacenar datos encriptados.<br>` `- Servicios de comunicación segura con bancos y marcas.|
||- Garantizar el cumplimiento de estándares PCI DSS.|- Componentes de conexión con bancos y redes para validar protocolos seguros.|
|**Gestión de Transacciones**|- Registrar todas las transacciones con su estado inicial (pendiente).|- Base de datos de transacciones (TRANSACCION).|
||- Actualizar el estado de las transacciones (aprobada, rechazada, en revisión).|- Tablas de historial de transacciones (HISTORIAL\_ESTADO\_TRANSACCION).|
||- Generar un historial detallado para auditorías.|- Servicios de monitoreo y auditoría.|
|**Gestión de Tarifas y Comisiones**|- Mantener un registro actualizado de las tarifas y comisiones por tipo de tarjeta y banco.|- Base de datos de comisiones (COMISION, COMISION\_SEGMENTO).|
||- Calcular tarifas dinámicas según los parámetros configurados.|- Servicios de integración con bancos y marcas para obtener reglas específicas.|
|**Conexión con Bancos y Redes**|- Establecer conexiones seguras con bancos y redes de tarjetas mediante claves de autenticación.|- Tablas de claves de seguridad (SEGURIDAD\_BANCO, SEGURIDAD\_MARCA).|
||- Ejecutar operaciones como autorización, reversión y consulta de estado de transacciones.|- Componentes de gestión de transacciones.|
||- Registrar logs de conexiones (operación realizada, resultado, timestamp).|- Base de datos de logs (LOG\_CONEXION).|
|**Prevención de Fraude**|- Implementar límites de transacciones por hora, día o monto máximo permitido.|- Base de datos de reglas de fraude (REGLA\_FRAUDE).|
||- Detectar inconsistencias geográficas en transacciones (por IP o ubicación del dispositivo).|- Servicios de geolocalización e integración con monitoreo de fraude.|
||- Registrar incidentes sospechosos y generar alertas.|- Componentes de monitoreo de fraude (MONITOREO\_FRAUDE).|
|**Monitoreo y Registro**|- Mantener un log detallado de todas las actividades del sistema (transacciones, conexiones, cambios).|- Base de datos de logs (LOG\_CONEXION).|
||- Proveer información para auditorías y resolución de conflictos.|- Servicios de análisis y generación de reportes.|

## <a name="_toc183176454"></a><a name="_toc183641579"></a><a name="_toc1054121192"></a>**Asignación de componentes**

|**Componente Lógico**|**Requisitos Funcionales Asociados**|
| :- | :- |
|**Recepción y Encriptación de Datos**|- Requisito 1: Recepción de Transacciones con Información Encriptada.<br>` `- Garantizar que la información de las tarjetas esté encriptada en tránsito y almacenamiento usando estándares como AES-256.|
|**Gestión de Transacciones**|- Requisito 2: Registro y Actualización del Estado de las Transacciones.<br>` `- Registrar transacciones y actualizar sus estados (pendiente, aprobada, rechazada, en revisión).<br>` `- Mantener trazabilidad histórica.|
|**Gestión de Tarifas y Comisiones**|- Requisito 3: Gestión de Tarifas y Comisiones.<br>` `- Registrar, actualizar y calcular dinámicamente tarifas según parámetros como banco, marca y tipo de tarjeta.|
|**Conexión con Bancos y Redes**|- Requisito 4: Claves de Conexión con Bancos y Marcas.<br>` `- Mantener claves de autenticación seguras para bancos y redes de tarjetas.<br>` `- Establecer conexiones seguras mediante protocolos como TLS 1.3.|
|**Prevención de Fraude**|- Requisito 5: Manejo de Fraude.<br>` `- Implementar límites de transacciones por tarjeta (número y monto).<br>` `- Restricción geográfica por tiempo.<br>` `- Detectar patrones inconsistentes y bloquear transacciones.|
|**Monitoreo y Registro**|- Requisito 6: Registro de Conexiones con Bancos y Marcas.<br>` `- Mantener logs detallados de conexiones y actividades del sistema.<br>` `- Registrar resultados de transacciones y eventos para auditorías.|

## <a name="_toc183641580"></a><a name="_toc1360180899"></a>**Flujo Procesador de Pagos**
![](/static/Aspose.Words.a46354b6-fcbc-4498-b260-ca2c234afca9.006.png)

