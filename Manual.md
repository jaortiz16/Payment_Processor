# Manual de Uso - API Procesador de Pagos

## Descripción General
Este sistema es un procesador de pagos que gestiona transacciones, comisiones, seguridad y monitoreo de fraude. A continuación, se detallan todos los endpoints disponibles por módulo.

## Módulo de Bancos
### Endpoints de Bancos
```http
GET /api/v1/bancos/activos
```
Obtiene la lista de todos los bancos activos en el sistema.

```http
GET /api/v1/bancos/buscar?razonSocial={razonSocial}&estado={estado}
```
Busca bancos por razón social y estado.
- `razonSocial`: Nombre o parte del nombre de la razón social
- `estado`: Estado del banco (ACT=Activo, INA=Inactivo)

```http
GET /api/v1/bancos/{id}
```
Obtiene un banco específico por su ID.

```http
GET /api/v1/bancos/ruc/{ruc}
```
Obtiene un banco por su número de RUC.

```http
POST /api/v1/bancos
```
Crea un nuevo banco.
```json
{
    "codigoInterno": "BANCO001",
    "ruc": "1234567890001",
    "razonSocial": "Banco Ejemplo S.A.",
    "nombreComercial": "Banco Ejemplo"
}
```

```http
PUT /api/v1/bancos/{id}
```
Actualiza la información de un banco existente.

```http
DELETE /api/v1/bancos/{id}
```
Inactiva un banco existente.

## Módulo de Comisiones
### Endpoints de Comisiones
```http
GET /api/v1/comisiones/tipo/{tipo}
```
Obtiene comisiones por tipo.
- `tipo`: POR (Porcentaje) o FIJ (Fijo)

```http
GET /api/v1/comisiones/monto?montoMinimo={min}&montoMaximo={max}
```
Busca comisiones por rango de monto base.

```http
GET /api/v1/comisiones/{id}
```
Obtiene una comisión específica por su ID.

```http
POST /api/v1/comisiones
```
Crea una nueva comisión.
```json
{
    "tipo": "POR",
    "montoBase": 2.5,
    "transaccionesBase": 100,
    "manejaSegmentos": true
}
```

```http
POST /api/v1/comisiones/{id}/segmentos
```
Agrega un segmento a una comisión existente.
```json
{
    "transaccionesHasta": 500,
    "monto": 1.8
}
```

```http
GET /api/v1/comisiones/{id}/calcular?numeroTransacciones={num}&montoTransaccion={monto}
```
Calcula la comisión para una transacción específica.

## Módulo de Transacciones
### Endpoints de Transacciones
```http
GET /api/v1/transacciones?estado={estado}&fechaInicio={inicio}&fechaFin={fin}
```
Obtiene transacciones por estado y rango de fechas.
- `estado`: PEN (Pendiente), APR (Aprobada), REC (Rechazada), REV (En Revisión)

```http
GET /api/v1/transacciones/{id}
```
Obtiene una transacción específica por su ID.

```http
POST /api/v1/transacciones
```
Crea una nueva transacción.
```json
{
    "banco": {"codigo": 1},
    "monto": 1000.00,
    "codigoMoneda": "USD",
    "marca": "VISA",
    "numeroTarjeta": "4111111111111111",
    "fechaExpiracionTarjeta": "12/25",
    "cvv": "123",
    "nombreTarjeta": "USUARIO EJEMPLO",
    "direccionTarjeta": "Dirección de ejemplo 123"
}
```

```http
PUT /api/v1/transacciones/{id}/estado
```
Actualiza el estado de una transacción.
```json
{
    "estado": "APR",
    "detalle": "Transacción aprobada por el banco"
}
```

## Módulo de Seguridad
### Endpoints de Seguridad
```http
POST /api/v1/seguridad/bancos
```
Registra credenciales de seguridad para un banco.

```http
PUT /api/v1/seguridad/marcas/{marca}
```
Actualiza credenciales de una marca de tarjeta.

```http
POST /api/v1/seguridad/logs
```
Registra un log de conexión.
```json
{
    "marca": "VISA",
    "codBanco": 1,
    "ipOrigen": "192.168.1.1",
    "operacion": "CONSULTA_SALDO",
    "resultado": "OK"
}
```

## Módulo de Monitoreo de Fraude
### Endpoints de Monitoreo
```http
POST /api/v1/reglas-fraude
```
Crea una nueva regla de fraude.
```json
{
    "nombreRegla": "Límite diario",
    "limiteTransacciones": 10,
    "periodoTiempo": "DIA",
    "limiteMontoTotal": 5000.00
}
```

```http
GET /api/v1/monitoreo-fraude/alertas
```
Obtiene las alertas de fraude detectadas.

## Notas Importantes
1. Todos los montos deben ser enviados con máximo 2 decimales
2. Las fechas deben estar en formato ISO-8601
3. Los estados válidos son:
   - Transacciones: PEN, APR, REC, REV
   - Bancos: ACT, INA
4. Los tipos de comisión válidos son:
   - POR: Porcentaje
   - FIJ: Monto Fijo
5. Los períodos de tiempo para reglas de fraude son:
   - HOR: Por hora
   - DIA: Por día
   - SEM: Por semana

## Manejo de Errores
La API retorna los siguientes códigos HTTP:
- 200: Operación exitosa
- 400: Error en la solicitud (datos inválidos)
- 401: No autorizado
- 404: Recurso no encontrado
- 500: Error interno del servidor

Cada respuesta de error incluye un mensaje descriptivo del problema. 