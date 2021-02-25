FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
copy ${JAR_FILE} product-app.jar
ENTRYPOINT ["java","-jar","/product-app.jar"]
EXPOSE 8080
