#build docker image
docker build  --no-cache --tag mnsdummy:0.1 .

#start container example
docker run -p 8588:8588 -e serverNo=1  --name testmns mnsdummy:0.1
#docker run -p 8589:8588 -e serverNo=2  --name testmns mnsdummy:0.1
#docker run -p 8590:8588 -e serverNo=3  --name testmns mnsdummy:0.1

#start container rm
docker rm -f testmns
docker rmi -f mnsdummy:0.1
