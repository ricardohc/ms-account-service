# Skill — ms-account-service

> Contrato: `ms-account-service-openapi.yaml` (fuente de verdad).
> Reglas transversales: ver `common-rules.md`.
> **Reactividad**: Spring WebFlux + Reactor (`Mono`/`Flux`). No usa RxJava.

## Responsabilidad
- Cuentas `SAVINGS`, `CHECKING`, `FIXED_TERM`.
- Perfiles `STANDARD`, `VIP`, `PYME`.
- Estados `ACTIVE`, `INACTIVE`, `BLOCKED`.
- Titulares y firmantes (BUSINESS).
- Saldos y movimientos de cuenta.

## Endpoints
| Método | Ruta | operationId |
|---|---|---|
| GET | `/api/v1/accounts` | `findAllAccounts` |
| POST | `/api/v1/accounts` | `createAccount` |
| GET | `/api/v1/accounts/{accountId}` | `findAccountById` |
| PATCH | `/api/v1/accounts/{accountId}` | `updateAccount` |
| DELETE | `/api/v1/accounts/{accountId}` | `deactivateAccount` |
| GET | `/api/v1/accounts/{accountId}/movements` | `findAccountMovements` |
| GET | `/api/v1/accounts/{accountId}/balance` | `findAccountBalance` |
| POST | `/api/v1/accounts/transfers` | `createTransfer` |

## Headers
- GETs: `X-Correlation-ID`, `X-Channel-ID`.
- POST, PATCH, DELETE: `X-Transaction-ID` (UUID).

## Persistencia
- Base: `banking_account`.
- Colecciones: `accounts`, `movements`.
- Escritura directa a MongoDB propia vía `ReactiveMongoRepository`.
- `accountNumber` único.

## Reglas de negocio
- PERSONAL: máximo una SAVINGS y una CHECKING.
- **FIXED_TERM (decisión cerrada)**: múltiples cuentas FIXED_TERM sin límite definido en el PDF. Solo permite **un movimiento de depósito o retiro** en el día específico del mes. Segundo movimiento el mismo día → 422.
- BUSINESS: no puede tener SAVINGS ni FIXED_TERM. Sí múltiples CHECKING.
- BUSINESS admite uno o más titulares y cero o más firmantes.
- Monto mínimo de apertura configurable (puede ser 0).

### Comisiones configurables
- **SAVINGS**: sin comisión de mantenimiento. Límite máximo de movimientos mensuales configurable. Superado el límite → se cobra comisión por movimiento excedente. Valor configurable.
- **CHECKING**: con comisión de mantenimiento configurable. Sin límite de movimientos.
- **Todas las cuentas**: número máximo configurable de depósitos y retiros sin comisión por transacción. Cada operación excedente paga comisión. Distinguir de la comisión de mantenimiento.
- **Ningún valor de comisión se hardcodea**: todos se leen de configuración externa.
- Registrar cada movimiento con `DEPOSIT`, `WITHDRAWAL`, `TRANSFER_IN`, `TRANSFER_OUT` y `balanceAfter`.

### Perfiles VIP / PYME
- **VIP (PERSONAL)**: cuenta de ahorro con promedio diario mínimo mensual configurable. Requiere tarjeta de crédito del banco al abrir la cuenta. Verificación vía `ms-card-service` (Parte II, API) o vía evento (Parte III). No se lee la base de Card.
- **PYME (BUSINESS)**: cuenta corriente sin comisión de mantenimiento. Requiere tarjeta de crédito del banco al abrir la cuenta.
- La asignación del **perfil** al cliente la hace `ms-customer-service`; `ms-account-service` valida el **requisito de producto** al crear la cuenta.

### Baja lógica
- `DELETE` es baja lógica: no permite borrar si hay saldo u obligaciones → 409.

### Transferencias
- `POST /transfers` es **fachada**: delega en `ms-transaction-service` y responde 202. **No** mueve saldo directamente.
- Reglas de validación de la fachada: cuenta origen ACTIVE y con saldo suficiente; cuenta destino existente y ACTIVE; `sourceAccountId != targetAccountId`; origen BLOCKED/INACTIVE → 409.

## Eventos (Parte III)
- Tópico: `banking.account.events.v1`.
- Productor: `ms-account-service`.
- Eventos: `AccountCreated`, `AccountUpdated`, `AccountDeactivated`.
- Evento adicional para reportes: `AccountMovementRegistered` (propuesta; formalizar en catálogo Kafka antes de implementar).

## Pruebas mínimas
- Crear SAVINGS, CHECKING, FIXED_TERM válidos → 201.
- PERSONAL con 2 SAVINGS → 409.
- BUSINESS con SAVINGS → 409.
- FIXED_TERM con dos retiros el mismo día → 422.
- Superar cupo gratuito de movimientos → comisión aplicada.
- VIP sin tarjeta de crédito → 422.
- PYME sin tarjeta de crédito → 422.
- DELETE con saldo → 409.
- `POST /transfers` responde 202 y no mueve saldo localmente.
- Controladores reactivos con Reactor.

## Criterios de aceptación
1. Endpoints y DTOs idénticos al YAML.
2. Persistencia solo en `banking_account`.
3. Movimientos y saldo consistentes con actualización atómica.
4. Transferencias delegadas, sin escrituras cruzadas a otras bases.
5. Comisiones configurables y diferenciadas.
6. Eventos de movimiento formalizados antes de publicarse.
7. Máximo de FIXED_TERM: sin límite impuesto.