# The Strategists - Back-end Service

This directory is a Spring Boot application that serves as a back-end service for The Strategists.

## Setup

Follow the steps mentioned below to understand how to setup the Spring Boot application, and create Docker images. You may also refer to the troubleshooting steps if you face any issues.

### General
- Please refer to the `application.yml` file to view all the Spring and application-related configurations.
- You will need to provide an admin's email address to run the application. Either create your own spring profile with the `strategists.admin.email` key, or pass it as a VM argument as `-Dstrategists.admin.email=<YOUR_EMAIL_ADDRESS>`. **DO NOT** hard-code an email address in the default `application.yml` file.
- If you want to log auto-generated SQLs, change the `spring.jpa.show-sql` to `true` in the `application.yml` or pass it as a VM argument as `-Dspring.jpa.show-sql=true`. If you are changing the default `application.yml` file, **DO NOT** commit it to the repository.
- H2 is enabled by default, if you want to disable it, change the `spring.h2.console.enabled` to `false` in the `application.yml` or pass it as a VM argument as `-Dspring.h2.console.enabled=false`. If you are changing the default `application.yml` file, **DO NOT** commit it to the repository.

### Docker
- Before creating a Docker image, make sure you have created a dedicated `application-docker.yml` file in the resources directory along with `application.yml`.
- Overwrite any default configuration in the dedicated `yml` file. **DO NOT** update the default `application.yml` file.
- Make sure to add the `strategists.admin.email` key in the Docker's dedicated `yml` file. The application won't start otherwise as the admin's email address is not provided in the default `application.yml` file.
- Once you have created a dedicated `yml` file, you can build a Docker image from the **parent directory (outside server)** using the `docker buildx build -f server/Dockerfile -t <REPOSITORY_NAME>/strategists-service:<TAG_NAME> .` command.
- To run the image, use the `docker run -e PROFILE=docker -p 8090:8090 <REPOSITORY_NAME>/strategists-service:<TAG_NAME>` command. This command assumes that your dedicated file is named `application-docker.yml`.

### Troubleshooting
- To check the files in the Docker container, enter its shell using the `docker exec -it <CONTAINER_ID or CONTAINER_NAME> sh` command.

## References
- Starter Dockerfile is from [this StackOverflow article](https://stackoverflow.com/questions/27767264/how-to-dockerize-maven-project-and-how-many-ways-to-accomplish-it). Read it to learn more.