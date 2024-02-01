######## Maven Build Stage ########

FROM maven:3.8.5-openjdk-17 as maven_build
WORKDIR /app

# copying pom.xml
COPY pom.xml .
    
# copying source code
COPY src ./src

# building the app and downloading dependencies only when these are new (thanks to the cache)
RUN --mount=type=cache,target=/root/.m2  mvn clean package -Dmaven.test.skip

# spliting the built app into multiple layers to improve layer rebuild
RUN mkdir -p target/docker-packaging && cd target/docker-packaging && jar -xf ../*.jar

######## Extracting Jar & Prediction Model ########

# installing python 3.9 and java jdk 17
FROM python:3.9-slim
COPY --from=openjdk:17-jdk-slim-buster /usr/local/openjdk-17 /usr/local/openjdk-17
ENV JAVA_HOME /usr/local/openjdk-17
RUN update-alternatives --install /usr/bin/java java /usr/local/openjdk-17/bin/java 1

# setting up working directory as 'app'
WORKDIR /app

# copying built app layer by layer
ARG DOCKER_PACKAGING_DIR=/app/target/docker-packaging
COPY --from=maven_build ${DOCKER_PACKAGING_DIR}/BOOT-INF/lib /app/lib
COPY --from=maven_build ${DOCKER_PACKAGING_DIR}/BOOT-INF/classes /app/classes
COPY --from=maven_build ${DOCKER_PACKAGING_DIR}/META-INF /app/META-INF

######## Execution Stage ########

# running the Spring Boot application
CMD java -cp .:classes:lib/* \
         -Djava.security.egd=file:/dev/./urandom \
         -Dspring.profiles.active=$PROFILE \
         com.strategists.game.StrategistsService
