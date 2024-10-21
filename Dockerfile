FROM amazoncorretto:17.0.5
ARG JAR_FILE=canvas-search.jar
COPY ${JAR_FILE} /app/canvas-search.jar
ENTRYPOINT ["java","-jar","/app/canvas-search.jar"]

