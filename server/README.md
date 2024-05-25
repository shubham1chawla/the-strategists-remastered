# The Strategists - Backend Service

This directory is a Spring Boot application that serves as a backend service for The Strategists.

## Setup

Follow the steps below to understand how to set up the Spring Boot application and create Docker images.

### Step 1 - Importing Maven Project

- Use any IDE, such as [Eclipse](https://www.eclipse.org/) or [IntelliJ](https://www.jetbrains.com/idea/), to import this `server` folder containing the `pom.xml` file as a maven project.

### Step 2 - Setting up Environment Variables

In addition to all the configurations mentioned in the `application.yml` file, please refer to the following variables required to run the server. Please refer to the `google-utils/README.md` file for Google-related configurations.

> [!NOTE]
> If you are using an IDE like Eclipse, you can pass these environment variables to the application as `-D<VARIABLE_NAME>=<VALUE>` by adding them to the VM arguments of the run configuration.

Variable | Description | Type | Default Value
--- | --- | --- | ---
ENABLE_H2_CONSOLE | If set, the server will expose the H2 database console URL by the server. | `boolean` | `false`
ENABLE_SSE_PING | If set, the server will send a periodic ping to keep the SSE channel open. | `boolean` | `true`
ENABLE_CLEAN_UP | If set, the server will delete games after some time of inactivity. | `boolean` | `true`
ENABLE_SKIP_PLAYER | If set, the server will skip players' turns after some time of inactivity. | `boolean` | `true`
ENABLE_PREDICTIONS | If set, the server will train and execute the prediction model. | `boolean` | `true`
GOOGLE_RECAPTCHA_SECRET_KEY | Google Recaptcha Secret Key (Version 2) that will verify users after they check the 'I am not a robot' box. It would be best if you either created your own or used the testing one mentioned on [this website](https://developers.google.com/recaptcha/docs/faq#id-like-to-run-automated-tests-with-recaptcha.-what-should-i-do). | `String` | none
GOOGLE_CREDENTIALS_JSON | Path to the Google Service Account's Credentials as a JSON file. Learn how to create service accounts from [this webpage](https://cloud.google.com/iam/docs/service-accounts-create). | `String` | none
GOOGLE_SPREADSHEET_ID | Google Spreadsheet ID, which manages user permission groups. | `String` | none
GOOGLE_SPREADSHEET_RANGE | Range you want the server to query to fetch the permission groups. | `String` | none
GOOGLE_DRIVE_DOWNLOAD_FOLDER_ID | Google Drive folder ID where all the game data is present. | `String` | none
GOOGLE_DRIVE_UPLOAD_FOLDER_ID | Google Drive folder ID where the server should upload new game data. | `String` | none

### Step 3 - Running Server

- After configuring the VM arguments, you can run the `StrategistsService.java` file from your IDE to start the backend server.

## Docker

Read the following steps to build and run the backend service's Docker image.

### Step 1 - How to build a Docker Image?

> [!IMPORTANT]
> Ensure you execute this build command from the root directory, not inside the `server` directory!

You can use the following command to build a Docker image for the backend service.

    docker buildx build -f server/Dockerfile -t strategists-service .

> [!TIP]
> You can also use the `docker-compose build` command from the root directory to build the server's Docker image.

### Step 2 - Setting up environment variables file

- Copy/Move the service account's `credentials.json` file in the `shared/secrets` directory.
- Create a `service.env.list` file inside the `shared/secrets` directory containing all the required environment variables.

> [!TIP]
> You can bypass Google Utils functionalities for **TESTING ONLY** by following the steps mentioned here.
> - Either edit the `application.yml` file or pass VM argument `-Dstrategists.google.utils.permissions.bypass-google-sheets-query-for-testing=true` to bypass querying Google Spreadsheets for fetching the permission groups. You must manually create a testing `permissions.json` file in the `shared/secrets` directory.

### Step 3 - How to run this Docker image?

You can run a container from this Docker image using the following command.

    docker run \
    --env-file shared/secrets/server.env.list \
    -v ./shared:/app/shared \
    -p 8090:8090 \
    strategists-service

> [!TIP]
> You can also use the `docker-compose up` command from the root directory to run the server's Docker image. Please make the necessary changes to expose the server's port so that APIs can be accessed.

### Step 4 - How do you enter an interactive shell attached to a running Docker container?
- To check the files in the Docker container, enter its shell using the `docker exec -it <CONTAINER_ID or CONTAINER_NAME> sh` command.

## References
- Starter Dockerfile is from [this StackOverflow article](https://stackoverflow.com/questions/27767264/how-to-dockerize-maven-project-and-how-many-ways-to-accomplish-it). Read it to learn more.
