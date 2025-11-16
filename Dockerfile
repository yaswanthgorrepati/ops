FROM eclipse-temurin:22-jre-alpine

WORKDIR /app

COPY target/*.jar ops.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "ops.jar"]
