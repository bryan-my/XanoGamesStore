# Xano Games Store

Aplicación Android nativa (Kotlin) que consume un backend de Xano para ofrecer un catálogo de videojuegos, autenticación con tokens, carrito local con checkout en la nube y un panel administrativo con aprobación de pedidos, alta de productos con subida de imágenes y gestión de usuarios.

## Características principales
- Autenticación por correo y contraseña con persistencia del token, recuperación del rol (`admin` o `customer`) y refresco de sesión automático (`LoginActivity`, `SignupActivity`, `SessionPrefs`, `AuthInterceptor`).
- Catálogo y buscador de productos con cuadrícula, shimmer de carga, filtros mediante `SearchView` y navegación al detalle con carrusel de imágenes (`ProductsFragment`, `ProductAdapter`, `ProductDetailFragment` + `ViewPager2`).
- Carrito en memoria controlado por `CartManager`, edición inline de cantidades, subtotal dinámico y proceso de pago que crea pedidos en Xano y descuenta stock vía `CartService`.
- Panel de pedidos con modo cliente y modo administrador: historial personal, detalle con listado de artículos y tablero para aprobar pedidos pendientes (`OrdersFragment`, `OrderDetailFragment`, `AdminOrdersFragment`).
- Formulario de alta/edición de juegos con soporte para múltiples imágenes mediante `ActivityResultContracts.PickMultipleVisualMedia`, subida multipart a `/upload` y asociación de metadatos devueltos por Xano (`AddProductFragment`, `UploadService`, `uriToContentPart`).
- Gestión de usuarios para administradores: creación rápida, edición de datos, bloqueo/desbloqueo y eliminación usando `UserService` y diálogos Material (`UsersFragment`, `UserAdapter`).
- Navegación inferior dinámica: `HomeActivity` obtiene el rol desde `/auth/me`, infla `bottom_customer` o `bottom_admin` y enruta a Perfil, Catálogo, Carrito, Pedidos, Alta de producto y Usuarios según permisos.

## Tecnologías y versiones
- Kotlin 2.0.21, Android Gradle Plugin 8.13.0, Gradle Wrapper configurado en `gradle-wrapper.properties`.
- Configuración Android: `compileSdk 34`, `targetSdk 34`, `minSdk 24`, `Java/Kotlin 17`, `viewBinding` habilitado.
- AndroidX: `appcompat 1.7.0`, `fragment-ktx 1.8.3`, `constraintlayout 2.2.0`, `recyclerview 1.3.2`, `lifecycle-runtime-ktx 2.8.6`, `swiperefreshlayout 1.1.0`.
- UI y utilidades: Material Components 1.12.0, Facebook Shimmer 0.5.0, `SwipeRefreshLayout`, `ViewPager2`, `Snackbar` y `BottomNavigationView`.
- Networking: Retrofit 2.9.0 + Gson converter, OkHttp 4.12.0 con `AuthInterceptor` propio y dependencia del logging-interceptor para diagnósticos, helper `extractHttpError`.
- Asincronía: `kotlinx-coroutines-android 1.9.0`, `lifecycleScope` / `viewLifecycleOwner.lifecycleScope`.
- Imágenes: Glide 4.16.0 con helper `ApiClient.fileUrl` para transformar rutas relativas en URLs absolutas.
- Testing listo para activarse con `junit 4.13.2`, `androidx.test.ext:junit 1.3.0` y Espresso 3.7.0 (ver `gradle/libs.versions.toml`).

## Arquitectura y flujo

**Configuración de red**
- `BuildConfig` expone cuatro constantes (`XANO_ORIGIN`, `XANO_BASE_AUTH`, `XANO_BASE_SHOP`, `XANO_BASE_UPLOAD`) definidas en `app/build.gradle.kts`.
- `ApiClient` centraliza la creación de `Retrofit`/`OkHttp` por dominio (auth, shop, upload) y adjunta `AuthInterceptor`, responsable de propagar `Authorization: Bearer <token>`.
- `UploadService` espera el campo multipart `content` y devuelve `UploadResponse`; `uriToContentPart` abstrae la lectura del `Uri`.
- `extractHttpError` resume errores HTTP e IO para mostrar mensajes amigables en UI.

