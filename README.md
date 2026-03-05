# Dashboard Android App

App nativa Android (Kotlin) que muestra el dashboard de tu servidor Python en tiempo real.

## Características
- Gráfica de líneas en tiempo real (auto-refresh configurable)
- Gráfica de barras por categorías
- Tabla de últimos registros con badges de estado
- Pantalla de configuración (URL del servidor + intervalo)
- Compilación automática del APK via GitHub Actions

---

## Cómo obtener el APK (sin instalar Android Studio)

### 1. Sube el proyecto a GitHub
```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/TU_USUARIO/dashboard-android.git
git push -u origin main
```

### 2. GitHub Actions compila automáticamente
En cuanto hagas push, ve a:
`https://github.com/TU_USUARIO/dashboard-android/actions`

Verás el workflow **Build APK** ejecutándose (~3-5 minutos).

### 3. Descarga el APK
Una vez completado el workflow:
- Haz clic en la ejecución
- En la sección **Artifacts** → descarga **Dashboard-APK**
- Extrae el ZIP → obtienes `app-debug.apk`

### 4. Distribuye a tu equipo
Envía el APK por WhatsApp, Telegram, email, Slack, etc.

Para instalarlo en Android:
- Ajustes → Seguridad → **Instalar apps de fuentes desconocidas** ✓
- Abre el APK y pulsa Instalar

---

## Configurar la IP del servidor

Al abrir la app por primera vez:
1. Pulsa el icono ⚙️ (ajustes) en la barra superior
2. Introduce la URL de tu servidor: `http://192.168.X.X:5000`
3. Ajusta el intervalo de refresco (por defecto: 5 segundos)
4. Pulsa **Guardar**

---

## Endpoints del servidor requeridos

Tu servidor Python debe exponer estos endpoints (mismos que el proyecto Flask adjunto):

| Endpoint | Respuesta |
|---|---|
| `GET /api/metrics` | `{ cpu, cpu_delta, mem, mem_delta, net, alerts }` |
| `GET /api/history` | `[ { t, a, b }, ... ]` |
| `GET /api/category` | `[ { name, value }, ... ]` |
| `GET /api/table` | `[ { id, name, value, status }, ... ]` |

---

## Estructura del proyecto

```
dashboard_android/
├── .github/workflows/build.yml   ← GitHub Actions (compilación automática)
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/dashboard/app/
│       │   ├── data/DashboardRepository.kt
│       │   ├── model/Models.kt
│       │   └── ui/
│       │       ├── MainActivity.kt
│       │       ├── DashboardViewModel.kt
│       │       ├── TableAdapter.kt
│       │       └── SettingsActivity.kt
│       └── res/layout, values, drawable, menu...
├── build.gradle
└── settings.gradle
```
