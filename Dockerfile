FROM gradle:8.2.1-jdk17 as gradle-cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home
COPY build.gradle.kts /home/gradle/java-code/
COPY src/main/graphql /home/gradle/java-code/src/main/graphql
WORKDIR /home/gradle/java-code
RUN gradle clean build -i --stacktrace -x bootJar

FROM gradle:8.2.1-jdk17 as app-artifact-cache
COPY --from=gradle-cache /home/gradle/cache_home /home/gradle/.gradle
COPY . /usr/src/java-code/
WORKDIR /usr/src/java-code
RUN gradle bootJar -i --stacktrace

FROM eclipse-temurin:17-jdk as run-app
USER root
WORKDIR /usr/src/java-app
COPY --from=app-artifact-cache /usr/src/java-code/build/libs/*.jar ./app.jar
COPY --from=app-artifact-cache /usr/src/java-code/src/scripts/start-server-docker.sh ./entry-point.sh
ENTRYPOINT ./entry-point.sh

FROM gradle:8.2.1-jdk17 as run-e2e
COPY --from=app-artifact-cache /home/gradle/.gradle /home/gradle/.gradle
COPY --from=app-artifact-cache /usr/src/java-code/ /usr/src/java-code/
WORKDIR /usr/src/java-code/
ENTRYPOINT ./src/scripts/run-all-tests-docker.sh