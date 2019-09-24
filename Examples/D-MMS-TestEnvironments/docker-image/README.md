# D-MMS TEST Environmental

MMS1

    - 8088
    - rabbitmq
       - 15672 
       - 5672
    - redis
       - 6379     
    - mcm
       - 8588
       - mariadb 
          - 3306

MMS2

    - 8089
    - rabbitmq
       - 15673 
       - 5673
    - redis
       - 6380     
    - mcm
       - 8589
       - mariadb 
          - 3307
          
MMS3

    - 8090
    - rabbitmq
       - 15674 
       - 5674
    - redis
       - 6381     
    - mcm
       - 8590
       - mariadb 
          - 3308


#######docker rm -f $(docker ps -aq --filter="name=mms")
docker stop mms1-rabbit mms1-redis mcm1-db

docker run -d --hostname mms1-rabbit --name mms1-rabbit -p 15672:15672 -p 5672:5672 rabbitmq:3-management 
docker run -d --hostname mms2-rabbit --name mms2-rabbit -p 15673:15672 -p 5673:5672 rabbitmq:3-management 
docker run -d --hostname mms3-rabbit --name mms3-rabbit -p 15674:15672 -p 5674:5672 rabbitmq:3-management

docker run --name mms1-redis -d -p 6379:6379 redis
docker run --name mms2-redis -d -p 6380:6379 redis
docker run --name mms3-redis -d -p 6381:6379 redis

docker run -dit --name mcm1-db --restart=always --publish=3306:3306 -e MYSQL_ROOT_PASSWORD=root123 ish128/geokkurodb:0.1
docker run -dit --name mcm2-db --restart=always --publish=3307:3306 -e MYSQL_ROOT_PASSWORD=root123 ish128/geokkurodb:0.1
docker run -dit --name mcm3-db --restart=always --publish=3308:3306 -e MYSQL_ROOT_PASSWORD=root123 ish128/geokkurodb:0.1

java -jar -Dspring.profiles.active=development -Dmcm.server.port=8588 -Dspring.datasource.url="jdbc:mysql://localhost:3306/geokkurodb?useUnicode=yes&characterEncoding=UTF-8"  mcm-0.0.1-SNAPSHOT.jar 
java -jar -Dspring.profiles.active=development -Dmcm.server.port=8589 -Dspring.datasource.url="jdbc:mysql://localhost:3307/geokkurodb?useUnicode=yes&characterEncoding=UTF-8"  mcm-0.0.1-SNAPSHOT.jar
java -jar -Dspring.profiles.active=development -Dmcm.server.port=8590 -Dspring.datasource.url="jdbc:mysql://localhost:3308/geokkurodb?useUnicode=yes&characterEncoding=UTF-8"  mcm-0.0.1-SNAPSHOT.jar





 
