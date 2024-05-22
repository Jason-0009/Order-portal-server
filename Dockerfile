FROM openjdk:21

VOLUME /tmp

COPY build/libs/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=production

ENTRYPOINT ["java","-jar","/app.jar"]