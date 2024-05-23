######## Maven Build Stage ########

FROM maven:3.8.5-openjdk-17 as maven_build
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

# installing python 3.9 and java jdk 17
FROM python:3.9-slim
COPY --from=openjdk:17-jdk-slim-buster /usr/local/openjdk-17 /usr/local/openjdk-17
ENV JAVA_HOME /usr/local/openjdk-17
RUN update-alternatives --install /usr/bin/java java /usr/local/openjdk-17/bin/java 1
WORKDIR /app

# copying built Spring Boot app layer by layer
ARG DOCKER_PACKAGING_DIR=/app/target/docker-packaging
COPY --from=maven_build ${DOCKER_PACKAGING_DIR}/BOOT-INF/lib /app/server/lib
COPY --from=maven_build ${DOCKER_PACKAGING_DIR}/BOOT-INF/classes /app/server/classes
COPY --from=maven_build ${DOCKER_PACKAGING_DIR}/META-INF /app/server/META-INF

# copying prediction model
COPY prediction /app/prediction

# installing poetry
WORKDIR /app/prediction
RUN python3 -m pip install --upgrade pip && \
    pip3 install poetry && \
    poetry install

# copying google-utils model
COPY google-utils /app/google-utils

# installing poetry
WORKDIR /app/google-utils
RUN python3 -m pip install --upgrade pip && \
    pip3 install poetry && \
    poetry install

######## Execution Stage ########

# running the Spring Boot application
WORKDIR /app/server
CMD java -cp .:classes:lib/* \
         -Djava.security.egd=file:/dev/./urandom \
         com.strategists.game.StrategistsService
