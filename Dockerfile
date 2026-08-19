# Этап 1: Сборка приложения
FROM amazoncorretto:25.0.3 AS builder
WORKDIR /build

# Устанавливаем xargs для gradlew
RUN dnf install -y findutils && dnf clean all

# Копируем АБСОЛЮТНО ВСЕ файлы проекта сразу
# Это необходимо, чтобы Gradle увидел папки buildSrc или gradle/plugins
COPY . .

# Даем права на запуск скрипта сборки
RUN chmod +x gradlew

# Запускаем полную сборку (скачивание зависимостей, npmInstall и упаковку jar)
RUN ./gradlew --no-daemon clean bootJar

# Этап 2: Финальный легковесный образ для запуска
FROM amazoncorretto:25.0.3-alpine
WORKDIR /app

# Копируем готовый jar-файл из этапа сборки
COPY --from=builder /build/build/libs/football-manager-0.0.1-SNAPSHOT.jar app.jar

# Распаковываем содержимое Spring Boot fat-jar обычным unzip в предсказуемую структуру
# (BOOT-INF/classes, BOOT-INF/lib) на диске. Это нужно для InMemoryJavaCompiler: он
# использует системный javac, а тот не умеет читать классы, вложенные внутрь fat-jar.
# После распаковки классы становятся обычной директорией на файловой системе, и
# стандартный classpath (java.class.path) начинает работать как обычно.
RUN mkdir extracted && unzip -q app.jar -d extracted

EXPOSE 8080
ENTRYPOINT ["java", "-cp", "extracted/BOOT-INF/classes:extracted/BOOT-INF/lib/*", "footballmanager.FootballManagerApp"]
