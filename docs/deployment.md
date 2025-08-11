# The Strategists - Deployment

This document serves to help admins deploy _The Strategists_ on a cloud environment, enabling
everyone to enjoy the game at via internet.

## Deploying Backend Service on _Google Cloud_

1. Create a Google Cloud VM instance, ideally `e2-highcpu-2` since the backend service builds
   machine learning models that require CPU power.
2. Allow `HTTP` & `HTTPS` traffic on this instance. Since we will be using `HTTP`, this step
   will allow traffic to come through port `80`.
3. Optionally, you can check the `Delete disk on VM delete` deletion rule for your peace of
   mind.
4. Once, the VM starts, use the `External IP` (`http://<external_ip>`) as the
   `VITE_API_BASE_URL` environment variable on _Netlify_ and rebuild the _Netlify_ app.
5. Follow the steps mentioned below to install _Docker_ on the VM instance.

### Installing _Docker_ on GCP VM Instance

> [!NOTE]
> These steps assume you are using a `debian`-based operating system.

1. Update the package list by running the following command -

```sh
sudo apt update
```

2. Install required packages -

```sh
sudo apt install apt-transport-https ca-certificates curl gnupg2 software-properties-common
```

3. Add the _Docker_ repository -

```sh
curl -fsSL https://download.docker.com/linux/debian/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/debian $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```

4. Update the package list again -

```sh
sudo apt update
```

5. Install _Docker_ engine -

```sh
sudo apt install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

6. Start and enable _Docker_ -

```sh
sudo systemctl start docker
sudo systemctl enable docker
```

7. Verify installation -

```sh
sudo docker run hello-world
```

### Starting Backend Service

To start the backend service, execute the following command -

```sh
docker-compose -f docker-compose.headless.yml up -d
```

> [!NOTE]
> This command assumes that you have configured all the enironment variables. If not, refer to
> the [self-hosting documentation](./self-host.md). Refer to the [Troubleshooting](#troubleshooting)
> section below if you face _Docker_-related issues.

#### Troubleshooting

If you are getting the following error -

```
permissions - docker: permission denied while trying to connect to the Docker daemon socket at unix:///var/run/docker.sock: Head "http://%2Fvar%2Frun%2Fdocker.sock/_ping": dial unix /var/run/docker.sock: connect: permission denied
```

Then run the following commands -

1. `sudo groupadd docker`
2. `sudo usermod -aG docker $USER`
3. `newgrp docker`
4. `sudo chmod 666 /var/run/docker.sock`
5. `sudo systemctl restart docker`
