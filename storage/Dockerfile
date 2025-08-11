FROM python:3.12-alpine

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
COPY storage storage
COPY main.py .

# starting fastapi server
CMD ["fastapi", "run", "main.py", "--port", "80"]
