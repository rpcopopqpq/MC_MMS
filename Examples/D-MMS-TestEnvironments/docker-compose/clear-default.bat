@echo off
docker-compose -p mcp-mms1 -f docker-compose-default.yml -f docker-compose-1.yml  down
docker-compose -p mcp-mms2 -f docker-compose-default.yml -f docker-compose-2.yml  down
docker-compose -p mcp-mms3 -f docker-compose-default.yml -f docker-compose-3.yml  down
docker-compose -p home-manager -f home-manager.yml down
