FROM openjdk:14-jdk-alpine
ARG JAR_FILE=target/DiplomaRepository.jar
WORKDIR /opt/app
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]