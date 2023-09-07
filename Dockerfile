FROM eclipse-temurin:17-jdk-jammy

WORKDIR /docker

COPY . .

RUN ./gradlew clean build

WORKDIR /docker/build/libs

ENTRYPOINT java -jar LearnSpring-0.0.1-SNAPSHOT.jar