FROM openjdk:17

WORKDIR /app
COPY ./build/libs/order-portal-0.0.1-SNAPSHOT.jar /app

EXPOSE 8080

CMD ["java", "-jar", "order-portal-0.0.1-SNAPSHOT.jar"]