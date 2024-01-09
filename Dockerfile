FROM eclipse-temurin:17-jdk-alpine

VOLUME /tmp

COPY build/libs/*.jar app.jar
COPY src/main/resources/order-portal.p12 order-portal.p12

ENTRYPOINT ["java","-jar","/app.jar"]

EXPOSE 8080