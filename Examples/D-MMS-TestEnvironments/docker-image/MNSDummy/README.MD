#build docker image
docker build --tag mnsdummy .

#start container example
docker run -p 8588:8588 -e mnsPort=8588  --name testmns mnsdummy
docker run -p 8589:8589 -e mnsPort=8589  --name testmns mnsdummy
docker run -p 8590:8590 -e mnsPort=8590  --name testmns mnsdummy

docker run -v ./java/:/app/mms/java/ -p 8588:8588 -e mnsPort=8588  --name testmns mnsdummy

#start container rm
docker rm -f testmns
docker rmi -f mnsdummy