**Autenticación y sesión**
- `LoginActivity` y `SignupActivity` realizan `auth/login`, `auth/signup` y `auth/me`, guardando `authToken`, `userId` y `userRole` en `SessionPrefs` (SharedPreferences).
- En el arranque se valida el token con `/auth/me`; si sigue vigente se navega directo a `HomeActivity`.
- `ProfileFragment` muestra los datos del usuario autenticado y permite cerrar sesión; `EditProfileFragment` usa `UserService` para actualizar nombre/correo.

**Navegación y vistas**
- `HomeActivity` controla la barra inferior (`BottomNavigationView`), infla menús distintos según rol y conmuta entre `ProfileFragment`, `ProductsFragment`, `CartFragment`, `OrdersFragment`, `AdminOrdersFragment`, `AddProductFragment` y `UsersFragment`.
- El `AppBar` se actualiza en cada selección y el historial de fragments se maneja con `supportFragmentManager`.

**Catálogo y detalle**
- `ProductsFragment` consulta `ProductService.getProducts()`, muestra shimmer mientras carga, admite filtros por nombre/categoría/marca y permite abrir detalle o lanzar acciones (añadir al carrito, editar, eliminar) dependiendo del rol.
- `ProductDetailFragment` pide `product/{id}`, renderiza carrusel (`ImageSliderAdapter` + `ThumbnailAdapter`) y ofrece un `FloatingActionButton` para agregar al carrito (oculto para administradores).

**Carrito y pedidos**
- `CartManager` mantiene en memoria la lista de `CartItem`, calcula el total y expone utilidades (`add`, `decrease`, `remove`, `clear`).
- `CartFragment` refleja el carrito con `CartItemAdapter`, permite limpiar contenido o ejecutar el checkout.
- `pagarCarrito()` crea un `CartDto` remoto (`CartService.createCart`), descuenta stock producto por producto (`updateProductStock`) y limpia el carrito local tras éxito.
- `OrdersFragment` filtra los pedidos del usuario autenticado; `OrderDetailFragment` reconsulta cada producto del pedido y aplica pausas para respetar límites de Xano.
- `AdminOrdersFragment` separa pedidos pendientes/aprobados y usa `updateCart` para aprobarlos.

**Gestión de productos e imágenes**
- `AddProductFragment` funciona tanto para altas como para edición (`AddProductFragment.edit(id)`), permitiendo seleccionar hasta 5 imágenes.
- Si el administrador no elige nuevas imágenes se reutilizan las existentes, gracias a la conversión entre `XanoImage` y `UploadResponse`.
- La subida es sincrónica (una petición por imagen) y cualquier error HTTP se muestra mediante `Snackbar`/`Toast`.

**Panel de administración y usuarios**
- `UsersFragment` se habilita sólo en menú admin; ofrece CRUD completo con diálogos Material y callbacks a `UserService` para crear, editar, bloquear (campo `active`) o eliminar usuarios.
- `Roles.ADMIN` y `Roles.CUSTOMER` viven en `core/Constants.kt` para mantener consistencia entre servidor y cliente.

## Estructura de carpetas
```
XanoGamesStore/
├─ app/
│  ├─ src/main/
│  │  ├─ java/com/miapp/xanogamesstore/
│  │  │  ├─ api/        # Retrofit services, interceptores, helpers multipart
│  │  │  ├─ core/       # Constantes compartidas (Roles, estados)
│  │  │  ├─ model/      # DTOs de auth, productos, carrito, usuarios
│  │  │  └─ ui/         # Activities, fragments y adapters (catalog, cart, orders, admin)
│  │  ├─ res/           # Layouts, menús, drawables, temas y strings
│  │  └─ AndroidManifest.xml
│  └─ build.gradle.kts  # Módulo de aplicación (viewBinding, BuildConfig fields, deps)
├─ gradle/              # catálogos de versiones y wrapper
├─ build.gradle.kts     # configuración raíz
└─ settings.gradle.kts  # declaración del módulo :app
```

