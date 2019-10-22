#build docker image
docker build --no-cache --tag home-manager:0.1 .

#start container example
docker run \
-v ./logs:/app/home-manager/logs \
-p 8081:8081 \ 
-e dbHost=localhost \
-e dbPort=3306 \
-e dbUser=hm \
-e dbPassword=hm1234 \
--name testHomemanager home-manager:0.1 

#start container rm
docker rm -f testHomemanager
docker rmi -f home-manager:0.1
