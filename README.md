# Libros API (Spring Boot 3 + Java 17)

API reactiva para gestion de libros con arquitectura por puertos y adaptadores.

## Stack

- Java 17 (records, lambdas, Optional)
- Spring Boot 3.3.x
- Spring WebFlux (`Mono`/`Flux`) para endpoints no bloqueantes
- Spring Data JPA + H2 en memoria
- JUnit 5 + Mockito + AssertJ + StepVerifier

## Endpoints

- `POST /libros` crea un libro
- `GET /libros/{id}` obtiene un libro por id
- `GET /libros` lista todos los libros
- `PATCH /libros/{id}` actualiza parcialmente un libro

## Caracteristicas implementadas

- Uso de `Optional` para validaciones y busquedas seguras
- Uso de `Predicate`, `Consumer` y `Supplier` en la capa de servicio
- Uso de Streams (`map`, `filter`, `toList`) para transformacion de colecciones
- Manejo de errores reactivos con `@RestControllerAdvice`

## Ejecutar

```bash
./gradlew bootRun
```

En Windows PowerShell:

```powershell
.\gradlew.bat bootRun
```

## Pruebas

```bash
./gradlew test
```

En Windows PowerShell:

```powershell
.\gradlew.bat test
```

## Configuracion relevante

Archivo: `src/main/resources/application.yaml`

- H2 en memoria (`jdbc:h2:mem:librosdb`)

