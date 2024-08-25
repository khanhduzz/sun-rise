FROM eclipse-temurin:21-jre-alpine
COPY target/sun-rise*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]