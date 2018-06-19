FROM openjdk:8-jdk-alpine
COPY target/equitybot-algoritham-svc-0.0.1.jar app.jar
EXPOSE 9012
ENTRYPOINT ["java","-Dspring.profiles.active=kubernetes","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]