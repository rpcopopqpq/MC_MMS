FROM openjdk:8-jdk-alpine

VOLUME /app/home-manager/logs

ENV dbHost localhost
ENV dbPort 3306

EXPOSE 8081

COPY home-manager*.jar /app/home-manager/home-manager.jar
RUN ["chmod", "+x", "/app/home-manager/home-manager.jar"]

WORKDIR /app/home-manager/
ENTRYPOINT java -jar -Dspring.profiles.active=development -Dspring.datasource.url="jdbc:mysql://$dbHost:$dbPort/mcmdb?useUnicode=yes&characterEncoding=UTF-8"  home-manager.jar
