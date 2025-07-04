FROM openjdk:21-jdk

WORKDIR /app

COPY build/libs/Igame-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]

EXPOSE 8585