FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY gradlew build.gradle.kts settings.gradle.kts lombok.config ./
COPY gradle ./gradle
COPY src ./src
RUN --mount=type=cache,target=/root/.gradle sh ./gradlew --no-daemon build

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/riot-take-home-0.0.1-SNAPSHOT.jar app.jar
USER 10001:10001
ENV HOST=0.0.0.0
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
