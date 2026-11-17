
# AquaControl — Backend API REST

**Proyecto académico | Duoc UC | Desarrollo de Aplicaciones Móviles — EA3**

Backend de AquaControl, un sistema de monitoreo y gestión de líneas de bebederos en instalaciones avícolas.

El proyecto proporciona una API REST desarrollada con **Java 21 y Spring Boot 4.1.2**, utilizando PostgreSQL para persistencia, JWT RS256 para autenticación, Swagger/OpenAPI para documentación y JaCoCo para cobertura de pruebas.

## 1. Objetivo

Implementar un servicio backend centralizado que permita gestionar la información operativa de AquaControl y conectar la aplicación Android mediante Retrofit.

El sistema contempla:

- Gestión de granjas, galpones y líneas de bebederos.
- Registro y consulta de temperaturas.
- Registro de eventos de flushing.
- Gestión de fotografías y alertas.
- Usuarios, autenticación y autorización.
- Consulta de información meteorológica.
- Documentación de servicios REST.
- Pruebas automatizadas y medición de cobertura.

## 2. Tecnologías

| Tecnología | Función |
|---|---|
| Java 21 | Lenguaje de programación |
| Spring Boot 4.1.2 | Framework backend |
| Spring Web MVC | API REST |
| Spring Data JPA | Persistencia |
| PostgreSQL | Base de datos relacional |
| Spring Security | Seguridad y autorización |
| JWT RS256 | Autenticación basada en tokens |
| Bean Validation | Validación de datos |
| Swagger / OpenAPI | Documentación de endpoints |
| Spring HATEOAS | Enlaces de navegación REST |
| Maven | Compilación y dependencias |
| JUnit | Pruebas automatizadas |
| JaCoCo | Cobertura de código |
| Postman | Pruebas de API |

## 3. Arquitectura

El backend utiliza una arquitectura modular basada en responsabilidades.

```text
Aplicación Android
  Kotlin + Retrofit + Room
           |
           | HTTP / JSON
           v
     Spring Security
        JWT RS256
           |
           v
       Controller
           |
           v
        Service
           |
           v
       Repository
           |
           v
       PostgreSQL
```

### Responsabilidades

**Controller:** recibe solicitudes HTTP y devuelve respuestas REST.

**Service:** implementa las reglas de negocio.

**Repository:** gestiona el acceso a datos mediante Spring Data JPA.

**Entity:** representa las entidades persistentes.

**DTO:** transporta datos entre la API y sus consumidores.

**Security:** gestiona autenticación y autorización.

## 4. Módulos principales

| Módulo | Responsabilidad |
|---|---|
| Auth | Autenticación y JWT |
| Usuario | Gestión de usuarios |
| Granja | Administración de granjas |
| Galpón | Administración de galpones |
| Línea | Gestión de líneas de bebederos |
| Temperatura | Registro y consulta de mediciones |
| Flushing | Gestión de eventos de limpieza |
| Foto | Gestión de fotografías |
| Alerta | Gestión de alertas |
| Meteorología | Consulta de información meteorológica |

Paquete principal:

```text
com.example.aquacontrol_backend
```

## 5. Requisitos

- JDK 21.
- PostgreSQL instalado y operativo.
- Git.
- Conexión a Internet para descargar dependencias Maven.
- Postman, opcional para pruebas manuales.

## 6. Instalación

Clonar el repositorio:

```bash
git clone https://github.com/rimsfar73/aquacontrol-backend.git
cd aquacontrol-backend
```

Verificar Java:

```bash
java -version
```

Compilar el proyecto desde Git Bash en Windows:

```bash
./mvnw.cmd -U clean compile
```

Ejecutar las pruebas:

```bash
./mvnw.cmd clean verify
```

Iniciar el servidor:

```bash
./mvnw.cmd spring-boot:run
```

En PowerShell, utilizar `.\mvnw.cmd` en lugar de `./mvnw.cmd`.

## 7. Base de datos

La aplicación utiliza PostgreSQL mediante Spring Data JPA.

La conexión se configura en:

```text
src/main/resources/application.properties
```

Antes de ejecutar, verificar que la base de datos exista y que las variables de entorno requeridas estén definidas.

