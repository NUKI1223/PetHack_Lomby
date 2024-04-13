FROM openjdk:17.0.2-jdk-slim-buster
ARG JAR_FILE=target/Lorby-0.0.1.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]