# 빌드
FROM gradle:jdk21 as builder

WORKDIR /libs

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN ./gradlew dependencies --no-daemon

COPY src src

RUN ./gradlew build --no-daemon -x test

# 실행
FROM openjdk:21-slim

WORKDIR /app

COPY --from=builder /libs/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT [ "java", "-jar",  "app.jar" ]