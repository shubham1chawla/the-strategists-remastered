# The Strategists - Front-end Application

This directory is a React application that serves as a web application for The Strategists.

## Setup

Follow the steps mentioned below to understand how to setup the React application, and create Docker images. You may also refer to the troubleshooting steps if you face any issues.

### General

- Please configure the `.env` file and populate any missing configuration before running the application.
- If you use a Mac, use the `host-local-mac.sh` command to locally host the web application on the network.

### Docker

- Before creating a Docker image, make sure to configure the `.env` file.
- Build a Docker image from the **current directory (web)** using the `docker buildx build -t <REPOSITORY_NAME>/strategists-web:<TAG_NAME> .` command.
- To run the image, use the `docker run -p 3000:80 <REPOSITORY_NAME>/strategists-web:<TAG_NAME>` command.

### Troubleshooting

- To check the files in the Docker container, enter its shell using the `docker exec -it <CONTAINER_ID or CONTAINER_NAME> sh` command.
