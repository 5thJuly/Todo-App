FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# --- STAGE 2: Use lightweight JDK image to run the app ---
FROM openjdk:21-jdk-slim
LABEL maintainer="Admin"
WORKDIR /app
COPY --from=build /app/target/todoapp-0.0.1-SNAPSHOT.jar /app/todoapp-0.0.1-SNAPSHOT.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "/app/todoapp-0.0.1-SNAPSHOT.jar"]