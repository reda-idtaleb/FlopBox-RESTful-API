# syntax=docker/dockerfile:1
FROM eclipse-temurin:25-jdk-jammy AS build
WORKDIR /workspace
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B -q -DskipTests dependency:go-offline
COPY src src
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests package

FROM eclipse-temurin:25-jre-alpine
RUN addgroup -S flopbox && adduser -S flopbox -G flopbox
WORKDIR /app
COPY --from=build --chown=flopbox:flopbox /workspace/target/flopbox-api-*.jar app.jar
USER flopbox
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget --spider -q http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]
