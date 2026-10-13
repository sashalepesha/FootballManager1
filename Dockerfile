# Этап 1: Сборка приложения
FROM amazoncorretto:25.0.3 AS builder

WORKDIR /build

# Устанавливаем утилиты, необходимые для сборки
RUN dnf install -y findutils sed && dnf clean all

# Копируем проект
COPY . .

# Исправляем окончания строк Windows CRLF на Linux LF
# и разрешаем запуск Gradle Wrapper
RUN sed -i 's/\r$//' gradlew \
    && chmod +x gradlew

# Собираем приложение
RUN ./gradlew --no-daemon clean bootJar

# Этап 2: Запуск приложения
FROM amazoncorretto:25.0.3-alpine

WORKDIR /app

# Копируем готовый Spring Boot JAR
COPY --from=builder /build/build/libs/football-manager-0.0.1-SNAPSHOT.jar app.jar

# Распаковываем JAR
RUN mkdir extracted \
    && unzip -q app.jar -d extracted

EXPOSE 8080

ENTRYPOINT ["java", "-cp", "extracted/BOOT-INF/classes:extracted/BOOT-INF/lib/*", "footballmanager.FootballManagerApp"]

