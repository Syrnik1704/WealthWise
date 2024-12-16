FROM node:18 AS build

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . ./
RUN npm run build --prod

RUN find /app/dist -name '*.map' -type f -delete

FROM nginx:alpine

COPY ./nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=build /app/dist/fe /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
