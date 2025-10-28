FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copia os arquivos do projeto
COPY . .

# Build da aplicação
RUN mvn clean package -DskipTests

# Stage final
FROM openjdk:21-jdk-slim

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copia o JAR do stage de build
COPY --from=build /app/bot-financas-whatsapp/target/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/telegram/status || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]