FROM openjdk:8-jdk-alpine

RUN mkdir -p /opt/k8spipeline

COPY ./build/libs/k8spipeline-0.0.1-*.jar /app/k8spipeline.jar

CMD ["java", "-jar", "/app/k8spipeline.jar"]
