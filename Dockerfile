# Start from base image containing Java Runtime
FROM openjdk:17-jdk-slim

# Information about who maintains the Docker Image
MAINTAINER eazybytes.com

# Copy Jar from local to Docker Image
COPY target/accounts-service-0.0.1-SNAPSHOT.jar accounts-service-0.0.1-SNAPSHOT.jar

# Command to run application (start container) from docker image
ENTRYPOINT ["java", "-jar", "accounts-service-0.0.1-SNAPSHOT.jar"]