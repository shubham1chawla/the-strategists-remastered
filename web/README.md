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

### Netlify

- The web application is hosted using [Netlify](https://netlify.com/), which help's use the `HTTPS` protocol required for Google sign-in and Recaptcha functionalities.
- Netlify handles all API proxies using Netlify Functions & Edge-functions since the APIs are accessed using `HTTP` protocol.
- Install the Netlify CLI using `npm i netlify-cli -g` command. If you are on mac, use `brew install netlify-cli` to avoid permission issues.
- To test the application using Netlify Function's use the command `netlify dev` command.

### Troubleshooting

- To check the files in the Docker container, enter its shell using the `docker exec -it <CONTAINER_ID or CONTAINER_NAME> sh` command.
