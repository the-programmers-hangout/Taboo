FROM gradle:7.0.2-jdk16 AS build
COPY --chown=gradle:gradle . /taboo
WORKDIR /taboo
RUN gradle shadowJar --no-daemon

FROM openjdk:11.0.8-jre-slim
RUN mkdir /config/
COPY --from=build /taboo/build/libs/Taboo.jar /

ENTRYPOINT ["java", "-jar", "/Taboo.jar"]