# Dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copia o pom.xml primeiro (para cache de dependências)
COPY bot-financas-whatsapp/pom.xml .

# Baixa as dependências (cache)
RUN mvn dependency:go-offline -f pom.xml

# Copia o código fonte
COPY bot-financas-whatsapp/src ./src

# Build da aplicação
RUN mvn clean package -DskipTests -f pom.xml

# Stage final
FROM openjdk:21-jdk-slim

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copia o JAR do stage de build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/telegram/status || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]