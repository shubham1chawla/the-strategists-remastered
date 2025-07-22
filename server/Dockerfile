######## Maven Build Stage ########

FROM maven:3.9.8-eclipse-temurin-21 AS maven_build
WORKDIR /app

# copying pom.xml
COPY /server/pom.xml .
    
# copying source code
COPY /server/src ./src

# building the app and downloading dependencies only when these are new (thanks to the cache)
RUN --mount=type=cache,target=/root/.m2  mvn clean package -Dmaven.test.skip

# spliting the built app into multiple layers to improve layer rebuild
RUN mkdir -p target/docker-packaging && cd target/docker-packaging && jar -xf ../*.jar

######## Extracting Jar & Prediction Model ########

FROM python:3.12-slim-bookworm

# installing java in the image
COPY --from=openjdk:21-jdk-slim-buster /usr/local/openjdk-21 /usr/local/openjdk-21
ENV JAVA_HOME /usr/local/openjdk-21
RUN update-alternatives --install /usr/bin/java java /usr/local/openjdk-21/bin/java 1

# installing uv in the image
COPY --from=ghcr.io/astral-sh/uv:0.7.16 /uv /uvx /bin/

# moving to working directory
WORKDIR /app

# copying built Spring Boot app layer by layer
ARG DOCKER_PACKAGING_DIR=/app/target/docker-packaging
COPY --from=maven_build ${DOCKER_PACKAGING_DIR}/BOOT-INF/lib /app/server/lib
COPY --from=maven_build ${DOCKER_PACKAGING_DIR}/BOOT-INF/classes /app/server/classes
COPY --from=maven_build ${DOCKER_PACKAGING_DIR}/META-INF /app/server/META-INF

# copying prediction model
COPY prediction /app/prediction

# setting up python venv
WORKDIR /app/prediction
RUN uv sync --locked

# copying google-utils model
COPY google-utils /app/google-utils

# setting up python venv
WORKDIR /app/google-utils
RUN uv sync --locked

######## Execution Stage ########

# running the Spring Boot application
WORKDIR /app/server
CMD java -cp .:classes:lib/* \
         -Djava.security.egd=file:/dev/./urandom \
         com.strategists.game.StrategistsService
