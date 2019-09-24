FROM openjdk:8-jdk-alpine

VOLUME /app/mms/jar
VOLUME /app/mms/logs
EXPOSE 8088

COPY start_mms.sh /app/mms/start_mms.sh
COPY jar/MMSServer.jar /app/mms/jar/MMSServer.jar
COPY lib/ /app/mms/lib/
COPY MMS-configuration/ /app/mms/MMS-configuration/

WORKDIR /app/mms

RUN sed -i 's/\r$//' /app/mms/start_mms.sh
RUN ["chmod", "+x", "/app/mms/start_mms.sh"]
CMD ["/app/mms/start_mms.sh"]

