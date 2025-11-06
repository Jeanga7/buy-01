FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /workspace/app
COPY pom.xml .
COPY src src/
RUN rm -rf ~/.m2/repository && mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy
VOLUME /tmp
COPY --from=build /workspace/app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
