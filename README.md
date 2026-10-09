# Auth Plugin (IntelliJ IDEA)

Плагин для IntelliJ IDEA на Java + Gradle. Добавляет инструментное окно **Auth**
(правая панель) с тремя вкладками:

1. **Авторизация** — поля «Логин» и «Пароль» + кнопка «Войти» (видна всегда).
2. **Сессия** — текущий авторизованный пользователь + кнопка «Выйти»
   (появляется только после успешной авторизации).
3. **Проекты ФТ** — скроллируемый список ФТ1…ФТ10 (высота элемента 30 px),
   по клику показывается уведомление (тоже только после авторизации).

## Структура проекта

```
├── build.gradle                        # конфигурация сборки (IntelliJ Platform Gradle Plugin 2.x)
├── settings.gradle
├── gradlew / gradle/wrapper/           # Gradle wrapper (9.8.0)
└── src/main/
    ├── java/com/example/auth/
    │   ├── AuthToolWindowFactory.java  # окно с двумя вкладками (Swing)
    │   └── AuthState.java              # общее состояние авторизации
    └── resources/META-INF/plugin.xml   # регистрация tool window
```

## Сборка

```bash
./gradlew buildPlugin
```

Готовый zip появится в `build/distributions/auth-idea-plugin-1.0.0.zip`.

Плагин собирается против **локально установленной IDEA** (`/snap/intellij-idea/current`).
Если путь другой — поправьте `local(...)` в `build.gradle`.

## Запуск в песочнице (debug)

```bash
./gradlew runIde
```

Откроется отдельный экземпляр IDE с подключённым плагином: откройте инструментное
окно `Auth` (View → Tool Windows → Auth).

## Установка в IDE

Settings → Plugins → ⚙ → **Install Plugin from Disk…** → выберите zip из
`build/distributions/` → перезапуск IDE.

## Что дальше

- Реальная авторизация: в `AuthToolWindowFactory.createAuthTab()` в месте с
  `TODO` вызовите свой HTTP-API (например, через `java.net.http.HttpClient`).
- Пароль можно хранить безопасно через `com.intellij.credentialstore.PasswordSafe`.
- При необходимости сохранять сессию между перезапусками — `PropertiesComponent`.
