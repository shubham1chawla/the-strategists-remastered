######## Maven Build Stage ########

FROM maven:3.9.8-eclipse-temurin-21 AS maven_build
WORKDIR /app

# copying pom.xml
COPY pom.xml .
    
# copying source code
COPY src src

# building the app and downloading dependencies only when these are new (thanks to the cache)
RUN --mount=type=cache,target=/root/.m2  mvn clean package -Dmaven.test.skip

# spliting the built app into multiple layers to improve layer rebuild
RUN mkdir -p target/docker-packaging && cd target/docker-packaging && jar -xf ../*.jar

######## Extracting Jar ########

FROM openjdk:26-ea-21-jdk-slim-trixie

# copying built Spring Boot app layer by layer
ARG DOCKER_PACKAGING_DIR=/app/target/docker-packaging
COPY --from=maven_build ${DOCKER_PACKAGING_DIR}/BOOT-INF/lib /app/lib
COPY --from=maven_build ${DOCKER_PACKAGING_DIR}/BOOT-INF/classes /app/classes
COPY --from=maven_build ${DOCKER_PACKAGING_DIR}/META-INF /app/META-INF

# moving to working directory
WORKDIR /app

######## Execution Stage ########

# running the Spring Boot application
CMD java -cp .:classes:lib/* \
         -Djava.security.egd=file:/dev/./urandom \
         com.strategists.game.StrategistsService