**Seguridad:** no subir contraseñas, archivos `.env`, claves privadas JWT ni credenciales reales al repositorio.

## 8. Autenticación

La API incorpora autenticación mediante JWT firmado con RS256.

Flujo general:

1. El usuario envía sus credenciales.
2. El backend valida la autenticación.
3. Se genera un token JWT.
4. El cliente utiliza el token para acceder a rutas protegidas.
5. Spring Security valida los permisos correspondientes.

Formato de autorización:

```http
Authorization: Bearer <TOKEN_JWT>
```

Los permisos dependen de la configuración de seguridad del servidor.

## 9. Documentación y pruebas REST

El backend incorpora Swagger/OpenAPI para documentar los servicios.

Con el servidor iniciado, consultar Swagger UI en la ruta configurada por Springdoc, habitualmente:

```text
http://localhost:8080/swagger-ui/index.html
```

Para pruebas con Postman:

1. Importar la colección AquaControl API.
2. Crear o importar el entorno AquaControl Local.
3. Configurar `baseUrl` como `http://localhost:8080`.
4. Obtener un token mediante autenticación.
5. Guardarlo en la variable `token` del entorno.
6. Ejecutar las solicitudes disponibles.

Los códigos HTTP permiten verificar resultados:

| Código | Significado |
|---|---|
| 200 | Operación exitosa |
| 201 | Recurso creado |
| 400 | Solicitud inválida |
| 401 | Autenticación requerida o inválida |
| 403 | Acceso no autorizado |
| 404 | Recurso no encontrado |
| 500 | Error interno del servidor |

## 10. HATEOAS

El backend incorpora enlaces HATEOAS en recursos compatibles.

Estos enlaces permiten descubrir operaciones relacionadas desde una respuesta REST.

La aplicación Android puede continuar utilizando sus rutas Retrofit actuales sin necesidad de interpretar automáticamente los enlaces.

## 11. Pruebas automatizadas

Ejecutar:

```bash
./mvnw.cmd clean verify
```

### Resultados registrados — 16/11/2026

| Indicador | Resultado |
|---|---|
| Pruebas aprobadas | 111 |
| Pruebas fallidas | 0 |
| Cobertura de instrucciones | 82 % |
| Cobertura de ramas | 67 % |
| Líneas cubiertas | 657 / 804 |

Estos resultados corresponden a una ejecución documentada y pueden cambiar con nuevas modificaciones.

### Reporte JaCoCo

Después de ejecutar Maven, abrir:

```text
target/site/jacoco/index.html
```

La cobertura de instrucciones supera el umbral de referencia del 80 %. La cobertura de ramas todavía requiere mejoras.

## 12. Integración Android

Repositorio frontend:

https://github.com/rimsfar73/AquaControl

La aplicación Android utiliza:

- Kotlin.
- Jetpack Compose.
- MVVM.
- StateFlow.
- Retrofit.
- Room Database.
- Navigation Compose.

Para acceder al backend local desde el emulador Android se puede utilizar:

```text
http://10.0.2.2:8080/
```

La dirección `10.0.2.2` representa el equipo anfitrión desde el emulador Android estándar.

Room proporciona almacenamiento local; PostgreSQL proporciona persistencia remota mediante la API.

La sincronización entre ambas fuentes debe verificarse mediante pruebas de integración.

## 13. Estado y mejoras pendientes

El backend dispone de una implementación modular, pruebas automatizadas y evidencia de operaciones REST.

Aspectos que requieren seguimiento:

- Incrementar la cobertura de ramas.
- Ampliar pruebas de controladores.
- Revisar respuestas HTTP 500 observadas.
- Validar permisos y escenarios 401/403.
- Comprobar la integración Android–API–PostgreSQL.
- Verificar el comportamiento de persistencia local y remota.

## 14. Equipo

**Rimsky Farías Soto**  
**Christian Quiroz Roa**

Duoc UC — Sede Melipilla  
Desarrollo de Aplicaciones Móviles — EA3

## 15. Repositorios

**Backend:**  
https://github.com/rimsfar73/aquacontrol-backend

**Frontend Android:**  
https://github.com/rimsfar73/AquaControl

---

**AquaControl — Proyecto académico de desarrollo de software.**
