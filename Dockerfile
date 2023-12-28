FROM maven:3.9.5-eclipse-temurin-21 AS builder

WORKDIR /app
COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn
COPY src src

RUN mvn package -Dmaven.test.skip=true

FROM maven:3.9.5-eclipse-temurin-21
WORKDIR /app
COPY --from=builder /app/target/travlr-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=3000
ENV SPRING_REDIS_HOST=host.docker.internal
ENV SPRING_REDIS_PORT=6379
ENV SPRING_REDIS_DATABASE=0
ENV SPRING_REDIS_USERNAME=
ENV SPRING_REDIS_PASSWORD=

ENV USER_KEY=users
ENV SESSION_KEY=authenticatedUsers
ENV SESSION_TIMEOUT=10

ENV GEOCODE_URL=https://maps.googleapis.com/maps/api/geocode/json

EXPOSE ${PORT}

ENTRYPOINT SERVER_PORT=${PORT} java -jar app.jar