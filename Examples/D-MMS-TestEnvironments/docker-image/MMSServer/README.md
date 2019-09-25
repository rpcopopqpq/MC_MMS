#build docker image
docker build --tag mmsserver .

#start container example
docker run -v ./log:/app/mms/logs -p 8088:8088 --name testmms mmsserver
docker run -v ./log:/app/mms/logs -p 8089:8088 --name testmms mmsserver

#start container rm
docker rm -f testmms
docker rmi -f mmsserver
