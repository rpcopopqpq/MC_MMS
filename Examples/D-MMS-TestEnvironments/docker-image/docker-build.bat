@echo off
docker rmi -f mnsdummy
docker rmi -f mmsserver


cd MNSDummy
docker build --tag mnsdummy .

cd ..\MMSServer
docker build --tag mmsserver .
