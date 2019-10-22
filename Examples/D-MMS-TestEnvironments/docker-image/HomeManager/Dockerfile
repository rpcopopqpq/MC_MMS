FROM openjdk:8-jdk-alpine

VOLUME /app/home-manager/logs

ENV dbHost localhost
ENV dbPort 3306
ENV dbUser hm
ENV dbPassword hm1234

EXPOSE 8081

COPY home-manager*.jar /app/home-manager/home-manager.jar
RUN ["chmod", "+x", "/app/home-manager/home-manager.jar"]

WORKDIR /app/home-manager/
ENTRYPOINT java -jar -Dspring.profiles.active=development -Dspring.datasource.url="jdbc:mysql://$dbHost:$dbPort/hmdb?useUnicode=yes&characterEncoding=UTF-8"  -Dspring.datasource.username=$dbUser -Dspring.datasource.password=$dbPassword home-manager.jar
