FROM openjdk:latest
LABEL authors="orestklymko"
ADD target/finance-backend-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar","app.jar"]