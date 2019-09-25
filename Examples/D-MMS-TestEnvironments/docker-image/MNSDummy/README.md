#build docker image
docker build --tag mnsdummy .

#start container example (optionNo : one of 1 ~ 3 )
docker run -p 8588:8588 -e optionNo=1 --name testmns mnsdummy
#docker run -p 8589:8588 -e optionNo=2 --name testmns mnsdummy

#start container rm
docker rm -f testmns
docker rmi -f mnsdummy
