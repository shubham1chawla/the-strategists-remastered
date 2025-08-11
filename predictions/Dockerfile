FROM python:3.12-slim

# installing libgomp1 for lightgbm issues inside docker
# https://stackoverflow.com/questions/55036740/lightgbm-inside-docker-libgomp-so-1-cannot-open-shared-object-file
RUN apt-get update && apt-get install -y --no-install-recommends apt-utils
RUN apt-get -y install curl
RUN apt-get install libgomp1

# installing uv in the image
COPY --from=ghcr.io/astral-sh/uv:0.7.16 /uv /uvx /bin/

# moving to app directory
WORKDIR /app

# installing dependencies and creating python venv
COPY pyproject.toml .
COPY uv.lock .
COPY .python-version .
COPY README.md .
RUN uv sync --locked
ENV PATH="/app/.venv/bin:$PATH"

# copying source code into image
COPY predictions predictions
COPY main.py .

# setting to suppress GitPython warnings
ENV GIT_PYTHON_REFRESH=quiet

# starting fastapi server
CMD ["fastapi", "run", "main.py", "--port", "80"]
