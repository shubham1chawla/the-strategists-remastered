######## React Build Stage ########

FROM node:22-alpine AS build
WORKDIR /app

# copying package.json and package-lock.json to the working directory
COPY package*.json ./

# installing dependencies
RUN npm install

# copying the entire application code to the container
COPY . .

# building the React app for production
RUN npm run build

######## Execution Stage ########

FROM nginx:alpine

# copying the built React app to Nginx's web server directory
COPY --from=build /app/build /usr/share/nginx/html

# copying Nginx's configuration
COPY /nginx/react.conf /etc/nginx/conf.d/
RUN rm /etc/nginx/conf.d/default.conf

# hydrating docker placeholder env variables
COPY hydration.sh /docker-entrypoint.d/hydration.sh
RUN chmod +x /docker-entrypoint.d/hydration.sh

# exposing port for the Nginx server
EXPOSE 3000

# starting Nginx when the container runs
CMD ["nginx", "-g", "daemon off;"]