## Configuración y ejecución
1. Requisitos: Android Studio (Ladybug o superior), JDK 17, Android SDK 24+, dispositivo o emulador con Google Play Services, backend Xano operativo con los endpoints mostrados abajo.
2. Clona el repositorio y abre la carpeta raíz `XanoGamesStore` en Android Studio.
3. Crea/actualiza `local.properties` con la ruta al SDK (`sdk.dir=...`).
4. Sincroniza Gradle; si necesitas sustituir los endpoints, edita los campos `buildConfigField` en `app/build.gradle.kts` y vuelve a sincronizar.
5. Ejecuta `app` sobre un emulador/dispositivo o usa la línea de comandos: `./gradlew installDebug`.
6. Crea una cuenta nueva o usa credenciales existentes; tras autenticarte podrás navegar según tu rol.

## Endpoints Xano de referencia
- **Auth (`XANO_BASE_AUTH`)**: `auth/login`, `auth/signup`, `auth/me`.
- **E-commerce (`XANO_BASE_SHOP`)**: `product` (GET/POST/PATCH/DELETE), `product/{id}`, `cart`, `cart/{id}`, `cart?user_id=`, `product/{product_id}` para stock, `user`, `user/{id}`.
- **Upload (`XANO_BASE_UPLOAD`)**: `upload` (campo multipart `content`).  
Puedes ajustar estos paths según tu workspace de Xano sin modificar código Kotlin gracias a las constantes de `BuildConfig`.

## Seguridad y depuración
- El token se guarda en `SharedPreferences`; no hay cifrado local ni renovación automática, por lo que conviene rotarlo en el backend cuando sea necesario.
- `AuthInterceptor` es el único interceptor activo; puedes añadir `HttpLoggingInterceptor` en `ApiClient.baseClient()` para ambientes de debug.
- El permiso `READ_MEDIA_IMAGES` (Android 13+) y `READ_EXTERNAL_STORAGE` (<=32) están declarados para permitir la selección de portadas.
- `OrderDetailFragment` y `AdminOrdersFragment` introducen `delay` para no gatillar límites de tasa en Xano; ajusta los valores si tu plan lo permite.

## Diseño y UX
- Temas basados en Material 3 (`Theme.Xano.NoActionBar`), toolbar con acciones contextuales y `BottomNavigationView` persistente.
- Tarjetas y fondos personalizados en `res/drawable/` (por ejemplo `bg_status_approved`/`pending` y estilos para ítems de carrito).
- Shimmer y placeholders mientras llega la respuesta del backend, toasts/Snackbars para feedback inmediato y diálogos para acciones destructivas.
- `ViewPager2` + miniaturas para portadas y `Glide` para todas las cargas remotas.

## Testing y mantenimiento
- Ejecuta pruebas unitarias con `./gradlew testDebugUnitTest` y pruebas instrumentadas con `./gradlew connectedDebugAndroidTest`.
- El `CartManager` hoy vive sólo en memoria; si necesitas persistencia entre sesiones, puedes serializarlo en `SharedPreferences` o Room.
- Si Xano cambia el contrato de imágenes o tokens, actualiza `UploadResponse`, `AuthResponse` y/o el `deserializer` correspondiente.
- Considera agregar estrategias de backoff o `retry` para las operaciones sensibles (`upload`, `createCart`, `updateProductStock`) y mover lógica de negocio a un módulo de dominio si el proyecto crece.

---

para el usuario de cliente se puede crear uno directamente desde la app

Usuario admin de prueba : usuarioadmin@gmail.com
contraseña: usuarioadmin123

