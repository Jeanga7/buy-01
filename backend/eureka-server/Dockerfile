# Build stage with Maven
FROM maven:3.8.7-eclipse-temurin-17-alpine AS build
WORKDIR /app
# Copy only the POM file first to leverage Docker cache
COPY pom.xml .
# Download dependencies (cached unless pom.xml changes)
RUN mvn dependency:go-offline -B
# Copy source code
COPY src ./src
# Package the application
RUN mvn clean package -DskipTests

# Final image
FROM eclipse-temurin:17-jre-alpine

# Install wget for healthcheck
USER root
RUN apk add --no-cache wget

WORKDIR /app
# Copy only the built JAR file
COPY --from=build /app/target/*.jar app.jar

# Run as non-root user
RUN addgroup -S spring && adduser -S spring -G spring
RUN chown -R spring:spring /app
USER spring:spring

# Optimize JVM for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+OptimizeStringConcat -XX:+UseStringDeduplication"
EXPOSE 8761

# Health check
HEALTHCHECK --interval=10s --timeout=5s --start-period=30s --retries=3 \
  CMD wget --spider -q http://localhost:8761/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
