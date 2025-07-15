FROM maven:3.9.10-eclipse-temurin-17

ENV APP_NAME=gestion-association

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 9092

ENTRYPOINT ["java", "-jar", "app.jar"]
