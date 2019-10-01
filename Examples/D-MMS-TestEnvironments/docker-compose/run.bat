@echo off

docker network create mcp-network
docker-compose -p mcp-mms1 -f docker-compose.yml -f docker-compose-1.yml  up -d
docker-compose -p mcp-mms2 -f docker-compose.yml -f docker-compose-2.yml  up -d
docker-compose -p mcp-mms3 -f docker-compose.yml -f docker-compose-3.yml  up -d
