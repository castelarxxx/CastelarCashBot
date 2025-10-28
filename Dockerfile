FROM openjdk:21-jdk-slim

# Instala dependências
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copia o JAR da aplicação
COPY bot-financas-whatsapp/target/*.jar app.jar

# Expõe a porta
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/telegram/status || exit 1

# Comando para rodar
ENTRYPOINT ["java", "-jar", "app.jar"]