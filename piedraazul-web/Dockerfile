# Etapa 1: compila el frontend Angular para producción
FROM node:22-alpine AS build

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npx ng build --configuration production

# Etapa 2: sirve los archivos estáticos con nginx
FROM nginx:1.27-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/piedraazul-web/browser /usr/share/nginx/html

EXPOSE 80
