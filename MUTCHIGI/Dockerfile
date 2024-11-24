FROM openjdk:17
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
COPY src/main/resources /app/resources
ENV GOOGLE_APPLICATION_CREDENTIALS=/app/resources/capston-test-436509-ca7d25492dcb.json
ENTRYPOINT ["java", "-jar", "/app.jar"]

