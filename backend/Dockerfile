# supports ARM64
FROM amazoncorretto:17-alpine AS build

WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Copy dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon || true


# Copy source code and build
COPY src ./src
RUN ./gradlew clean bootJar --no-daemon

# Production stage
FROM amazoncorretto:17-alpine

WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

# Create dir for images
RUN mkdir -p /app/images

#Copy the built jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

#EXPOSE PORT
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
