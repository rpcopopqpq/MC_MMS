FROM openjdk:8-jdk-alpine

VOLUME /app/mcm/logs

ENV dbHost localhost
ENV dbPort 3306

EXPOSE 8588

COPY mcm*.jar /app/mcm/mcm.jar
RUN ["chmod", "+x", "/app/mcm/mcm.jar"]

WORKDIR /app/mcm/
ENTRYPOINT java -jar -Dspring.profiles.active=development -Dspring.datasource.url="jdbc:mysql://$dbHost:$dbPort/mcmdb?useUnicode=yes&characterEncoding=UTF-8"  mcm.jar
