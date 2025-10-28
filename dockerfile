FROM openjdk:21-jdk-slim

RUN apt-get update && apt-get install -y curl

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/telegram/status || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]