FROM openjdk:8-jdk-alpine

ENV dbHost localhost
ENV dbPort 3306

EXPOSE 8588

COPY bootJar/mcm-*.jar /app/mcm/mcm.jar

WORKDIR /app/mcm
#ENTRYPOINT java -jar -Dspring.profiles.active=development -Dmcm.server.port=$mcmPort -Dspring.datasource.url=jdbc:mysql://localhost:3306/geokkurodb?useUnicode=yes&characterEncoding=UTF-8  mcm-0.0.1-SNAPSHOT.jar
ENTRYPOINT java -jar -Dspring.profiles.active=development -Dspring.datasource.url=jdbc:mysql://$dbHost:$dbPort/mcmdb?useUnicode=yes&characterEncoding=UTF-8  mcm.jar
