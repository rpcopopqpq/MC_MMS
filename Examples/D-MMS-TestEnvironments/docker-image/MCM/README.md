#build docker image
docker build --no-cache --tag mcm:0.1 .

#start container example
docker run -v ./logs:/app/mcm/logs -p 8588:8588 -e dbHost=localhost -e dbPort=3306 --name testmcm mcm:0.1 

#start container rm
docker rm -f testmcm
docker rmi -f mcm:0.1
