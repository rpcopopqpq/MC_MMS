#build docker image
docker build --no-cache --tag mmsserver:0.1 .

#start container example
docker run -v ./logs:/app/mms/logs -p 8088:8088 --name testmms mmsserver:0.1
#docker run -v ./logs:/app/mms/logs -p 8089:8088 --name testmms mmsserver:0.1

#start container rm
docker rm -f testmms
docker rmi -f mmsserver:0.1
