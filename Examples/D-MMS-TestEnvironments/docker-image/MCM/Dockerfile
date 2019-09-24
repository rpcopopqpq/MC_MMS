FROM java:8
EXPOSE 8588

ADD mcm-0.0.1-SNAPSHOT.jar mcm-0.0.1-SNAPSHOT.jar

ENTRYPOINT java -jar -Dspring.profiles.active=development -Dmcm.server.port=8588 -Dspring.datasource.url=jdbc:mysql://localhost:3306/geokkurodb?useUnicode=yes&characterEncoding=UTF-8  mcm-0.0.1-SNAPSHOT.jar
