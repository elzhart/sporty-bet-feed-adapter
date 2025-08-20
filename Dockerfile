# ========= 1) Build stage =========
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY gradlew /app/gradlew
COPY gradle /app/gradle
COPY build.gradle settings.gradle /app/
RUN chmod +x /app/gradlew
RUN ./gradlew --no-daemon dependencies > /dev/null || true

COPY src /app/src
RUN ./gradlew --no-daemon clean bootJar -x test

# ========= 2) Runtime stage =========
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV JAVA_OPTS=""
COPY --from=build /app/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]