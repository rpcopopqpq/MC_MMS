@echo off
FOR /f "tokens=*" %%i IN ('docker ps --filter "name=mcp_mms" -aq') DO docker rm -f %%i
