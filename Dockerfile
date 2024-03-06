#
# Project build (Multi-Stage)
# --------------------------------
#
# We use a Maven image to build the project with Java 17
# We will call this sub-environment "build"
# Copy all the contents of the repository
# Run the mvn clean package command (It will generate a JAR file for deployment)
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package

# We use an Openjdk v17 image
# We expose the port that our component will use to listen to requests
# We copy from "build" the generated JAR (the generation path is the same as we would see locally) and we move and rename it in destination as app.jar
# We mark the starting point of the image with the command "java -jar app.jar" that will execute our component.
FROM openjdk:17
EXPOSE 8080
COPY --from=build /target/microservice-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]