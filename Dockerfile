FROM gradle:latest as builder
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle --no-daemon build

FROM openjdk:latest
EXPOSE 8080
COPY --from=builder /home/gradle/src/build/libs/ImageStorage.jar /app/ImageStorage.jar
WORKDIR /app
CMD java -jar ./ImageStorage.jar